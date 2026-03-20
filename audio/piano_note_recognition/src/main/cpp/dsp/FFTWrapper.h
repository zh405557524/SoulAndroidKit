#pragma once

#include <cstdint>

/**
 * @class FFTWrapper
 * @brief 对固定 2048 点做加窗 FFT，输出幅度谱与各 bin，并估计音量（RMS）
 *
 * 说明：
 * - 计划接入 PFFFT 时，可在此类内替换实现，对外仍暴露 [analyze] 与 [Spectrum]。
 * - 当前为 Radix-2 迭代 FFT 回退实现，保证无第三方库亦可编译运行。
 */
class FFTWrapper {
public:
    static constexpr int32_t kFftSize = 2048;
    static constexpr int32_t kNumBins = kFftSize / 2;

    /** FFT 输出：仅正频率 bin（不含 Nyquist 的单独处理，长度为 N/2） */
    struct Spectrum {
        const float* magnitudes; ///< 长度 [numBins]
        int32_t numBins;
        float rms;               ///< 映射到 0..1 的“音量”启发值
    };

    FFTWrapper();

    /**
     * @param time 至少 [kFftSize] 个连续采样
     * @return 幅度谱指针指向内部缓冲，仅在下次 [analyze] 前有效
     */
    Spectrum analyze(const float* time);

private:
    /** Hann 窗 + 填充实部/虚部为 0，同时计算未加窗 RMS（用于音量） */
    void applyWindow(const float* time);
    /** 原地 Cooley-Tukey，结果写入 [buf_]，再求前一半 bin 幅度 */
    void fftRadix2Iterative();

    float window_[kFftSize];
    float rmsCache_{0.0f};
    float magnitudes_[kNumBins];

    struct Complex {
        float re;
        float im;
    };
    Complex buf_[kFftSize];
};
