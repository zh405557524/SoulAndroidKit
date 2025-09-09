package com.soul.common.module.net;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 网络管理
 * Author: 祝明
 * CreateDate: 2019-07-02 17:35
 * UpdateUser:
 * UpdateDate: 2019-07-02 17:35
 * UpdateRemark:
 */
public class NetManager implements INetManager {


    private static final int NETWORK_TYPE_GSM = 16;
    private static final int NETWORK_TYPE_TD_SCDMA = 17;
    private static final int NETWORK_TYPE_IWLAN = 18;

    /**
     * 网络状态
     */
    private int mCurrentNetType;

    private static NetManager sNetManager;

    /**
     * 网络监听的list集合
     */
    private List<NetChangeListener> mNetChangeListeners;

    /**
     * 网络状态广播
     */
    private final NetworkConnectChangedReceiver mNetBroadcastReceiver;

    public static NetManager getInstance(Context context) {
        if (sNetManager == null) {
            synchronized (NetManager.class) {
                if (sNetManager == null) {
                    sNetManager = new NetManager(context);
                }
            }
        }
        return sNetManager;
    }

    //单例
    private NetManager(Context context) {
        mNetChangeListeners = new ArrayList<>();

        //1 主动获取一次网络连接类型(如果耗时需要切线程处理)
        mCurrentNetType = getNetType(context, true);
        //2 开始监听网络连接类型
        /**------------------------------网络状态变化广播 start--------------------------------*/
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        mNetBroadcastReceiver = new NetworkConnectChangedReceiver();
        //注册广播接收
        context.registerReceiver(mNetBroadcastReceiver, filter);
        /**------------------------------网络状态变化广播 end--------------------------------*/
        //3 网络状态发生变化，需要notify

    }

    @Override
    public void regNetChangeListener(NetChangeListener netChangeListener) {
        if (mNetChangeListeners != null) {
            mNetChangeListeners.add(netChangeListener);
        }
    }

    @Override
    public void unRegNetChangeListener(NetChangeListener netChangeListener) {
        if (mNetChangeListeners != null) {
            mNetChangeListeners.remove(netChangeListener);
        }
    }


    /**
     * 获取网络状态
     *
     * @param context
     * @param isRefresh true 强制刷新 ；false 使用缓存状态
     * @return
     */
    @Override
    public int getNetType(Context context, boolean isRefresh) {
        if (!isRefresh) {
            return mCurrentNetType;
        } else {
            mCurrentNetType = NET_TYPE_NONE;
            NetworkInfo info = getActiveNetworkInfo(context);
            if (info != null && info.isAvailable()) {

                if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                    mCurrentNetType = NET_TYPE_WIFI;
                } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                    switch (info.getSubtype()) {

                        case NETWORK_TYPE_GSM:
                        case TelephonyManager.NETWORK_TYPE_GPRS:
                        case TelephonyManager.NETWORK_TYPE_CDMA:
                        case TelephonyManager.NETWORK_TYPE_EDGE:
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_IDEN:
                            mCurrentNetType = NET_TYPE_2G;
                            break;

                        case NETWORK_TYPE_TD_SCDMA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_A:
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        case TelephonyManager.NETWORK_TYPE_EHRPD:
                        case TelephonyManager.NETWORK_TYPE_HSPAP:
                            mCurrentNetType = NET_TYPE_3G;
                            break;

                        case NETWORK_TYPE_IWLAN:
                        case TelephonyManager.NETWORK_TYPE_LTE:
                            mCurrentNetType = NET_TYPE_4G;
                            break;
                        default:

                            String subtypeName = info.getSubtypeName();
                            if (subtypeName.equalsIgnoreCase("TD-SCDMA")
                                    || subtypeName.equalsIgnoreCase("WCDMA")
                                    || subtypeName.equalsIgnoreCase("CDMA2000")) {
                                mCurrentNetType = NET_TYPE_3G;
                            } else {
                                mCurrentNetType = NET_TYPE_UNKOWN;
                            }
                            break;
                    }

                    if (mCurrentNetType == NET_TYPE_UNKOWN && isWifi5G(context)) {
                        mCurrentNetType = NET_TYPE_5G;
                    }

                } else {
                    mCurrentNetType = NET_TYPE_UNKOWN;
                }
            }
            if (mCurrentNetType == NET_TYPE_UNKOWN && isAvailable(context, true)) {
                //无法判断出网络类型，且有网络链接时，判定为其他链接类型
                mCurrentNetType = NET_TYPE_UNKOWN;
            }
            return mCurrentNetType;
        }
    }


    /**
     * 判断是否有网络
     *
     * @param context
     * @param isRefresh true 强制刷新 ；false 使用缓存状态
     * @return
     */
    @Override
    public boolean isAvailable(Context context, boolean isRefresh) {
        if (!isRefresh) {
            return mCurrentNetType != NET_TYPE_NONE;
        } else {
            ConnectivityManager manager = (ConnectivityManager) context.getApplicationContext().getSystemService(
                    Context.CONNECTIVITY_SERVICE);
            if (null == manager)
                return false;
            @SuppressLint("MissingPermission") NetworkInfo info = manager.getActiveNetworkInfo();
            if (null == info || !info.isAvailable())
                return false;
            return true;
        }
    }

    /**
     * 判断 wifi 是否是 5G 频段.
     * 需要权限:
     * <uses-permission android:name="android.permission.INTERNET" />
     * <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
     * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
     * <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
     *
     * @return
     */
    private boolean isWifi5G(Context context) {
        int freq = 0;
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.LOLLIPOP) {
            freq = wifiInfo.getFrequency();
        } else {
            String ssid = wifiInfo.getSSID();
            if (ssid != null && ssid.length() > 2) {
                String ssidTemp = ssid.substring(1, ssid.length() - 1);
                @SuppressLint("MissingPermission") List<ScanResult> scanResults = wifiManager.getScanResults();
                for (ScanResult scanResult : scanResults) {
                    if (scanResult.SSID.equals(ssidTemp)) {
                        freq = scanResult.frequency;
                        break;
                    }
                }
            }
        }
        return freq > 4900 && freq < 5900;
    }


    /**
     * 获取活动网络信息
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>}</p>
     *
     * @return NetworkInfo
     */
    @SuppressLint("MissingPermission")
    private static NetworkInfo getActiveNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }


    /**
     * 销毁
     */
    public void onDestroy(Context context) {
        context.unregisterReceiver(mNetBroadcastReceiver);
        if (mNetChangeListeners != null) {
            mNetChangeListeners.clear();
            mNetChangeListeners = null;
        }
    }

    private class NetworkConnectChangedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (!TextUtils.isEmpty(action) && ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                final int netType = getNetType(context, true);
                if (mNetChangeListeners != null) {
                    for (NetChangeListener netChangeListener : mNetChangeListeners) {
                        netChangeListener.onNetStateChange(netType);
                    }
                }
            }
        }
    }
}