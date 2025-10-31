package com.soul.soulkit.test

import android.graphics.Color
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.soul.android_kit.R
import com.soul.soulkit.test.core.common.TestItem

/**
 * 基础测试Activity，包含通用的测试功能
 */
open class BaseTestActivity : AppCompatActivity() {

    /**
     * 测试结果枚举
     */
    enum class TestResult {
        NONE,    // 未测试
        SUCCESS, // 成功
        FAILURE  // 失败
    }

    /**
     * 创建分类标题
     */
    protected fun createCategoryTitle(title: String): TextView {
        val textView = TextView(this)
        textView.text = title
        textView.textSize = 18f
        textView.setTextColor(ContextCompat.getColor(this, R.color.primary))
        textView.setPadding(32, 48, 32, 16)
        textView.setTypeface(null, android.graphics.Typeface.BOLD)
        return textView
    }

    /**
     * 创建测试按钮
     */
    protected fun createTestButton(test: TestItem): Button {
        val button = Button(this)
        button.text = test.name
        button.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(32, 8, 32, 8)
        }
        
        // 设置初始状态
        setButtonState(button, TestResult.NONE)
        
        // 设置点击事件
        button.setOnClickListener {
            try {
                val result = test.testAction()
                setButtonState(button, if (result) TestResult.SUCCESS else TestResult.FAILURE)
            } catch (e: Exception) {
                setButtonState(button, TestResult.FAILURE, e.message)
            }
        }
        
        return button
    }

    /**
     * 创建分隔线
     */
    protected fun createDivider(): View {
        val divider = View(this)
        divider.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            2
        ).apply {
            setMargins(32, 16, 32, 16)
        }
        divider.setBackgroundColor(ContextCompat.getColor(this, R.color.divider))
        return divider
    }

    /**
     * 设置按钮状态
     */
    protected fun setButtonState(button: Button, result: TestResult, errorMessage: String? = null) {
        when (result) {
            TestResult.NONE -> {
                button.setBackgroundColor(ContextCompat.getColor(this, R.color.button_default))
                button.setTextColor(Color.BLACK)
            }
            TestResult.SUCCESS -> {
                button.setBackgroundColor(ContextCompat.getColor(this, R.color.button_success))
                button.setTextColor(Color.WHITE)
            }
            TestResult.FAILURE -> {
                button.setBackgroundColor(ContextCompat.getColor(this, R.color.button_failure))
                button.setTextColor(Color.WHITE)
                errorMessage?.let {
                    // 可以在这里显示错误信息，比如Toast或者Log
                    android.util.Log.e("TestError", "${button.text}: $it")
                }
            }
        }
    }

    /**
     * 显示测试结果统计
     */
    protected fun showTestStatistics(
        totalTests: Int,
        successCount: Int,
        failureCount: Int
    ) {
        val message = """
            测试完成！
            总计: $totalTests
            成功: $successCount
            失败: $failureCount
            成功率: ${if (totalTests > 0) (successCount * 100 / totalTests) else 0}%
        """.trimIndent()
        
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("测试结果统计")
            .setMessage(message)
            .setPositiveButton("确定", null)
            .show()
    }
} 