/**
 * 钢琴音符识谱模块：Kotlin API + JNI + CMake 原生库。
 *
 * NDK 要点：
 * - [prefab]：启用后 CMake 可通过 find_package(oboe) 链接 Oboe
 * - [ANDROID_STL=c++_shared]：Oboe AAR 的 prefab 产物要求共享 C++ 运行时，否则 configureCMake 阶段会失败
 * - [abiFilters]：与工程策略一致，当前仅 arm64-v8a
 */
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.soul.piano_note_recognition"
    compileSdk = 36

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        externalNativeBuild {
            cmake {
                cppFlags("")
                // Oboe Prefab 依赖要求使用 shared STL，避免 prefab 选择失败
                arguments("-DANDROID_STL=c++_shared")
                abiFilters("arm64-v8a")
            }
        }
    }

    buildFeatures {
        prefab = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    externalNativeBuild {
        cmake {
            path = file("CMakeLists.txt")
            version = "3.22.1"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation("com.google.oboe:oboe:1.9.3")
    implementation(libs.kotlinx.coroutines.android)
}