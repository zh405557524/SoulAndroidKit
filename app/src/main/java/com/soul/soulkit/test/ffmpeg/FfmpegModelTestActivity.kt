package com.soul.soulkit.test.ffmpeg

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.soul.android_kit.R
import com.soul.ffmpegkit.FFmpegTestActivity
import com.soul.soulkit.test.TestModule
import com.soul.soulkit.test.TestModuleAdapter

/**
 * FFmpeg 专项模块测试入口 Activity
 * 
 * 展示 ffmpeg 大类下的所有子模块，采用与 TestMainActivity 相同的布局和交互方式。
 * 根据功能测试规则，ffmpeg 大类下的小模块包括：
 * - ffmpeg_kit - FFmpeg处理核心
 */
class FfmpegModelTestActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TestModuleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_main)
        
        // 设置标题
        supportActionBar?.title = "FFmpeg 专项模块测试"
        
        initViews()
        initData()
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        
        adapter = TestModuleAdapter { module ->
            startTestActivity(module)
        }
        recyclerView.adapter = adapter
    }

    private fun initData() {
        val modules = listOf(
            TestModule(
                name = "FFmpeg Kit FFmpeg处理核心",
                description = "FFmpeg 视频处理、编码、解码等核心功能测试",
                icon = "🎥",
                activityClass = FFmpegTestActivity::class.java
            )
        )
        
        adapter.updateModules(modules)
    }

    private fun startTestActivity(module: TestModule) {
        module.activityClass?.let { activityClass ->
            val intent = Intent(this, activityClass)
            startActivity(intent)
        }
    }
}
