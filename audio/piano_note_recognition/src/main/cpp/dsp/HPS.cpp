#include "HPS.h"

#include <algorithm>
#include <cmath>
#include <cfloat>

namespace {
constexpr float kEps = 1e-12f;
}

HPS::Result HPS::detect(const float* magnitudes, int32_t numBins, float sampleRate,
                          int32_t maxHarmonics, float minHz, float maxHz) {
    if (magnitudes == nullptr || numBins <= 1 || sampleRate <= 0.0f) {
        return Result{-1.0f, 0.0f};
    }
    if (maxHarmonics < 2) maxHarmonics = 2;

    // 频率分辨率须与 FFT 点数一致：本工程固定 2048 点
    const float freqRes = sampleRate / 2048.0f;

    // 将 Hz 范围映射到 bin 索引范围 [kMin, kMax]
    const int32_t kMin = std::max<int32_t>(1, static_cast<int32_t>(std::floor(minHz / freqRes)));
    const int32_t kMax = std::min<int32_t>(numBins - 1,
                                           static_cast<int32_t>(std::ceil(maxHz / freqRes)));
    if (kMax <= kMin) return Result{-1.0f, 0.0f};

    // 归一化：避免多个小于 1 的数连乘下溢为 0
    float maxMag = 0.0f;
    for (int32_t k = kMin; k <= kMax && k < numBins; ++k) {
        maxMag = std::max(maxMag, magnitudes[k]);
    }
    const float invMax = 1.0f / (maxMag + kEps);

    float bestScore = -1.0f;
    int32_t bestK = -1;

    for (int32_t k = kMin; k <= kMax; ++k) {
        float score = 1.0f;
        for (int32_t h = 1; h <= maxHarmonics; ++h) {
            const int32_t kh = k * h;
            if (kh >= numBins) {
                score = 0.0f;
                break;
            }
            score *= (magnitudes[kh] * invMax);
        }
        if (score > bestScore) {
            bestScore = score;
            bestK = k;
        }
    }

    if (bestK < 0) return Result{-1.0f, 0.0f};

    const float frequencyHz = static_cast<float>(bestK) * freqRes;
    const float confidence = std::clamp(bestScore, 0.0f, 1.0f);
    return Result{frequencyHz, confidence};
}
