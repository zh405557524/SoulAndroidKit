#include <jni.h>
#include <android/log.h>
#include <unistd.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <errno.h>
#include <string.h>
#include <stdlib.h>
#include <pthread.h>
#include <sys/time.h>
#include "libavutil/log.h"
#include "libavformat/avformat.h"
#include "ffmpeg_executor.h"

#define LOG_TAG "FFmpegKitConfig"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

// 全局变量用于跟踪消息传输状态
static pthread_mutex_t message_mutex = PTHREAD_MUTEX_INITIALIZER;
static int pending_messages[1000] = {0}; // 支持最多1000个session
static int session_count = 0;

// 初始化FFmpeg和日志重定向
static int init_ffmpeg_once() {
    static int initialized = 0;
    if (!initialized) {
        // FFmpeg 6.0+ 不再需要 av_register_all()
        avformat_network_init();
        initialized = 1;
        LOGI("FFmpeg initialized successfully");
    }
    return 0;
}

// 获取session在数组中的索引
static int get_session_index(long session_id) {
    // 简单的哈希函数，实际项目中可能需要更复杂的映射
    return (int)(session_id % 1000);
}

// JNI方法：获取传输中的消息数量
JNIEXPORT jint JNICALL
Java_com_soul_ffmpegkit_FFmpegKitConfig_messagesInTransmit(JNIEnv *env, jclass clazz, jlong session_id) {
    pthread_mutex_lock(&message_mutex);
    
    int index = get_session_index(session_id);
    int count = pending_messages[index];
    
    pthread_mutex_unlock(&message_mutex);
    
    LOGD("Session %ld has %d messages in transmit", session_id, count);
    return (jint)count;
}

// JNI方法：注册新的FFmpeg命名管道
JNIEXPORT jint JNICALL
Java_com_soul_ffmpegkit_FFmpegKitConfig_registerNewNativeFFmpegPipe(JNIEnv *env, jclass clazz, jstring pipe_path) {
    if (pipe_path == NULL) {
        LOGE("Pipe path is null");
        return -1;
    }
    
    const char *path = (*env)->GetStringUTFChars(env, pipe_path, NULL);
    if (path == NULL) {
        LOGE("Failed to get pipe path string");
        return -1;
    }
    
    LOGI("Creating named pipe: %s", path);
    
    // 删除可能存在的旧管道
    if (access(path, F_OK) == 0) {
        if (unlink(path) != 0) {
            LOGE("Failed to remove existing pipe %s: %s", path, strerror(errno));
            (*env)->ReleaseStringUTFChars(env, pipe_path, path);
            return -1;
        }
    }
    
    // 创建命名管道
    if (mkfifo(path, 0666) != 0) {
        LOGE("Failed to create named pipe %s: %s", path, strerror(errno));
        (*env)->ReleaseStringUTFChars(env, pipe_path, path);
        return -1;
    }
    
    // 设置管道权限
    if (chmod(path, 0666) != 0) {
        LOGE("Failed to set pipe permissions %s: %s", path, strerror(errno));
        unlink(path); // 清理失败的管道
        (*env)->ReleaseStringUTFChars(env, pipe_path, path);
        return -1;
    }
    
    LOGI("Successfully created named pipe: %s", path);
    (*env)->ReleaseStringUTFChars(env, pipe_path, path);
    return 0;
}

// JNI方法：启用native重定向
JNIEXPORT void JNICALL
Java_com_soul_ffmpegkit_FFmpegKitConfig_enableNativeRedirection(JNIEnv *env, jclass clazz) {
    init_ffmpeg_once();
    
    // 设置FFmpeg日志回调
    av_log_set_level(AV_LOG_INFO);
    
    LOGI("Native redirection enabled");
}

// JNI方法：禁用native重定向
JNIEXPORT void JNICALL
Java_com_soul_ffmpegkit_FFmpegKitConfig_disableNativeRedirection(JNIEnv *env, jclass clazz) {
    // 恢复默认日志处理
    av_log_set_level(AV_LOG_QUIET);
    
    LOGI("Native redirection disabled");
}

// JNI方法：获取native日志级别
JNIEXPORT jint JNICALL
Java_com_soul_ffmpegkit_FFmpegKitConfig_getNativeLogLevel(JNIEnv *env, jclass clazz) {
    int level = av_log_get_level();
    LOGD("Current native log level: %d", level);
    return (jint)level;
}

// JNI方法：设置native日志级别
JNIEXPORT void JNICALL
Java_com_soul_ffmpegkit_FFmpegKitConfig_setNativeLogLevel(JNIEnv *env, jclass clazz, jint level) {
    av_log_set_level((int)level);
    LOGI("Native log level set to: %d", level);
}

// JNI方法：获取FFmpeg版本
JNIEXPORT jstring JNICALL
Java_com_soul_ffmpegkit_FFmpegKitConfig_getNativeFFmpegVersion(JNIEnv *env, jclass clazz) {
    init_ffmpeg_once();
    
    const char* version = av_version_info();
    LOGI("FFmpeg version: %s", version);
    
    return (*env)->NewStringUTF(env, version);
}

// JNI方法：获取FFmpegKit版本
JNIEXPORT jstring JNICALL
Java_com_soul_ffmpegkit_FFmpegKitConfig_getNativeVersion(JNIEnv *env, jclass clazz) {
    // 这里返回一个固定的版本号，实际项目中应该从构建系统获取
    const char* version = "6.0-soul-android";
    LOGI("FFmpegKit version: %s", version);
    
    return (*env)->NewStringUTF(env, version);
}

// JNI方法：获取构建日期
JNIEXPORT jstring JNICALL
Java_com_soul_ffmpegkit_FFmpegKitConfig_getNativeBuildDate(JNIEnv *env, jclass clazz) {
    // 这里返回编译时的日期，实际项目中应该从构建系统获取
    const char* build_date = __DATE__ " " __TIME__;
    LOGI("Build date: %s", build_date);
    
    return (*env)->NewStringUTF(env, build_date);
}

// JNI方法：设置环境变量
JNIEXPORT jint JNICALL
Java_com_soul_ffmpegkit_FFmpegKitConfig_setNativeEnvironmentVariable(JNIEnv *env, jclass clazz, jstring name, jstring value) {
    if (name == NULL || value == NULL) {
        LOGE("Environment variable name or value is null");
        return -1;
    }
    
    const char *var_name = (*env)->GetStringUTFChars(env, name, NULL);
    const char *var_value = (*env)->GetStringUTFChars(env, value, NULL);
    
    if (var_name == NULL || var_value == NULL) {
        LOGE("Failed to get environment variable strings");
        if (var_name) (*env)->ReleaseStringUTFChars(env, name, var_name);
        if (var_value) (*env)->ReleaseStringUTFChars(env, value, var_value);
        return -1;
    }
    
    int result = setenv(var_name, var_value, 1);
    if (result == 0) {
        LOGI("Environment variable set: %s=%s", var_name, var_value);
    } else {
        LOGE("Failed to set environment variable %s: %s", var_name, strerror(errno));
    }
    
    (*env)->ReleaseStringUTFChars(env, name, var_name);
    (*env)->ReleaseStringUTFChars(env, value, var_value);
    
    return result;
}

// JNI方法：忽略信号
JNIEXPORT void JNICALL
Java_com_soul_ffmpegkit_FFmpegKitConfig_ignoreNativeSignal(JNIEnv *env, jclass clazz, jint signal_num) {
    // 在Android中，信号处理比较受限，这里只是记录日志
    LOGI("Ignoring signal: %d", signal_num);
    // 实际的信号忽略逻辑需要根据具体需求实现
}

// JNI方法：执行FFmpeg命令
JNIEXPORT jint JNICALL
Java_com_soul_ffmpegkit_FFmpegKitConfig_nativeFFmpegExecute(JNIEnv *env, jclass clazz, jlong session_id, jobjectArray arguments) {
    if (arguments == NULL) {
        LOGE("Arguments array is null");
        return -1;
    }
    
    init_ffmpeg_once();
    
    // 更新消息传输计数
    pthread_mutex_lock(&message_mutex);
    int index = get_session_index(session_id);
    pending_messages[index] = 0; // 重置计数
    pthread_mutex_unlock(&message_mutex);
    
    // 使用执行器执行FFmpeg命令
    int result = ffmpeg_execute_command(env, session_id, arguments);
    
    LOGI("FFmpeg execution completed for session %ld with result: %d", session_id, result);
    
    return result;
}

// JNI方法：执行FFprobe命令
JNIEXPORT jint JNICALL
Java_com_soul_ffmpegkit_FFmpegKitConfig_nativeFFprobeExecute(JNIEnv *env, jclass clazz, jlong session_id, jobjectArray arguments) {
    if (arguments == NULL) {
        LOGE("Arguments array is null");
        return -1;
    }
    
    init_ffmpeg_once();
    
    // 更新消息传输计数
    pthread_mutex_lock(&message_mutex);
    int index = get_session_index(session_id);
    pending_messages[index] = 0; // 重置计数
    pthread_mutex_unlock(&message_mutex);
    
    // 使用执行器执行FFprobe命令
    int result = ffprobe_execute_command(env, session_id, arguments);
    
    LOGI("FFprobe execution completed for session %ld with result: %d", session_id, result);
    
    return result;
}

// JNI方法：取消FFmpeg操作
JNIEXPORT void JNICALL
Java_com_soul_ffmpegkit_FFmpegKitConfig_nativeFFmpegCancel(JNIEnv *env, jclass clazz, jlong session_id) {
    LOGI("Cancelling FFmpeg operation for session %ld", session_id);
    
    // 更新消息传输计数
    pthread_mutex_lock(&message_mutex);
    int index = get_session_index(session_id);
    pending_messages[index] = 0; // 清零计数
    pthread_mutex_unlock(&message_mutex);
    
    // 使用执行器取消操作
    ffmpeg_cancel_execution(session_id);
    
    LOGI("FFmpeg operation cancelled for session %ld", session_id);
} 