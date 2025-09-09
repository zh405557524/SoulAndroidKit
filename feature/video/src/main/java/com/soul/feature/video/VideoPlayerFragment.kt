package com.soul.feature.video

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.soul.feature.video.databinding.FragmentVideoPlayerBinding

/**
 * 视频播放器 Fragment
 * 提供视频播放界面和控制功能
 * 
 * 数据流向：
 * ViewModel (播放状态) -> Fragment (监听并转发) -> VideoPlayerManager (外部回调)
 */
class VideoPlayerFragment : Fragment() {

    private var _binding: FragmentVideoPlayerBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: VideoPlayerViewModel
    private var isUserSeeking = false

    // 外部控制接口 - 供 VideoPlayerManager 使用
    var onPlaybackStateChanged: ((Boolean) -> Unit)? = null
    var onProgressChanged: ((Long, Long) -> Unit)? = null
    var onError: ((String) -> Unit)? = null
        var onReady: (() -> Unit)? = null
    var onPlaybackCompleted: (() -> Unit)? = null
    
    companion object {
        private const val TAG = "VideoPlayerFragment"
        
        /**
         * 创建Fragment实例
         */
        fun newInstance(): VideoPlayerFragment {
            return VideoPlayerFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVideoPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupUI()
        observeViewModel()
    }

    /**
     * 设置 ViewModel
     */
    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[VideoPlayerViewModel::class.java]
    }

    /**
     * 设置 UI 控件
     */
    private fun setupUI() {
        // 播放/暂停按钮
        binding.playPauseButton.setOnClickListener {
            viewModel.togglePlayPause()
        }

        // 停止按钮
        binding.stopButton.setOnClickListener {
            viewModel.stop()
        }

        // 进度条控制
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val progressFloat = progress / 1000f
                    viewModel.seekToProgress(progressFloat)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                isUserSeeking = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                isUserSeeking = false
            }
        })

        // 错误提示点击清除
        binding.errorText.setOnClickListener {
            viewModel.clearError()
        }
    }

    /**
     * 观察 ViewModel 数据变化
     */
    private fun observeViewModel() {
        // 播放状态
        viewModel.isPlaying.observe(viewLifecycleOwner) { isPlaying ->
            binding.playPauseButton.setImageResource(
                if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play_arrow
            )
            // 传递播放状态给 VideoPlayerManager
            Log.d(TAG, "ViewModel -> Fragment: onPlaybackStateChanged($isPlaying)")
            onPlaybackStateChanged?.invoke(isPlaying)
        }

                // 播放进度
        viewModel.currentPosition.observe(viewLifecycleOwner) { currentPosition ->
            binding.currentTimeText.text = viewModel.formatTime(currentPosition)
            
            // 只在用户非手动拖拽时更新进度条
            if (!isUserSeeking) {
                val progress = (viewModel.getCurrentProgress() * 1000).toInt()
                binding.seekBar.progress = progress
            }
            
            val duration = viewModel.duration.value ?: 0L
            // 传递播放进度给 VideoPlayerManager
            onProgressChanged?.invoke(currentPosition, duration)
        }

        // 视频时长
        viewModel.duration.observe(viewLifecycleOwner) { duration ->
            binding.durationText.text = viewModel.formatTime(duration)
        }

        // 准备状态
        viewModel.isReady.observe(viewLifecycleOwner) { isReady ->
            binding.loadingIndicator.visibility = if (isReady) View.GONE else View.VISIBLE
            // 当准备完成时，传递给 VideoPlayerManager
            if (isReady) {
                Log.d(TAG, "ViewModel -> Fragment: onReady()")
                onReady?.invoke()
            }
        }

        // 错误信息
        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                binding.errorText.text = errorMessage
                binding.errorText.visibility = View.VISIBLE
                // 传递错误信息给 VideoPlayerManager
                Log.e(TAG, "ViewModel -> Fragment: onError($errorMessage)")
                onError?.invoke(errorMessage)
            } else {
                binding.errorText.visibility = View.GONE
            }
        }

        // 播放完成状态
        viewModel.isCompleted.observe(viewLifecycleOwner) { isCompleted ->
            if (isCompleted) {
                binding.seekBar.progress = 0
                // 传递播放完成状态给 VideoPlayerManager
                Log.d(TAG, "ViewModel -> Fragment: onPlaybackCompleted()")
                onPlaybackCompleted?.invoke()
            }
        }
    }

    /**
     * 内部接口：初始化视频（仅供 VideoPlayerManager 调用）
     */
    internal fun initializeVideo(videoUrl: String) {
        viewModel.initializeVideo(videoUrl, binding.playerView)
    }

    /**
     * 内部接口：播放（仅供 VideoPlayerManager 调用）
     */
    internal fun play() {
        viewModel.play()
    }

    /**
     * 内部接口：暂停（仅供 VideoPlayerManager 调用）
     */
    internal fun pause() {
        viewModel.pause()
    }

    /**
     * 内部接口：停止（仅供 VideoPlayerManager 调用）
     */
    internal fun stop() {
        viewModel.stop()
    }

    /**
     * 内部接口：跳转到指定位置（仅供 VideoPlayerManager 调用）
     */
    internal fun seekTo(position: Long) {
        viewModel.seekTo(position)
    }

    /**
     * 内部接口：获取当前播放位置（仅供 VideoPlayerManager 调用）
     */
    internal fun getCurrentPosition(): Long {
        return viewModel.currentPosition.value ?: 0L
    }

    /**
     * 内部接口：获取视频总时长（仅供 VideoPlayerManager 调用）
     */
    internal fun getDuration(): Long {
        return viewModel.duration.value ?: 0L
    }

    /**
     * 内部接口：是否正在播放（仅供 VideoPlayerManager 调用）
     */
    internal fun isPlaying(): Boolean {
        return viewModel.isPlaying.value ?: false
    }

    /**
     * 内部接口：显示/隐藏控制栏（仅供 VideoPlayerManager 调用）
     */
    internal fun setControlsVisible(visible: Boolean) {
        binding.controlsContainer.visibility = if (visible) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 