package com.soul.common.module.bluetooth;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.soul.lib.module.bluetooth.ble.BleCommunication;
import com.soul.lib.module.bluetooth.ble.BluetoothBleConnectManager;
import com.soul.lib.utils.LogUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import androidx.annotation.RequiresApi;

/**
 * Description: 蓝牙连接数据处理中心
 * Author: 祝明
 * CreateDate: 2021/6/11 18:11
 * UpdateUser:
 * UpdateDate: 2021/6/11 18:11
 * UpdateRemark:
 */
public class BluetoothProcessorManager implements BluetoothBleConnectManager.OnBluetoothBleConnectListener {

    public static String TAG = BluetoothProcessorManager.class.getSimpleName();

    private final BluetoothBleConnectManager mBluetoothBleConnectManager;
    private List<BluetoothGattCharacteristic> mBluetoothGattCharacteristics;
    private int[] mToken = new int[]{232, 19};
    private BluetoothGatt mBluetoothGatt;
    private final Handler mHandler;


    private byte mDeviceType = 1;

    private byte[] keyTab;

    private int mFrequency;
    private String mMac;
    private long mLastHeartbeatTimestamp;
    private String keyTabStr;

    public BluetoothProcessorManager() {
        mBluetoothBleConnectManager = new BluetoothBleConnectManager();
        mBluetoothBleConnectManager.setOnBluetoothBleConnectListener(this);
        mHandler = mBluetoothBleConnectManager.getHandler();

        keyTab = new byte[0];
    }


    /**
     * 开始连接
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void startConnect(Activity activity) {
        //第一步 搜索
        mBluetoothBleConnectManager.search(activity);
    }
    /**------------------------------蓝牙连接 回调 start--------------------------------*/


    /**
     * 搜索设备回调
     *
     * @param device
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onScanDevice(BluetoothDevice device) {
        if (isConnectClassicBT(device.getAddress()) && matchBleAddress(device)) {//蓝牙设备匹配成功，则进行连接
            LogUtils.i(TAG, "匹配成功:" + device.getName() + "   " + device.getAddress());
            mBluetoothBleConnectManager.connect(device);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onServices(List<BluetoothGattService> services) {
        BluetoothGattService bluetoothGattService = matchServices(services);//匹配到数据传输的服务
        if (bluetoothGattService != null) {
            mBluetoothGattCharacteristics = bluetoothGattService.getCharacteristics();
            //发送ble认证
            sendCertification();
        }
    }

    @Override
    public void onConnected(BluetoothGatt bluetoothGatt) {
        mBluetoothGatt = bluetoothGatt;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        handlerBlueConnectMessage(characteristic.getValue());
    }

    /**------------------------------蓝牙连接 回调 end--------------------------------*/

    /**
     * 发送ble认证
     */
    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void sendCertification() {
        this.mToken[0] = (new Random()).nextInt(99);
        this.mToken[1] = (new Random()).nextInt(99);
        final int t1 = this.mToken[0];
        final int t2 = this.mToken[1];
        if (mBluetoothGatt == null) {
            LogUtils.i(TAG, "mBluetoothGatt  is null");
            return;
        }
        LogUtils.i(TAG, "send certification ");
        int index = 1;
        List<String> uuids = new ArrayList<>();
        Iterator<BluetoothGattCharacteristic> iterator = mBluetoothGattCharacteristics.iterator();

        while (true) {
            BluetoothGattCharacteristic characteristic;
            boolean enabled;
            do {
                if (!iterator.hasNext()) {
                    return;
                }
                characteristic = (BluetoothGattCharacteristic) iterator.next();
                LogUtils.i(TAG, "BluetoothGattService characteristic :" + characteristic.getUuid().toString());
                if (uuids.contains(characteristic.getUuid().toString())) {
                    return;
                }

                uuids.add(characteristic.getUuid().toString());
                enabled = this.mBluetoothGatt.setCharacteristicNotification(characteristic, true);
            } while (!enabled);

            characteristic.setWriteType(2);

            for (Iterator var8 = characteristic.getDescriptors().iterator(); var8.hasNext(); ++index) {
                final BluetoothGattDescriptor dp = (BluetoothGattDescriptor) var8.next();
                if ((characteristic.getProperties() & 16) != 0) {
                    dp.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                } else if ((characteristic.getProperties() & 32) != 0) {
                    dp.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
                }

                final BluetoothGattCharacteristic finalCharacteristic = characteristic;
                mHandler.postDelayed(new Runnable() {
                    @SuppressLint({"NewApi"})
                    public void run() {
                        if (BluetoothProcessorManager.this.mBluetoothGatt != null) {
                            Log.e("写描述结果", mBluetoothGatt.writeDescriptor(dp) + " BluetoothGattDescriptor:" + dp.getUuid());
                        }

                        if (finalCharacteristic.getDescriptors().indexOf(dp) == finalCharacteristic.getDescriptors().size() - 1 && finalCharacteristic.getUuid().toString().contains("000010b1")) {
                            sendToDevice("000010b1", new byte[]{10, 1}, new byte[]{ByteUtil.hexToByte(Integer.toHexString(t1)), ByteUtil.hexToByte(Integer.toHexString(t2))});
                        }

                    }
                }, index * 200);
            }
        }

    }

    /**
     * 发送数据给设备
     *
     * @param uuid
     * @param cmd
     * @param data
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void sendToDevice(String uuid, byte[] cmd, byte[] data) {

        if (mBluetoothGatt == null) {
            LogUtils.i(TAG, "mBluetoothGatt  is null");
            return;
        }

        boolean isSend = false;
        Iterator var5 = this.mBluetoothGatt.getServices().iterator();
        while (var5.hasNext()) {
            BluetoothGattService service = (BluetoothGattService) var5.next();
            Iterator var7 = service.getCharacteristics().iterator();

            while (var7.hasNext()) {
                final BluetoothGattCharacteristic characteristic = (BluetoothGattCharacteristic) var7.next();
                if (characteristic.getUuid().toString().toLowerCase().contains(uuid) && !isSend) {
                    Log.d(this.TAG, "sendToDevice uuid:" + characteristic.getUuid());
                    isSend = true;
                    byte[] protocol = this.createProtocol(cmd, data);
                    characteristic.setWriteType(1);
                    characteristic.setValue(protocol);

                    LogUtils.i(TAG, "sendToDevice uuid:" + characteristic.getUuid());
                    LogUtils.i(TAG, "sendToDevice data:" + Arrays.toString(protocol));
                    mHandler.postDelayed(new Runnable() {
                        @SuppressLint("MissingPermission")
                        public void run() {
                            if (mBluetoothGatt != null) {
                                mBluetoothGatt.writeCharacteristic(characteristic);
                            }

                        }
                    }, 1000);
                }
            }
        }
    }

    /**
     * 匹配蓝牙服务
     *
     * @param services
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private BluetoothGattService matchServices(List<BluetoothGattService> services) {
        for (BluetoothGattService service : services) {
            if (service.getUuid().toString().toLowerCase().trim().toLowerCase().contains("000010b0")) {
                return service;
            }
        }
        return null;
    }


    /**
     * 匹配ble蓝牙耳机地址
     */
    @SuppressLint("MissingPermission")
    private boolean matchBleAddress(BluetoothDevice device) {
        String[] ad1 = device.getAddress().split(":");
        String[] ad2 = device.getAddress().split(":");
        if (ad1.length > 2 && ad2.length > 2 && ad1[0].equals(ad2[0]) && ad1[1].equals(ad2[1]) && !TextUtils.isEmpty(device.getName())) {
            LogUtils.i(TAG, "matchBleAddress succeed:" + device.getName() + "   " + device.getAddress());
            return true;
        }
        return false;
    }


    /**
     * 判断给定的设备mac地址是否已连接经典蓝牙
     *
     * @param macAddress 设备mac地址,例如"78:02:B7:01:01:16"
     * @return
     */
    @SuppressLint("MissingPermission")
    public static boolean isConnectClassicBT(String macAddress) {
        if (TextUtils.isEmpty(macAddress)) {
            return false;
        }
        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Class<BluetoothAdapter> bluetoothAdapterClass = BluetoothAdapter.class;//得到BluetoothAdapter的Class对象
        try {
            //是否存在连接的蓝牙设备
            Method method = bluetoothAdapterClass.getDeclaredMethod("getConnectionState", (Class[]) null);
            //打开权限
            method.setAccessible(true);
            int state = (int) method.invoke(bluetoothAdapter, (Object[]) null);
            if (state == BluetoothAdapter.STATE_CONNECTED) {
                Log.d("test", "BluetoothAdapter.STATE_CONNECTED");
                @SuppressLint("MissingPermission") Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();
                Log.d("test", "devices:" + devices.size());
                for (BluetoothDevice device : devices) {
                    Method isConnectedMethod = BluetoothDevice.class.getDeclaredMethod("isConnected", (Class[]) null);
                    method.setAccessible(true);
                    boolean isConnected = (boolean) isConnectedMethod.invoke(device, (Object[]) null);
                    if (isConnected) {
                        {
                            LogUtils.i(TAG, "macAddress.contains(device.getAddress())" + macAddress.contains(device.getAddress()));
                            LogUtils.i(TAG, "macAddress:" + macAddress + "   device.getAddress():" + device.getAddress());
                            return macAddress.contains(device.getAddress());
                        }
                    } else {
                        Log.d("test", device.getName() + " connect false(" + device.getAddress() + ")");
                    }
                }

            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 检查蓝牙设备keyTab
     *
     * @return
     */
    private boolean checkBluetoothDevice(BluetoothDevice device, int rssi, byte[] scanRecord) {
        byte[] newByte = new byte[12];
        Log.d(this.TAG, "ScanRecord 1:" + Arrays.toString(scanRecord));
        int index = -1;

        for (int i = 0; i < scanRecord.length - 5; ++i) {
            if (index < scanRecord.length - 5 && (scanRecord[i] == 65 || scanRecord[i] == 13) && scanRecord[i + 1] == 1) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            return false;
        } else {
            System.arraycopy(scanRecord, index, newByte, 0, 12);
            Log.d(this.TAG, "ScanRecord 2:" + Arrays.toString(newByte));
            boolean address = false;
            String key;
            //            if (this.mConnectDevices != null && this.mConnectDevices.size() > 0) {
            //                byte[] mac = new byte[6];
            //                System.arraycopy(newByte, 2, mac, 0, 6);
            //                Log.v("Mac", Arrays.toString(mac));
            //                key = ByteUtil.bytesToMac(mac).toUpperCase();
            //                Log.v("Mac", key);
            //                Iterator var10 = this.mConnectDevices.iterator();
            //
            //                while (var10.hasNext()) {
            //                    BluetoothDevice bluetooth = (BluetoothDevice) var10.next();
            //                    if (bluetooth.getAddress().toUpperCase().equals(key)) {
            address = true;
            //                    } else {
            //                        this.callbackMsg("校验BLE广播数据失败 Mac地址不匹配 经典地址：" + bluetooth.getAddress() + " 广播中地址: " + key);
            //                    }
            //                }
            //            }

            //            Iterator var15 = BluetoothSDK.getKeyList().iterator();
            //
            //            boolean check;
            //            byte[] ik;
            //            do {
            //                do {
            //                    if (!var15.hasNext()) {
            //                        return false;
            //                    }
            //
            //                    key = (String) var15.next();
            //                } while (TextUtils.isEmpty(key));
            //
            //                String[] ks = key.split(",");
            //                ik = new byte[ks.length];
            //
            //                for (int i = 0; i < ks.length; ++i) {
            //                    String k = ks[i];
            //                    ik[i] = Integer.valueOf(k).byteValue();
            //                }
            //
            //                check = this.checkBroadcastData(newByte, ik);
            //                Log.d(this.TAG, "checkDevice iValue:" + check);
            //            } while (!check || !address);
            //
            //            this.keyTab = ik;
            //            this.keyTabStr = key;
        }
        return true;
    }

    /**
     * 创建通讯数据
     *
     * @param cmd
     * @param data
     * @return
     */
    private byte[] createProtocol(byte[] cmd, byte[] data) {
        byte[] protocol = new byte[16];
        byte[] head = new byte[]{-21, this.mDeviceType};
        System.arraycopy(head, 0, protocol, 0, 2);
        byte[] length = new byte[]{ByteUtil.hexToByte(Integer.toHexString(cmd.length + data.length))};
        System.arraycopy(length, 0, protocol, 2, 1);
        System.arraycopy(cmd, 0, protocol, 3, 2);
        System.arraycopy(data, 0, protocol, 5, data.length);
        byte[] sub = new byte[length.length + cmd.length + data.length + this.keyTab.length];
        System.arraycopy(protocol, 2, sub, 0, sub.length - this.keyTab.length);
        System.arraycopy(keyTab, 0, sub, sub.length - this.keyTab.length, this.keyTab.length);
        Log.d(this.TAG, "sub:" + Arrays.toString(ByteUtil.bytesToDec(sub)));
        byte[] secret = MD5Util.encrypt(sub);
        Log.d(this.TAG, "secret:" + Arrays.toString(ByteUtil.bytesToDec(secret)));
        System.arraycopy(secret, 0, protocol, 5 + data.length, 2);
        Log.d(this.TAG, "createProtocol:" + Arrays.toString(ByteUtil.bytesToDec(protocol)));
        Log.d(this.TAG, "createProtocol:" + ByteUtil.bytesToHexString(protocol));
        return protocol;
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void handlerBlueConnectMessage(byte[] data) {
        int[] iData = ByteUtil.bytesToDec(data);
        byte[] cmd = new byte[2];
        byte[] token = new byte[2];
        System.arraycopy(data, 3, cmd, 0, 2);
        System.arraycopy(data, 5, token, 0, 2);
        Log.d(TAG, "handleCharacteristicChanged token:" + Arrays.toString(token));
        if (!this.isValidMessage(data)) {
            Log.e(TAG, "无效Message");
        } else {
            Log.d(TAG, "handleCharacteristicChanged cmd:" + Arrays.toString(cmd));
            String scmd = ByteUtil.bytesToHexString(cmd);
            if (!TextUtils.isEmpty(scmd)) {
                BleCommunication communication = new BleCommunication(scmd);
                Log.d(TAG, "handleCharacteristicChanged cmd:" + scmd.toLowerCase());
                String var7 = scmd.toLowerCase();
                byte var8 = -1;
                switch (var7.hashCode()) {
                    case 1524722:
                        if (var7.equals("0a01")) {
                            var8 = 0;
                        }
                        break;
                    case 1524723:
                        if (var7.equals("0a02")) {
                            var8 = 2;
                        }
                        break;
                    case 1524724:
                        if (var7.equals("0a03")) {
                            var8 = 1;
                        }
                        break;
                    case 1525684:
                        if (var7.equals("0b02")) {
                            var8 = 3;
                        }
                        break;
                    case 1526644:
                        if (var7.equals("0c01")) {
                            var8 = 4;
                        }
                        break;
                    case 1526645:
                        if (var7.equals("0c02")) {
                            var8 = 5;
                        }
                }

                switch (var8) {
                    case 0:
                        LogUtils.i(TAG, "BLE认证失败：" + Arrays.toString(data));
                        mBluetoothBleConnectManager.disConnect();
                        break;
                    case 1://连接出错
                        mBluetoothBleConnectManager.disConnect();
                        LogUtils.i(TAG, "BLE认证失败：" + Arrays.toString(data));
                        break;
                    case 2:
                        Log.d(this.TAG, "认证");
                        communication.setFrequency(iData[13]);
                        this.mFrequency = communication.getFrequency();
                        byte[] mac = new byte[6];
                        System.arraycopy(data, 7, mac, 0, 6);
                        int[] iMac = ByteUtil.bytesToDec(mac);
                        StringBuilder sb = new StringBuilder();

                        for (int i = 0; i < 6; ++i) {
                            sb.append(Integer.toHexString(iMac[i])).append(":");
                        }

                        if (sb.length() > 0) {
                            sb.deleteCharAt(sb.length() - 1);
                        }

                        this.mMac = sb.toString();
                        communication.setMac(sb.toString());
                        communication.setType(BleCommunication.Type.CERTIFICATION);

                        LogUtils.i(TAG, "BLE认证成功 " + Arrays.toString(iData));
                        break;
                    case 3:
                        Log.d(this.TAG, "心跳");
                        LogUtils.i(TAG, "接收到心跳 " + Arrays.toString(iData));
                        communication.setLeftBattery(iData[7]);
                        communication.setRightBattery(iData[8]);
                        communication.setBoxBattery(iData[9]);
                        communication.setCnt((long) (iData[10] + iData[11] + iData[12]));
                        communication.setType(BleCommunication.Type.HEARTBEAT);
                        this.mLastHeartbeatTimestamp = System.currentTimeMillis();
                        break;
                    case 4:
                        return;
                    case 5:
                        Log.d(this.TAG, "双击");
                        LogUtils.i(TAG, "接收到双击 " + Arrays.toString(iData));
                        this.sendDoubleClickResponse();
                        communication.setType(BleCommunication.Type.DOUBLE_CLICK);
                }

                communication.setMac(this.mMac);
                if (mBluetoothBleConnectManager.getCurrentDevice() != null) {
                    communication.setName(mBluetoothBleConnectManager.getCurrentDevice().getName());
                }
            }
        }
    }

    @RequiresApi(api = 18)
    public void sendDoubleClickResponse() {
        Log.d(this.TAG, "发送双击响应");
        int t1 = this.mToken[0];
        int t2 = this.mToken[1];
        this.sendToDevice("000010b3", new byte[]{12, 1}, new byte[]{ByteUtil.hexToByte(Integer.toHexString(t1)), ByteUtil.hexToByte(Integer.toHexString(t2)), 6});
    }

    /**
     * 判断数据是否有效
     *
     * @param message
     * @return
     */
    private boolean isValidMessage(byte[] message) {
        int length = message[2] + 1;
        byte[] sub = new byte[length + this.keyTab.length];
        System.arraycopy(message, 2, sub, 0, message[2] + 1);
        System.arraycopy(this.keyTab, 0, sub, sub.length - this.keyTab.length, this.keyTab.length);
        Log.v("sub:", Arrays.toString(sub));
        Log.d(TAG, "isValidMessage:" + Arrays.toString(sub));
        byte[] md5Byte = MD5Util.encrypt(sub);
        Log.d(TAG, "isValidMessage:" + Arrays.toString(md5Byte));
        Log.d(TAG, "isValidMessage:" + Arrays.toString(message));
        boolean valid = false;
        if (md5Byte[0] == message[length + 2] && md5Byte[1] == message[length + 3]) {
            valid = true;
        } else {
            Log.d(TAG, "失效数据:" + Arrays.toString(message));
            message[length + 2] = md5Byte[0];
            message[length + 3] = md5Byte[1];
            Log.d(TAG, "正确数据应该为:" + Arrays.toString(message));
        }
        return valid;
    }
}
