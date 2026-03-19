package com.soul.soulkit.test.core

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.soul.android_kit.R
import com.soul.soulkit.test.TestModule
import com.soul.soulkit.test.TestModuleAdapter
import com.soul.soulkit.test.core.common.CommonTestActivity

/**
 * Core 核心模块测试入口 Activity
 * 
 * 展示 core 大类下的所有子模块，采用与 TestMainActivity 相同的布局和交互方式。
 * 根据功能测试规则，core 大类下的小模块包括：
 * - common - 通用工具
 * - database - 数据库
 * - designsystem - 设计系统
 * - composeui - Compose UI工具
 * - ui - 传统UI
 * - network - 网络请求
 * - media - 多媒体基础
 */
class CoreModelTestActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TestModuleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_main)
        
        // 设置标题
        supportActionBar?.title = "Core 核心模块测试"
        
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
                name = "Common 通用工具",
                description = "字符串、设备、网络、文件、加密等通用工具类测试",
                icon = "🔧",
                activityClass = CommonTestActivity::class.java
            ),
//            TestModule(
//                name = "Database 数据库",
//                description = "数据库相关功能测试",
//                icon = "💾",
//                activityClass = DatabaseTestActivity::class.java
//            ),
            TestModule(
                name = "DesignSystem 设计系统",
                description = "设计系统组件测试",
                icon = "🎨",
                activityClass = null // TODO: 待创建 DesignSystemTestActivity
            ),
            TestModule(
                name = "ComposeUI Compose UI工具",
                description = "Compose UI 相关功能测试",
                icon = "🎭",
                activityClass = null // TODO: 待创建 ComposeUITestActivity
            ),
            TestModule(
                name = "UI 传统UI",
                description = "传统 UI 组件相关功能测试",
                icon = "📱",
                activityClass = null // TODO: 待创建 UITestActivity
            ),
            TestModule(
                name = "Network 网络请求",
                description = "网络请求相关功能测试",
                icon = "🌐",
                activityClass = null // TODO: 待创建 NetworkTestActivity
            ),
            TestModule(
                name = "Media 多媒体基础",
                description = "多媒体基础功能测试",
                icon = "🎬",
                activityClass = null // TODO: 待创建 MediaTestActivity
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
