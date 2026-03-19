package com.soul.soulkit.test.kit

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.soul.android_kit.R
import com.soul.soulkit.test.TestModule
import com.soul.soulkit.test.TestModuleAdapter
import com.soul.soulkit.test.kit.database.DatabaseTestActivity
import com.soul.soulkit.test.kit.video.VideoTestActivity

/**
 * Kit 能力模块测试入口 Activity
 * 
 * 展示 kit 大类下的所有子模块，采用与 TestMainActivity 相同的布局和交互方式。
 * 根据功能测试规则，kit 大类下的小模块包括：
 * - video - 视频能力
 * - picker - 媒体选择器
 * - download - 下载管理
 * - player-media3 - 播放器
 */
class KitCoreModelTestActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TestModuleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_main)
        
        // 设置标题
        supportActionBar?.title = "Kit 能力模块测试"
        
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
                name = "Video 视频能力",
                description = "视频播放、处理相关功能测试",
                icon = "🎬",
                activityClass = VideoTestActivity::class.java
            ),
            TestModule(
                name = "Database 数据库",
                description = "数据库相关功能测试",
                icon = "💾",
                activityClass = DatabaseTestActivity::class.java
            ),
            TestModule(
                name = "Picker 媒体选择器",
                description = "图片、视频等媒体选择功能测试",
                icon = "📷",
                activityClass = null // TODO: 待创建 PickerTestActivity
            ),
            TestModule(
                name = "Download 下载管理",
                description = "文件下载管理功能测试",
                icon = "⬇️",
                activityClass = null // TODO: 待创建 DownloadTestActivity
            ),
            TestModule(
                name = "Player-Media3 播放器",
                description = "基于 Media3 的播放器功能测试",
                icon = "▶️",
                activityClass = null // TODO: 待创建 PlayerMedia3TestActivity
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
