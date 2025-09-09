# Video 播放器模块

## 概述

这是一个基于 ExoPlayer 的视频播放器模块，提供完整的视频播放功能和简洁的 API 接口。

## 功能特性

- ✅ 支持多种视频格式（MP4、WebM、MKV 等）
- ✅ 网络视频和本地视频播放
- ✅ 播放控制（播放、暂停、停止、进度跳转）
- ✅ 进度条拖拽控制
- ✅ 播放状态和进度监听
- ✅ 错误处理和显示
- ✅ 传统 XML 布局 + Fragment 架构
- ✅ MVVM 架构模式

## 核心组件

### 1. 接口定义
- `IVideoPlayer` - 视频播放器核心接口
- `OnPlaybackStateListener` - 播放状态监听
- `OnProgressListener` - 播放进度监听

### 2. 实现类
- `VideoPlayerImpl` - ExoPlayer 实现类
- `VideoPlayerViewModel` - ViewModel 管理播放状态
- `VideoPlayerFragment` - UI Fragment 组件

### 3. 管理器
- `VideoPlayerManager` - 单例模式，外部调用统一入口，实现 IVideoPlayer 接口

## 快速使用

### 推荐方式：使用 VideoPlayerManager（单例）

```kotlin
// 1. 获取管理器实例
val videoPlayerManager = VideoPlayerManager.getInstance()

// 2. 绑定到容器
videoPlayerManager.attach(supportFragmentManager, binding.videoContainer)

// 3. 设置监听
videoPlayerManager.setOnPlaybackStateListener(object : IVideoPlayer.OnPlaybackStateListener {
    override fun onPlaybackStateChanged(isPlaying: Boolean) {
        // 播放状态变化
    }
    
    override fun onReady() {
        // 准备完成
    }
    
    override fun onError(error: String) {
        // 错误处理
    }
    
    override fun onPlaybackCompleted() {
        // 播放完成
    }
})

videoPlayerManager.setOnProgressListener(object : IVideoPlayer.OnProgressListener {
    override fun onProgressUpdate(currentPosition: Long, duration: Long) {
        // 播放进度变化
    }
})

// 4. 初始化并播放视频
videoPlayerManager.initialize("https://example.com/video.mp4")
videoPlayerManager.play()
```

### 方式二：直接使用播放器实现

```kotlin
// 1. 创建播放器
val videoPlayer = VideoPlayerManager.createVideoPlayer(this)

// 2. 设置监听
videoPlayer.setOnPlaybackStateListener(object : IVideoPlayer.OnPlaybackStateListener {
    override fun onPlaybackStateChanged(isPlaying: Boolean) {
        // 播放状态变化
    }
    
    override fun onReady() {
        // 准备完成
    }
    
    override fun onError(error: String) {
        // 播放错误
    }
    
    override fun onPlaybackCompleted() {
        // 播放完成
    }
})

// 3. 初始化并播放
videoPlayer.initialize("https://example.com/video.mp4")
videoPlayer.play()
```

## API 文档

### VideoPlayerManager 主要方法

```kotlin
// 绑定管理
fun attach(fragmentManager: FragmentManager, container: FrameLayout)
fun unbind()
fun isAttached(): Boolean

// 视频控制 (实现 IVideoPlayer 接口)
fun initialize(videoUrl: String)
fun play()
fun pause()
fun stop()
fun seekTo(position: Long)

// 状态获取
fun getCurrentPosition(): Long
fun getDuration(): Long
fun isPlaying(): Boolean

// 监听设置
fun setOnPlaybackStateListener(listener: OnPlaybackStateListener?)
fun setOnProgressListener(listener: OnProgressListener?)

// 界面控制
fun setControlsVisible(visible: Boolean)

// 资源管理
fun release()
```

### IVideoPlayer 接口方法

```kotlin
// 播放控制
fun initialize(videoUrl: String)
fun play()
fun pause()
fun stop()
fun seekTo(position: Long)

// 状态获取
fun getCurrentPosition(): Long
fun getDuration(): Long
fun isPlaying(): Boolean

// 资源管理
fun release()

// 监听设置
fun setOnPlaybackStateListener(listener: OnPlaybackStateListener?)
fun setOnProgressListener(listener: OnProgressListener?)
```

## 权限要求

模块会自动申请以下权限：

```xml
<!-- 网络权限 -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

<!-- 存储权限 -->
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_MEDIA_VIDEO" />

<!-- 唤醒锁权限 -->
<uses-permission android:name="android.permission.WAKE_LOCK" />
```

## 依赖配置

在使用模块的 `build.gradle.kts` 中添加：

```kotlin
implementation(project(":feature:video"))
```

## 注意事项

1. **生命周期管理**：Fragment 会自动管理播放器生命周期，Activity 销毁时会自动释放资源。

2. **错误处理**：建议设置错误监听器，处理网络异常、格式不支持等情况。

3. **权限处理**：播放本地文件需要存储权限，建议使用运行时权限申请。

4. **性能优化**：避免同时创建多个播放器实例，及时释放不用的播放器。

## 示例项目

参考 `app` 模块中的 `VideoTestActivity` 获取完整使用示例。 