package com.soul.soulkit.test.kit.database

import android.os.Bundle
import android.widget.TextView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

/**
 * Database模块功能测试Activity
 * TODO: 待实现具体的数据库测试功能
 */
class DatabaseTestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 临时布局
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(32, 32, 32, 32)
        
        val textView = TextView(this)
        textView.text = "Database模块测试\n\n🚧 功能开发中..."
        textView.textSize = 18f
        
        layout.addView(textView)
        setContentView(layout)
    }
} 