package com.soul.piano_note_recognition

import android.os.Handler
import android.os.HandlerThread
import androidx.annotation.Keep

/**
 * 钢琴音符实时识别入口（Kotlin 层）。
 *
 * 职责：
 * - 通过 JNI 启动/停止底层 Oboe 录音与 C++ 音高检测；
 * - 将识别结果通过 [NoteCallback] 回传给业务层（MIDI、音量、可信度、频率）。
 *
 * 线程与幂等：
 * - [start] / [stop] 使用 [stateLock] 保证线程安全；
 * - 已在运行时再次 [start] 返回 false；已停止时再次 [stop] 返回 false；
 * - C++ 侧若出现“重复 nativeStart”（例如 JVM 异常后重入），会先释放旧资源再建新的，避免泄漏。
 *
 * 回调线程：
 * - JNI 从音频线程调用 [onNoteDetected]；此处再 [Handler.post] 到内部 [HandlerThread]，避免阻塞音频回调。
 */
class PianoNoteRecognizer private constructor() {

    /**
     * 音符检测结果回调。
     *
     * @param midiNote MIDI 音符号（21–108 为钢琴常用范围；C++ 侧无效帧不会调用本方法）
     * @param volume 音量（当前为 0..1 的启发式映射，与 FFT 侧 RMS 相关）
     * @param confidence 当前帧选用算法路径的可信度 0..1
     * @param frequencyHz 估计基频 Hz
     */
    fun interface NoteCallback {
        fun onNote(midiNote: Int, volume: Float, confidence: Float, frequency: Float)
    }

    /** 用户回调；与 [isRunning] 一起在 [stateLock] 下读写，避免竞态。 */
    @Volatile
    private var callback: NoteCallback? = null

    /** 与 native 层运行状态对齐：只有 nativeStart 成功后才为 true。 */
    @Volatile
    private var isRunning: Boolean = false

    /**
     * 专用工作线程：承载业务侧 [NoteCallback]，不把耗时/锁操作放在 Oboe 音频线程。
     * lazy + start()：首次使用前启动线程。
     */
    private val handlerThread: HandlerThread by lazy {
        HandlerThread("piano-note-recognition").apply { start() }
    }
    private val handler: Handler by lazy { Handler(handlerThread.looper) }

    /** [start]/[stop] 的互斥锁。 */
    private val stateLock = Any()

    /**
     * 开始录音与识别。
     *
     * @return true 表示 native 启动成功；false 表示已在运行或 native 启动失败（此时会清空 callback）
     */
    fun start(callback: NoteCallback): Boolean = synchronized(stateLock) {
        // 不允许重复启动：与用户需求一致，避免多路 Oboe stream 叠加
        if (isRunning) return false
        this.callback = callback
        val ok = nativeStart()
        if (!ok) {
            this.callback = null
            isRunning = false
            return false
        }
        isRunning = true
        true
    }

    /**
     * 停止录音并释放 native 资源。
     *
     * @return true 表示确实执行了 stop；false 表示当前未在运行（幂等）
     */
    fun stop(): Boolean = synchronized(stateLock) {
        if (!isRunning) return false
        val ok = nativeStop()
        isRunning = false
        this.callback = null
        ok
    }

    /** 对应 JNI：创建 AudioEngine、Oboe 流、注册回调到 Java 的 [onNoteDetected]。 */
    private external fun nativeStart(): Boolean

    /** 对应 JNI：停止流、delete AudioEngine、删除 GlobalRef。 */
    private external fun nativeStop(): Boolean

    /**
     * 由 C++ 在音频线程调用（非主线程）。
     * 使用 @Keep 防止混淆后 JNI 找不到方法。
     *
     * 关键逻辑：只把数据 post 到 [handler]，再调用用户 [callback]。
     */
    @Keep
    fun onNoteDetected(midiNote: Int, volume: Float, confidence: Float, frequency: Float) {
        val cb = callback ?: return
        handler.post { cb.onNote(midiNote, volume, confidence, frequency) }
    }

    companion object {
        private const val TAG = "PianoNoteRecognizer"

        @Volatile
        private var INSTANCE: PianoNoteRecognizer? = null

        init {
            // CMake 产物名：add_library(piano_note_recognition SHARED ...)
            System.loadLibrary("piano_note_recognition")
        }

        /** 双重检查锁定单例，与项目内其它 Manager 风格一致。 */
        fun getInstance(): PianoNoteRecognizer {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: PianoNoteRecognizer().also { INSTANCE = it }
            }
        }
    }
}
