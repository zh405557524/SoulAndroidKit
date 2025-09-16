@file:Suppress("unused")

package com.soul.utils

import android.net.Uri
import android.util.Log
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFmpegKitConfig
import com.arthenica.ffmpegkit.ReturnCode
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * 统一的 FFmpeg 裁剪工具（基于 FFmpegKit）
 *
 * 功能：
 * - 无损快剪：速度快（-c copy），但可能受关键帧影响不够精确
 * - 精确重编码：更准（去掉 -c copy，使用编码器），代价是耗时更久
 * - 进度回调：通过 FFmpeg 统计回调推算（time / 预估时长）
 * - 取消 & 超时：支持外部取消，或传入 timeoutMs
 * - HLS(M3U8) 支持：自动加入常用协议白名单与缓冲参数
 */
object FfmpegCutter {

    private const val TAG = "FfmpegCutter"

    interface Listener {
        /** 0f..1f */
        fun onProgress(progress: Float) {}
        fun onLog(line: String) {}
        fun onCompleted(output: String) {}
        fun onError(throwable: Throwable) {}
        fun onCanceled() {}
    }

    data class Options(
        /** 输入地址（本地/网络均可；HLS 可直接传 .m3u8） */
        val input: String,
        /** 输出文件（建议 mp4 或 ts，根据你的需求） */
        val output: String,
        /** 起始毫秒（>=0） */
        val startMs: Long = 0L,
        /** 裁剪时长毫秒（>0）。若 =0 代表到结尾 */
        val durationMs: Long = 0L,
        /** 是否精确裁剪（重编码） */
        val reencode: Boolean = false,
        /** 精确裁剪的视频编码器（reencode=true 生效） */
        val videoCodec: String = "libx264",
        /** 精确裁剪的音频编码器（reencode=true 生效） */
        val audioCodec: String = "aac",
        /** 输出容器额外参数（例如 faststart） */
        val extraMuxFlags: String = "+faststart",
        /** 任务超时（毫秒），<=0 表示不限制 */
        val timeoutMs: Long = 0L,
        /** 预计总时长（毫秒），用于更稳的进度估算；不传则用 durationMs */
        val expectedTotalMs: Long? = null,
        /** 覆盖输出 */
        val overwrite: Boolean = true
    )

    /**
     * 核心裁剪（协程版）
     * - 可被外部 cancel
     * - 建议放在 IO 线程调用
     */
    suspend fun cut(
        options: Options,
        listener: Listener? = null
    ): String = suspendCancellableCoroutine { cont ->
        try {
            ensureOutputDir(options.output)

            // 配置进度回调
            val totalMs = (options.expectedTotalMs ?: options.durationMs).coerceAtLeast(1L)
            val cmd = buildCommand(options)
            listener?.onLog("FFmpeg cmd: $cmd")
            Log.d(TAG, "execute: $cmd")

            val session = FFmpegKit.executeAsync(
                cmd,
                { completedSession ->
                    FFmpegKitConfig.enableStatisticsCallback(null)
                    when {
                        ReturnCode.isSuccess(completedSession.returnCode) -> {
                            listener?.onCompleted(options.output)
                            if (cont.isActive) cont.resume(options.output)
                        }

                        ReturnCode.isCancel(completedSession.returnCode) -> {
                            listener?.onCanceled()
                            if (cont.isActive) cont.resumeWithException(CancellationException("FFmpeg canceled"))
                        }

                        else -> {
                            val fail = IllegalStateException(
                                "FFmpeg failed: rc=${completedSession.returnCode}. logs=${completedSession.allLogsAsString}"
                            )
                            listener?.onError(fail)
                            if (cont.isActive) cont.resumeWithException(fail)
                        }
                    }
                },
                { log ->
                    listener?.onLog(log.message)
                    // 可选：从 log 解析 "time=00:00:xx.xxx" 提升进度精度
                }, { stats ->
                    // 如果要手动解析进度，也可以用 stats.getTime()
                    val t = stats.time.toLong().coerceAtLeast(0L)
                    val p = (t / totalMs.toFloat()).coerceIn(0f, 1f)
                    listener?.onProgress(p)
                }
            )

            // 处理外部取消
            cont.invokeOnCancellation {
                try {
                    session.cancel()
                } catch (e: Throwable) {
                    Log.w(TAG, "cancel error: $e")
                }
            }

            // 超时
            if (options.timeoutMs > 0) {
                // 简易超时：开个线程延时后 cancel
                Thread {
                    try {
                        Thread.sleep(options.timeoutMs)
                        if (cont.isActive) {
                            session.cancel()
                        }
                    } catch (_: InterruptedException) {
                    }
                }.start()
            }
        } catch (e: Throwable) {
            FFmpegKitConfig.enableStatisticsCallback(null)
            listener?.onError(e)
            if (cont.isActive) cont.resumeWithException(e)
        }
    }

    // --- 构建命令 ---

    private fun buildCommand(opt: Options): String {
        val isHls = isHls(opt.input)

        val commonHead = buildList {
            add("-hide_banner")
            if (opt.overwrite) add("-y")
            // HLS 常见：允许网络协议
            if (isHls) {
                addAll(
                    listOf(
                        "-protocol_whitelist", "file,http,https,tcp,tls,crypto"
                    )
                )
                // 降低网络波动影响（可按需调整）
                addAll(listOf("-rw_timeout", "15000000")) // 15s
            }
        }

        val timeArgsCopy = buildList {
            // 无损快剪：-ss 放在 -i 前 更快，但不够精确
            if (opt.startMs > 0) addAll(listOf("-ss", msToTime(opt.startMs)))
            addAll(listOf("-i", quote(opt.input)))
            if (opt.durationMs > 0) addAll(listOf("-t", msToTime(opt.durationMs)))
            addAll(listOf("-c", "copy"))
            // 处理负时间戳，避免音画对不齐
            addAll(listOf("-avoid_negative_ts", "make_zero"))
            // HLS → 输出 MP4 有时需要 -fflags +genpts（视源具体表现决定）
            // addAll(listOf("-fflags", "+genpts"))
        }

        val timeArgsReencode = buildList {
            // 精确裁剪：-ss 放在 -i 后更准（也可前后都放，前快后准）
            addAll(listOf("-ss", msToTime(opt.startMs.coerceAtLeast(0))))
            addAll(listOf("-i", quote(opt.input)))
            if (opt.durationMs > 0) addAll(listOf("-t", msToTime(opt.durationMs)))
            addAll(listOf("-c:v", opt.videoCodec))
            addAll(listOf("-c:a", opt.audioCodec))
            // 码率/预设可按需配置（示例保守值）
            addAll(listOf("-preset", "veryfast"))
            // 容器优化
            if (opt.extraMuxFlags.isNotBlank()) {
                addAll(listOf("-movflags", opt.extraMuxFlags))
            }
        }

        val args = if (opt.reencode) timeArgsReencode else timeArgsCopy

        val out = quote(opt.output)
        return (commonHead + args + out).joinToString(" ")
    }

    // --- 工具方法 ---

    private fun isHls(path: String): Boolean {
        val s = path.lowercase()
        return s.endsWith(".m3u8") || s.contains("m3u8?")
    }

    private fun msToTime(ms: Long): String {
        val totalSec = ms / 1000
        val h = totalSec / 3600
        val m = (totalSec % 3600) / 60
        val s = (totalSec % 60)
        val msRemain = (ms % 1000)
        return String.format("%02d:%02d:%02d.%03d", h, m, s, msRemain)
    }

    private fun quote(p: String): String = if (needsQuote(p)) "\"$p\"" else p

    private fun needsQuote(p: String): Boolean = p.any { it.isWhitespace() }

    private fun ensureOutputDir(output: String) {
        runCatching {
            val dir = File(Uri.parse(output).path ?: output).parentFile ?: return
            if (!dir.exists()) dir.mkdirs()
        }
    }
}
