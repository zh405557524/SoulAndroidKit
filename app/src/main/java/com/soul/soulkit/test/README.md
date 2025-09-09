# SoulKit 功能测试系统

## 📚 概述

SoulKit功能测试系统是一个完整的Android应用测试框架，用于验证各个模块的功能是否正常工作。该系统支持手动点击测试和自动化批量测试。

## 🏗️ 系统架构

```
app/src/main/java/com/soul/soulkit/test/
├── TestMainActivity.kt          # 主测试入口
├── TestModuleAdapter.kt         # 模块列表适配器
├── BaseTestActivity.kt          # 基础测试Activity
├── common/
│   ├── CommonTestActivity.kt    # Common模块测试
│   └── CommonTestMethods.kt     # Common模块测试方法
└── database/
    └── DatabaseTestActivity.kt # Database模块测试(待完善)
```

## 🚀 快速开始

### 1. 启动测试
在Android设备或模拟器上安装应用后，您会看到两个启动图标：
- **Androidkit** - 主应用
- **功能测试** - 测试系统

点击"功能测试"启动测试系统。

### 2. 选择测试模块
主界面显示所有可用的测试模块：
- ✅ **Common模块测试** - 已完成，可立即测试
- ⏳ **Database模块测试** - 待开发
- ⏳ **其他模块** - 待开发

## 🔧 Common模块测试详解

Common模块测试包含 **12个分类，共48个测试项**：

### 📋 测试分类

1. **🔧 基础工具** (4项)
   - Global初始化测试
   - Global上下文测试  
   - Global主线程测试
   - Utils空值判断测试

2. **📝 字符串处理** (4项)
   - StringUtils空值测试
   - StringUtils比较测试
   - StringUtils反转测试
   - StringUtils全半角转换

3. **🔍 正则验证** (4项)
   - 手机号验证测试
   - 邮箱验证测试
   - 身份证验证测试
   - URL验证测试

4. **🔐 加密解密** (4项)
   - MD5加密测试
   - SHA1加密测试
   - Base64编码测试
   - AES加密测试

5. **📱 设备信息** (4项)
   - 设备ID获取测试
   - 设备型号测试
   - Root检测测试
   - 应用信息测试

6. **🌐 网络检测** (4项)
   - 网络连接状态测试
   - WiFi连接测试
   - 移动数据测试
   - 网络类型测试

7. **📂 文件操作** (4项)
   - 文件存在检测
   - 目录创建测试
   - 文件复制测试
   - 文件大小测试

8. **🕒 时间处理** (4项)
   - 当前时间获取
   - 时间戳转换
   - 时间差计算
   - 友好时间显示

9. **📐 尺寸转换** (4项)
   - dp转px测试
   - px转dp测试
   - sp转px测试
   - 屏幕尺寸测试

10. **💾 数据存储** (4项)
    - SP存储字符串
    - SP存储整数
    - SP存储布尔值
    - SP清除数据

11. **💬 用户交互** (4项)
    - Toast显示测试
    - 剪贴板复制测试
    - 剪贴板获取测试
    - 键盘显示隐藏

12. **📊 日志管理** (4项)
    - Debug日志测试
    - Info日志测试
    - Error日志测试
    - 文件日志测试

## 🎯 测试方式

### 手动测试
1. 点击任意测试按钮
2. 按钮颜色变化表示测试结果：
   - **灰色** - 未测试
   - **绿色** - 测试成功 ✅
   - **红色** - 测试失败 ❌

### 自动化测试
1. 点击 **🤖 开始自动测试** 按钮
2. 系统将自动依次执行所有测试项
3. 每个测试间隔500ms，便于观察
4. 测试过程中显示"自动测试进行中..."
5. 完成后恢复按钮状态

### 清除结果
点击 **🧹 清除结果** 按钮，将所有测试按钮重置为未测试状态。

## 📊 测试状态说明

| 颜色 | 状态 | 说明 |
|------|------|------|
| 灰色 | 未测试 | 初始状态或已清除 |
| 绿色 | 成功 | 功能正常工作 |
| 红色 | 失败 | 功能异常或错误 |

## 🛠️ 扩展开发

### 添加新的测试模块

1. **创建测试Activity**：
```kotlin
class NewModuleTestActivity : BaseTestActivity() {
    // 继承BaseTestActivity获得通用功能
}
```

2. **在TestMainActivity中注册**：
```kotlin
TestModule(
    name = "新模块测试",
    description = "新模块功能描述", 
    icon = "🎯",
    activityClass = NewModuleTestActivity::class.java
)
```

3. **在AndroidManifest.xml中声明**：
```xml
<activity
    android:name=".test.newmodule.NewModuleTestActivity"
    android:exported="false" />
```

### 添加新的测试项

参考`CommonTestMethods.kt`的模式：

```kotlin
fun YourTestActivity.testNewFeature(): Boolean {
    return try {
        // 测试逻辑
        val result = SomeUtils.someMethod()
        result.isNotEmpty() // 返回测试结果
    } catch (e: Exception) {
        false // 异常时返回失败
    }
}
```

## 🎨 UI特性

- **Material Design** 风格界面
- **卡片式布局** 清晰易读
- **图标分类** 直观识别
- **状态指示** 即时反馈
- **滚动支持** 适配长列表
- **响应式设计** 支持不同屏幕尺寸

## 📱 兼容性

- **最低API级别**: 21 (Android 5.0)
- **目标API级别**: 34 (Android 14)
- **支持架构**: ARM64, ARM, x86, x86_64

## 🔍 调试信息

测试失败时，错误信息会输出到Logcat：
```
E/TestError: [测试名称]: [错误详情]
```

使用以下命令查看：
```bash
adb logcat -s TestError
```

## 📞 技术支持

如需添加新的测试功能或遇到问题，请参考：
1. `BaseTestActivity.kt` - 基础功能实现
2. `CommonTestMethods.kt` - 测试方法示例
3. Common模块API文档 - 了解可测试的功能 