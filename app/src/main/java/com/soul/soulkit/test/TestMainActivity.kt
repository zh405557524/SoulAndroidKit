package com.soul.soulkit.test

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.soul.android_kit.R
import com.soul.soulkit.test.common.CommonTestActivity
import com.soul.soulkit.test.database.DatabaseTestActivity

/**
 * 主测试Activity - 各个模块功能测试的分发入口
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
                name = "Common模块测试",
                description = "字符串、设备、网络、文件、加密等工具类测试",
                icon = "🔧",
                activityClass = CommonTestActivity::class.java
            ),
            TestModule(
                name = "Database模块测试", 
                description = "数据库相关功能测试",
                icon = "💾",
                activityClass = DatabaseTestActivity::class.java
            ),
            TestModule(
                name = "DesignSystem模块测试",
                description = "设计系统组件测试",
                icon = "🎨", 
                activityClass = null // TODO: 待创建
            ),
            TestModule(
                name = "Media模块测试",
                description = "媒体播放相关功能测试",
                icon = "🎵",
                activityClass = null // TODO: 待创建
            ),
            TestModule(
                name = "Network模块测试",
                description = "网络请求相关功能测试", 
                icon = "🌐",
                activityClass = null // TODO: 待创建
            ),
            TestModule(
                name = "UI模块测试",
                description = "UI组件相关功能测试",
                icon = "📱", 
                activityClass = null // TODO: 待创建
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