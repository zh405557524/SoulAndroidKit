#include "FFTWrapper.h"

#include <algorithm>
#include <cmath>
#include <cstring>

namespace {
constexpr float kPi = 3.14159265358979323846f;
}

FFTWrapper::FFTWrapper() {
    // Hann 窗：减轻频谱泄漏，便于 HPS 看谐波
    for (int32_t n = 0; n < kFftSize; ++n) {
        window_[n] = 0.5f - 0.5f * std::cos(2.0f * kPi * n / static_cast<float>(kFftSize - 1));
    }
    std::fill_n(magnitudes_, kNumBins, 0.0f);
}

void FFTWrapper::applyWindow(const float* time) {
    // 音量：时域 RMS（未加窗），反映整体响度
    double sumSq = 0.0;
    for (int32_t i = 0; i < kFftSize; ++i) {
        const float v = time[i];
        sumSq += static_cast<double>(v) * static_cast<double>(v);
        const float wv = v * window_[i];
        buf_[i].re = wv;
        buf_[i].im = 0.0f;
    }
    rmsCache_ = static_cast<float>(std::sqrt(sumSq / static_cast<double>(kFftSize)));
}

void FFTWrapper::fftRadix2Iterative() {
    const int32_t n = kFftSize;

    // 位反序置换：原地重排，为迭代 FFT 做准备
    int32_t j = 0;
    for (int32_t i = 1; i < n; ++i) {
        int32_t bit = n >> 1;
        while (j & bit) {
            j ^= bit;
            bit >>= 1;
        }
        j ^= bit;
        if (i < j) {
            std::swap(buf_[i], buf_[j]);
        }
    }

    // 逐级合并，len = 2,4,...,N
    for (int32_t len = 2; len <= n; len <<= 1) {
        const float ang = -2.0f * kPi / static_cast<float>(len);
        const float wlenRe = std::cos(ang);
        const float wlenIm = std::sin(ang);

        for (int32_t i = 0; i < n; i += len) {
            float wRe = 1.0f;
            float wIm = 0.0f;
            const int32_t half = len >> 1;
            for (int32_t j2 = 0; j2 < half; ++j2) {
                const int32_t u = i + j2;
                const int32_t v = i + j2 + half;

                const float tRe = buf_[v].re * wRe - buf_[v].im * wIm;
                const float tIm = buf_[v].re * wIm + buf_[v].im * wRe;

                const float uRe = buf_[u].re;
                const float uIm = buf_[u].im;

                buf_[u].re = uRe + tRe;
                buf_[u].im = uIm + tIm;
                buf_[v].re = uRe - tRe;
                buf_[v].im = uIm - tIm;

                const float nextWRe = wRe * wlenRe - wIm * wlenIm;
                const float nextWIm = wRe * wlenIm + wIm * wlenRe;
                wRe = nextWRe;
                wIm = nextWIm;
            }
        }
    }

    // 正频率 bin 幅度 |X[k]|
    for (int32_t k = 0; k < kNumBins; ++k) {
        const float re = buf_[k].re;
        const float im = buf_[k].im;
        magnitudes_[k] = std::sqrt(re * re + im * im);
    }
}

FFTWrapper::Spectrum FFTWrapper::analyze(const float* time) {
    applyWindow(time);
    fftRadix2Iterative();

    // 将 RMS 粗略映射到 0..1（浮点 PCM 常见幅度下可调系数）
    float volume = rmsCache_;
    volume = std::clamp(volume * 2.0f, 0.0f, 1.0f);
    rmsCache_ = volume;

    return Spectrum{magnitudes_, kNumBins, rmsCache_};
}
