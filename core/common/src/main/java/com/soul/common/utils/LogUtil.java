package com.soul.common.utils;

import android.os.Message;
import android.util.Log;

import com.soul.common.module.log.LogManger;


/**
 * Description:LocalSocket 日志工具类
 * Author: zhanghan
 * CreateDate: 2019-06-25 18:00
 * UpdateUser:
 * UpdateDate: 2019-06-25 18:00
 * UpdateRemark:
 */
public class LogUtil {
    public static boolean sIsDebug;

    public static boolean isService;

    /**
     * Priority constant for the println method; use Log.v.
     */
    public static final int VERBOSE = 2;

    /**
     * Priority constant for the println method; use Log.d.
     */
    public static final int DEBUG = 3;

    /**
     * Priority constant for the println method; use Log.i.
     */
    public static final int INFO = 4;

    /**
     * Priority constant for the println method; use Log.w.
     */
    public static final int WARN = 5;

    /**
     * Priority constant for the println method; use Log.e.
     */
    public static final int ERROR = 6;

    /**
     * Priority constant for the println method.
     */
    public static final int ASSERT = 7;

    /**
     * default tag
     */
    public static final String DEFAULT_TAG = "SW_";

    private LogUtil() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    private static Message createMessage(int what) {
        return createMessage(what, null);
    }

    private static Message createMessage(int what, Object object) {
        Message obtain = Message.obtain();
        obtain.what = what;
        obtain.obj = object;
        return obtain;
    }

    //===================================LogUtil.v
    public static int v(String msg) {
        return log(VERBOSE, msg);
    }

    public static int v(String tag, String msg) {
        return log(VERBOSE, tag, msg);
    }

    public static int v(String msg, Throwable tr) {
        return log(VERBOSE, msg, tr);
    }

    public static int v(String tag, String msg, Throwable tr) {
        return log(VERBOSE, tag, msg, tr);
    }

    //===================================LogUtil.d
    public static int d(String msg) {
        return log(DEBUG, msg);
    }

    public static int d(String tag, String msg) {
        return log(DEBUG, tag, msg);
    }

    public static int d(String msg, Throwable tr) {
        return log(DEBUG, msg, tr);
    }

    public static int d(String tag, String msg, Throwable tr) {
        return log(DEBUG, tag, msg, tr);
    }

    //===================================LogUtil.i
    public static int i(String tag, String msg) {
        return log(INFO, tag, msg);
    }

    public static int i(String msg) {
        return log(INFO, msg);
    }

    public static int i(String msg, Throwable tr) {
        return log(INFO, msg, tr);
    }

    public static int i(String tag, String msg, Throwable tr) {
        return log(INFO, tag, msg, tr);
    }

    //===================================LogUtil.w
    public static int w(String tag, String msg) {
        return log(WARN, tag, msg);
    }

    public static int w(String msg) {
        return log(WARN, msg);
    }

    public static int w(String msg, Throwable tr) {
        return log(WARN, msg, tr);
    }

    public static int w(String tag, String msg, Throwable tr) {
        return log(WARN, tag, msg, tr);
    }

    //===================================LogUtil.e

    public static int e(String tag, String msg) {
        return log(ERROR, tag, msg);
    }

    public static int e(String msg) {
        return log(ERROR, msg);
    }

    public static int e(String msg, Throwable tr) {
        return log(ERROR, msg, tr);
    }

    public static int e(String tag, String msg, Throwable tr) {
        return log(ERROR, tag, msg, tr);
    }


    //===============================================impl


    private static int log(int level, String msg) {
        return log(level, DEFAULT_TAG, msg);
    }

    private static int log(int level, String msg, Throwable tr) {
        return log(level, DEFAULT_TAG, msg, tr);
    }

    private static int log(int level, String tag, String msg) {
        if (sIsDebug) {
            saveLog(tag + " " + msg);
            return Log.println(level, tag, msg);
        }
        return -1;
    }

    private static int log(int level, String tag, String msg, Throwable tr) {
        if (sIsDebug) {
            saveLog(tag + " " + msg);
            return Log.println(level, tag, msg + ":\n" + Log.getStackTraceString(tr));
        }
        return -1;
    }

    private static void saveLog(String msg) {
        LogManger.getInstance().saveLog(msg);
    }


}

