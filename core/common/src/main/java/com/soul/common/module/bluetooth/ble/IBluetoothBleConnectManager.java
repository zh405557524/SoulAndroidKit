package com.soul.common.module.bluetooth.ble;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;

/**
 * Description: 蓝牙连接管理接口
 * Author: 祝明
 * CreateDate: 2021/6/10 18:24
 * UpdateUser:
 * UpdateDate: 2021/6/10 18:24
 * UpdateRemark:
 */
public interface IBluetoothBleConnectManager {


    /**
     * 搜索蓝牙列表
     *
     * @param activity
     */
    void search(Activity activity);

    /**
     * 链接蓝牙
     *
     * @param device 蓝牙设备
     */
    void connect(BluetoothDevice device);

    /**
     * 断开蓝牙链接
     */
    void disConnect();

}
