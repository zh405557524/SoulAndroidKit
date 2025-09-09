package com.soul.common.module.net;

/**
 * Description:网络帮助类
 * Author: 祝明
 * CreateDate: 2019-07-03 11:17
 * UpdateUser:
 * UpdateDate: 2019-07-03 11:17
 * UpdateRemark:
 */
public class NetUtils {


    public static String getNetStateName(int netType) {
        String netTypeName = "无网络";
        switch (netType) {
            case INetManager.NET_TYPE_NONE:
                netTypeName = "无网络";
                break;
            case INetManager.NET_TYPE_2G:
                netTypeName = "2G网络";
                break;
            case INetManager.NET_TYPE_3G:
                netTypeName = "3G网络";
                break;
            case INetManager.NET_TYPE_4G:
                netTypeName = "4G网络";
                break;
            case INetManager.NET_TYPE_5G:
                netTypeName = "5G网络";
                break;
            case INetManager.NET_TYPE_WIFI:
                netTypeName = "WIFI";
                break;
            case INetManager.NET_TYPE_UNKOWN:
                netTypeName = "未知网络";
                break;
        }
        return netTypeName;
    }
}
