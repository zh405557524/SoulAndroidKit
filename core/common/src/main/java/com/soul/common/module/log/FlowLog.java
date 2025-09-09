package com.soul.common.module.log;


import com.soul.common.utils.LogUtil;

/**
 * Description: 流程日志
 * Author: 祝明
 * CreateDate: 2024/5/30 10:23
 * UpdateUser:
 * UpdateDate: 2024/5/30 10:23
 * UpdateRemark:
 */
public class FlowLog {

    public static String TAG = FlowLog.class.getSimpleName();

    public static void setTagName(String tag) {
        TAG = tag;
    }

    public static void i(String msg) {
        LogUtil.i(TAG, msg);
    }

}
