package com.soul.soulkit.test.core.database

import android.content.ContentValues
import com.soul.database.DatabaseManager
import com.soul.database.DatabaseQuery
import com.soul.database.dao.UserDao
import com.soul.database.entity.User
import kotlinx.coroutines.runBlocking

/**
 * Database模块测试方法扩展
 */

// 数据库管理测试
fun DatabaseTestActivity.testDatabaseInit(): Boolean {
    return try {
        DatabaseManager.initDatabase(
            databaseName = "test_database",
            version = 1,
            onCreateCallback = { db ->
                // 创建测试表
                DatabaseQuery.execSQL(
                    db,
                    """
                    CREATE TABLE IF NOT EXISTS test_table (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL,
                        value INTEGER NOT NULL
                    )
                """.trimIndent()
                )
            }
        )
        DatabaseManager.isDatabaseInitialized("test_database")
    } catch (e: Exception) {
        false
    }
}

fun DatabaseTestActivity.testGetDatabase(): Boolean {
    return try {
        if (!DatabaseManager.isDatabaseInitialized("test_database")) {
            testDatabaseInit()
        }
        val db = DatabaseManager.getReadableDatabase("test_database")
        db != null && db.isOpen
    } catch (e: Exception) {
        false
    }
}

fun DatabaseTestActivity.testDatabaseStatus(): Boolean {
    return try {
        if (!DatabaseManager.isDatabaseInitialized("test_database")) {
            testDatabaseInit()
        }
        DatabaseManager.isDatabaseInitialized("test_database")
    } catch (e: Exception) {
        false
    }
}

fun DatabaseTestActivity.testDatabaseClose(): Boolean {
    return try {
        if (!DatabaseManager.isDatabaseInitialized("test_database")) {
            testDatabaseInit()
        }
        DatabaseManager.closeDatabase("test_database")
        !DatabaseManager.isDatabaseInitialized("test_database")
    } catch (e: Exception) {
        false
    }
}

// 基础CRUD操作测试
fun DatabaseTestActivity.testInsert(): Boolean {
    return try {
        if (!DatabaseManager.isDatabaseInitialized("test_database")) {
            testDatabaseInit()
        }
        val db = DatabaseManager.getWritableDatabase("test_database")
        
        // 清理旧数据
        DatabaseQuery.delete(db, "test_table")
        
        val values = ContentValues().apply {
            put("name", "test_item")
            put("value", 100)
        }
        val id = DatabaseQuery.insert(db, "test_table", values)
        id > 0
    } catch (e: Exception) {
        false
    }
}

fun DatabaseTestActivity.testBatchInsert(): Boolean {
    return try {
        if (!DatabaseManager.isDatabaseInitialized("test_database")) {
            testDatabaseInit()
        }
        val db = DatabaseManager.getWritableDatabase("test_database")
        
        // 清理旧数据
        DatabaseQuery.delete(db, "test_table")
        
        val valuesList = listOf(
            ContentValues().apply {
                put("name", "item1")
                put("value", 1)
            },
            ContentValues().apply {
                put("name", "item2")
                put("value", 2)
            },
            ContentValues().apply {
                put("name", "item3")
                put("value", 3)
            }
        )
        
        val count = DatabaseQuery.batchInsert(db, "test_table", valuesList)
        count == 3
    } catch (e: Exception) {
        false
    }
}

fun DatabaseTestActivity.testQueryOne(): Boolean {
    return try {
        if (!DatabaseManager.isDatabaseInitialized("test_database")) {
            testDatabaseInit()
            testInsert()
        }
        val db = DatabaseManager.getReadableDatabase("test_database")
        val values = DatabaseQuery.queryOne(
            db,
            "test_table",
            selection = "name = ?",
            selectionArgs = arrayOf("test_item")
        )
        values != null && values.getAsString("name") == "test_item"
    } catch (e: Exception) {
        false
    }
}

fun DatabaseTestActivity.testQueryAll(): Boolean {
    return try {
        if (!DatabaseManager.isDatabaseInitialized("test_database")) {
            testDatabaseInit()
            testBatchInsert()
        }
        val db = DatabaseManager.getReadableDatabase("test_database")
        val valuesList = DatabaseQuery.queryAll(db, "test_table")
        valuesList.isNotEmpty()
    } catch (e: Exception) {
        false
    }
}

fun DatabaseTestActivity.testUpdate(): Boolean {
    return try {
        if (!DatabaseManager.isDatabaseInitialized("test_database")) {
            testDatabaseInit()
            testInsert()
        }
        val db = DatabaseManager.getWritableDatabase("test_database")
        
        val values = ContentValues().apply {
            put("value", 200)
        }
        
        val count = DatabaseQuery.update(
            db,
            "test_table",
            values,
            whereClause = "name = ?",
            whereArgs = arrayOf("test_item")
        )
        
        // 验证更新
        val updated = DatabaseQuery.queryOne(
            db,
            "test_table",
            selection = "name = ?",
            selectionArgs = arrayOf("test_item")
        )
        count > 0 && updated?.getAsInteger("value") == 200
    } catch (e: Exception) {
        false
    }
}

fun DatabaseTestActivity.testDelete(): Boolean {
    return try {
        if (!DatabaseManager.isDatabaseInitialized("test_database")) {
            testDatabaseInit()
            testInsert()
        }
        val db = DatabaseManager.getWritableDatabase("test_database")
        
        val count = DatabaseQuery.delete(
            db,
            "test_table",
            whereClause = "name = ?",
            whereArgs = arrayOf("test_item")
        )
        
        // 验证删除
        val deleted = DatabaseQuery.queryOne(
            db,
            "test_table",
            selection = "name = ?",
            selectionArgs = arrayOf("test_item")
        )
        count > 0 && deleted == null
    } catch (e: Exception) {
        false
    }
}

fun DatabaseTestActivity.testCount(): Boolean {
    return try {
        if (!DatabaseManager.isDatabaseInitialized("test_database")) {
            testDatabaseInit()
            testBatchInsert()
        }
        val db = DatabaseManager.getReadableDatabase("test_database")
        val count = DatabaseQuery.count(db, "test_table")
        count >= 0
    } catch (e: Exception) {
        false
    }
}

// 事务处理测试
fun DatabaseTestActivity.testTransactionCommit(): Boolean {
    return try {
        if (!DatabaseManager.isDatabaseInitialized("test_database")) {
            testDatabaseInit()
        }
        val db = DatabaseManager.getWritableDatabase("test_database")
        
        // 清理旧数据
        DatabaseQuery.delete(db, "test_table")
        
        DatabaseQuery.transaction(db) { database ->
            DatabaseQuery.insert(
                database, "test_table",
                ContentValues().apply {
                    put("name", "trans1")
                    put("value", 1)
                }
            )
            DatabaseQuery.insert(
                database, "test_table",
                ContentValues().apply {
                    put("name", "trans2")
                    put("value", 2)
                }
            )
        }
        
        val count = DatabaseQuery.count(db, "test_table")
        count == 2L
    } catch (e: Exception) {
        false
    }
}

fun DatabaseTestActivity.testTransactionRollback(): Boolean {
    return try {
        if (!DatabaseManager.isDatabaseInitialized("test_database")) {
            testDatabaseInit()
        }
        val db = DatabaseManager.getWritableDatabase("test_database")
        
        // 清理旧数据
        DatabaseQuery.delete(db, "test_table")
        
        val initialCount = DatabaseQuery.count(db, "test_table")
        
        try {
            DatabaseQuery.transaction(db) { database ->
                DatabaseQuery.insert(
                    database, "test_table",
                    ContentValues().apply {
                        put("name", "rollback_test")
                        put("value", 1)
                    }
                )
                // 模拟异常，触发回滚
                throw RuntimeException("模拟异常")
            }
        } catch (e: Exception) {
            // 预期异常
        }
        
        val finalCount = DatabaseQuery.count(db, "test_table")
        finalCount == initialCount
    } catch (e: Exception) {
        false
    }
}

fun DatabaseTestActivity.testBatchTransaction(): Boolean {
    return try {
        if (!DatabaseManager.isDatabaseInitialized("test_database")) {
            testDatabaseInit()
        }
        val db = DatabaseManager.getWritableDatabase("test_database")
        
        // 清理旧数据
        DatabaseQuery.delete(db, "test_table")
        
        DatabaseQuery.transaction(db) { database ->
            repeat(10) { index ->
                DatabaseQuery.insert(
                    database, "test_table",
                    ContentValues().apply {
                        put("name", "batch_$index")
                        put("value", index)
                    }
                )
            }
        }
        
        val count = DatabaseQuery.count(db, "test_table")
        count == 10L
    } catch (e: Exception) {
        false
    }
}

// 高级查询测试
fun DatabaseTestActivity.testConditionalQuery(): Boolean {
    return try {
        if (!DatabaseManager.isDatabaseInitialized("test_database")) {
            testDatabaseInit()
            testBatchInsert()
        }
        val db = DatabaseManager.getReadableDatabase("test_database")
        
        val valuesList = DatabaseQuery.queryAll(
            db,
            "test_table",
            selection = "value > ?",
            selectionArgs = arrayOf("1")
        )
        valuesList.size == 2 // item2和item3
    } catch (e: Exception) {
        false
    }
}

fun DatabaseTestActivity.testOrderByQuery(): Boolean {
    return try {
        if (!DatabaseManager.isDatabaseInitialized("test_database")) {
            testDatabaseInit()
            testBatchInsert()
        }
        val db = DatabaseManager.getReadableDatabase("test_database")
        
        val valuesList = DatabaseQuery.queryAll(
            db,
            "test_table",
            orderBy = "value DESC"
        )
        
        if (valuesList.isEmpty()) return false
        
        // 验证排序：第一个应该是value最大的
        val firstValue = valuesList[0].getAsInteger("value") ?: 0
        val lastValue = valuesList[valuesList.size - 1].getAsInteger("value") ?: 0
        firstValue >= lastValue
    } catch (e: Exception) {
        false
    }
}

fun DatabaseTestActivity.testRawQuery(): Boolean {
    return try {
        if (!DatabaseManager.isDatabaseInitialized("test_database")) {
            testDatabaseInit()
            testBatchInsert()
        }
        val db = DatabaseManager.getReadableDatabase("test_database")
        
        val cursor = DatabaseQuery.rawQuery(
            db,
            "SELECT COUNT(*) as total FROM test_table WHERE value > ?",
            arrayOf("1")
        )
        
        val result = try {
            cursor.moveToFirst()
            cursor.getInt(0) == 2
        } finally {
            cursor.close()
        }
        result
    } catch (e: Exception) {
        false
    }
}

fun DatabaseTestActivity.testTableExists(): Boolean {
    return try {
        if (!DatabaseManager.isDatabaseInitialized("test_database")) {
            testDatabaseInit()
        }
        val db = DatabaseManager.getReadableDatabase("test_database")
        
        val exists = DatabaseQuery.tableExists(db, "test_table")
        val notExists = DatabaseQuery.tableExists(db, "non_existent_table")
        
        exists && !notExists
    } catch (e: Exception) {
        false
    }
}

// User DAO测试
fun DatabaseTestActivity.testUserTableInit(): Boolean {
    return try {
        val userDao = UserDao("test_user_database")
        userDao.initTable()
        true
    } catch (e: Exception) {
        false
    }
}

fun DatabaseTestActivity.testUserInsert(): Boolean {
    return try {
        val userDao = UserDao("test_user_database")
        if (!DatabaseManager.isDatabaseInitialized("test_user_database")) {
            userDao.initTable()
        }
        
        val user = User(
            name = "测试用户",
            age = 25,
            email = "test@example.com"
        )
        val id = userDao.insert(user)
        id > 0
    } catch (e: Exception) {
        false
    }
}

fun DatabaseTestActivity.testUserQuery(): Boolean {
    return try {
        val userDao = UserDao("test_user_database")
        if (!DatabaseManager.isDatabaseInitialized("test_user_database")) {
            userDao.initTable()
            testUserInsert()
        }
        
        val users = userDao.findAll()
        users.isNotEmpty()
    } catch (e: Exception) {
        false
    }
}

fun DatabaseTestActivity.testUserUpdate(): Boolean {
    return try {
        val userDao = UserDao("test_user_database")
        if (!DatabaseManager.isDatabaseInitialized("test_user_database")) {
            userDao.initTable()
            testUserInsert()
        }
        
        val users = userDao.findAll()
        if (users.isEmpty()) {
            testUserInsert()
            return testUserUpdate()
        }
        
        val user = users[0]
        val updatedUser = user.copy(age = 30)
        val count = userDao.update(updatedUser)
        
        // 验证更新
        val updated = userDao.findById(user.id)
        count > 0 && updated?.age == 30
    } catch (e: Exception) {
        false
    }
}

fun DatabaseTestActivity.testUserDelete(): Boolean {
    return try {
        val userDao = UserDao("test_user_database")
        if (!DatabaseManager.isDatabaseInitialized("test_user_database")) {
            userDao.initTable()
            testUserInsert()
        }
        
        val users = userDao.findAll()
        if (users.isEmpty()) {
            testUserInsert()
            return testUserDelete()
        }
        
        val user = users[0]
        val count = userDao.deleteById(user.id)
        
        // 验证删除
        val deleted = userDao.findById(user.id)
        count > 0 && deleted == null
    } catch (e: Exception) {
        false
    }
}

fun DatabaseTestActivity.testUserBatch(): Boolean {
    return try {
        val userDao = UserDao("test_user_database")
        if (!DatabaseManager.isDatabaseInitialized("test_user_database")) {
            userDao.initTable()
        }
        
        val users = listOf(
            User(name = "用户1", age = 20, email = "user1@example.com"),
            User(name = "用户2", age = 25, email = "user2@example.com"),
            User(name = "用户3", age = 30, email = "user3@example.com")
        )
        
        val count = userDao.batchInsert(users)
        count == 3
    } catch (e: Exception) {
        false
    }
}

fun DatabaseTestActivity.testUserConditionalQuery(): Boolean {
    return try {
        val userDao = UserDao("test_user_database")
        if (!DatabaseManager.isDatabaseInitialized("test_user_database")) {
            userDao.initTable()
            testUserBatch()
        }
        
        val users = userDao.findByAgeRange(25, 30)
        users.isNotEmpty()
    } catch (e: Exception) {
        false
    }
}

// 异步操作测试
fun DatabaseTestActivity.testCoroutineInsert(): Boolean {
    return try {
        runBlocking {
            val userDao = UserDao("test_user_database")
            if (!DatabaseManager.isDatabaseInitialized("test_user_database")) {
                userDao.initTable()
            }
            
            userDao.withDatabase { db ->
                val values = ContentValues().apply {
                    put("name", "协程用户")
                    put("age", 28)
                    put("email", "coroutine@example.com")
                    put("create_time", System.currentTimeMillis())
                }
                DatabaseQuery.insert(db, "user", values)
            }
        }
        true
    } catch (e: Exception) {
        false
    }
}

fun DatabaseTestActivity.testCoroutineQuery(): Boolean {
    return try {
        val result = runBlocking {
            val userDao = UserDao("test_user_database")
            if (!DatabaseManager.isDatabaseInitialized("test_user_database")) {
                userDao.initTable()
                testCoroutineInsert()
            }
            
            userDao.withDatabase { db ->
                val count = DatabaseQuery.count(db, "user")
                count > 0
            }
        }
        result
    } catch (e: Exception) {
        false
    }
}

fun DatabaseTestActivity.testCoroutineBatch(): Boolean {
    return try {
        runBlocking {
            val userDao = UserDao("test_user_database")
            if (!DatabaseManager.isDatabaseInitialized("test_user_database")) {
                userDao.initTable()
            }
            
            userDao.withDatabase { db ->
                DatabaseQuery.transaction(db) { database ->
                    repeat(5) { index ->
                        val values = ContentValues().apply {
                            put("name", "协程批量用户$index")
                            put("age", 20 + index)
                            put("email", "batch$index@example.com")
                            put("create_time", System.currentTimeMillis())
                        }
                        DatabaseQuery.insert(database, "user", values)
                    }
                }
            }
        }
        true
    } catch (e: Exception) {
        false
    }
}

