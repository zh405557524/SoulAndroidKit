package com.soul.soulkit.test.core.database

import android.os.Bundle
import android.widget.Button
import android.widget.ScrollView
import android.widget.LinearLayout
import com.soul.android_kit.R
import com.soul.soulkit.test.BaseTestActivity
import com.soul.soulkit.test.TestCategory
import com.soul.soulkit.test.TestItem
import kotlinx.coroutines.*

/**
 * Database模块功能测试Activity
 */
class DatabaseTestActivity : BaseTestActivity() {

    private lateinit var scrollView: ScrollView
    private lateinit var containerLayout: LinearLayout
    private lateinit var autoTestButton: Button
    private lateinit var clearResultsButton: Button

    private val testCategories = mutableListOf<TestCategory>()
    private var autoTestJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_database_test)

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
                    name = "🗄️ 数据库管理",
                    tests = listOf(
                        TestItem("数据库初始化测试") { testDatabaseInit() },
                        TestItem("数据库获取测试") { testGetDatabase() },
                        TestItem("数据库状态检测") { testDatabaseStatus() },
                        TestItem("数据库关闭测试") { testDatabaseClose() }
                    )
                ),
                TestCategory(
                    name = "📝 基础CRUD操作",
                    tests = listOf(
                        TestItem("插入数据测试") { testInsert() },
                        TestItem("批量插入测试") { testBatchInsert() },
                        TestItem("查询单条数据") { testQueryOne() },
                        TestItem("查询所有数据") { testQueryAll() },
                        TestItem("更新数据测试") { testUpdate() },
                        TestItem("删除数据测试") { testDelete() },
                        TestItem("查询数量测试") { testCount() }
                    )
                ),
                TestCategory(
                    name = "🔄 事务处理",
                    tests = listOf(
                        TestItem("事务提交测试") { testTransactionCommit() },
                        TestItem("事务回滚测试") { testTransactionRollback() },
                        TestItem("批量事务操作") { testBatchTransaction() }
                    )
                ),
                TestCategory(
                    name = "🔍 高级查询",
                    tests = listOf(
                        TestItem("条件查询测试") { testConditionalQuery() },
                        TestItem("排序查询测试") { testOrderByQuery() },
                        TestItem("原生SQL查询") { testRawQuery() },
                        TestItem("表存在检测") { testTableExists() }
                    )
                ),
                TestCategory(
                    name = "👤 User DAO测试",
                    tests = listOf(
                        TestItem("User表初始化") { testUserTableInit() },
                        TestItem("User插入测试") { testUserInsert() },
                        TestItem("User查询测试") { testUserQuery() },
                        TestItem("User更新测试") { testUserUpdate() },
                        TestItem("User删除测试") { testUserDelete() },
                        TestItem("User批量操作") { testUserBatch() },
                        TestItem("User条件查询") { testUserConditionalQuery() }
                    )
                ),
                TestCategory(
                    name = "⚡ 异步操作",
                    tests = listOf(
                        TestItem("协程插入测试") { testCoroutineInsert() },
                        TestItem("协程查询测试") { testCoroutineQuery() },
                        TestItem("协程批量操作") { testCoroutineBatch() }
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
}



