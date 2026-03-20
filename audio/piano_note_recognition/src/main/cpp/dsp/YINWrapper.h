#pragma once

#include <cstdint>

/**
 * @class YINWrapper
 * @brief 时域 YIN 类基频检测（计划由 Aubio 替代；当前为可编译回退实现）
 *
 * 输出 pitchHz 与 confidence；与 [PitchDetector] 中阈值 0.85 配合决定融合优先级。
 */
class YINWrapper {
public:
    struct Result {
        float pitchHz;    ///< <=0 无效
        float confidence; ///< 0..1，由 CMND 谷值启发映射
    };

    YINWrapper();

    /**
     * @param time 时域缓冲（本工程为 2048）
     * @param numSamples 样本数
     * @param sampleRate Hz
     */
    Result detect(const float* time, int32_t numSamples, float sampleRate);
};
