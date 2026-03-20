#pragma once

/**
 * @file pffft.h
 * @brief PFFFT 头文件占位：请在你完成交叉编译后替换为官方/实际导出的头文件。
 *
 * 接入步骤（概要）：
 * - 将 libpffft.a 放到 cpp/lib/${ANDROID_ABI}/
 * - 在 [FFTWrapper] 中用 pffft_new_setup / pffft_transform_ordered 等替换当前 Radix-2 回退实现
 * - CMake 在检测到 libpffft.a 存在时会自动链接 imported target `pffft`
 */

#ifdef __cplusplus
extern "C" {
#endif

typedef struct pffft_plan_t pffft_plan;

/** 创建 n 点 FFT 计划（flags 依 PFFFT 文档） */
pffft_plan* pffft_new_setup(int n, int flags);
void pffft_destroy_setup(pffft_plan* plan);
/** direction 等参数以实际 PFFFT API 为准，此处仅为占位签名 */
void pffft_transform_ordered(pffft_plan* plan, const float* in, float* out,
                            float* tmp, int direction);

#ifdef __cplusplus
}
#endif
