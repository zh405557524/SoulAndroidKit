package com.soul.feature.video

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.ui.PlayerView

/**
 * 视频播放器实现类
 * 使用 ExoPlayer 实现视频播放功能
 */
@OptIn(UnstableApi::class)
class VideoPlayerImpl(private val context: Context) : IVideoPlayer {
    val TAG = "VideoPlayerImpl"

    private var exoPlayer: ExoPlayer? = null
    private var playerView: PlayerView? = null
    private var playbackStateListener: IVideoPlayer.OnPlaybackStateListener? = null
    private var progressListener: IVideoPlayer.OnProgressListener? = null

    // 进度更新处理器
    private val progressHandler = Handler(Looper.getMainLooper())
    private val progressRunnable = object : Runnable {
        override fun run() {
            updateProgress()
            progressHandler.postDelayed(this, PROGRESS_UPDATE_INTERVAL)
        }
    }

    // ExoPlayer 监听器
    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            Log.i(TAG, "onPlaybackStateChanged: $playbackState")
            when (playbackState) {
                Player.STATE_READY -> {
                    playbackStateListener?.onReady()
                }

                Player.STATE_ENDED -> {
                    stopProgressUpdate()
                    playbackStateListener?.onPlaybackCompleted()
                }

                Player.STATE_IDLE -> {
                    // 空闲状态
                }

                Player.STATE_BUFFERING -> {
                    // 缓冲状态
                }
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            playbackStateListener?.onPlaybackStateChanged(isPlaying)
            if (isPlaying) {
                startProgressUpdate()
            } else {
                stopProgressUpdate()
            }
        }

        override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
            stopProgressUpdate()
            playbackStateListener?.onError(error.message ?: "播放错误")
        }
    }

    override fun initialize(videoUrl: String, playerView: PlayerView) {
        release() // 先释放之前的资源
        exoPlayer = ExoPlayer.Builder(context).build().apply {
            addListener(playerListener)
            val mediaItem = MediaItem.fromUri(videoUrl)
            setMediaItem(mediaItem)
            prepare()
        }
        playerView.player = exoPlayer
        this.playerView = playerView
    }


    /**
     * 解绑 PlayerView
     */
    fun unbindPlayerView() {
        playerView?.player = null
        playerView = null
    }


    override fun play() {
        exoPlayer?.play()
    }

    override fun pause() {
        exoPlayer?.pause()
    }

    override fun stop() {
        exoPlayer?.stop()
        stopProgressUpdate()
    }

    override fun seekTo(position: Long) {
        exoPlayer?.seekTo(position)
    }

    override fun getCurrentPosition(): Long {
        return exoPlayer?.currentPosition ?: 0L
    }

    override fun getDuration(): Long {
        return exoPlayer?.duration?.takeIf { it > 0 } ?: 0L
    }

    override fun isPlaying(): Boolean {
        return exoPlayer?.isPlaying ?: false
    }

    override fun release() {
        stopProgressUpdate()
        exoPlayer?.removeListener(playerListener)
        exoPlayer?.release()
        exoPlayer = null
        unbindPlayerView()
    }

    override fun setOnPlaybackStateListener(listener: IVideoPlayer.OnPlaybackStateListener?) {
        this.playbackStateListener = listener
    }

    override fun setOnProgressListener(listener: IVideoPlayer.OnProgressListener?) {
        this.progressListener = listener
    }

    /**
     * 开始进度更新
     */
    private fun startProgressUpdate() {
        stopProgressUpdate()
        progressHandler.post(progressRunnable)
    }

    /**
     * 停止进度更新
     */
    private fun stopProgressUpdate() {
        progressHandler.removeCallbacks(progressRunnable)
    }

    /**
     * 更新播放进度
     */
    private fun updateProgress() {
        val currentPosition = getCurrentPosition()
        val duration = getDuration()
        progressListener?.onProgressUpdate(currentPosition, duration)
    }

    companion object {
        private const val PROGRESS_UPDATE_INTERVAL = 100L // 100ms 更新一次进度
    }
} 