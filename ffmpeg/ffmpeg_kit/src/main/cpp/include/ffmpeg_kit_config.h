#ifndef FFMPEG_KIT_CONFIG_H
#define FFMPEG_KIT_CONFIG_H

#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif

// JNI方法声明

// 消息传输相关
JNIEXPORT jint JNICALL
Java_com_soul_ffmpegkit_FFmpegKitConfig_messagesInTransmit(JNIEnv *env, jclass clazz, jlong session_id);

// 命名管道相关
JNIEXPORT jint JNICALL
Java_com_soul_ffmpegkit_FFmpegKitConfig_registerNewNativeFFmpegPipe(JNIEnv *env, jclass clazz, jstring pipe_path);

// 重定向控制
JNIEXPORT void JNICALL
Java_com_soul_ffmpegkit_FFmpegKitConfig_enableNativeRedirection(JNIEnv *env, jclass clazz);

JNIEXPORT void JNICALL
Java_com_soul_ffmpegkit_FFmpegKitConfig_disableNativeRedirection(JNIEnv *env, jclass clazz);

// 日志级别
JNIEXPORT jint JNICALL
Java_com_soul_ffmpegkit_FFmpegKitConfig_getNativeLogLevel(JNIEnv *env, jclass clazz);

JNIEXPORT void JNICALL
Java_com_soul_ffmpegkit_FFmpegKitConfig_setNativeLogLevel(JNIEnv *env, jclass clazz, jint level);

// 版本信息
JNIEXPORT jstring JNICALL
Java_com_soul_ffmpegkit_FFmpegKitConfig_getNativeFFmpegVersion(JNIEnv *env, jclass clazz);

JNIEXPORT jstring JNICALL
Java_com_soul_ffmpegkit_FFmpegKitConfig_getNativeVersion(JNIEnv *env, jclass clazz);

JNIEXPORT jstring JNICALL
Java_com_soul_ffmpegkit_FFmpegKitConfig_getNativeBuildDate(JNIEnv *env, jclass clazz);

// 环境变量
JNIEXPORT jint JNICALL
Java_com_soul_ffmpegkit_FFmpegKitConfig_setNativeEnvironmentVariable(JNIEnv *env, jclass clazz, jstring name, jstring value);

// 信号处理
JNIEXPORT void JNICALL
Java_com_soul_ffmpegkit_FFmpegKitConfig_ignoreNativeSignal(JNIEnv *env, jclass clazz, jint signal_num);

// 执行方法
JNIEXPORT jint JNICALL
Java_com_soul_ffmpegkit_FFmpegKitConfig_nativeFFmpegExecute(JNIEnv *env, jclass clazz, jlong session_id, jobjectArray arguments);

JNIEXPORT jint JNICALL
Java_com_soul_ffmpegkit_FFmpegKitConfig_nativeFFprobeExecute(JNIEnv *env, jclass clazz, jlong session_id, jobjectArray arguments);

JNIEXPORT void JNICALL
Java_com_soul_ffmpegkit_FFmpegKitConfig_nativeFFmpegCancel(JNIEnv *env, jclass clazz, jlong session_id);

#ifdef __cplusplus
}
#endif

#endif // FFMPEG_KIT_CONFIG_H 