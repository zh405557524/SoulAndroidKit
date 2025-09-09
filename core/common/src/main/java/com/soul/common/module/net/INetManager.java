package com.soul.common.module.net;

import android.content.Context;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;

/**
 * Description: 网络管理 接口
 * Author: 祝明
 * CreateDate: 2019-07-02 17:35
 * UpdateUser:
 * UpdateDate: 2019-07-02 1735
 * UpdateRemark:
 */
public interface INetManager {

    /**
     * 网络未连接
     */
    int NET_TYPE_NONE = 0;
    /**
     * 网络类型为2g
     */
    int NET_TYPE_2G = 1;
    /**
     * 网络类型为3G
     */
    int NET_TYPE_3G = 2;
    /**
     * 网络类型为4G
     */
    int NET_TYPE_4G = 3;
    /**
     * 网络类型为5G
     */
    int NET_TYPE_5G = 4;
    /**
     * 网络类型为WIFI
     */
    int NET_TYPE_WIFI = 5;
    /**
     * 网络类型为其他连接方式
     */
    int NET_TYPE_UNKOWN = 6;//连接类型为蓝牙、usb等其他类型

    @IntDef({NET_TYPE_NONE, NET_TYPE_2G, NET_TYPE_3G, NET_TYPE_4G, NET_TYPE_5G, NET_TYPE_WIFI, NET_TYPE_UNKOWN})
    @Retention(RetentionPolicy.SOURCE)
    @interface NetType {

    }

    /**
     * 注册网络状态监听
     */
    void regNetChangeListener(NetChangeListener netChangeListener);

    /**
     * 注销网络状态监听
     */
    void unRegNetChangeListener(NetChangeListener netChangeListener);


    /**
     * 获取网络状态
     *
     * @param isRefresh true 强制刷新 ；false 使用缓存状态
     * @return {@link NetType}
     */
    int getNetType(Context context, boolean isRefresh);

    /**
     * 判断当前网络状态
     *
     * @param isRefresh true 强制刷新 ；false 使用缓存状态
     * @return true 有网络，false 无网络
     */
    boolean isAvailable(Context context, boolean isRefresh);


}
