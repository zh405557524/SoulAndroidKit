#include "AudioEngine.h"

#include <cstring>

#include <oboe/Oboe.h>

#include "../PitchDetector.h"

/**
 * Oboe 音频数据就绪回调：把 float 缓冲交给 [AudioEngine::processAudio]。
 */
class AudioEngine::CallbackImpl final : public oboe::AudioStreamCallback {
public:
    explicit CallbackImpl(AudioEngine& engine) : engine_(engine) {}

    oboe::DataCallbackResult onAudioReady(oboe::AudioStream* /*stream*/,
                                          void* audioData,
                                          int32_t numFrames) override {
        // 已 stop 时尽快返回，避免在 teardown 过程中仍处理数据
        if (!engine_.running_.load()) {
            return oboe::DataCallbackResult::Continue;
        }
        auto* data = static_cast<const float*>(audioData);
        engine_.processAudio(data, numFrames);
        return oboe::DataCallbackResult::Continue;
    }

private:
    AudioEngine& engine_;
};

AudioEngine::AudioEngine() {
    callback_ = new CallbackImpl(*this);
    pitchDetector_ = new PitchDetector();
    window_.resize(windowSize_);
}

AudioEngine::~AudioEngine() {
    stop();
    delete pitchDetector_;
    pitchDetector_ = nullptr;
    delete callback_;
    callback_ = nullptr;
}

bool AudioEngine::start(NoteCallback cb) {
    std::lock_guard<std::mutex> lock(cbMutex_);
    // 与 JNI 层“重复 start 先停旧”配合：正常情况下不应在 running 时再 start
    if (running_.load()) return false;

    noteCb_ = std::move(cb);
    windowFilled_ = 0;
    if (!window_.empty()) {
        std::fill(window_.begin(), window_.end(), 0.0f);
    }

    // --- Oboe 输入流配置：低延迟、单声道 float、48k、每次回调 hopSize_ 帧 ---
    oboe::AudioStreamBuilder builder;
    builder.setDirection(oboe::Direction::Input);
    builder.setSharingMode(oboe::SharingMode::Shared);
    builder.setPerformanceMode(oboe::PerformanceMode::LowLatency);
    builder.setFormat(oboe::AudioFormat::Float);
    builder.setChannelCount(1);
    builder.setSampleRate(sampleRate_);
    builder.setFramesPerCallback(hopSize_);
    builder.setCallback(callback_);

    builder.setUsage(oboe::Usage::Game);
    builder.setContentType(oboe::ContentType::Music);

    oboe::Result result = builder.openStream(&stream_);
    if (result != oboe::Result::OK || stream_ == nullptr) {
        stream_ = nullptr;
        running_.store(false);
        return false;
    }

    result = stream_->requestStart();
    if (result != oboe::Result::OK) {
        oboe::Result closeResult = stream_->close();
        (void)closeResult;
        stream_ = nullptr;
        running_.store(false);
        return false;
    }

    running_.store(true);
    return true;
}

bool AudioEngine::stop() {
    std::lock_guard<std::mutex> lock(cbMutex_);
    if (!running_.load()) return false;

    running_.store(false);

    if (stream_ != nullptr) {
        stream_->requestStop();
        stream_->close();
        stream_ = nullptr;
    }

    noteCb_ = nullptr;
    return true;
}

void AudioEngine::processAudio(const float* input, int32_t numFrames) {
    if (input == nullptr || numFrames <= 0) return;

    // --- 阶段 1：冷启动填满 2048 点窗口 ---
    const int32_t canCopy = std::min<int32_t>(numFrames, windowSize_ - windowFilled_);
    if (windowFilled_ < windowSize_) {
        if (canCopy > 0) {
            std::memcpy(window_.data() + windowFilled_, input, static_cast<size_t>(canCopy) * sizeof(float));
            windowFilled_ += canCopy;
        }
        if (windowFilled_ < windowSize_) return;
    }

    // --- 阶段 2：滑窗：左移 n，尾部接入本包最新 n 个采样（n<=512）---
    const int32_t n = std::min<int32_t>(numFrames, windowSize_);
    if (n <= 0) return;

    if (n < windowSize_) {
        std::memmove(window_.data(), window_.data() + n, static_cast<size_t>(windowSize_ - n) * sizeof(float));
        std::memcpy(window_.data() + (windowSize_ - n), input, static_cast<size_t>(n) * sizeof(float));
    } else {
        // 单次回调长度超过窗口：只保留末尾 windowSize_（异常保护）
        std::memcpy(window_.data(), input + (numFrames - windowSize_), static_cast<size_t>(windowSize_) * sizeof(float));
    }

    // --- 阶段 3：整窗送 PitchDetector；无效帧（midi<0）直接丢弃，减轻 JNI 压力 ---
    const auto result = pitchDetector_->process(window_.data(), windowSize_, sampleRate_);
    if (result.midiNote < 0) return;

    NoteCallback cb;
    {
        std::lock_guard<std::mutex> lock(cbMutex_);
        cb = noteCb_;
    }
    if (cb) {
        // PitchDetector::NoteResult 与 AudioEngine::NoteResult 结构相同但类型不同，显式拷贝
        AudioEngine::NoteResult out;
        out.midiNote = result.midiNote;
        out.volume = result.volume;
        out.confidence = result.confidence;
        out.frequencyHz = result.frequencyHz;
        cb(out);
    }
}
