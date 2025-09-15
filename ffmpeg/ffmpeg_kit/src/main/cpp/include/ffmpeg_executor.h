#ifndef FFMPEG_EXECUTOR_H
#define FFMPEG_EXECUTOR_H

#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif

// 执行FFmpeg命令
int ffmpeg_execute_command(JNIEnv *env, long session_id, jobjectArray arguments);

// 执行FFprobe命令
int ffprobe_execute_command(JNIEnv *env, long session_id, jobjectArray arguments);

// 取消执行
void ffmpeg_cancel_execution(long session_id);

// 检查session是否正在运行
int is_session_running(long session_id);

#ifdef __cplusplus
}
#endif

#endif // FFMPEG_EXECUTOR_H 