package com.soul.common.module.bluetooth.ble;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;


import com.soul.common.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.RequiresApi;

/**
 * Description: 蓝牙4.0管理类
 * Author: 祝明
 * CreateDate: 2021/5/10 10:27
 * UpdateUser:
 * UpdateDate: 2021/5/10 10:27
 * UpdateRemark:
 * <p> 需要如下权限
 * <uses-permission android:name="android.permission.BLUETOOTH"/>
 * <uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>
 * <uses-permission android:name="android.permission.VIBRATE" />
 */
public class BluetoothBleManager implements IBluetoothBleManager {

    public static String TAG = BluetoothBleManager.class.getSimpleName();

    private static BluetoothBleManager sBluetoothManager;
    /**
     * 蓝牙连接适配
     */
    private BluetoothAdapter mDefaultAdapter;
    /**
     * 蓝牙设备连接
     */
    private BluetoothGatt mBluetoothGatt;

    public static BluetoothBleManager getInstance() {
        if (sBluetoothManager == null) {
            synchronized (BluetoothBleManager.class) {
                if (sBluetoothManager == null) {
                    sBluetoothManager = new BluetoothBleManager();
                }
            }
        }
        return sBluetoothManager;
    }

    private BluetoothBleManager() {
        mDefaultAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothDevices = new ArrayList<>();

    }

    /**
     * 打开蓝牙设置的请求码
     */
    public static final int openBlueSettingRequestCode = 1;

    /**
     * 蓝牙是否打开
     *
     * @return true 已打开; false 未打开
     */
    @Override
    public boolean isOpen() {
        if (mDefaultAdapter == null) {
            mDefaultAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        if (mDefaultAdapter != null) {
            return mDefaultAdapter.isEnabled();
        }
        return false;
    }


    /**
     * 打开蓝牙设置
     */
    @SuppressLint("MissingPermission")
    @Override
    public void openSetting(Activity activity) {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(enableBtIntent, openBlueSettingRequestCode);
    }

    /**
     * 搜索到蓝牙设备集合
     */
    private final List<BluetoothDevice> mBluetoothDevices;


    /**
     * 搜索蓝牙设备
     */
    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void searchDevice() {
        mBluetoothDevices.clear();
        mDefaultAdapter.startLeScan(mLeScanCallback);
    }

    @Override
    public List<BluetoothDevice> getDeviceInfo() {
        return mBluetoothDevices;
    }

    /**
     * 连接蓝牙
     */
    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void connect(Context context, BluetoothDevice bluetoothDevice) {
        mDefaultAdapter.stopLeScan(mLeScanCallback);
        SystemClock.sleep(100);
        if (bluetoothDevice == null) {
            Log.e(TAG, "bluetoothDevice is null");
            return;
        }
        disConnect();
        mBluetoothGatt = bluetoothDevice.connectGatt(context, false, mBluetoothGattCallback);
    }


    @Override
    public void sendData() {

    }

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void disConnect() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();
        }
    }

    private OnBluetoothConnectListener mOnBluetoothConnectListener;

    /**
     * 蓝牙连接监听
     */
    interface OnBluetoothConnectListener {

        /**
         * 搜索的设备回调
         */
        void onScanDevice(BluetoothDevice device);

        /**
         * 已成功连接蓝牙设备
         *
         * @param bluetoothGatt 蓝牙描述符
         */
        void onConnected(BluetoothGatt bluetoothGatt);

        /**
         * 连接断开
         */
        void onDisConnected();

        /**
         * 获取蓝牙服务成功
         *
         * @param services 蓝牙服务
         */
        void onServices(List<BluetoothGattService> services);

        /**
         * 蓝牙特征回调
         *
         * @param gatt
         * @param characteristic
         */
        void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic);
    }


    /**
     * 设置蓝牙链接监听
     *
     * @param onBluetoothConnectListener
     */
    public void setOnBluetoothConnectListener(OnBluetoothConnectListener onBluetoothConnectListener) {
        mOnBluetoothConnectListener = onBluetoothConnectListener;
    }

    /**
     * 蓝牙搜索回调
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            do {
                if (device == null) {
                    break;
                }
                LogUtils.i(TAG, "onLeScan:" + device.getName());
                mBluetoothDevices.add(device);
                if (mOnBluetoothConnectListener != null) {
                    mOnBluetoothConnectListener.onScanDevice(device);
                }
            } while (false);
        }
    };

    /**
     * 蓝牙连接回调
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private BluetoothGattCallback mBluetoothGattCallback = new BluetoothGattCallback() {

        /**
         * 连接状态回调
         * @param gatt
         * @param status
         * @param newState
         */
        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            LogUtils.i(TAG, "onConnectionStateChange newState:" + newState);
            do {
                if (newState == BluetoothGatt.STATE_CONNECTED) {//已连接
                    //开始扫描服务
                    mBluetoothGatt.discoverServices();
                    LogUtils.i(TAG, "已连接");
                    if (mOnBluetoothConnectListener != null) {
                        mOnBluetoothConnectListener.onConnected(mBluetoothGatt);
                    }
                    break;
                }
                if (newState == BluetoothGatt.STATE_DISCONNECTED) {//断开连接
                    LogUtils.i(TAG, "断开连接");
                    if (mOnBluetoothConnectListener != null) {
                        mOnBluetoothConnectListener.onDisConnected();
                    }
                }
            } while (false);
        }


        /**
         * 扫描服务回调
         * @param gatt
         * @param status
         */
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //获取服务列表
                List<BluetoothGattService> services = mBluetoothGatt.getServices();
                LogUtils.i(TAG, "获取服务成功");
                if (mOnBluetoothConnectListener != null) {
                    mOnBluetoothConnectListener.onServices(services);
                }


            }
        }

        /**
         * 接受数据
         * @param gatt
         * @param characteristic
         * @param status
         */
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            LogUtils.i(TAG, "onCharacteristicWrite");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                LogUtils.i(TAG, "发送成功");
                onCharacteristicChanged(gatt, characteristic);
            }

        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                LogUtils.i(TAG, "收到回调");
                onCharacteristicChanged(gatt, characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            if (mOnBluetoothConnectListener != null) {
                mOnBluetoothConnectListener.onCharacteristicChanged(gatt, characteristic);
            }
        }
    };

}
