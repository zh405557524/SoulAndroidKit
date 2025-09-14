package com.soul.ffmpegkit

/**
 * FFmpeg 工具类
 * 提供基于 FFmpeg 的基本功能
 */
class FFmpegPlayer {
    
    companion object {
        // 加载本地库
        init {
            System.loadLibrary("ffmpeg_player")
        }
    }
    
    /**
     * 获取FFmpeg版本信息
     * @return FFmpeg版本字符串
     */
    external fun getFFmpegVersion(): String
    
    /**
     * 获取FFmpeg配置信息
     * @return FFmpeg配置字符串
     */
    external fun getFFmpegConfiguration(): String
    
    /**
     * 获取支持的格式信息
     * @return 支持的格式字符串
     */
    external fun getSupportedFormats(): String
}
