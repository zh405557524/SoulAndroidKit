package com.soul.ffmpegkit

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.soul.ffmpeg_kit.R

/**
 * FFmpeg 测试 Activity
 */
class FFmpegTestActivity : Activity() {
    private var versionButton: Button? = null
    private var configButton: Button? = null
    private var formatsButton: Button? = null
    private var infoTextView: TextView? = null
    private var ffmpegPlayer: FFmpegPlayer? = null

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
        infoTextView = findViewById<TextView?>(R.id.tv_info)

        versionButton!!.setOnClickListener(View.OnClickListener { v: View? -> getVersion() })
        configButton!!.setOnClickListener(View.OnClickListener { v: View? -> getConfiguration() })
        formatsButton!!.setOnClickListener(View.OnClickListener { v: View? -> getFormats() })
    }

    private fun initPlayer() {
        try {
            ffmpegPlayer = FFmpegPlayer()
            Toast.makeText(this, "FFmpeg库加载成功", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "FFmpeg库加载失败: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun getVersion() {
        try {
            val version = ffmpegPlayer!!.getFFmpegVersion()
            infoTextView!!.text = "FFmpeg版本: $version"
            Toast.makeText(this, "获取版本成功", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "获取版本失败: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun getConfiguration() {
        try {
            val config = ffmpegPlayer!!.getFFmpegConfiguration()
            infoTextView!!.text = "FFmpeg配置: $config"
            Toast.makeText(this, "获取配置成功", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "获取配置失败: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun getFormats() {
        try {
            val formats = ffmpegPlayer!!.getSupportedFormats()
            infoTextView!!.text = "支持的格式: $formats"
            Toast.makeText(this, "获取格式成功", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "获取格式失败: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}