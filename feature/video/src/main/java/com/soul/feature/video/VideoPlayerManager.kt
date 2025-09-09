package com.soul.feature.video

import android.util.Log
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.media3.ui.PlayerView

/**
 * 视频播放器管理器（单例模式）
 * 提供给外部使用的统一接口，实现 IVideoPlayer 接口
 */
class VideoPlayerManager private constructor() : IVideoPlayer {
    
    companion object {
        private const val TAG = "VideoPlayerManager"
        
        @Volatile
        private var INSTANCE: VideoPlayerManager? = null
        
        fun getInstance(): VideoPlayerManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: VideoPlayerManager().also { INSTANCE = it }
            }
        }
    }

    private var currentFragment: VideoPlayerFragment? = null
    private var currentFragmentManager: FragmentManager? = null
    private var currentContainerId: Int? = null

    // IVideoPlayer 接口实现
    private var playbackStateListener: IVideoPlayer.OnPlaybackStateListener? = null
    private var progressListener: IVideoPlayer.OnProgressListener? = null

    /**
     * 绑定到指定容器
     * @param fragmentManager Fragment管理器
     * @param container 容器FrameLayout
     */
    fun attach(fragmentManager: FragmentManager, container: FrameLayout) {
        // 先解绑之前的
        unbind()

        // 保存引用
        currentFragmentManager = fragmentManager
        currentContainerId = container.id

                // 创建新的Fragment
        currentFragment = VideoPlayerFragment.newInstance().apply {
            // 设置内部回调，转发给外部监听器
            onPlaybackStateChanged = { isPlaying ->
                Log.d(TAG, "Fragment -> Manager: onPlaybackStateChanged($isPlaying)")
                playbackStateListener?.onPlaybackStateChanged(isPlaying)
            }
            
            onProgressChanged = { currentPosition, duration ->
                progressListener?.onProgressUpdate(currentPosition, duration)
            }
            
            onError = { error ->
                Log.e(TAG, "Fragment -> Manager: onError($error)")
                playbackStateListener?.onError(error)
            }
            
            onReady = {
                Log.d(TAG, "Fragment -> Manager: onReady()")
                playbackStateListener?.onReady()
            }
            
            onPlaybackCompleted = {
                Log.d(TAG, "Fragment -> Manager: onPlaybackCompleted()")
                playbackStateListener?.onPlaybackCompleted()
            }
        }

        // 添加Fragment到容器
        fragmentManager.beginTransaction()
            .replace(container.id, currentFragment!!)
            .commit()
    }

    /**
     * 解绑Fragment
     */
    fun unbind() {
        currentFragment = null
        currentFragmentManager = null
        currentContainerId = null
    }

    // IVideoPlayer 接口实现
    override fun initialize(videoUrl: String, playerView: PlayerView) {
        // 注意：这里的 playerView 参数在当前实现中不使用，因为我们通过Fragment内部管理
        currentFragment?.initializeVideo(videoUrl)
    }

    /**
     * 简化的初始化方法，不需要传递 PlayerView
     */
    fun initialize(videoUrl: String) {
        currentFragment?.initializeVideo(videoUrl)
    }

    override fun play() {
        currentFragment?.play()
    }

    override fun pause() {
        currentFragment?.pause()
    }

    override fun stop() {
        currentFragment?.stop()
    }

    override fun seekTo(position: Long) {
        currentFragment?.seekTo(position)
    }

    override fun getCurrentPosition(): Long {
        return currentFragment?.getCurrentPosition() ?: 0L
    }

    override fun getDuration(): Long {
        return currentFragment?.getDuration() ?: 0L
    }

    override fun isPlaying(): Boolean {
        return currentFragment?.isPlaying() ?: false
    }

    override fun release() {
        currentFragment?.stop()
        unbind()
    }

    override fun setOnPlaybackStateListener(listener: IVideoPlayer.OnPlaybackStateListener?) {
        this.playbackStateListener = listener
    }

    override fun setOnProgressListener(listener: IVideoPlayer.OnProgressListener?) {
        this.progressListener = listener
    }

    /**
     * 显示/隐藏控制栏
     */
    fun setControlsVisible(visible: Boolean) {
        currentFragment?.setControlsVisible(visible)
    }

    /**
     * 检查是否已绑定
     */
    fun isAttached(): Boolean {
        return currentFragment != null && currentFragmentManager != null
    }
} 