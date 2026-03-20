#pragma once

#include <cstdint>

#include "dsp/FFTWrapper.h"
#include "dsp/HPS.h"
#include "dsp/YINWrapper.h"

/**
 * @class PitchDetector
 * @brief FFT 频谱 + HPS 谐波积谱 + YIN 时域基频，按规则融合后输出 MIDI 与元数据
 *
 * 融合策略（与计划一致）：
 * - YIN 可信度 > 0.85 且频率有效 -> 采用 YIN 基频
 * - 否则若 HPS 有足够置信度 -> 采用 HPS 基频
 * - 否则本帧视为无效（midi=-1），由 [AudioEngine] 决定是否回调
 */
class PitchDetector {
public:
    struct NoteResult {
        int32_t midiNote;     ///< 21..108；-1 无效
        float volume;         ///< 0..1，来自 FFT 侧 RMS 映射
        float confidence;     ///< 0..1，融合路径的可信度
        float frequencyHz;    ///< 选用算法的基频；midi<0 时可能仍带频率（越界裁剪场景）
    };

    PitchDetector();

    /**
     * 对固定长度窗口做一帧识别。
     * @param window 时域样本，长度至少 [FFTWrapper::kFftSize]（2048）
     * @param windowSize 实际长度，应 >= 2048
     * @param sampleRate 采样率 Hz，须与 Oboe 一致（当前 48000）
     */
    NoteResult process(const float* window, int32_t windowSize, float sampleRate);

private:
    FFTWrapper fft_;
    HPS hps_;
    YINWrapper yin_;
};
