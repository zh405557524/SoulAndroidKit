cmake_minimum_required(VERSION 3.22.1)
project(ffmpeg_kit LANGUAGES C CXX)

# ===== 输出目录仍写入 jniLibs（按你的要求）=====
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${PROJECT_SOURCE_DIR}/jniLibs/${ANDROID_ABI})
set(CMAKE_RUNTIME_OUTPUT_DIRECTORY ${CMAKE_LIBRARY_OUTPUT_DIRECTORY})
set(CMAKE_ARCHIVE_OUTPUT_DIRECTORY ${CMAKE_LIBRARY_OUTPUT_DIRECTORY})

# 语言标准
set(CMAKE_C_STANDARD 11)
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# 源码与头文件目录
set(INCLUDE_DIR ${CMAKE_SOURCE_DIR}/src/main/cpp/include)
set(SRC_DIR     ${CMAKE_SOURCE_DIR}/src/main/cpp/src)

# 递归收集源码（注意变量展开）
file(GLOB_RECURSE glob_source_files
        "${SRC_DIR}/*.c" "${SRC_DIR}/*.cpp" "${SRC_DIR}/*.cc")

# 生成你的 so（库名尽量唯一，避免冲突）
add_library(ffmpeg_player SHARED ${glob_source_files})

# 头文件（目标级）
target_include_directories(ffmpeg_player PRIVATE
        ${INCLUDE_DIR}/ffmpeg
        ${INCLUDE_DIR}
        ${SRC_DIR}
)

# 常见编译/链接优化（可选）
target_compile_options(ffmpeg_player PRIVATE
        -fvisibility=hidden
        -ffunction-sections -fdata-sections
)
set_target_properties(ffmpeg_player PROPERTIES
        LINK_FLAGS "-Wl,--gc-sections"
)

# 预编译 FFmpeg .so 所在目录（你的 jniLibs）
set(LIB_DIR ${CMAKE_SOURCE_DIR}/src/main/jniLibs)

# ===== 导入 FFmpeg 预编译库 =====
add_library(avcodec   SHARED IMPORTED)
set_target_properties(avcodec   PROPERTIES IMPORTED_LOCATION "${LIB_DIR}/${ANDROID_ABI}/libavcodec.so")

add_library(avformat  SHARED IMPORTED)
set_target_properties(avformat  PROPERTIES IMPORTED_LOCATION "${LIB_DIR}/${ANDROID_ABI}/libavformat.so")

add_library(avutil    SHARED IMPORTED)
set_target_properties(avutil    PROPERTIES IMPORTED_LOCATION "${LIB_DIR}/${ANDROID_ABI}/libavutil.so")

add_library(avfilter  SHARED IMPORTED)
set_target_properties(avfilter  PROPERTIES IMPORTED_LOCATION "${LIB_DIR}/${ANDROID_ABI}/libavfilter.so")

add_library(avdevice  SHARED IMPORTED)
set_target_properties(avdevice  PROPERTIES IMPORTED_LOCATION "${LIB_DIR}/${ANDROID_ABI}/libavdevice.so")

add_library(swscale   SHARED IMPORTED)
set_target_properties(swscale   PROPERTIES IMPORTED_LOCATION "${LIB_DIR}/${ANDROID_ABI}/libswscale.so")

add_library(swresample SHARED IMPORTED)
set_target_properties(swresample PROPERTIES IMPORTED_LOCATION "${LIB_DIR}/${ANDROID_ABI}/libswresample.so")

# ===== NDK / 系统库 =====
find_library(log_lib      log)
find_library(android_lib  android)
find_library(dl_lib       dl)
find_library(m_lib        m)
# 你有 .cpp 源，显式链接 C++ 运行时更稳
#find_library(cpp_shared   c++_shared)

# ===== 链接（顺序很重要）=====
# 高层 → 低层：avdevice → avfilter → avformat → avcodec → swscale/swresample → avutil → 系统库
target_link_libraries(ffmpeg_player
        avdevice
        avfilter
        avformat
        avcodec
        swscale
        swresample
        avutil
        ${log_lib}
        ${android_lib}
        ${dl_lib}
        ${m_lib}
#        ${cpp_shared}
)
