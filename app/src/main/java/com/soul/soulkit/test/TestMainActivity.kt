package com.soul.soulkit.test

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.soul.android_kit.R
import com.soul.soulkit.test.core.CoreModelTestActivity
import com.soul.soulkit.test.ffmpeg.FfmpegModelTestActivity
import com.soul.soulkit.test.kit.KitCoreModelTestActivity as KitCoreModelTestActivity

/**
 * 主测试Activity - 各个模块功能测试的分发入口
 * 
 * 根据功能测试规则，采用三级层级结构：
 * 第一层：大类（Category）- 本页面展示的三个大类模块
 * 第二层：小模块分类（Submodule）- 在各个大类测试Activity中展示
 * 第三层：业务功能列表（Feature List）- 每个小模块下的具体功能测试
 */
class TestMainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TestModuleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_main)
        
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
                name = "Core 核心模块",
                description = "Android 核心功能模块，包含 Android 原生代码的封装，不依赖第三方库，提供基础能力和工具类",
                icon = "🔧",
                activityClass = CoreModelTestActivity::class.java
            ),
            TestModule(
                name = "Kit 能力模块",
                description = "常用能力封装模块，封装经过市场验证的第三方库，稳定、常用的开发库",
                icon = "📦",
                activityClass = KitCoreModelTestActivity::class.java
            ),
            TestModule(
                name = "FFmpeg 专项模块",
                description = "专项业务模块，视频处理相关功能，大型专业业务模块",
                icon = "🎥",
                activityClass = FfmpegModelTestActivity::class.java
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

/**
 * 测试模块数据类
 */
data class TestModule(
    val name: String,
    val description: String,
    val icon: String,
    val activityClass: Class<*>?
)
