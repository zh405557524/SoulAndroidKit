package com.soul.ffmpegkit

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.soul.common.Global
import com.soul.common.utils.LogUtil
import com.soul.ffmpeg_kit.R
import com.soul.utils.FfmpegCutter
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * FFmpeg 测试 Activity
 */
class FFmpegTestActivity : Activity() {
    val TAG = "FFmpegTestActivity"
    private var versionButton: Button? = null
    private var configButton: Button? = null
    private var formatsButton: Button? = null
    private var btn_cutVideo: Button? = null
    private var infoTextView: TextView? = null
//    private var ffmpegPlayer: FFmpegPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ffmpeg_test)

        initViews()
        initPlayer()
    }

    private fun initViews() {
        versionButton = findViewById<Button?>(R.id.btn_version)
        configButton = findViewById<Button?>(R.id.btn_config)
        formatsButton = findViewById<Button?>(R.id.btn_formats)
        btn_cutVideo = findViewById<Button?>(R.id.btn_cutVideo)
        infoTextView = findViewById<TextView?>(R.id.tv_info)

        versionButton!!.setOnClickListener(View.OnClickListener { v: View? -> getVersion() })
        configButton!!.setOnClickListener(View.OnClickListener { v: View? -> getConfiguration() })
        formatsButton!!.setOnClickListener(View.OnClickListener { v: View? -> getFormats() })
        btn_cutVideo!!.setOnClickListener(View.OnClickListener { v: View? -> btn_cutVideo() })
    }

    private fun initPlayer() {
        try {
//            ffmpegPlayer = FFmpegPlayer()
            Toast.makeText(this, "FFmpeg库加载成功", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "FFmpeg库加载失败: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun getVersion() {
        try {
//            val version = ffmpegPlayer!!.getFFmpegVersion()
//            infoTextView!!.text = "FFmpeg版本: $version"
            Toast.makeText(this, "获取版本成功", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "获取版本失败: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun getConfiguration() {
        try {
//            val config = ffmpegPlayer!!.getFFmpegConfiguration()
//            infoTextView!!.text = "FFmpeg配置: $config"
            Toast.makeText(this, "获取配置成功", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "获取配置失败: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun getFormats() {
        try {
//            val formats = ffmpegPlayer!!.getSupportedFormats()
//            infoTextView!!.text = "支持的格式: $formats"
            Toast.makeText(this, "获取格式成功", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "获取格式失败: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}

private fun FFmpegTestActivity.btn_cutVideo() {
    val timestamp = System.currentTimeMillis()
    val outputFileName = "clip_${timestamp}_${1000}_${1000}.mp4"
    val outputPath = "${Global.getExternalCacheDir()}/live_clips/$outputFileName"
    // 配置FFmpeg裁剪选项
    val options = FfmpegCutter.Options(
        input = "https://cos.szchuyue.cn/live/115500263/115500263.m3u8",
        output = outputPath,
        startMs = 1000 * 2,
        durationMs = 1000 * 20,
        reencode = false, // 优先使用无损快剪
        expectedTotalMs = 18 * 1000,
        timeoutMs = 120_000L // 2分钟超时
    )
    // 创建监听器
    val listener = object : FfmpegCutter.Listener {
        override fun onProgress(progress: Float) {
            // 可以在这里更新进度UI
            LogUtil.i(TAG, "progress: $progress")
        }

        override fun onLog(line: String) {
            // FFmpeg日志
            LogUtil.i(TAG, "line: $line")
        }

        override fun onCompleted(output: String) {
            LogUtil.i(TAG, "output: $output")
        }

        override fun onError(throwable: Throwable) {
            LogUtil.i(TAG, "throwable: $throwable")
        }

        override fun onCanceled() {
            LogUtil.i(TAG, "canceled")
        }
    }

    GlobalScope.launch {
        // 执行FFmpeg裁剪
        FfmpegCutter.cut(options, listener)
    }
}
