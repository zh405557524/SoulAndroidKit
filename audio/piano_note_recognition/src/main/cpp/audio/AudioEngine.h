#pragma once

#include <atomic>
#include <cstdint>
#include <functional>
#include <mutex>
#include <vector>

namespace oboe {
class AudioStreamCallback;
class AudioStream;
}

class PitchDetector;

/**
 * @class AudioEngine
 * @brief Oboe 采集 + 滑窗缓冲 + 调用 [PitchDetector] 的一帧结果回调
 *
 * 数据流（与计划一致）：
 * Oboe 每次回调提供 hop（512）帧 float PCM
 *   -> 维护长度 2048 的滑动窗口
 *   -> [PitchDetector::process] 得到 MIDI / 音量 / 可信度 / 频率
 *   -> 通过 [NoteCallback] 交给 JNI 层转发 Kotlin
 *
 * 线程：
 * - [start]/[stop] 由 JNI 线程调用，内部用 [cbMutex_] 与 [running_] 协调
 * - [processAudio] 在 Oboe 音频回调线程执行，需避免长时间阻塞
 */
class AudioEngine {
public:
    /** 一帧有效识别结果（与 JNI 回调字段对应） */
    struct NoteResult {
        int32_t midiNote;     ///< 钢琴范围 21..108；-1 表示无效/静音帧
        float volume;         ///< 音量 0..1（由 FFT 侧 RMS 映射）
        float confidence;     ///< 融合后可信度 0..1
        float frequencyHz;    ///< 估计基频 Hz
    };

    using NoteCallback = std::function<void(const NoteResult&)>;

    AudioEngine();
    ~AudioEngine();

    /**
     * 打开 Oboe 输入流并 requestStart。
     * @param cb 每帧（窗口更新后）若 midi>=0 则调用
     * @return 流打开且启动成功为 true
     */
    bool start(NoteCallback cb);

    /** 停止流、清空回调；可重复调用（第二次返回 false） */
    bool stop();

    bool isRunning() const { return running_.load(); }

private:
    /**
     * Oboe 回调入口：累积/滑动窗口后做一次检测。
     */
    void processAudio(const float* input, int32_t numFrames);

    std::atomic<bool> running_{false};
    std::mutex cbMutex_;
    NoteCallback noteCb_;

    /** 算法参数：与 PitchDetector / FFTWrapper 的 2048 点一致 */
    static constexpr int32_t sampleRate_ = 48000;
    static constexpr int32_t windowSize_ = 2048;
    static constexpr int32_t hopSize_ = 512;
    std::vector<float> window_;
    /** 启动后前几包用于填满 window_ */
    int32_t windowFilled_{0};

    class CallbackImpl;
    CallbackImpl* callback_{nullptr};
    oboe::AudioStream* stream_{nullptr};

    PitchDetector* pitchDetector_{nullptr};
};
