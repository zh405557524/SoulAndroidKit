/**
 * @file native-lib.cpp
 * @brief JNI 桥接层：Kotlin [PianoNoteRecognizer] <-> C++ [AudioEngine]
 *
 * 关键设计：
 * - 全局互斥 [gMutex]：nativeStart/nativeStop 与资源释放串行化，避免与音频线程竞态导致 UAF。
 * - Java 全局引用 [gJavaObjGlobal]：音频回调线程需要调用 Kotlin 实例方法，不能用局部 jobject。
 * - 重复 nativeStart：若 [gRunning] 已为 true，先 [stopLocked] 释放旧 Oboe/引擎/GlobalRef（应对 JVM 异常后重入）。
 * - [getJNIEnv]：音频线程可能未 Attach，使用 AttachCurrentThread（注意后续可考虑 Detach 策略）。
 */

#include <jni.h>

#include <mutex>
#include <atomic>
#include <new>

#include "audio/AudioEngine.h"

namespace {
/** 保护 JNI 全局状态与 Engine 生命周期的互斥锁 */
std::mutex gMutex;
/** 与 Kotlin isRunning 语义对齐：native 侧是否已成功启动流 */
std::atomic<bool> gRunning{false};

JavaVM* gVm = nullptr;
/** Kotlin PianoNoteRecognizer 实例的全局引用，供音频线程回调 */
jobject gJavaObjGlobal = nullptr;
jmethodID gOnNoteDetected = nullptr;

AudioEngine* gEngine = nullptr;

/**
 * 获取当前线程可用的 JNIEnv。
 * - 已附加：直接返回
 * - 未附加（常见于 Oboe 回调线程）：AttachCurrentThread
 */
JNIEnv* getJNIEnv() {
    if (gVm == nullptr) return nullptr;
    JNIEnv* env = nullptr;
    const jint res = gVm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6);
    if (res == JNI_OK) return env;
    if (res == JNI_EDETACHED) {
        if (gVm->AttachCurrentThread(&env, nullptr) == JNI_OK) {
            return env;
        }
    }
    return nullptr;
}

/** 释放全局引用，避免泄漏 */
void clearJavaRef(JNIEnv* env) {
    if (gJavaObjGlobal != nullptr) {
        env->DeleteGlobalRef(gJavaObjGlobal);
        gJavaObjGlobal = nullptr;
    }
}

/**
 * 在已持有 [gMutex] 的前提下：停止引擎、释放 C++ 对象、清理 JNI 引用。
 */
void stopLocked(JNIEnv* env) {
    if (gEngine != nullptr) {
        gEngine->stop();
        delete gEngine;
        gEngine = nullptr;
    }
    clearJavaRef(env);
    gOnNoteDetected = nullptr;
    gRunning.store(false);
}

/**
 * 从音频线程把一帧识别结果回调到 Kotlin。
 * 关键：CallVoidMethod 可能抛 Java 异常，在 native 侧 Clear，避免异常挂起影响后续回调。
 */
void callbackToJava(const AudioEngine::NoteResult& res) {
    if (!gRunning.load()) return;
    if (gJavaObjGlobal == nullptr || gOnNoteDetected == nullptr) return;
    JNIEnv* env = getJNIEnv();
    if (env == nullptr) return;

    env->CallVoidMethod(gJavaObjGlobal, gOnNoteDetected,
                         static_cast<jint>(res.midiNote),
                         static_cast<jfloat>(res.volume),
                         static_cast<jfloat>(res.confidence),
                         static_cast<jfloat>(res.frequencyHz));
    if (env->ExceptionCheck()) {
        env->ExceptionClear();
    }
}
} // namespace

/**
 * JNI：启动识别。
 * 流程：缓存 JavaVM -> 若已在运行则先释放 -> NewGlobalRef(thiz) -> GetMethodID -> new AudioEngine -> start
 */
extern "C" JNIEXPORT jboolean JNICALL
Java_com_soul_piano_1note_1recognition_PianoNoteRecognizer_nativeStart(JNIEnv* env, jobject thiz) {
    std::lock_guard<std::mutex> lock(gMutex);

    if (gVm == nullptr) {
        env->GetJavaVM(&gVm);
    }

    // JVM 层崩溃/重入时可能再次 start：必须先拆掉旧资源，防止 Oboe 流与 GlobalRef 泄漏
    if (gRunning.load()) {
        stopLocked(env);
    }

    clearJavaRef(env);
    gJavaObjGlobal = env->NewGlobalRef(thiz);
    if (gJavaObjGlobal == nullptr) {
        return JNI_FALSE;
    }

    jclass cls = env->GetObjectClass(thiz);
    if (cls == nullptr) {
        return JNI_FALSE;
    }
    // Kotlin: fun onNoteDetected(midiNote: Int, volume: Float, confidence: Float, frequency: Float)
    gOnNoteDetected = env->GetMethodID(cls, "onNoteDetected", "(IFFF)V");
    if (gOnNoteDetected == nullptr) {
        return JNI_FALSE;
    }

    if (gEngine != nullptr) {
        delete gEngine;
        gEngine = nullptr;
    }

    gEngine = new (std::nothrow) AudioEngine();
    if (gEngine == nullptr) {
        return JNI_FALSE;
    }

    const bool ok = gEngine->start([](const AudioEngine::NoteResult& res) {
        callbackToJava(res);
    });

    gRunning.store(ok);
    return ok ? JNI_TRUE : JNI_FALSE;
}

/**
 * JNI：停止识别。
 * 若未运行则返回 false（与 Kotlin stop 幂等语义一致）。
 */
extern "C" JNIEXPORT jboolean JNICALL
Java_com_soul_piano_1note_1recognition_PianoNoteRecognizer_nativeStop(JNIEnv* env, jobject /*thiz*/) {
    std::lock_guard<std::mutex> lock(gMutex);
    if (!gRunning.load()) return JNI_FALSE;
    stopLocked(env);
    return JNI_TRUE;
}
