package com.soul.ffmpeg_kit

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * FFmpeg 测试 Activity
 */
class FFmpegTestActivity : Activity(), SurfaceHolder.Callback {
    private var surfaceView: SurfaceView? = null
    private var playButton: Button? = null
    private var stopButton: Button? = null
    private var ffmpegPlayer: FFmpegPlayer? = null
    private var surfaceHolder: SurfaceHolder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ffmpeg_test)

        initViews()
        initPlayer()
        requestPermissions()
    }

    private fun initViews() {
        surfaceView = findViewById<SurfaceView?>(R.id.surface_view)
        playButton = findViewById<Button?>(R.id.btn_play)
        stopButton = findViewById<Button?>(R.id.btn_stop)

        surfaceHolder = surfaceView!!.getHolder()
        surfaceHolder!!.addCallback(this)

        playButton!!.setOnClickListener(View.OnClickListener { v: View? -> playVideo() })
        stopButton!!.setOnClickListener(View.OnClickListener { v: View? -> stopVideo() })
    }

    private fun initPlayer() {
        ffmpegPlayer = FFmpegPlayer()
    }

    private fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "存储权限已获取", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "需要存储权限才能播放视频", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun playVideo() {
        // 检查权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this, "请先授予存储权限", Toast.LENGTH_SHORT).show()
            return
        }


        // 这里需要替换为实际的视频文件路径
        val videoPath = "/sdcard/test_video.mp4"
        ffmpegPlayer!!.playVideo(videoPath, surfaceHolder!!.getSurface())
        Toast.makeText(this, "开始播放视频: " + videoPath, Toast.LENGTH_SHORT).show()
    }

    private fun stopVideo() {
        ffmpegPlayer!!.stopPlay()
        Toast.makeText(this, "停止播放", Toast.LENGTH_SHORT).show()
    }


    override fun surfaceChanged(
        p0: SurfaceHolder,
        p1: Int,
        p2: Int,
        p3: Int
    ) {
        // Surface 创建完成
        Toast.makeText(this, "视频渲染表面已准备就绪", Toast.LENGTH_SHORT).show()
    }

    override fun surfaceCreated(p0: SurfaceHolder) {
    }

    override fun surfaceDestroyed(p0: SurfaceHolder) {
        // Surface 销毁
        if (ffmpegPlayer != null) {
            ffmpegPlayer!!.stopPlay()
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1001
    }
}