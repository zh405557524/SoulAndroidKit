#include "YINWrapper.h"

#include <algorithm>
#include <cfloat>
#include <cmath>
#include <vector>

namespace {
constexpr float kEps = 1e-12f;

/** YIN 论文中的绝对阈值，越小越“挑剔”（漏检多）；越大越宽松（误检多） */
constexpr float kDefaultThreshold = 0.15f;
}

YINWrapper::YINWrapper() = default;

YINWrapper::Result YINWrapper::detect(const float* time, int32_t numSamples, float sampleRate) {
    if (time == nullptr || numSamples <= 0 || sampleRate <= 0.0f) {
        return Result{-1.0f, 0.0f};
    }

    // 延迟 tau 与频率关系：f = sampleRate / tau；限制在钢琴 A0~C8
    const float minHz = 27.5f;
    const float maxHz = 4186.0f;
    int32_t tauMin = static_cast<int32_t>(std::floor(sampleRate / maxHz));
    int32_t tauMax = static_cast<int32_t>(std::min<double>(std::floor(sampleRate / minHz), numSamples - 1));
    if (tauMin < 2) tauMin = 2;
    if (tauMax <= tauMin) return Result{-1.0f, 0.0f};

    const int32_t size = tauMax + 1;
    std::vector<float> diff(size, 0.0f);
    std::vector<float> cmnd(size, 0.0f);

    // --- 1) 差分函数 d(tau) = sum_i (x[i]-x[i+tau])^2 ---
    for (int32_t tau = tauMin; tau <= tauMax; ++tau) {
        double sum = 0.0;
        for (int32_t i = 0; i < numSamples - tau; ++i) {
            const float delta = time[i] - time[i + tau];
            sum += static_cast<double>(delta) * static_cast<double>(delta);
        }
        diff[tau] = static_cast<float>(sum);
    }

    // --- 2) 累积均值归一化差分 d'(tau) ---
    cmnd[tauMin] = 1.0f;
    double runningSum = 0.0;
    for (int32_t tau = tauMin + 1; tau <= tauMax; ++tau) {
        runningSum += static_cast<double>(diff[tau]);
        const float denom = static_cast<float>(runningSum + kEps);
        cmnd[tau] = static_cast<float>(diff[tau] * static_cast<double>(tau) / denom);
    }

    // --- 3) 绝对阈值：第一个低于阈值的 tau，并向右走到局部最小 ---
    const float threshold = kDefaultThreshold;
    int32_t tauEstimate = -1;
    for (int32_t tau = tauMin; tau <= tauMax; ++tau) {
        if (cmnd[tau] < threshold) {
            while (tau + 1 <= tauMax && cmnd[tau + 1] < cmnd[tau]) {
                ++tau;
            }
            tauEstimate = tau;
            break;
        }
    }

    // 未过阈值则取全局最小 cmnd 作为兜底（弱音/噪声场景）
    if (tauEstimate < 0) {
        float best = FLT_MAX;
        int32_t bestTau = -1;
        for (int32_t tau = tauMin; tau <= tauMax; ++tau) {
            if (cmnd[tau] < best) {
                best = cmnd[tau];
                bestTau = tau;
            }
        }
        if (bestTau < 0) return Result{-1.0f, 0.0f};
        tauEstimate = bestTau;
    }

    // --- 4) 抛物线插值细化 tau，减轻整数 bin 误差 ---
    float betterTau = static_cast<float>(tauEstimate);
    if (tauEstimate > tauMin && tauEstimate + 1 <= tauMax) {
        const float s0 = cmnd[tauEstimate - 1];
        const float s1 = cmnd[tauEstimate];
        const float s2 = cmnd[tauEstimate + 1];
        const float denom = (2.0f * s1 - s0 - s2);
        if (std::fabs(denom) > kEps) {
            betterTau = betterTau + (s2 - s0) / (2.0f * denom);
        }
    }

    const float pitchHz = sampleRate / (betterTau + kEps);

    // confidence：cmnd 越小越可信；简单线性映射到 0..1
    const float cmndVal = cmnd[tauEstimate];
    float confidence = 1.0f - std::clamp(cmndVal, 0.0f, 1.0f);
    confidence = std::clamp(confidence, 0.0f, 1.0f);

    if (pitchHz < minHz || pitchHz > maxHz) {
        return Result{-1.0f, 0.0f};
    }
    return Result{pitchHz, confidence};
}
