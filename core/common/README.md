# Common Module API 文档

## 📚 概述

Common模块是项目的核心工具库，提供了丰富的Android开发工具类，包括字符串处理、设备信息、网络检测、文件操作、加密解密、日志管理等功能。

## 🚀 快速开始

### 初始化
```kotlin
// Application中初始化
Global.init(this)
```

## 📖 API分类

### 🔧 基础工具类

#### Global - 全局管理器
| 方法 | 参数 | 返回值 | 说明 |
|-----|------|--------|------|
| `init(Application)` | Application | void | 初始化全局管理器 |
| `getContext()` | - | Context | 获取应用上下文 |
| `getMainThreadHandler()` | - | Handler | 获取主线程Handler |
| `isMainThread()` | - | boolean | 是否为主线程 |
| `runOnUiThread(Runnable)` | Runnable | void | 在主线程执行任务 |

#### Utils - 基础工具
| 方法 | 参数 | 返回值 | 说明 |
|-----|------|--------|------|
| `isEmpty(Object)` | Object | boolean | 判断对象是否为空 |
| `isNotEmpty(Object)` | Object | boolean | 判断对象是否不为空 |

### 📝 字符串处理

#### StringUtils - 字符串工具
| 方法 | 参数 | 返回值 | 说明 |
|-----|------|--------|------|
| `isEmpty(CharSequence)` | CharSequence | boolean | 字符串是否为空 |
| `isSpace(String)` | String | boolean | 是否为空格字符串 |
| `equals(CharSequence, CharSequence)` | CharSequence, CharSequence | boolean | 字符串比较 |
| `reverse(String)` | String | String | 反转字符串 |
| `toDBC(String)` | String | String | 转全角字符 |
| `toSBC(String)` | String | String | 转半角字符 |

#### RegexUtils - 正则验证工具
| 方法 | 参数 | 返回值 | 说明 |
|-----|------|--------|------|
| `isMobileSimple(CharSequence)` | CharSequence | boolean | 简单手机号验证 |
| `isMobileExact(CharSequence)` | CharSequence | boolean | 精确手机号验证 |
| `isTel(CharSequence)` | CharSequence | boolean | 电话号码验证 |
| `isIDCard15(CharSequence)` | CharSequence | boolean | 15位身份证验证 |
| `isIDCard18(CharSequence)` | CharSequence | boolean | 18位身份证验证 |
| `isEmail(CharSequence)` | CharSequence | boolean | 邮箱验证 |
| `isURL(CharSequence)` | CharSequence | boolean | URL验证 |
| `isIP(CharSequence)` | CharSequence | boolean | IP地址验证 |

### 🔐 加密解密

#### EncryptUtils - 加密工具
| 方法 | 参数 | 返回值 | 说明 |
|-----|------|--------|------|
| `encryptMD2ToString(String)` | String | String | MD2加密 |
| `encryptMD5ToString(String)` | String | String | MD5加密 |
| `encryptSHA1ToString(String)` | String | String | SHA1加密 |
| `encryptSHA224ToString(String)` | String | String | SHA224加密 |
| `encryptSHA256ToString(String)` | String | String | SHA256加密 |
| `encryptHmacMD5ToString(String, String)` | String, String | String | HmacMD5加密 |
| `encryptHmacSHA1ToString(String, String)` | String, String | String | HmacSHA1加密 |
| `encryptAES(byte[], byte[], String, byte[])` | byte[], byte[], String, byte[] | byte[] | AES加密 |
| `decryptAES(byte[], byte[], String, byte[])` | byte[], byte[], String, byte[] | byte[] | AES解密 |

#### EncodeUtils - 编码工具
| 方法 | 参数 | 返回值 | 说明 |
|-----|------|--------|------|
| `base64Encode(String)` | String | byte[] | Base64编码 |
| `base64Encode2String(String)` | String | String | Base64编码转字符串 |
| `base64Decode(String)` | String | byte[] | Base64解码 |
| `htmlEncode(CharSequence)` | CharSequence | String | HTML编码 |
| `htmlDecode(String)` | String | CharSequence | HTML解码 |
| `urlEncode(String)` | String | String | URL编码 |
| `urlDecode(String)` | String | String | URL解码 |

### 📱 设备信息

#### DeviceUtils - 设备工具
| 方法 | 参数 | 返回值 | 说明 |
|-----|------|--------|------|
| `isDeviceRooted()` | - | boolean | 设备是否已root |
| `getSDKVersionName()` | - | String | 获取SDK版本名 |
| `getSDKVersionCode()` | - | int | 获取SDK版本号 |
| `getAndroidID()` | - | String | 获取AndroidID |
| `getMacAddress()` | - | String | 获取MAC地址 |
| `getManufacturer()` | - | String | 获取设备制造商 |
| `getModel()` | - | String | 获取设备型号 |
| `getABIs()` | - | String[] | 获取CPU架构 |

#### AppUtils - 应用工具
| 方法 | 参数 | 返回值 | 说明 |
|-----|------|--------|------|
| `isAppInstalled(String)` | String | boolean | 应用是否已安装 |
| `installApp(String)` | String | boolean | 安装应用 |
| `uninstallApp(String)` | String | boolean | 卸载应用 |
| `isAppRoot()` | - | boolean | 应用是否有root权限 |
| `launchApp(String)` | String | void | 启动应用 |
| `exitApp()` | - | void | 退出应用 |
| `getAppPackageName()` | - | String | 获取应用包名 |
| `getAppName()` | - | String | 获取应用名称 |
| `getAppVersionName()` | - | String | 获取应用版本名 |
| `getAppVersionCode()` | - | int | 获取应用版本号 |

#### NetworkUtils - 网络工具
| 方法 | 参数 | 返回值 | 说明 |
|-----|------|--------|------|
| `openWirelessSettings()` | - | void | 打开网络设置 |
| `isConnected()` | - | boolean | 网络是否连接 |
| `isAvailable()` | - | boolean | 网络是否可用 |
| `isAvailableByPing()` | - | boolean | 通过ping检测网络 |
| `getMobileDataEnabled()` | - | boolean | 移动数据是否可用 |
| `is4G()` | - | boolean | 是否4G网络 |
| `getWifiEnabled()` | - | boolean | WiFi是否可用 |
| `isWifiConnected()` | - | boolean | 是否连接WiFi |
| `getNetworkOperatorName()` | - | String | 获取网络运营商名称 |
| `getNetworkType()` | - | int | 获取网络类型 |
| `getIPAddress(boolean)` | boolean | String | 获取IP地址 |

### 📂 文件操作

#### FileUtils - 文件工具
| 方法 | 参数 | 返回值 | 说明 |
|-----|------|--------|------|
| `getFileByPath(String)` | String | File | 根据路径获取文件 |
| `isFileExists(String)` | String | boolean | 文件是否存在 |
| `isDir(String)` | String | boolean | 是否为目录 |
| `isFile(String)` | String | boolean | 是否为文件 |
| `createOrExistsDir(String)` | String | boolean | 创建目录 |
| `createOrExistsFile(String)` | String | boolean | 创建文件 |
| `createFileByDeleteOldFile(String)` | String | boolean | 删除旧文件并创建新文件 |
| `copyDir(String, String)` | String, String | boolean | 复制目录 |
| `copyFile(String, String)` | String, String | boolean | 复制文件 |
| `moveDir(String, String)` | String, String | boolean | 移动目录 |
| `moveFile(String, String)` | String, String | boolean | 移动文件 |
| `deleteDir(String)` | String | boolean | 删除目录 |
| `deleteFile(String)` | String | boolean | 删除文件 |
| `listFilesInDir(String)` | String | List<File> | 获取目录下所有文件 |
| `getFileSize(String)` | String | long | 获取文件大小 |
| `getDirSize(String)` | String | long | 获取目录大小 |

#### CleanUtils - 清理工具
| 方法 | 参数 | 返回值 | 说明 |
|-----|------|--------|------|
| `cleanInternalCache()` | - | boolean | 清除内部缓存 |
| `cleanInternalFiles()` | - | boolean | 清除内部文件 |
| `cleanInternalDbs()` | - | boolean | 清除内部数据库 |
| `cleanInternalSP()` | - | boolean | 清除内部SharedPreferences |
| `cleanExternalCache()` | - | boolean | 清除外部缓存 |

#### ZipUtils - 压缩工具
| 方法 | 参数 | 返回值 | 说明 |
|-----|------|--------|------|
| `zipFiles(Collection<String>, String)` | Collection<String>, String | boolean | 压缩文件 |
| `zipFile(String, String)` | String, String | boolean | 压缩单个文件 |
| `unzipFile(String, String)` | String, String | List<File> | 解压文件 |
| `getFilesPath(String)` | String | List<String> | 获取压缩文件中的文件路径 |
| `getComments(String)` | String | String | 获取压缩文件注释 |

### 🕒 时间日期

#### TimeUtils - 时间工具
| 方法 | 参数 | 返回值 | 说明 |
|-----|------|--------|------|
| `millis2String(long)` | long | String | 时间戳转字符串 |
| `string2Millis(String)` | String | long | 字符串转时间戳 |
| `string2Date(String, DateFormat)` | String, DateFormat | Date | 字符串转Date |
| `date2String(Date, DateFormat)` | Date, DateFormat | String | Date转字符串 |
| `date2Millis(Date)` | Date | long | Date转时间戳 |
| `millis2Date(long)` | long | Date | 时间戳转Date |
| `getTimeSpan(String, String, int)` | String, String, int | long | 获取时间差 |
| `getFitTimeSpan(String, String, int)` | String, String, int | String | 获取合适的时间差 |
| `getNowMills()` | - | long | 获取当前时间戳 |
| `getNowString()` | - | String | 获取当前时间字符串 |
| `getNowDate()` | - | Date | 获取当前时间Date |
| `getTimeSpanByNow(String, int)` | String, int | long | 获取与当前时间的时间差 |
| `getFitTimeSpanByNow(String, int)` | String, int | String | 获取与当前时间的合适时间差 |
| `getFriendlyTimeSpanByNow(String)` | String | String | 获取友好的时间差描述 |
| `isToday(String)` | String | boolean | 是否为今天 |
| `isLeapYear(int)` | int | boolean | 是否为闰年 |
| `getChineseWeek(String)` | String | String | 获取中文星期 |
| `getUSWeek(String)` | String | String | 获取美式星期 |

#### DateUtils - 简单日期工具
| 方法 | 参数 | 返回值 | 说明 |
|-----|------|--------|------|
| `getCurrentTime()` | - | String | 获取当前时间 |
| `timeStamp2Date(String, String)` | String, String | String | 时间戳转日期 |
| `date2TimeStamp(String, String)` | String, String | String | 日期转时间戳 |

### 📐 尺寸转换

#### SizeUtils - 尺寸工具
| 方法 | 参数 | 返回值 | 说明 |
|-----|------|--------|------|
| `dp2px(float)` | float | int | dp转px |
| `px2dp(float)` | float | int | px转dp |
| `sp2px(float)` | float | int | sp转px |
| `px2sp(float)` | float | int | px转sp |

#### ConvertUtils - 转换工具
| 方法 | 参数 | 返回值 | 说明 |
|-----|------|--------|------|
| `bytes2HexString(byte[])` | byte[] | String | 字节数组转16进制字符串 |
| `hexString2Bytes(String)` | String | byte[] | 16进制字符串转字节数组 |
| `chars2Bytes(char[])` | char[] | byte[] | 字符数组转字节数组 |
| `bytes2Chars(byte[])` | byte[] | char[] | 字节数组转字符数组 |
| `byte2FitMemorySize(long)` | long | String | 字节转合适内存大小 |
| `byte2MemorySize(long, int)` | long, int | double | 字节转指定内存大小 |
| `input2OutputStream(InputStream)` | InputStream | ByteArrayOutputStream | 输入流转输出流 |
| `output2InputStream(OutputStream)` | OutputStream | ByteArrayInputStream | 输出流转输入流 |

### 🎨 界面工具

#### BarUtils - 状态栏导航栏工具
| 方法 | 参数 | 返回值 | 说明 |
|-----|------|--------|------|
| `getStatusBarHeight()` | - | int | 获取状态栏高度 |
| `setStatusBarVisibility(Activity, boolean)` | Activity, boolean | void | 设置状态栏可见性 |
| `isStatusBarVisible(Activity)` | Activity | boolean | 状态栏是否可见 |
| `setStatusBarLightMode(Activity, boolean)` | Activity, boolean | boolean | 设置状态栏亮色模式 |
| `setStatusBarColor(Activity, int)` | Activity, int | void | 设置状态栏颜色 |
| `setStatusBarColor(Activity, int, int)` | Activity, int, int | void | 设置状态栏颜色和透明度 |
| `addMarginTopEqualStatusBarHeight(View)` | View | void | 为View添加状态栏高度的上边距 |
| `subtractMarginTopEqualStatusBarHeight(View)` | View | void | 为View减去状态栏高度的上边距 |
| `setStatusBarLightMode(Fragment, boolean)` | Fragment, boolean | boolean | 设置Fragment状态栏亮色模式 |

#### ScreenUtils - 屏幕工具
| 方法 | 参数 | 返回值 | 说明 |
|-----|------|--------|------|
| `getScreenWidth()` | - | int | 获取屏幕宽度 |
| `getScreenHeight()` | - | int | 获取屏幕高度 |
| `getScreenDensity()` | - | float | 获取屏幕密度 |
| `getScreenDensityDpi()` | - | int | 获取屏幕密度DPI |
| `setFullScreen(Activity)` | Activity | void | 设置全屏 |
| `setLandscape(Activity)` | Activity | void | 设置横屏 |
| `setPortrait(Activity)` | Activity | void | 设置竖屏 |
| `isLandscape()` | - | boolean | 是否横屏 |
| `isPortrait()` | - | boolean | 是否竖屏 |
| `getScreenRotation(Activity)` | Activity | int | 获取屏幕旋转角度 |
| `screenShot(Activity)` | Activity | Bitmap | 截屏 |
| `isScreenLock()` | - | boolean | 屏幕是否锁定 |

#### KeyboardUtils - 键盘工具
| 方法 | 参数 | 返回值 | 说明 |
|-----|------|--------|------|
| `showSoftInput(View)` | View | void | 显示软键盘 |
| `showSoftInput(Activity)` | Activity | void | 显示软键盘 |
| `hideSoftInput(View)` | View | void | 隐藏软键盘 |
| `hideSoftInput(Activity)` | Activity | void | 隐藏软键盘 |
| `toggleSoftInput()` | - | void | 切换软键盘显示状态 |
| `isSoftInputVisible(Activity)` | Activity | boolean | 软键盘是否显示 |

### 💬 用户交互

#### ToastUtils - Toast工具
| 方法 | 参数 | 返回值 | 说明 |
|-----|------|--------|------|
| `showShort(CharSequence)` | CharSequence | void | 显示短时Toast |
| `showShort(int)` | int | void | 显示短时Toast |
| `showLong(CharSequence)` | CharSequence | void | 显示长时Toast |
| `showLong(int)` | int | void | 显示长时Toast |
| `showShortSafe(CharSequence)` | CharSequence | void | 安全显示短时Toast |
| `showShortSafe(int)` | int | void | 安全显示短时Toast |
| `showLongSafe(CharSequence)` | CharSequence | void | 安全显示长时Toast |
| `showLongSafe(int)` | int | void | 安全显示长时Toast |
| `show(CharSequence, int)` | CharSequence, int | void | 显示指定时长Toast |
| `cancel()` | - | void | 取消Toast |

#### SnackbarUtils - Snackbar工具
| 方法 | 参数 | 返回值 | 说明 |
|-----|------|--------|------|
| `with(View)` | View | SnackbarUtils | 创建Snackbar |
| `setMessage(CharSequence)` | CharSequence | SnackbarUtils | 设置消息 |
| `setMessage(int)` | int | SnackbarUtils | 设置消息 |
| `setDuration(int)` | int | SnackbarUtils | 设置持续时间 |
| `setTextColor(int)` | int | SnackbarUtils | 设置文字颜色 |
| `setBgColor(int)` | int | SnackbarUtils | 设置背景颜色 |
| `setAction(CharSequence, View.OnClickListener)` | CharSequence, View.OnClickListener | SnackbarUtils | 设置动作 |
| `setBottomMargin(int)` | int | SnackbarUtils | 设置底部边距 |
| `show()` | - | void | 显示Snackbar |

#### ClipboardUtils - 剪贴板工具
| 方法 | 参数 | 返回值 | 说明 |
|-----|------|--------|------|
| `copyText(CharSequence)` | CharSequence | void | 复制文本到剪贴板 |
| `getText()` | - | CharSequence | 获取剪贴板文本 |
| `copyUri(Uri)` | Uri | void | 复制Uri到剪贴板 |
| `getUri()` | - | Uri | 获取剪贴板Uri |
| `copyIntent(Intent)` | Intent | void | 复制Intent到剪贴板 |
| `getIntent()` | - | Intent | 获取剪贴板Intent |

### 💾 数据存储

#### SPUtils - SharedPreferences工具
| 方法 | 参数 | 返回值 | 说明 |
|-----|------|--------|------|
| `getInstance()` | - | SPUtils | 获取实例 |
| `getInstance(String)` | String | SPUtils | 获取指定名称的实例 |
| `put(String, Object)` | String, Object | void | 存储数据 |
| `getString(String)` | String | String | 获取字符串 |
| `getString(String, String)` | String, String | String | 获取字符串（带默认值） |
| `getInt(String)` | String | int | 获取整数 |
| `getInt(String, int)` | String, int | int | 获取整数（带默认值） |
| `getLong(String)` | String | long | 获取长整数 |
| `getLong(String, long)` | String, long | long | 获取长整数（带默认值） |
| `getFloat(String)` | String | float | 获取浮点数 |
| `getFloat(String, float)` | String, float | float | 获取浮点数（带默认值） |
| `getBoolean(String)` | String | boolean | 获取布尔值 |
| `getBoolean(String, boolean)` | String, boolean | boolean | 获取布尔值（带默认值） |
| `getStringSet(String)` | String | Set<String> | 获取字符串集合 |
| `getAll()` | - | Map<String, ?> | 获取所有数据 |
| `contains(String)` | String | boolean | 是否包含指定key |
| `remove(String)` | String | void | 移除指定key |
| `clear()` | - | void | 清除所有数据 |

### 🔗 系统交互

#### IntentUtils - Intent工具
| 方法 | 参数 | 返回值 | 说明 |
|-----|------|--------|------|
| `getInstallAppIntent(String)` | String | Intent | 获取安装App的Intent |
| `getUninstallAppIntent(String)` | String | Intent | 获取卸载App的Intent |
| `getLaunchAppIntent(String)` | String | Intent | 获取打开App的Intent |
| `getAppDetailsSettingsIntent(String)` | String | Intent | 获取App具体设置的Intent |
| `getShareTextIntent(String)` | String | Intent | 获取分享文本的Intent |
| `getShareImageIntent(String)` | String | Intent | 获取分享图片的Intent |
| `getComponentIntent(String, String)` | String, String | Intent | 获取其他应用组件的Intent |
| `getShutdownIntent()` | - | Intent | 获取关机的Intent |
| `getDialIntent(String)` | String | Intent | 获取拨号的Intent |
| `getCallIntent(String)` | String | Intent | 获取通话的Intent |
| `getSendSmsIntent(String, String)` | String, String | Intent | 获取发送短信的Intent |
| `getCaptureIntent(Uri)` | Uri | Intent | 获取拍照的Intent |

#### PhoneUtils - 手机工具
| 方法 | 参数 | 返回值 | 说明 |
|-----|------|--------|------|
| `isPhone()` | - | boolean | 判断设备是否为手机 |
| `getDeviceId()` | - | String | 获取设备IMEI码 |
| `getSerial()` | - | String | 获取设备序列号 |
| `getIMSI()` | - | String | 获取IMSI码 |
| `getSimOperatorName()` | - | String | 获取Sim卡运营商名称 |
| `getSimOperatorByMnc()` | - | String | 根据MNC获取Sim卡运营商名称 |
| `getPhoneStatus()` | - | PhoneUtils.PhoneInfo | 获取手机状态信息 |
| `dial(String)` | String | void | 跳至拨号界面 |
| `call(String)` | String | void | 拨打电话 |
| `sendSms(String, String)` | String, String | void | 跳至发送短信界面 |
| `sendSmsSilent(String, String)` | String, String | void | 发送短信 |

#### ServiceUtils - 服务工具
| 方法 | 参数 | 返回值 | 说明 |
|-----|------|--------|------|
| `getAllRunningServices()` | - | Set<String> | 获取所有运行的服务 |
| `startService(String)` | String | boolean | 启动服务 |
| `stopService(String)` | String | boolean | 停止服务 |
| `bindService(String, ServiceConnection, int)` | String, ServiceConnection, int | boolean | 绑定服务 |
| `unbindService(ServiceConnection)` | ServiceConnection | void | 解绑服务 |
| `isServiceRunning(String)` | String | boolean | 服务是否运行 |

### 📊 日志管理

#### LogUtils - 日志工具
| 方法 | 参数 | 返回值 | 说明 |
|-----|------|--------|------|
| `v(Object...)` | Object... | void | Verbose日志 |
| `vTag(String, Object...)` | String, Object... | void | 带标签的Verbose日志 |
| `d(Object...)` | Object... | void | Debug日志 |
| `dTag(String, Object...)` | String, Object... | void | 带标签的Debug日志 |
| `i(Object...)` | Object... | void | Info日志 |
| `iTag(String, Object...)` | String, Object... | void | 带标签的Info日志 |
| `w(Object...)` | Object... | void | Warn日志 |
| `wTag(String, Object...)` | String, Object... | void | 带标签的Warn日志 |
| `e(Object...)` | Object... | void | Error日志 |
| `eTag(String, Object...)` | String, Object... | void | 带标签的Error日志 |
| `a(Object...)` | Object... | void | Assert日志 |
| `aTag(String, Object...)` | String, Object... | void | 带标签的Assert日志 |
| `file(Object...)` | Object... | void | 文件日志 |
| `json(String)` | String | void | JSON日志 |
| `xml(String)` | String | void | XML日志 |

#### LogUtil - 简单日志工具
| 方法 | 参数 | 返回值 | 说明 |
|-----|------|--------|------|
| `v(String, String)` | String, String | void | Verbose日志 |
| `d(String, String)` | String, String | void | Debug日志 |
| `i(String, String)` | String, String | void | Info日志 |
| `w(String, String)` | String, String | void | Warn日志 |
| `e(String, String)` | String, String | void | Error日志 |

### 🛠 其他工具

#### ProcessUtils - 进程工具
| 方法 | 参数 | 返回值 | 说明 |
|-----|------|--------|------|
| `getForegroundProcessName()` | - | String | 获取前台进程名 |
| `getAllBackgroundProcesses()` | - | Set<String> | 获取所有后台进程 |
| `killAllBackgroundProcesses()` | - | Set<String> | 杀死所有后台进程 |
| `killBackgroundProcesses(String)` | String | boolean | 杀死后台进程 |
| `isMainProcess()` | - | boolean | 是否为主进程 |
| `getCurrentProcessName()` | - | String | 获取当前进程名 |

#### PermissionsUtils - 权限工具
| 方法 | 参数 | 返回值 | 说明 |
|-----|------|--------|------|
| `getPermissions()` | - | List<String> | 获取应用权限 |
| `getPermissions(String)` | String | List<String> | 获取指定应用权限 |
| `isGranted(String...)` | String... | boolean | 权限是否授予 |
| `launchAppDetailsSettings()` | - | void | 打开应用详情页 |
| `permission2Desc(String)` | String | String | 权限转描述 |

#### VibrationUtils - 震动工具
| 方法 | 参数 | 返回值 | 说明 |
|-----|------|--------|------|
| `vibrate(long)` | long | void | 震动指定时长 |
| `vibrate(long[], int)` | long[], int | void | 指定模式震动 |
| `cancel()` | - | void | 取消震动 |

#### CameraUtils - 相机工具
| 方法 | 参数 | 返回值 | 说明 |
|-----|------|--------|------|
| `takePicture(Fragment, int)` | Fragment, int | void | 拍照 |
| `takePicture(Activity, int)` | Activity, int | void | 拍照 |

## 📝 使用示例

```java
// 初始化
Global.init(this);

// 字符串操作
String text = "Hello World";
boolean isEmpty = StringUtils.isEmpty(text);
String reversed = StringUtils.reverse(text);

// 设备信息
String deviceId = DeviceUtils.getAndroidID();
String manufacturer = DeviceUtils.getManufacturer();
boolean isRooted = DeviceUtils.isDeviceRooted();

// 网络检测
boolean isConnected = NetworkUtils.isConnected();
boolean isWifi = NetworkUtils.isWifiConnected();
String networkType = NetworkUtils.getNetworkOperatorName();

// 文件操作
boolean dirExists = FileUtils.createOrExistsDir("/sdcard/test");
boolean fileCopied = FileUtils.copyFile("/sdcard/source.txt", "/sdcard/dest.txt");
long fileSize = FileUtils.getFileSize("/sdcard/test.txt");

// 加密解密
String md5 = EncryptUtils.encryptMD5ToString("password");
String sha1 = EncryptUtils.encryptSHA1ToString("password");
String base64 = EncodeUtils.base64Encode2String("hello");

// Toast显示
ToastUtils.showShort("Hello");
ToastUtils.showLongSafe("Safe Toast");

// 数据存储
SPUtils.getInstance().put("key", "value");
String value = SPUtils.getInstance().getString("key");

// 尺寸转换
int px = SizeUtils.dp2px(16);
int dp = SizeUtils.px2dp(64);

// 时间处理
String timeStr = TimeUtils.getNowString();
boolean isToday = TimeUtils.isToday("2024-01-01 10:00:00");
String friendlyTime = TimeUtils.getFriendlyTimeSpanByNow("2024-01-01 10:00:00");

// 正则验证
boolean isMobile = RegexUtils.isMobileSimple("13800138000");
boolean isEmail = RegexUtils.isEmail("test@email.com");

// 日志输出
LogUtils.d("Debug message");
LogUtils.i("Info message");
LogUtils.e("Error message");
```

## 📞 注意事项

1. **权限要求**：部分功能需要相应的Android权限
2. **API兼容性**：支持Android API 21+
3. **线程安全**：大部分工具类都是线程安全的
4. **初始化**：使用前需要在Application中调用`Global.init(this)`
5. **异常处理**：工具类内部已做异常处理，返回合理的默认值
