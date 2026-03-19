package com.soul.feature.video

import androidx.media3.ui.PlayerView

/**
 * 视频播放器接口
 * 定义视频播放的核心功能
 */
interface IVideoPlayer {
    
    /**
     * 初始化播放器
     * @param videoUrl 视频地址
     */
    fun initialize(videoUrl: String,playerView: PlayerView)
    
    /**
     * 播放视频
     */
    fun play()
    
    /**
     * 暂停播放
     */
    fun pause()
    
    /**
     * 停止播放
     */
    fun stop()
    
    /**
     * 设置播放进度
     * @param position 播放位置（毫秒）
     */
    fun seekTo(position: Long)
    
    /**
     * 获取当前播放位置
     * @return 当前位置（毫秒）
     */
    fun getCurrentPosition(): Long
    
    /**
     * 获取视频总时长
     * @return 总时长（毫秒）
     */
    fun getDuration(): Long
    
    /**
     * 是否正在播放
     * @return true表示正在播放
     */
    fun isPlaying(): Boolean
    
    /**
     * 释放播放器资源
     */
    fun release()
    
    /**
     * 设置播放状态监听器
     */
    fun setOnPlaybackStateListener(listener: OnPlaybackStateListener?)
    
    /**
     * 设置播放进度监听器
     */
    fun setOnProgressListener(listener: OnProgressListener?)
    
    /**
     * 播放状态监听接口
     */
    interface OnPlaybackStateListener {
        /**
         * 播放状态改变
         * @param isPlaying 是否正在播放
         */
        fun onPlaybackStateChanged(isPlaying: Boolean)
        
        /**
         * 播放准备完成
         */
        fun onReady()
        
        /**
         * 播放错误
         * @param error 错误信息
         */
        fun onError(error: String)
        
        /**
         * 播放结束
         */
        fun onPlaybackCompleted()
    }
    
    /**
     * 播放进度监听接口
     */
    interface OnProgressListener {
        /**
         * 播放进度更新
         * @param currentPosition 当前位置（毫秒）
         * @param duration 总时长（毫秒）
         */
        fun onProgressUpdate(currentPosition: Long, duration: Long)
    }
} 