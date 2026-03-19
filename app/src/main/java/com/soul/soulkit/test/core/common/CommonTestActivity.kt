package com.soul.soulkit.test.core.common

import android.os.Bundle
import android.widget.Button
import android.widget.ScrollView
import android.widget.LinearLayout
import com.soul.android_kit.R
import com.soul.soulkit.test.BaseTestActivity
import com.soul.common.Global
import com.soul.soulkit.test.TestCategory
import com.soul.soulkit.test.TestItem
import kotlinx.coroutines.*

/**
 * Common模块功能测试Activity
 */
class CommonTestActivity : BaseTestActivity() {

    private lateinit var scrollView: ScrollView
    private lateinit var containerLayout: LinearLayout
    private lateinit var autoTestButton: Button
    private lateinit var clearResultsButton: Button

    private val testCategories = mutableListOf<TestCategory>()
    private var autoTestJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_common_test)

        initViews()
        initTestCategories()
        setupTestButtons()
    }

    private fun initViews() {
        scrollView = findViewById(R.id.scrollView)
        containerLayout = findViewById(R.id.containerLayout)
        autoTestButton = findViewById(R.id.btnAutoTest)
        clearResultsButton = findViewById(R.id.btnClearResults)

        autoTestButton.setOnClickListener { startAutoTest() }
        clearResultsButton.setOnClickListener { clearAllResults() }
    }

    private fun initTestCategories() {
        testCategories.addAll(
            listOf(
                TestCategory(
                    name = "🔧 基础工具",
                    tests = listOf(
                        TestItem("Global初始化测试") { testGlobalInit() },
                        TestItem("Global上下文测试") { testGlobalContext() },
                        TestItem("Global主线程测试") { testGlobalMainThread() },
                        TestItem("Utils空值判断测试") { testUtilsEmpty() }
                    )
                ),
                TestCategory(
                    name = "📝 字符串处理",
                    tests = listOf(
                        TestItem("StringUtils空值测试") { testStringEmpty() },
                        TestItem("StringUtils比较测试") { testStringEquals() },
                        TestItem("StringUtils反转测试") { testStringReverse() },
                        TestItem("StringUtils全半角转换") { testStringConvert() }
                    )
                ),
                TestCategory(
                    name = "🔍 正则验证",
                    tests = listOf(
                        TestItem("手机号验证测试") { testMobileValidation() },
                        TestItem("邮箱验证测试") { testEmailValidation() },
                        TestItem("身份证验证测试") { testIDCardValidation() },
                        TestItem("URL验证测试") { testURLValidation() }
                    )
                ),
                TestCategory(
                    name = "🔐 加密解密",
                    tests = listOf(
                        TestItem("MD5加密测试") { testMD5Encrypt() },
                        TestItem("SHA1加密测试") { testSHA1Encrypt() },
                        TestItem("Base64编码测试") { testBase64Encode() },
                        TestItem("AES加密测试") { testAESEncrypt() }
                    )
                ),
                TestCategory(
                    name = "📱 设备信息",
                    tests = listOf(
                        TestItem("设备ID获取测试") { testDeviceId() },
                        TestItem("设备型号测试") { testDeviceModel() },
                        TestItem("Root检测测试") { testDeviceRoot() },
                        TestItem("应用信息测试") { testAppInfo() }
                    )
                ),
                TestCategory(
                    name = "🌐 网络检测",
                    tests = listOf(
                        TestItem("网络连接状态测试") { testNetworkConnection() },
                        TestItem("WiFi连接测试") { testWiFiConnection() },
                        TestItem("移动数据测试") { testMobileData() },
                        TestItem("网络类型测试") { testNetworkType() }
                    )
                ),
                TestCategory(
                    name = "📂 文件操作",
                    tests = listOf(
                        TestItem("文件存在检测") { testFileExists() },
                        TestItem("目录创建测试") { testCreateDir() },
                        TestItem("文件复制测试") { testCopyFile() },
                        TestItem("文件大小测试") { testFileSize() }
                    )
                ),
                TestCategory(
                    name = "🕒 时间处理",
                    tests = listOf(
                        TestItem("当前时间获取") { testCurrentTime() },
                        TestItem("时间戳转换") { testTimeConvert() },
                        TestItem("时间差计算") { testTimeSpan() },
                        TestItem("友好时间显示") { testFriendlyTime() }
                    )
                ),
                TestCategory(
                    name = "📐 尺寸转换",
                    tests = listOf(
                        TestItem("dp转px测试") { testDp2Px() },
                        TestItem("px转dp测试") { testPx2Dp() },
                        TestItem("sp转px测试") { testSp2Px() },
                        TestItem("屏幕尺寸测试") { testScreenSize() }
                    )
                ),
                TestCategory(
                    name = "💾 数据存储",
                    tests = listOf(
                        TestItem("SP存储字符串") { testSPString() },
                        TestItem("SP存储整数") { testSPInt() },
                        TestItem("SP存储布尔值") { testSPBoolean() },
                        TestItem("SP清除数据") { testSPClear() }
                    )
                ),
                TestCategory(
                    name = "💬 用户交互",
                    tests = listOf(
                        TestItem("Toast显示测试") { testToastShow() },
                        TestItem("剪贴板复制测试") { testClipboardCopy() },
                        TestItem("剪贴板获取测试") { testClipboardGet() },
                        TestItem("键盘显示隐藏") { testKeyboard() }
                    )
                ),
                TestCategory(
                    name = "📊 日志管理",
                    tests = listOf(
                        TestItem("Debug日志测试") { testLogDebug() },
                        TestItem("Info日志测试") { testLogInfo() },
                        TestItem("Error日志测试") { testLogError() },
                        TestItem("文件日志测试") { testLogFile() }
                    )
                )
            ))
    }

    private fun setupTestButtons() {
        testCategories.forEach { category ->
            // 添加分类标题
            val categoryTitle = createCategoryTitle(category.name)
            containerLayout.addView(categoryTitle)

            // 添加测试按钮
            category.tests.forEach { test ->
                val button = createTestButton(test)
                containerLayout.addView(button)
            }

            // 添加分隔线
            val divider = createDivider()
            containerLayout.addView(divider)
        }
    }

    private fun startAutoTest() {
        autoTestJob?.cancel()
        autoTestJob = CoroutineScope(Dispatchers.Main).launch {
            autoTestButton.isEnabled = false
            autoTestButton.text = "自动测试进行中..."

            try {
                testCategories.forEach { category ->
                    category.tests.forEach { test ->
                        delay(500) // 每个测试间隔500ms
                        val button = findTestButton(test.name)
                        button?.let { btn ->
                            try {
                                test.testAction()
                                setButtonState(btn, TestResult.SUCCESS)
                            } catch (e: Exception) {
                                setButtonState(btn, TestResult.FAILURE, e.message)
                            }
                        }
                    }
                }
            } finally {
                autoTestButton.isEnabled = true
                autoTestButton.text = "🤖 开始自动测试"
            }
        }
    }

    private fun clearAllResults() {
        testCategories.forEach { category ->
            category.tests.forEach { test ->
                val button = findTestButton(test.name)
                button?.let { setButtonState(it, TestResult.NONE) }
            }
        }
    }

    private fun findTestButton(testName: String): Button? {
        for (i in 0 until containerLayout.childCount) {
            val view = containerLayout.getChildAt(i)
            if (view is Button && view.text.toString().contains(testName)) {
                return view
            }
        }
        return null
    }

    // 测试方法实现
    private fun testGlobalInit(): Boolean {
        return try {
            Global.init(application, true)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun testGlobalContext(): Boolean {
        return Global.getContext() != null
    }

    private fun testGlobalMainThread(): Boolean {
        return Global.isMainProcess(this)
    }

    // ... 其他测试方法将在下一个文件中实现
}

