package com.soul.feature.video

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.media3.ui.PlayerView

/**
 * 播放配置类
 * @param videoUrl 视频地址
 * @param autoPlay 是否自动播放，默认false
 * @param looping 是否循环播放，默认false
 * @param muted 是否静音，默认false
 */
data class PlayConfig(
    val videoUrl: String,
    val autoPlay: Boolean = false,
    val looping: Boolean = false,
    val muted: Boolean = false
)

/**
 * 视频播放器管理器（单例模式）
 * 只管理播放逻辑，不管理UI布局
 * 外部需传入PlayerView，可选择传入lifecycleOwner进行生命周期管理
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

    // 核心播放器实现
    private var videoPlayerImpl: VideoPlayerImpl? = null
    private var context: Context? = null
    private var playerView: PlayerView? = null

    // 状态管理
    private var currentPlayConfig: PlayConfig? = null
    private var lifecycleOwner: LifecycleOwner? = null

    // 外部监听器
    private var playbackStateListener: IVideoPlayer.OnPlaybackStateListener? = null
    private var progressListener: IVideoPlayer.OnProgressListener? = null

    // 生命周期观察者
    private val lifecycleObserver = LifecycleEventObserver { _, event ->
        when (event) {
            Lifecycle.Event.ON_PAUSE -> {
                Log.d(TAG, "生命周期: ON_PAUSE - 暂停播放")
                pause()
            }

            Lifecycle.Event.ON_DESTROY -> {
                Log.d(TAG, "生命周期: ON_DESTROY - 释放资源")
                release()
            }

            else -> {}
        }
    }

    /**
     * 初始化播放器
     * @param context 上下文
     * @param playerView 播放器视图（外部传入）
     * @param lifecycleOwner 生命周期所有者（可选）
     * @param playConfig 播放配置（可选）
     */
    fun initialize(
        context: Context,
        playerView: PlayerView,
        lifecycleOwner: LifecycleOwner? = null,
        playConfig: PlayConfig? = null
    ) {
        Log.d(TAG, "初始化播放器，配置: ")

        // 先释放之前的资源
        release()

        // 保存引用
        this.context = context
        this.playerView = playerView
        this.lifecycleOwner = lifecycleOwner
        this.currentPlayConfig = playConfig

        // 注册生命周期观察者（如果有）
        lifecycleOwner?.lifecycle?.addObserver(lifecycleObserver)

        // 初始化播放器实现
        videoPlayerImpl = VideoPlayerImpl(context)
        setupVideoPlayerListeners()
        // 应用播放配置
        playConfig?.let { config ->
            applyPlayConfig(config)
        }
    }

    /**
     * 应用播放配置
     */
    private fun applyPlayConfig(config: PlayConfig) {
        Log.d(TAG, "应用播放配置: ")

        // 初始化视频
        playerView?.let { pv ->
            videoPlayerImpl?.initialize(config.videoUrl, pv)
        }

        // 自动播放
        if (config.autoPlay) {
            Handler(Looper.getMainLooper()).postDelayed({
                play()
                Log.d(TAG, "自动播放已启动")
            }, 500)
        }
    }

    /**
     * 设置播放器监听器
     */
    private fun setupVideoPlayerListeners() {
        videoPlayerImpl!!.setOnPlaybackStateListener(object : IVideoPlayer.OnPlaybackStateListener {
            override fun onPlaybackStateChanged(isPlaying: Boolean) {
                playbackStateListener?.onPlaybackStateChanged(isPlaying)
            }

            override fun onReady() {
                playbackStateListener?.onReady()
            }

            override fun onError(error: String) {
                playbackStateListener?.onError(error)
            }

            override fun onPlaybackCompleted() {
                // 处理循环播放
                currentPlayConfig?.let { config ->
                    if (config.looping) {
                        Log.d(TAG, "循环播放：重新开始播放")
                        Handler(Looper.getMainLooper()).postDelayed({
                            seekTo(0)
                            play()
                        }, 100)
                    }
                }

                playbackStateListener?.onPlaybackCompleted()
            }
        })

        videoPlayerImpl?.setOnProgressListener(object : IVideoPlayer.OnProgressListener {
            override fun onProgressUpdate(currentPosition: Long, duration: Long) {
                progressListener?.onProgressUpdate(currentPosition, duration)
            }
        })
    }

    // ==================== IVideoPlayer 接口实现 ====================

    @Deprecated("不提供使用")
    override fun initialize(videoUrl: String, playerView: PlayerView) {
        throw RuntimeException("请勿使用该方法")
    }

    override fun play() {
        videoPlayerImpl?.play()
    }

    override fun pause() {
        videoPlayerImpl?.pause()
    }

    override fun stop() {
        videoPlayerImpl?.stop()
    }

    override fun seekTo(position: Long) {
        videoPlayerImpl?.seekTo(position)
    }

    override fun getCurrentPosition(): Long {
        return videoPlayerImpl?.getCurrentPosition() ?: 0L
    }

    override fun getDuration(): Long {
        return videoPlayerImpl?.getDuration() ?: 0L
    }

    override fun isPlaying(): Boolean {
        return videoPlayerImpl?.isPlaying() ?: false
    }

    override fun release() {
        Log.d(TAG, "释放播放器资源")

        // 移除生命周期观察者
        lifecycleOwner?.lifecycle?.removeObserver(lifecycleObserver)

        // 释放播放器资源
        videoPlayerImpl?.release()
        videoPlayerImpl = null

        // 清空引用
        playerView = null
        context = null
        lifecycleOwner = null
        currentPlayConfig = null
    }

    override fun setOnPlaybackStateListener(listener: IVideoPlayer.OnPlaybackStateListener?) {
        this.playbackStateListener = listener
    }

    override fun setOnProgressListener(listener: IVideoPlayer.OnProgressListener?) {
        this.progressListener = listener
    }

    // ==================== 其他方法 ====================

    /**
     * 播放/暂停切换
     */
    fun togglePlayPause() {
        if (isPlaying()) {
            pause()
        } else {
            play()
        }
    }

    /**
     * 获取当前播放配置
     */
    fun getCurrentPlayConfig(): PlayConfig? = currentPlayConfig

    /**
     * 更新播放配置
     */
    fun updatePlayConfig(config: PlayConfig) {
        currentPlayConfig = config
        applyPlayConfig(config)
    }

    /**
     * 检查是否已初始化
     */
    fun isInitialized(): Boolean {
        return videoPlayerImpl != null && playerView != null
    }

    /**
     * 获取当前PlayerView
     */
    fun getPlayerView(): PlayerView? = playerView
}
