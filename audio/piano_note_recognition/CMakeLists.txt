# piano_note_recognition 原生库构建脚本
# - Oboe：通过 Gradle prefab 提供 oboe::oboe（须与 build.gradle.kts 中 ANDROID_STL=c++_shared 一致）
# - PFFFT / Aubio：可选静态库；若 cpp/lib/${ANDROID_ABI}/ 下不存在对应 .a，则仅告警并使用 C++ 回退实现

cmake_minimum_required(VERSION 3.22.1)
project(piano_note_recognition LANGUAGES C CXX)

set(CMAKE_C_STANDARD 11)
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

set(NATIVE_SRC_DIR ${CMAKE_SOURCE_DIR}/src/main/cpp)
set(INCLUDE_DIR ${NATIVE_SRC_DIR}/include)

# Oboe (Prefab)
find_package(oboe REQUIRED CONFIG)

# NDK / 系统库
find_library(log_lib log)
find_library(android_lib android)
find_library(m_lib m)

# ---------- Third-party static libs (optional imported) ----------
# 用户后续替换为交叉编译得到的 .a：libpffft.a / libaubio.a
set(PFFFT_LIB_PATH ${NATIVE_SRC_DIR}/lib/${ANDROID_ABI}/libpffft.a)
set(AUBIO_LIB_PATH ${NATIVE_SRC_DIR}/lib/${ANDROID_ABI}/libaubio.a)

set(PIANO_NOTE_THIRD_PARTY_LIBS "")
if(EXISTS "${PFFFT_LIB_PATH}")
    add_library(pffft STATIC IMPORTED)
    set_target_properties(pffft PROPERTIES IMPORTED_LOCATION "${PFFFT_LIB_PATH}")
    list(APPEND PIANO_NOTE_THIRD_PARTY_LIBS pffft)
else()
    message(WARNING "PFFFT lib not found: ${PFFFT_LIB_PATH} (fallback FFT will be used)")
endif()

if(EXISTS "${AUBIO_LIB_PATH}")
    add_library(aubio STATIC IMPORTED)
    set_target_properties(aubio PROPERTIES IMPORTED_LOCATION "${AUBIO_LIB_PATH}")
    list(APPEND PIANO_NOTE_THIRD_PARTY_LIBS aubio)
else()
    message(WARNING "Aubio lib not found: ${AUBIO_LIB_PATH} (fallback YIN will be used)")
endif()

# ---------- JNI shared library ----------
add_library(piano_note_recognition SHARED
    ${NATIVE_SRC_DIR}/native-lib.cpp
    ${NATIVE_SRC_DIR}/audio/AudioEngine.cpp
    ${NATIVE_SRC_DIR}/dsp/FFTWrapper.cpp
    ${NATIVE_SRC_DIR}/dsp/HPS.cpp
    ${NATIVE_SRC_DIR}/dsp/YINWrapper.cpp
    ${NATIVE_SRC_DIR}/PitchDetector.cpp
)

target_include_directories(piano_note_recognition PRIVATE
    ${INCLUDE_DIR}
    ${NATIVE_SRC_DIR}
)

target_link_libraries(piano_note_recognition
    oboe::oboe
    ${log_lib}
    ${android_lib}
    ${m_lib}
    ${PIANO_NOTE_THIRD_PARTY_LIBS}
)

