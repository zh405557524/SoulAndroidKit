#include <jni.h>
#include <android/log.h>
#include <libavformat/avformat.h>  // for avformat_configuration()
#include <string.h>

#define LOG_TAG "AbiDetect"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

/** Full name of the Java class that owns native functions in this file. */
static const char *abiDetectClassName = "com/arthenica/ffmpegkit/AbiDetect";

/** ---------- ABI helpers (compile-time detection) ---------- */

static const char* detect_build_abi(void) {
#if defined(__aarch64__)
    return "arm64-v8a";
#elif defined(__arm__)
    /* 这里也可以进一步判断 NEON / ARMv7，但通常返回 armeabi-v7a 即可 */
    return "armeabi-v7a";
#elif defined(__x86_64__)
    return "x86_64";
#elif defined(__i386__)
    return "x86";
#else
    return "unknown";
#endif
}

static const char* detect_cpu_abi(void) {
    /* 对移动端来说，build ABI 与 运行时 ABI 基本一致。
       如果你真的需要运行时探测（区分 armv7 neon 等），可以接入
       自己的 /proc/cpuinfo 解析；这里返回与构建一致的值即可。 */
    return detect_build_abi();
}

/** ---------- JNI methods ---------- */

JNIEXPORT jstring JNICALL
Java_com_arthenica_ffmpegkit_AbiDetect_getNativeAbi(JNIEnv *env, jclass clazz) {
    (void)clazz;
    return (*env)->NewStringUTF(env, detect_build_abi());
}

JNIEXPORT jstring JNICALL
Java_com_arthenica_ffmpegkit_AbiDetect_getNativeCpuAbi(JNIEnv *env, jclass clazz) {
    (void)clazz;
    return (*env)->NewStringUTF(env, detect_cpu_abi());
}

JNIEXPORT jboolean JNICALL
Java_com_arthenica_ffmpegkit_AbiDetect_isNativeLTSBuild(JNIEnv *env, jclass clazz) {
    (void)env; (void)clazz;
#if defined(FFMPEG_KIT_LTS)
    return JNI_TRUE;
#else
    return JNI_FALSE;
#endif
}

JNIEXPORT jstring JNICALL
Java_com_arthenica_ffmpegkit_AbiDetect_getNativeBuildConf(JNIEnv *env, jclass clazz) {
    (void)clazz;
    /* 公开 API：返回 FFmpeg 的 configure 字符串 */
    const char *conf = avformat_configuration();
    if (!conf) conf = "";
    return (*env)->NewStringUTF(env, conf);
}

/** ---------- JNI registration ---------- */

static JNINativeMethod abiDetectMethods[] = {
        {"getNativeAbi",      "()Ljava/lang/String;", (void*) Java_com_arthenica_ffmpegkit_AbiDetect_getNativeAbi},
        {"getNativeCpuAbi",   "()Ljava/lang/String;", (void*) Java_com_arthenica_ffmpegkit_AbiDetect_getNativeCpuAbi},
        {"isNativeLTSBuild",  "()Z",                  (void*) Java_com_arthenica_ffmpegkit_AbiDetect_isNativeLTSBuild},
        {"getNativeBuildConf","()Ljava/lang/String;", (void*) Java_com_arthenica_ffmpegkit_AbiDetect_getNativeBuildConf},
};

jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    (void)reserved;
    JNIEnv *env = NULL;
    if ((*vm)->GetEnv(vm, (void**)&env, JNI_VERSION_1_6) != JNI_OK) {
        LOGE("OnLoad failed to GetEnv for class %s.", abiDetectClassName);
        return JNI_FALSE;
    }

    jclass cls = (*env)->FindClass(env, abiDetectClassName);
    if (cls == NULL) {
        LOGE("OnLoad failed to FindClass %s.", abiDetectClassName);
        return JNI_FALSE;
    }

    if ((*env)->RegisterNatives(env, cls, abiDetectMethods,
                                sizeof(abiDetectMethods)/sizeof(abiDetectMethods[0])) < 0) {
        LOGE("OnLoad failed to RegisterNatives for class %s.", abiDetectClassName);
        return JNI_FALSE;
    }

    return JNI_VERSION_1_6;
}
