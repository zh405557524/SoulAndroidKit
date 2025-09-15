#include <jni.h>
#include <android/log.h>
#include <unistd.h>
#include <sys/wait.h>
#include <errno.h>
#include <string.h>
#include <stdlib.h>
#include <pthread.h>
#include <signal.h>
#include "libavformat/avformat.h"
#include "libavcodec/avcodec.h"
#include "libavutil/log.h"

#define LOG_TAG "FFmpegExecutor"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

// Session管理结构
typedef struct {
    long session_id;
    pid_t process_id;
    int is_running;
    int return_code;
    pthread_mutex_t mutex;
} session_info_t;

// 全局session管理
static session_info_t sessions[1000];
static pthread_mutex_t sessions_mutex = PTHREAD_MUTEX_INITIALIZER;
static int next_session_index = 0;

// 获取session信息
static session_info_t* get_session_info(long session_id) {
    pthread_mutex_lock(&sessions_mutex);
    
    for (int i = 0; i < 1000; i++) {
        if (sessions[i].session_id == session_id) {
            pthread_mutex_unlock(&sessions_mutex);
            return &sessions[i];
        }
    }
    
    pthread_mutex_unlock(&sessions_mutex);
    return NULL;
}

// 创建新的session信息
static session_info_t* create_session_info(long session_id) {
    pthread_mutex_lock(&sessions_mutex);
    
    session_info_t* session = &sessions[next_session_index % 1000];
    session->session_id = session_id;
    session->process_id = 0;
    session->is_running = 0;
    session->return_code = -1;
    pthread_mutex_init(&session->mutex, NULL);
    
    next_session_index++;
    
    pthread_mutex_unlock(&sessions_mutex);
    return session;
}

// 日志回调函数
static void ffmpeg_log_callback(void *avcl, int level, const char *fmt, va_list vl) {
    if (level > av_log_get_level()) {
        return;
    }
    
    char log_buffer[1024];
    vsnprintf(log_buffer, sizeof(log_buffer), fmt, vl);
    
    // 根据级别输出到Android日志
    switch (level) {
        case AV_LOG_ERROR:
        case AV_LOG_FATAL:
        case AV_LOG_PANIC:
            LOGE("%s", log_buffer);
            break;
        case AV_LOG_WARNING:
            __android_log_print(ANDROID_LOG_WARN, LOG_TAG, "%s", log_buffer);
            break;
        case AV_LOG_INFO:
            LOGI("%s", log_buffer);
            break;
        case AV_LOG_DEBUG:
        case AV_LOG_VERBOSE:
        default:
            LOGD("%s", log_buffer);
            break;
    }
}

// 初始化FFmpeg执行器
static int init_ffmpeg_executor() {
    static int initialized = 0;
    if (!initialized) {
        // 初始化FFmpeg
        avformat_network_init();
        
        // 设置日志回调
        av_log_set_callback(ffmpeg_log_callback);
        av_log_set_level(AV_LOG_INFO);
        
        initialized = 1;
        LOGI("FFmpeg executor initialized");
    }
    return 0;
}

// 执行FFmpeg命令的核心函数
static int execute_ffmpeg_command(long session_id, char** argv, int argc, int is_ffprobe) {
    session_info_t* session = get_session_info(session_id);
    if (!session) {
        session = create_session_info(session_id);
    }
    
    pthread_mutex_lock(&session->mutex);
    session->is_running = 1;
    session->return_code = -1;
    pthread_mutex_unlock(&session->mutex);
    
    LOGI("Executing %s command for session %ld with %d arguments", 
         is_ffprobe ? "ffprobe" : "ffmpeg", session_id, argc);
    
    // 打印命令参数（用于调试）
    for (int i = 0; i < argc; i++) {
        LOGD("Arg[%d]: %s", i, argv[i]);
    }
    
    // 在实际项目中，这里应该调用FFmpeg的main函数或相应的API
    // 由于FFmpeg的命令行解析比较复杂，这里模拟执行过程
    
    int result = 0;
    
    // 模拟处理时间
    usleep(100000); // 100ms
    
    // 这里可以添加实际的FFmpeg/FFprobe执行逻辑
    if (is_ffprobe) {
        // 模拟FFprobe执行
        LOGI("FFprobe execution simulated for session %ld", session_id);
        result = 0; // 假设成功
    } else {
        // 模拟FFmpeg执行
        LOGI("FFmpeg execution simulated for session %ld", session_id);
        result = 0; // 假设成功
    }
    
    pthread_mutex_lock(&session->mutex);
    session->is_running = 0;
    session->return_code = result;
    pthread_mutex_unlock(&session->mutex);
    
    LOGI("%s execution completed for session %ld with result: %d", 
         is_ffprobe ? "FFprobe" : "FFmpeg", session_id, result);
    
    return result;
}

// 将Java字符串数组转换为C字符串数组
static char** convert_java_args_to_c(JNIEnv *env, jobjectArray args, int* argc) {
    *argc = (*env)->GetArrayLength(env, args);
    char** argv = (char**)malloc(sizeof(char*) * (*argc + 1));
    
    if (!argv) {
        LOGE("Failed to allocate memory for arguments");
        return NULL;
    }
    
    for (int i = 0; i < *argc; i++) {
        jstring arg = (jstring)(*env)->GetObjectArrayElement(env, args, i);
        const char* arg_str = (*env)->GetStringUTFChars(env, arg, NULL);
        
        if (arg_str) {
            argv[i] = strdup(arg_str);
            (*env)->ReleaseStringUTFChars(env, arg, arg_str);
        } else {
            argv[i] = strdup("");
        }
        
        (*env)->DeleteLocalRef(env, arg);
    }
    
    argv[*argc] = NULL;
    return argv;
}

// 释放C字符串数组
static void free_c_args(char** argv, int argc) {
    if (argv) {
        for (int i = 0; i < argc; i++) {
            if (argv[i]) {
                free(argv[i]);
            }
        }
        free(argv);
    }
}

// 执行FFmpeg命令
int ffmpeg_execute_command(JNIEnv *env, long session_id, jobjectArray arguments) {
    init_ffmpeg_executor();
    
    if (!arguments) {
        LOGE("Arguments array is null");
        return -1;
    }
    
    int argc;
    char** argv = convert_java_args_to_c(env, arguments, &argc);
    if (!argv) {
        return -1;
    }
    
    int result = execute_ffmpeg_command(session_id, argv, argc, 0);
    
    free_c_args(argv, argc);
    return result;
}

// 执行FFprobe命令
int ffprobe_execute_command(JNIEnv *env, long session_id, jobjectArray arguments) {
    init_ffmpeg_executor();
    
    if (!arguments) {
        LOGE("Arguments array is null");
        return -1;
    }
    
    int argc;
    char** argv = convert_java_args_to_c(env, arguments, &argc);
    if (!argv) {
        return -1;
    }
    
    int result = execute_ffmpeg_command(session_id, argv, argc, 1);
    
    free_c_args(argv, argc);
    return result;
}

// 取消执行
void ffmpeg_cancel_execution(long session_id) {
    session_info_t* session = get_session_info(session_id);
    if (!session) {
        LOGE("Session %ld not found for cancellation", session_id);
        return;
    }
    
    pthread_mutex_lock(&session->mutex);
    
    if (session->is_running && session->process_id > 0) {
        LOGI("Cancelling session %ld (PID: %d)", session_id, session->process_id);
        kill(session->process_id, SIGTERM);
        session->is_running = 0;
        session->return_code = -2; // 取消标志
    }
    
    pthread_mutex_unlock(&session->mutex);
}

// 检查session是否正在运行
int is_session_running(long session_id) {
    session_info_t* session = get_session_info(session_id);
    if (!session) {
        return 0;
    }
    
    pthread_mutex_lock(&session->mutex);
    int running = session->is_running;
    pthread_mutex_unlock(&session->mutex);
    
    return running;
} 