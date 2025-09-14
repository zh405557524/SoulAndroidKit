#include <jni.h>
#include <android/log.h>

#define LOG_TAG "FFmpegPlayerStub"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

// Stub implementation for FFmpeg player
JNIEXPORT void JNICALL
Java_com_soul_ffmpeg_kit_FFmpegPlayer_playVideo(JNIEnv *env, jobject thiz, jstring videoPath, jobject surface) {
    LOGI("FFmpeg Player Stub: playVideo called");
    // This is a stub implementation
    // TODO: Implement actual FFmpeg functionality
}

JNIEXPORT void JNICALL
Java_com_soul_ffmpeg_kit_FFmpegPlayer_stopPlay(JNIEnv *env, jobject thiz) {
    LOGI("FFmpeg Player Stub: stopPlay called");
    // This is a stub implementation
}

JNIEXPORT jlong JNICALL
Java_com_soul_ffmpeg_kit_FFmpegPlayer_getDuration(JNIEnv *env, jobject thiz) {
    LOGI("FFmpeg Player Stub: getDuration called");
    // This is a stub implementation
    return 0;
}

JNIEXPORT jlong JNICALL
Java_com_soul_ffmpeg_kit_FFmpegPlayer_getCurrentPosition(JNIEnv *env, jobject thiz) {
    LOGI("FFmpeg Player Stub: getCurrentPosition called");
    // This is a stub implementation
    return 0;
}

JNIEXPORT void JNICALL
Java_com_soul_ffmpeg_kit_FFmpegPlayer_seekTo(JNIEnv *env, jobject thiz, jlong position) {
    LOGI("FFmpeg Player Stub: seekTo called with position %ld", position);
    // This is a stub implementation
}

JNIEXPORT jboolean JNICALL
Java_com_soul_ffmpeg_kit_FFmpegPlayer_isPlaying(JNIEnv *env, jobject thiz) {
    LOGI("FFmpeg Player Stub: isPlaying called");
    // This is a stub implementation
    return JNI_FALSE;
} 