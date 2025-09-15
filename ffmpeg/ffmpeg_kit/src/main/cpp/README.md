# FFmpegKit JNI 实现

这个目录包含了 FFmpegKitConfig 类的 JNI 方法的 C 层实现。

## 文件结构

```
cpp/
├── src/
│   ├── ffmpeg_kit_config.c      # FFmpegKitConfig JNI方法实现
│   ├── ffmpeg_executor.c        # FFmpeg执行器实现
│   ├── ffmpeg_player.c          # 现有的FFmpeg播放器实现
│   ├── test_config.c            # 测试配置文件
│   ├── android_lts_support.c    # Android LTS支持
│   └── config.h                 # 配置头文件
├── include/
│   ├── ffmpeg_kit_config.h      # FFmpegKitConfig头文件声明
│   ├── ffmpeg_executor.h        # FFmpeg执行器头文件
│   └── ffmpeg/                  # FFmpeg头文件目录
└── README.md                    # 本文件
```

## 实现的JNI方法

### 1. 消息传输相关
- `messagesInTransmit(long sessionId)` - 获取传输中的消息数量

### 2. 命名管道相关  
- `registerNewNativeFFmpegPipe(String pipePath)` - 创建新的FFmpeg命名管道

### 3. 重定向控制
- `enableNativeRedirection()` - 启用native重定向
- `disableNativeRedirection()` - 禁用native重定向

### 4. 日志级别管理
- `getNativeLogLevel()` - 获取当前日志级别
- `setNativeLogLevel(int level)` - 设置日志级别

### 5. 版本信息
- `getNativeFFmpegVersion()` - 获取FFmpeg版本
- `getNativeVersion()` - 获取FFmpegKit版本  
- `getNativeBuildDate()` - 获取构建日期

### 6. 环境变量
- `setNativeEnvironmentVariable(String name, String value)` - 设置环境变量

### 7. 信号处理
- `ignoreNativeSignal(int signalNum)` - 忽略指定信号

### 8. 执行方法
- `nativeFFmpegExecute(long sessionId, String[] arguments)` - 执行FFmpeg命令
- `nativeFFprobeExecute(long sessionId, String[] arguments)` - 执行FFprobe命令  
- `nativeFFmpegCancel(long sessionId)` - 取消FFmpeg操作

## 功能特性

### 线程安全
- 使用 pthread 互斥锁保护共享资源
- 每个 session 都有独立的状态管理

### 内存管理
- 正确的 JNI 字符串资源释放
- 动态内存分配和释放管理

### 错误处理
- 完整的错误检查和日志记录
- 优雅的失败处理

### 日志系统
- 集成 Android Log 系统
- 支持不同级别的日志输出
- FFmpeg 日志回调集成

## 编译配置

项目使用 CMake 构建系统，配置文件为根目录的 `CMakeLists.txt`。

### 依赖库
- FFmpeg 预编译库 (libavcodec, libavformat, libavutil, 等)
- Android NDK 标准库
- pthread 线程库

### 编译选项
- C11 标准
- C++17 标准  
- 函数和数据段分离优化
- 隐藏符号可见性

## 使用示例

### Java 层调用
```java
// 获取FFmpeg版本
String version = FFmpegKitConfig.getFFmpegVersion();

// 创建命名管道
String pipePath = "/data/data/com.example.app/cache/pipes/pipe_001";
int result = FFmpegKitConfig.registerNewFFmpegPipe(context);

// 执行FFmpeg命令
String[] args = {"-i", "input.mp4", "-c:v", "h264", "output.mp4"};
FFmpegSession session = FFmpegKit.execute(args);
```

### 测试方法
项目包含了测试配置文件 `test_config.c`，可以验证各项功能：
- 基本功能测试
- 管道创建测试  
- 环境变量设置测试
- FFmpeg 执行测试

## 注意事项

1. **权限要求**: 创建命名管道需要相应的文件系统权限
2. **路径限制**: 管道路径应该在应用的私有目录下
3. **内存限制**: Session 数量限制为 1000 个
4. **线程安全**: 所有方法都是线程安全的
5. **资源清理**: 确保及时清理不再使用的 session

## 开发说明

### 添加新的JNI方法
1. 在 `ffmpeg_kit_config.h` 中声明方法
2. 在 `ffmpeg_kit_config.c` 中实现方法
3. 更新 Java 层的 native 方法声明
4. 重新编译项目

### 调试技巧
- 使用 `adb logcat` 查看日志输出
- 在 native 代码中添加 `LOGI/LOGE` 日志
- 使用 `gdb` 进行断点调试

## 版本信息

- 当前版本: 6.0-soul-android
- FFmpeg 版本: 6.0+
- 最小 Android API: 21 (Android 5.0)
- NDK 版本: r21+ 