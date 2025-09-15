#include <jni.h>
#include <android/log.h>
#include "ffmpeg_kit_config.h"
#include "ffmpeg_executor.h"

#define LOG_TAG "TestConfig"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

// 测试方法：验证基本功能
JNIEXPORT jstring JNICALL
Java_com_soul_ffmpegkit_TestConfig_testBasicFunctions(JNIEnv *env, jobject thiz) {
    LOGI("开始测试基本功能");
    
    // 测试获取FFmpeg版本
    jstring version = Java_com_soul_ffmpegkit_FFmpegKitConfig_getNativeFFmpegVersion(env, NULL);
    const char* version_str = (*env)->GetStringUTFChars(env, version, NULL);
    LOGI("FFmpeg版本: %s", version_str);
    (*env)->ReleaseStringUTFChars(env, version, version_str);
    
    // 测试获取日志级别
    jint log_level = Java_com_soul_ffmpegkit_FFmpegKitConfig_getNativeLogLevel(env, NULL);
    LOGI("当前日志级别: %d", log_level);
    
    // 测试设置日志级别
    Java_com_soul_ffmpegkit_FFmpegKitConfig_setNativeLogLevel(env, NULL, 32); // AV_LOG_INFO
    LOGI("日志级别已设置为INFO");
    
    // 测试启用重定向
    Java_com_soul_ffmpegkit_FFmpegKitConfig_enableNativeRedirection(env, NULL);
    LOGI("已启用native重定向");
    
    // 测试消息传输计数
    jint messages = Java_com_soul_ffmpegkit_FFmpegKitConfig_messagesInTransmit(env, NULL, 12345);
    LOGI("Session 12345 传输中的消息数: %d", messages);
    
    return (*env)->NewStringUTF(env, "基本功能测试完成");
}

// 测试方法：验证管道创建
JNIEXPORT jstring JNICALL
Java_com_soul_ffmpegkit_TestConfig_testPipeCreation(JNIEnv *env, jobject thiz, jstring pipe_path) {
    if (pipe_path == NULL) {
        LOGE("管道路径为空");
        return (*env)->NewStringUTF(env, "管道路径为空");
    }
    
    LOGI("开始测试管道创建");
    
    // 测试创建命名管道
    jint result = Java_com_soul_ffmpegkit_FFmpegKitConfig_registerNewNativeFFmpegPipe(env, NULL, pipe_path);
    
    if (result == 0) {
        LOGI("管道创建成功");
        return (*env)->NewStringUTF(env, "管道创建成功");
    } else {
        LOGE("管道创建失败，错误码: %d", result);
        return (*env)->NewStringUTF(env, "管道创建失败");
    }
}

// 测试方法：验证环境变量设置
JNIEXPORT jstring JNICALL
Java_com_soul_ffmpegkit_TestConfig_testEnvironmentVariable(JNIEnv *env, jobject thiz) {
    LOGI("开始测试环境变量设置");
    
    jstring var_name = (*env)->NewStringUTF(env, "FFMPEG_TEST_VAR");
    jstring var_value = (*env)->NewStringUTF(env, "test_value_123");
    
    jint result = Java_com_soul_ffmpegkit_FFmpegKitConfig_setNativeEnvironmentVariable(env, NULL, var_name, var_value);
    
    (*env)->DeleteLocalRef(env, var_name);
    (*env)->DeleteLocalRef(env, var_value);
    
    if (result == 0) {
        LOGI("环境变量设置成功");
        return (*env)->NewStringUTF(env, "环境变量设置成功");
    } else {
        LOGE("环境变量设置失败，错误码: %d", result);
        return (*env)->NewStringUTF(env, "环境变量设置失败");
    }
}

// 测试方法：模拟FFmpeg执行
JNIEXPORT jstring JNICALL
Java_com_soul_ffmpegkit_TestConfig_testFFmpegExecution(JNIEnv *env, jobject thiz) {
    LOGI("开始测试FFmpeg执行");
    
    // 创建测试参数数组
    jobjectArray args = (*env)->NewObjectArray(env, 3, (*env)->FindClass(env, "java/lang/String"), NULL);
    
    jstring arg1 = (*env)->NewStringUTF(env, "ffmpeg");
    jstring arg2 = (*env)->NewStringUTF(env, "-version");
    jstring arg3 = (*env)->NewStringUTF(env, "-f");
    
    (*env)->SetObjectArrayElement(env, args, 0, arg1);
    (*env)->SetObjectArrayElement(env, args, 1, arg2);
    (*env)->SetObjectArrayElement(env, args, 2, arg3);
    
    // 执行FFmpeg命令
    jlong session_id = 98765;
    jint result = Java_com_soul_ffmpegkit_FFmpegKitConfig_nativeFFmpegExecute(env, NULL, session_id, args);
    
    // 清理资源
    (*env)->DeleteLocalRef(env, arg1);
    (*env)->DeleteLocalRef(env, arg2);
    (*env)->DeleteLocalRef(env, arg3);
    (*env)->DeleteLocalRef(env, args);
    
    if (result == 0) {
        LOGI("FFmpeg执行成功");
        return (*env)->NewStringUTF(env, "FFmpeg执行成功");
    } else {
        LOGE("FFmpeg执行失败，错误码: %d", result);
        return (*env)->NewStringUTF(env, "FFmpeg执行失败");
    }
} 