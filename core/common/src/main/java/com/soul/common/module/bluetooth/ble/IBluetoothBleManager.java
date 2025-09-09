package com.soul.common.module.bluetooth.ble;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;

import java.util.List;

/**
 * Description: 蓝牙接口
 * Author: 祝明
 * CreateDate: 2021/5/10 10:27
 * UpdateUser:
 * UpdateDate: 2021/5/10 10:27
 * UpdateRemark:
 */
public interface IBluetoothBleManager {


    /**
     * 蓝牙是否打开
     *
     * @return true 已打开; false 未打开
     */
    boolean isOpen();

    /**
     * 打开蓝牙设置
     */
    void openSetting(Activity activity);

    /**
     * 搜索蓝牙设备
     */
    void searchDevice( );

    /**
     * 获取蓝牙信息
     */
    List<BluetoothDevice> getDeviceInfo();

    /**
     * 进行蓝牙连接
     *
     * @param bluetoothDevice 蓝牙设备
     */
    void connect(Context context, BluetoothDevice bluetoothDevice);

    /**
     * 发送数据
     */
    void sendData();

    void disConnect();


}
