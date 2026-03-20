#include "PitchDetector.h"

#include <cmath>
#include <algorithm>

namespace {
constexpr float kA4 = 440.0f;
constexpr int32_t kMidiMin = 21;
constexpr int32_t kMidiMax = 108;

/**
 * 由基频 Hz 换算 MIDI 音符号（四舍五入），并限制在钢琴范围 [kMidiMin, kMidiMax]。
 * 公式：midi = 69 + 12 * log2(f / 440)，其中 69 对应 A4。
 */
int32_t freqToMidi(float freqHz) {
    if (freqHz <= 0.0f) return -1;
    const float midiFloat = 69.0f + 12.0f * std::log2(freqHz / kA4);
    int32_t midi = static_cast<int32_t>(std::lround(midiFloat));
    if (midi < kMidiMin || midi > kMidiMax) return -1;
    return midi;
}
}

PitchDetector::PitchDetector() = default;

PitchDetector::NoteResult PitchDetector::process(const float* window, int32_t windowSize, float sampleRate) {
    if (window == nullptr || windowSize < FFTWrapper::kFftSize || sampleRate <= 0.0f) {
        return NoteResult{-1, -1.0f, 0.0f, -1.0f};
    }

    // 1) FFT：得到各 bin 幅度 + 时域 RMS（映射为 volume）
    const auto spectrum = fft_.analyze(window);

    // 2) HPS：在钢琴基频范围内搜索谐波积最大的 bin -> 候选基频
    constexpr int32_t kMaxHarmonics = 5;
    constexpr float minHz = 27.5f;
    constexpr float maxHz = 4186.0f;
    const auto hpsRes = hps_.detect(spectrum.magnitudes, spectrum.numBins, sampleRate,
                                      kMaxHarmonics, minHz, maxHz);

    // 3) YIN：时域自相关类方法，输出 pitch + 启发式 confidence
    const auto yinRes = yin_.detect(window, windowSize, sampleRate);

    // 4) 融合：高置信 YIN 优先，否则回退 HPS
    float chosenFreq = -1.0f;
    float chosenConfidence = 0.0f;
    if (yinRes.confidence > 0.85f && yinRes.pitchHz > 0.0f) {
        chosenFreq = yinRes.pitchHz;
        chosenConfidence = yinRes.confidence;
    } else if (hpsRes.confidence > 0.05f && hpsRes.frequencyHz > 0.0f) {
        chosenFreq = hpsRes.frequencyHz;
        chosenConfidence = hpsRes.confidence;
    }

    if (chosenFreq <= 0.0f) {
        return NoteResult{-1, spectrum.rms, 0.0f, -1.0f};
    }

    const int32_t midi = freqToMidi(chosenFreq);
    if (midi < 0) {
        return NoteResult{-1, spectrum.rms, 0.0f, chosenFreq};
    }

    const float confidence = std::clamp(chosenConfidence, 0.0f, 1.0f);
    return NoteResult{midi, std::clamp(spectrum.rms, 0.0f, 1.0f), confidence, chosenFreq};
}
