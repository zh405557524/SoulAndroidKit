package com.soul.ffmpeg_kit

import android.view.Surface

/**
 * FFmpeg 视频播放器
 * 提供基于 FFmpeg 的视频播放功能
 */
class FFmpegPlayer {
    
    companion object {
        // 加载本地库
        init {
            System.loadLibrary("ffmpeg_player")
        }
    }
    
    /**
     * 播放视频
     * @param videoPath 视频文件路径
     * @param surface 渲染表面
     */
    external fun playVideo(videoPath: String, surface: Surface)
    
    /**
     * 停止播放
     */
    external fun stopPlay()
    
    /**
     * 获取视频时长（毫秒）
     * @return 视频时长
     */
    external fun getDuration(): Long
    
    /**
     * 获取当前播放位置（毫秒）
     * @return 当前播放位置
     */
    external fun getCurrentPosition(): Long
    
    /**
     * 设置播放位置
     * @param position 播放位置（毫秒）
     */
    external fun seekTo(position: Long)
    
    /**
     * 检查是否正在播放
     * @return true 如果正在播放，否则 false
     */
    external fun isPlaying(): Boolean
}
