package com.soul.android_kit

import android.app.Application
import com.soul.common.Global

/**
 * Description: TODO
 * Author: zhuMing
 * CreateDate: 2025/9/15 20:41
 * ProjectName: android-kit
 * UpdateUser:
 * UpdateDate: 2025/9/15 20:41
 * UpdateRemark:
 */
class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Global.init(this, true)
    }
}