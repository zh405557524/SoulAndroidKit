# SoulAndroidKit

Android 的通用工具库，提供了一套完整的模块化架构，帮助开发者快速构建高质量的Android应用。

## 📋 项目概述

SoulAndroidKit 是一个基于现代Android开发最佳实践的工具库，采用模块化架构设计，支持 Jetpack Compose、Room数据库、协程等最新技术栈。

## 🏗️ 架构设计

项目采用多模块架构，每个模块负责特定的功能领域，具有高内聚、低耦合的特点：

```
soul-android-kit/
├─ settings.gradle.kts
├─ build.gradle.kts
├─ gradle/libs.versions.toml
├─ app/                                 # 壳应用，仅做组装与导航
│  └─ build.gradle.kts
├─ core/                                # 地基：跨业务的通用能力
│  ├─ common/                           # 扩展、结果封装、日志、协程工具等
│  ├─ ui/                               # 通用 UI（StatefulLayout/对话框/基类）
│  └─ designsystem/                     # 主题/颜色/排版/尺寸/动效
├─ feature/                             # 业务功能（api/impl 双模块）
│  ├─ poster/
│  │  ├─ api/
│  │  └─ impl/
│  └─ video/
│     ├─ editor/
│     │  ├─ api/
│     │  └─ impl/
│     └─ split/
│        ├─ api/
│        └─ impl/
├─ kit/                                 # 常用三方 & 系统能力
│  ├─ picker/                           # 媒体选择（系统 Photo Picker / 可换三方）
│  ├─ download/                         # DownloadManager 或替换为三方下载器
│  ├─ player-media3/                    # Media3/ExoPlayer 播放器门面
│  └─ image-glide/ (可选)               # Glide/Fresco 等图片加载门面
└─ integration/ (预留、可空)            # 重型/厂商 SDK（Azure/RTC/Billing 等）
   └─ azure/speech/ (暂不用先不 include)
```

## 📱 核心模块详解

### 🔧 Common (通用工具模块)
**路径**: `core/common`

提供项目中通用的工具类、扩展函数和基础设施代码。

**主要功能**:
- 常用工具函数
- Kotlin扩展函数
- 通用常量定义
- 基础异常处理
- 日志工具

**适用场景**:
- 字符串处理、日期格式化等通用操作
- 跨模块共享的工具函数
- 应用级别的配置常量

### 🗄️ Database (数据库模块)
**路径**: `core/database`

基于Room数据库的本地数据持久化解决方案。

**主要功能**:
- Room数据库配置
- Entity实体类定义
- DAO数据访问对象
- 数据库迁移策略
- 类型转换器

**技术栈**:
- Room Runtime: 数据库运行时
- Room KTX: Kotlin协程支持
- Room Compiler: 注解处理器

**适用场景**:
- 用户偏好设置存储
- 本地缓存数据管理
- 离线数据同步

### 🎨 DesignSystem (设计系统模块)
**路径**: `core/designsystem`

基于Material Design 3的设计系统，提供统一的UI组件和主题。

**主要功能**:
- 主题配置(颜色、字体、间距)
- 自定义Compose组件
- Material Design 3适配
- 深色模式支持
- 响应式布局组件

**技术栈**:
- Jetpack Compose
- Material Design 3
- Compose Material Icons Extended

**适用场景**:
- 统一应用视觉风格
- 可复用的UI组件库
- 主题切换功能

### 📺 Media (多媒体模块)
**路径**: `core/media`

提供完整的多媒体处理能力，包括图片、视频、相机功能。

**主要功能**:
- 图片加载和缓存
- 视频播放控制
- 相机拍照和录像
- 媒体文件管理
- 图片编辑和压缩

**技术栈**:
- ExoPlayer: 视频播放
- Glide: 图片加载
- CameraX: 相机功能

**适用场景**:
- 社交应用的媒体分享
- 视频播放器应用
- 图片编辑和处理

### 🌐 Network (网络请求模块)
**路径**: `core/network`

基于Retrofit和OkHttp的网络请求解决方案。

**主要功能**:
- RESTful API调用
- JSON数据序列化
- 网络请求拦截器
- 错误处理和重试
- 网络状态监控

**技术栈**:
- Retrofit: HTTP客户端
- OkHttp: 网络库
- Gson: JSON解析
- Kotlin Coroutines: 异步处理

**适用场景**:
- API数据获取
- 文件上传下载
- 实时数据同步

### 🖼️ UI (用户界面模块)
**路径**: `core/ui`

提供通用的UI组件和导航功能。

**主要功能**:
- 通用UI组件
- 页面导航管理
- ViewModel基础类
- 状态管理
- 生命周期处理

**技术栈**:
- Jetpack Compose
- Navigation Compose
- Lifecycle ViewModel Compose

**适用场景**:
- 页面间导航
- 状态管理
- 通用UI交互

## 🚀 快速开始

### 环境要求
- Android Studio Arctic Fox 或更高版本
- Kotlin 1.9+
- Android API 21+
- Java 11

### 集成步骤

1. **克隆项目**
```bash
git clone https://github.com/your-username/SoulAndroidKit.git
cd SoulAndroidKit
```

2. **打开项目**
在Android Studio中打开项目

3. **同步依赖**
等待Gradle同步完成

4. **运行应用**
选择设备或模拟器，点击运行按钮

## 📦 依赖管理

项目使用Gradle版本目录(`libs.versions.toml`)统一管理依赖版本：

### 主要依赖版本
- **Kotlin**: 2.0.21
- **Compose BOM**: 2024.09.00
- **Room**: 2.6.1
- **Retrofit**: 2.9.0
- **ExoPlayer**: 1.2.1

## 🔨 开发指南

### 添加新模块
1. 在`core/`目录下创建新模块
2. 在`settings.gradle.kts`中注册模块
3. 在`libs.versions.toml`中添加相关依赖
4. 更新本README文档

### 代码规范
- 遵循Kotlin官方编码规范
- 使用ktlint进行代码格式化
- 编写单元测试覆盖核心功能

## 📄 许可证

本项目采用 [MIT License](LICENSE) 许可证。

## 🤝 贡献指南

欢迎提交Issue和Pull Request来完善这个项目！

1. Fork本项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开Pull Request

## 📞 联系方式

如果您有任何问题或建议，请通过以下方式联系我们：

- 提交GitHub Issue
- 发送邮件到: your-email@example.com
