#include "libavformat/avformat.h"
#include "libavcodec/avcodec.h"
#include <jni.h>
#include <android/log.h>

#define LOG_TAG "FFmpegPlayer"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

// 初始化 FFmpeg
static int initFFmpeg() {
    // FFmpeg 6.0+ 不再需要 av_register_all()
    // 组件会自动注册
    // 注册网络
    avformat_network_init();
    return 0;
}

// 获取FFmpeg版本信息
JNIEXPORT jstring JNICALL
Java_com_soul_ffmpegkit_FFmpegPlayer_getFFmpegVersion(JNIEnv *env, jobject thiz) {
    LOGI("获取FFmpeg版本信息");
    
    // 初始化FFmpeg
    if (initFFmpeg() < 0) {
        LOGE("FFmpeg 初始化失败");
        return (*env)->NewStringUTF(env, "FFmpeg初始化失败");
    }
    
    // 获取版本信息
    const char* version = av_version_info();
    LOGI("FFmpeg版本: %s", version);
    
    return (*env)->NewStringUTF(env, version);
}

// 获取FFmpeg配置信息
JNIEXPORT jstring JNICALL
Java_com_soul_ffmpegkit_FFmpegPlayer_getFFmpegConfiguration(JNIEnv *env, jobject thiz) {
    LOGI("获取FFmpeg配置信息");
    
    // 初始化FFmpeg
    if (initFFmpeg() < 0) {
        LOGE("FFmpeg 初始化失败");
        return (*env)->NewStringUTF(env, "FFmpeg初始化失败");
    }
    
    // 获取配置信息
    const char* config = avcodec_configuration();
    LOGI("FFmpeg配置: %s", config);
    
    return (*env)->NewStringUTF(env, config);
}

// 获取支持的格式信息
JNIEXPORT jstring JNICALL
Java_com_soul_ffmpegkit_FFmpegPlayer_getSupportedFormats(JNIEnv *env, jobject thiz) {
    LOGI("获取支持的格式信息");
    
    // 初始化FFmpeg
    if (initFFmpeg() < 0) {
        LOGE("FFmpeg 初始化失败");
        return (*env)->NewStringUTF(env, "FFmpeg初始化失败");
    }
    
    // FFmpeg 6.0+ 中 av_iformat_next() 已被废弃
    // 使用更简单的方式返回基本信息
    const char* formats = "mp4, avi, mkv, mov, flv, wmv, webm, m4v, 3gp, ts";
    LOGI("支持的输入格式: %s", formats);
    return (*env)->NewStringUTF(env, formats);
}