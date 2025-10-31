cmake_minimum_required(VERSION 3.22.1)
project(ffmpeg_kit LANGUAGES C CXX)

# ===== 输出目录仍写入 jniLibs（按你的要求）=====
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${PROJECT_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI})
set(CMAKE_RUNTIME_OUTPUT_DIRECTORY ${CMAKE_LIBRARY_OUTPUT_DIRECTORY})
set(CMAKE_ARCHIVE_OUTPUT_DIRECTORY ${CMAKE_LIBRARY_OUTPUT_DIRECTORY})

# 语言标准
set(CMAKE_C_STANDARD 11)
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# 源码与头文件目录
set(INCLUDE_DIR ${CMAKE_SOURCE_DIR}/src/main/cpp/include)
set(SRC_DIR     ${CMAKE_SOURCE_DIR}/src/main/cpp/src)
set(LIB_DIR     ${CMAKE_SOURCE_DIR}/src/main/cpp/lib)

# 架构标志定义（只支持arm64-v8a）
if(ANDROID_ABI STREQUAL "arm64-v8a")
    set(MY_ARCH_FLAGS "ARM64_V8A")
    set(MY_ARM_NEON TRUE)
else()
    message(FATAL_ERROR "Only arm64-v8a architecture is supported")
endif()

# 编译标志
#set(MY_CFLAGS "-Wall -Werror -Wno-unused-parameter -Wno-switch -Wno-sign-compare -DFFMPEG_KIT_${MY_ARCH_FLAGS}")
set(MY_WARNINGS
        -Wall
        -Wextra
        -Werror
        -Wno-unused-parameter
        -Wno-switch
        -Wno-sign-compare
)
# CPU Features 静态库
add_library(cpu_features STATIC IMPORTED)
set_target_properties(cpu_features PROPERTIES 
    IMPORTED_LOCATION "${LIB_DIR}/cpu_features/libcpu_features.a"
)

add_library(ndk_compat STATIC IMPORTED)
set_target_properties(ndk_compat PROPERTIES 
    IMPORTED_LOCATION "${LIB_DIR}/cpu_features/libndk_compat.a"
)

# 预编译 FFmpeg .so 所在目录（你的 jniLibs）
set(FFMPEG_LIB_DIR ${CMAKE_SOURCE_DIR}/src/main/jniLibs)

# ===== 导入 FFmpeg 预编译库 =====
add_library(avcodec   SHARED IMPORTED)
set_target_properties(avcodec   PROPERTIES IMPORTED_LOCATION "${FFMPEG_LIB_DIR}/${ANDROID_ABI}/libavcodec.so")

add_library(avformat  SHARED IMPORTED)
set_target_properties(avformat  PROPERTIES IMPORTED_LOCATION "${FFMPEG_LIB_DIR}/${ANDROID_ABI}/libavformat.so")

add_library(avutil    SHARED IMPORTED)
set_target_properties(avutil    PROPERTIES IMPORTED_LOCATION "${FFMPEG_LIB_DIR}/${ANDROID_ABI}/libavutil.so")

add_library(avfilter  SHARED IMPORTED)
set_target_properties(avfilter  PROPERTIES IMPORTED_LOCATION "${FFMPEG_LIB_DIR}/${ANDROID_ABI}/libavfilter.so")

add_library(avdevice  SHARED IMPORTED)
set_target_properties(avdevice  PROPERTIES IMPORTED_LOCATION "${FFMPEG_LIB_DIR}/${ANDROID_ABI}/libavdevice.so")

add_library(swscale   SHARED IMPORTED)
set_target_properties(swscale   PROPERTIES IMPORTED_LOCATION "${FFMPEG_LIB_DIR}/${ANDROID_ABI}/libswscale.so")

add_library(swresample SHARED IMPORTED)
set_target_properties(swresample PROPERTIES IMPORTED_LOCATION "${FFMPEG_LIB_DIR}/${ANDROID_ABI}/libswresample.so")

# ===== NDK / 系统库 =====
find_library(log_lib      log)
find_library(android_lib  android)
find_library(z_lib        z)
find_library(dl_lib       dl)
find_library(m_lib        m)
find_library(cpp_shared   c++_shared)

# ===== 创建 ffmpegkit_abidetect 库 =====
add_library(ffmpegkit_abidetect SHARED
    ${SRC_DIR}/ffmpegkit_abidetect.c
)

target_include_directories(ffmpegkit_abidetect PRIVATE
    ${INCLUDE_DIR}/ffmpeg
    ${INCLUDE_DIR}/cpu_features
    ${INCLUDE_DIR}/ndk_compat
    ${SRC_DIR}
)

target_compile_options(ffmpegkit_abidetect PRIVATE
    ${MY_CFLAGS}
    -Wall -Wextra -Werror -Wno-unused-parameter
)

target_link_libraries(ffmpegkit_abidetect
    cpu_features
    ndk_compat
    ${log_lib}
    ${z_lib}
    ${android_lib}
)

# ===== 创建主要的 ffmpegkit 库 =====
# 根据Android.mk定义的源文件列表
set(MY_SRC_FILES
    ${SRC_DIR}/ffmpegkit.c
    ${SRC_DIR}/ffprobekit.c
    ${SRC_DIR}/ffmpegkit_exception.c
    ${SRC_DIR}/fftools_cmdutils.c
    ${SRC_DIR}/fftools_ffmpeg.c
    ${SRC_DIR}/fftools_ffprobe.c
    ${SRC_DIR}/fftools_ffmpeg_mux.c
    ${SRC_DIR}/fftools_ffmpeg_mux_init.c
    ${SRC_DIR}/fftools_ffmpeg_demux.c
    ${SRC_DIR}/fftools_ffmpeg_opt.c
    ${SRC_DIR}/fftools_opt_common.c
    ${SRC_DIR}/fftools_ffmpeg_hw.c
    ${SRC_DIR}/fftools_ffmpeg_filter.c
    ${SRC_DIR}/fftools_objpool.c
    ${SRC_DIR}/fftools_sync_queue.c
    ${SRC_DIR}/fftools_thread_queue.c
)

# 根据Android版本添加LTS支持
if(ANDROID_PLATFORM_LEVEL LESS_EQUAL 17)
    list(APPEND MY_SRC_FILES ${SRC_DIR}/android_lts_support.c)
endif()

add_library(ffmpegkit SHARED ${MY_SRC_FILES})

target_include_directories(ffmpegkit PRIVATE
    ${INCLUDE_DIR}/ffmpeg
    ${INCLUDE_DIR}/cpu_features
    ${INCLUDE_DIR}/ndk_compat
    ${SRC_DIR}
)

target_compile_options(ffmpegkit PRIVATE
    ${MY_CFLAGS}
)

target_link_options(ffmpegkit PRIVATE "-Wl,-z,max-page-size=16384")

# ===== 链接（顺序很重要）=====
# 高层 → 低层：avdevice → avfilter → avformat → avcodec → swscale/swresample → avutil → 系统库
target_link_libraries(ffmpegkit
    avdevice
    avfilter
    avformat
    avcodec
    swscale
    swresample
    avutil
    ${log_lib}
    ${z_lib}
    ${android_lib}
    ${dl_lib}
    ${m_lib}
    ${cpp_shared}
)
