# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.
cmake_minimum_required(VERSION 3.22.1)

# Set project name
project("ffmpeg_player")

# Set the output directory for generated so dynamic library
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${PROJECT_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI})

# Create a simple library to make the build pass
add_library( # Sets the name of the library.
             ffmpeg_player

             # Sets the library as a shared library.
             SHARED

             # Use the stub file for now
             src/main/jni/ffmpeg_player_stub.c )

# Find the log library
find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

# Link libraries
target_link_libraries( # Specifies the target library.
                       ffmpeg_player

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )
