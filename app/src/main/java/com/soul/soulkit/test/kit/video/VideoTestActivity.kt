package com.soul.soulkit.test.kit.video

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.soul.android_kit.R
import com.soul.android_kit.databinding.ActivityVideoTestBinding
import com.soul.common.utils.LogUtil
import com.soul.feature.video.IVideoPlayer
import com.soul.feature.video.PlayConfig
import com.soul.feature.video.VideoPlayerManager

/**
 * 视频播放器测试Activity
 * 演示如何使用视频播放模块
 */
class VideoTestActivity : AppCompatActivity() {
    val TAG = "VideoTestActivity"

    private lateinit var binding: ActivityVideoTestBinding
    private val videoPlayerManager = VideoPlayerManager.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupVideoPlayer()
        setupUI()
    }

    private fun setupVideoPlayer() {
        // 创建默认播放配置（可以根据需要修改）
        val defaultConfig = PlayConfig(
            videoUrl = "https://clip-live.oss-cn-shenzhen.aliyuncs.com/record/clip-live/f9c78238-0d10-443e-958e-9abbc0eb5ee2-douyin/32a2cb66-18d9-3ea5-a1d5-d1608483be2f_editing_1756188137.m3u8",
            autoPlay = false,
            looping = false,
            muted = false
        )

        videoPlayerManager.initialize(this, binding.videoContainer, this)
        // 设置播放状态监听
        videoPlayerManager.setOnPlaybackStateListener(object :
            IVideoPlayer.OnPlaybackStateListener {
            override fun onPlaybackStateChanged(isPlaying: Boolean) {
                runOnUiThread {
                    binding.statusText.text = if (isPlaying) "正在播放" else "已暂停"
                }
            }

            override fun onReady() {
                Log.i(TAG, "onReady")
                runOnUiThread {
                    binding.statusText.text = "准备完成"
                }
            }

            override fun onError(error: String) {
                runOnUiThread {
                    binding.statusText.text = "错误：$error"
                    Toast.makeText(this@VideoTestActivity, error, Toast.LENGTH_LONG).show()
                }
            }

            override fun onPlaybackCompleted() {
                runOnUiThread {
                    binding.statusText.text = "播放完成"
                }
            }
        })

        // 设置进度监听
        videoPlayerManager.setOnProgressListener(object : IVideoPlayer.OnProgressListener {
            override fun onProgressUpdate(currentPosition: Long, duration: Long) {
                runOnUiThread {
                    val current = formatTime(currentPosition)
                    val total = formatTime(duration)
                    binding.statusText.text = "播放中：$current / $total"
                }
            }
        })
    }

    private fun setupUI() {
        // 加载视频按钮
        binding.loadVideoButton.setOnClickListener {
            loadVideo()
        }

        // 播放按钮
        binding.playButton.setOnClickListener {
            videoPlayerManager.play()
        }

        // 暂停按钮
        binding.pauseButton.setOnClickListener {
            videoPlayerManager.pause()
        }

        // 停止按钮
        binding.stopButton.setOnClickListener {
            videoPlayerManager.stop()
        }
    }

    private fun loadVideo() {
        val videoUrl = binding.videoUrlEditText.text.toString().trim()

        if (videoUrl.isEmpty()) {
            Toast.makeText(this, "请输入视频地址", Toast.LENGTH_SHORT).show()
            return
        }

        // 创建播放配置（根据 UI 选项）
        val playConfig = PlayConfig(
            videoUrl = videoUrl,
            autoPlay = binding.autoPlayCheckBox.isChecked,
            looping = binding.loopingCheckBox.isChecked,
            muted = binding.mutedCheckBox.isChecked
        )

        // 更新播放配置
        videoPlayerManager.updatePlayConfig(playConfig)

        binding.statusText.text = "正在加载视频..."
    }

    /**
     * 格式化时间显示
     */
    private fun formatTime(timeMs: Long): String {
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

    override fun onDestroy() {
        super.onDestroy()
        videoPlayerManager.release()
    }
} 