#pragma once

#include <cstdint>

/**
 * @class HPS
 * @brief 谐波积谱（Harmonic Product Spectrum）：用 FFT 幅度谱估计基频
 *
 * 思路：对每个候选基频 bin k，把 |X[k]|*|X[2k]|*|X[3k]|*... 相乘（归一化后），
 * 得分最高的 k 对应基频 f = k * (sampleRate / fftSize)。
 */
class HPS {
public:
    struct Result {
        float frequencyHz; ///< <=0 表示无效
        float confidence;  ///< 0..1，来自归一化乘积的启发值
    };

    /**
     * @param magnitudes FFT 幅度，下标 k 对应频率 k * sampleRate / fftSize
     * @param numBins 幅度长度（通常为 N/2）
     * @param sampleRate 采样率
     * @param maxHarmonics 参与乘积的最大谐波次数（含基频为 1）
     * @param minHz/maxHz 钢琴基频搜索范围
     */
    Result detect(const float* magnitudes, int32_t numBins, float sampleRate,
                   int32_t maxHarmonics, float minHz, float maxHz);
};
