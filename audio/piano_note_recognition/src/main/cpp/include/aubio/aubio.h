#pragma once

/**
 * @file aubio.h
 * @brief Aubio 头文件占位：请在你完成交叉编译后替换为真实 Aubio 头文件集合。
 *
 * 接入步骤（概要）：
 * - 将 libaubio.a 放到 cpp/lib/${ANDROID_ABI}/
 * - 在 [YINWrapper] 中用 new_aubio_pitch / aubio_pitch_do（及 fvec 等）替换当前 YIN 回退实现
 * - CMake 在检测到 libaubio.a 存在时会自动链接 imported target `aubio`
 *
 * 注意：真实 Aubio 的函数名、结构体与头文件路径可能与此占位不同，以你编译产物为准。
 */

#ifdef __cplusplus
extern "C" {
#endif

typedef struct _aubio_pitch_t aubio_pitch_t;
typedef struct _fvec_t fvec_t;

aubio_pitch_t* new_aubio_pitch(const char* mode, unsigned int buf_size,
                               unsigned int hop_size, unsigned int samplerate);
void del_aubio_pitch(aubio_pitch_t* p);

/** 占位：真实 API 可能返回 void 或使用不同参数 */
int aubio_pitch_do(aubio_pitch_t* p, const fvec_t* input, fvec_t* out);

#ifdef __cplusplus
}
#endif
