package com.soul.feature.video

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.media3.ui.PlayerView

/**
 * 视频播放器 ViewModel
 * 管理视频播放的逻辑和UI状态
 */
class VideoPlayerViewModel(application: Application) : AndroidViewModel(application) {
    
    private val videoPlayer: VideoPlayerImpl = VideoPlayerImpl(application)
    
    // 播放状态
    private val _isPlaying = MutableLiveData<Boolean>(false)
    val isPlaying: LiveData<Boolean> = _isPlaying
    
    // 播放进度
    private val _currentPosition = MutableLiveData<Long>(0L)
    val currentPosition: LiveData<Long> = _currentPosition
    
    // 视频总时长
    private val _duration = MutableLiveData<Long>(0L)
    val duration: LiveData<Long> = _duration
    
    // 播放准备状态
    private val _isReady = MutableLiveData<Boolean>(false)
    val isReady: LiveData<Boolean> = _isReady
    
    // 错误信息
    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> = _errorMessage
    
    // 播放完成状态
    private val _isCompleted = MutableLiveData<Boolean>(false)
    val isCompleted: LiveData<Boolean> = _isCompleted
    
    init {
        setupVideoPlayerListeners()
    }
    
    /**
     * 设置播放器监听器
     */
    private fun setupVideoPlayerListeners() {
        videoPlayer.setOnPlaybackStateListener(object : IVideoPlayer.OnPlaybackStateListener {
            override fun onPlaybackStateChanged(isPlaying: Boolean) {
                _isPlaying.postValue(isPlaying)
            }
            
            override fun onReady() {
                _isReady.postValue(true)
                _errorMessage.postValue(null)
                _isCompleted.postValue(false)
            }
            
            override fun onError(error: String) {
                _errorMessage.postValue(error)
                _isReady.postValue(false)
            }
            
            override fun onPlaybackCompleted() {
                _isCompleted.postValue(true)
                _isPlaying.postValue(false)
            }
        })
        
        videoPlayer.setOnProgressListener(object : IVideoPlayer.OnProgressListener {
            override fun onProgressUpdate(currentPosition: Long, duration: Long) {
                _currentPosition.postValue(currentPosition)
                _duration.postValue(duration)
            }
        })
    }
    
    /**
     * 初始化视频
     */
    fun initializeVideo(videoUrl: String,view: PlayerView) {
        videoPlayer.initialize(videoUrl,view)
    }

    
    /**
     * 播放/暂停切换
     */
    fun togglePlayPause() {
        if (videoPlayer.isPlaying()) {
            videoPlayer.pause()
        } else {
            videoPlayer.play()
        }
    }
    
    /**
     * 播放
     */
    fun play() {
        videoPlayer.play()
    }
    
    /**
     * 暂停
     */
    fun pause() {
        videoPlayer.pause()
    }
    
    /**
     * 停止
     */
    fun stop() {
        videoPlayer.stop()
    }
    
    /**
     * 跳转到指定位置
     */
    fun seekTo(position: Long) {
        videoPlayer.seekTo(position)
    }
    
    /**
     * 跳转到指定进度（百分比）
     */
    fun seekToProgress(progress: Float) {
        val duration = _duration.value ?: 0L
        if (duration > 0) {
            val position = (duration * progress).toLong()
            seekTo(position)
        }
    }
    
    /**
     * 获取当前播放进度（百分比）
     */
    fun getCurrentProgress(): Float {
        val current = _currentPosition.value ?: 0L
        val total = _duration.value ?: 0L
        return if (total > 0) current.toFloat() / total.toFloat() else 0f
    }
    
    /**
     * 格式化时间显示
     */
    fun formatTime(timeMs: Long): String {
        val totalSeconds = timeMs / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        
        return if (hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%d:%02d", minutes, seconds)
        }
    }
    
    /**
     * 清除错误信息
     */
    fun clearError() {
        _errorMessage.value = null
    }
    
    override fun onCleared() {
        super.onCleared()
        videoPlayer.release()
    }
} 