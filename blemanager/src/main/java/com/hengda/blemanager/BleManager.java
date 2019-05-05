package com.hengda.blemanager;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BleManager {
    private static final String TAG = "BleManager";
    private Context mContext;
    private static UUID UUID_NOTIFY = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");
    private static UUID UUID_SERVICE = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");//蓝牙特征值
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mBluetoothDevice;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattCharacteristic mBluetoothGattCharacteristic;
    private Parser mParser = new Parser();
    private OnBleConnListener mOnBleConnListener;
    private OnLockOrUnLockListener mOnLockOrUnLockListener;
    private OnCorrectListener mOnCorrectListener;
    private boolean mScanning;//扫描状态
    private final static int SCAN_TIME = 5 * 1000;//扫描时间
    private List<BluetoothDevice> deviceList = new ArrayList<>();//设备list
    private int type = 0;//开锁上锁、上下到位校准

    public static BleManager getInstance() {
        return BleManagerHolder.sBleManager;
    }

    private static class BleManagerHolder {
        @SuppressLint("StaticFieldLeak")
        private static final BleManager sBleManager = new BleManager();
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    /**
     * 初始化
     */
    public void init(Context context) {
        mContext = context;
        BluetoothManager bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothManager != null) {
            mBluetoothAdapter = bluetoothManager.getAdapter();
        }
        if (isSupportBle()) {
            if (!isBlueEnable()) {
                enableBluetooth();
            }
        }
    }

    /**
     * 蓝牙扫描结果的回调
     */
    public BluetoothAdapter.LeScanCallback scanCallBack = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (device != null && device.getName() != null && device.getName().contains("B400")) {
                deviceList.add(device);
                deviceList = removeDuplicate(deviceList);
            }
        }
    };

    /**
     * 扫描蓝牙设备
     *
     * @param enable
     */
    public void scanBluetoothDevice(boolean enable) {
        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(scanCallBack);
                }
            }, SCAN_TIME);
            mScanning = true;
            mBluetoothAdapter.startLeScan(scanCallBack);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(scanCallBack);
        }
    }

    /**
     * 是否支持蓝牙
     *
     * @return true
     */
    private boolean isSupportBle() {
        return mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    /**
     * 打开蓝牙
     */
    private void enableBluetooth() {
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.enable();
        }
    }

    /**
     * 关闭蓝牙
     */
    private void disableBluetooth() {
        if (mBluetoothAdapter != null) {
            if (mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.disable();
            }
        }
    }

    /**
     * 判断蓝牙是否可用
     *
     * @return true
     */
    private boolean isBlueEnable() {
        return mBluetoothAdapter != null && mBluetoothAdapter.isEnabled();
    }

    /**
     * 连接蓝牙
     */
    public void connectBle(String mac, OnBleConnListener onBleConnListener) {
        try {
            if (mScanning) {
                mBluetoothAdapter.stopLeScan(scanCallBack);
                mScanning = false;
            }
            mOnBleConnListener = onBleConnListener;
            closeConnect();
            //通过蓝牙设备地址 获取远程设备 开始连接
            mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(mac);
            //第二个参数 是否要自动连接
            if (mBluetoothDevice != null) {
                mBluetoothGatt = mBluetoothDevice.connectGatt(mContext, false, gattCallBack);
            }

            if (!deviceList.contains(mBluetoothDevice)) {
                deviceList.add(mBluetoothDevice);
            }
            Log.d(TAG, "BluetoothGatt.connect() == " + mBluetoothGatt.connect());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 重连
     */
    private void reconnectBle() {
        try {
            if (mBluetoothDevice != null) {
                mBluetoothGatt = mBluetoothDevice.connectGatt(mContext, false, gattCallBack);
//                Thread.sleep(2000);
                Log.d(TAG, "reconnectBle: ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 开锁
     */
    public void unLock(String nodeId, OnLockOrUnLockListener onLockResultListener) {
        if (mBluetoothGatt == null) {
            reconnectBle();
        }
        mOnLockOrUnLockListener = onLockResultListener;
        type = ControlLockType.UNLOCK;
        readData(nodeId);
    }

    /**
     * 上锁
     */
    public void lock(String nodeId, OnLockOrUnLockListener onLockResultListener) {
        if (mBluetoothGatt == null) {
            reconnectBle();
        }
        mOnLockOrUnLockListener = onLockResultListener;
        type = ControlLockType.LOCK;
        readData(nodeId);
    }

    /**
     * 下到位校准
     */
    public void downCorrect(String nodeId, OnCorrectListener onCorrectListener) {
        if (mBluetoothGatt == null) {
            reconnectBle();
        }
        mOnCorrectListener = onCorrectListener;
        type = ControlLockType.DOWN_CORRECT;
        writeData(nodeId);
    }

    /**
     * 上到位校准
     */
    public void upCorrect(String nodeId, OnCorrectListener onCorrectListener) {
        if (mBluetoothGatt == null) {
            reconnectBle();
        }
        mOnCorrectListener = onCorrectListener;
        type = ControlLockType.UP_CORRECT;
        writeData(nodeId);
    }

    /**
     * 实现连接成功或者失败状态的回调
     */
    private BluetoothGattCallback gattCallBack = new BluetoothGattCallback() {

        //蓝牙连接状态改变后调用 此回调 (断开，连接)
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {//连接成功
                //连接成功后去发现该连接的设备的服务
                Log.d(TAG, "蓝牙发现服务");
                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {//连接失败 或者连接断开都会调用此方法
                Log.d(TAG, "蓝牙连接失败");
                mOnBleConnListener.onConnFail();
                closeConnect();
            }
        }

        //连接成功后发现设备服务后调用此方法
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {//发现该设备的服务
                mBluetoothGatt = gatt;
                //拿到该服务 1,通过UUID拿到指定的服务  2,可以拿到该设备上所有服务的集合
                List<BluetoothGattService> serviceList = gatt.getServices();
                //可以遍历获得该设备上的服务集合，通过服务可以拿到该服务的UUID，和该服务里的所有属性Characteristic
                for (BluetoothGattService bs : serviceList) {
                    if (bs.getUuid().toString().equals(UUID_SERVICE.toString())) {
                        mBluetoothGattCharacteristic = bs.getCharacteristic(UUID_NOTIFY);
                        gatt.setCharacteristicNotification(mBluetoothGattCharacteristic, true);
                        mOnBleConnListener.onConnSuccess();
                        break;
                    }
                }
            }
        }

        // 订阅了远端设备的Characteristic信息后，
        // 当远端设备的Characteristic信息发生改变后,回调此方法
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.d(TAG, "接收:" + MISC.byteArray2String(characteristic.getValue()));
            List<ProtocolBase> list = mParser.parsing(characteristic.getValue());
            if (list != null) {
                for (ProtocolBase base : list) {
                    //判断数据帧有效
                    if (base.isValid()) {
                        if (base.getFunCode() == FunCode.FUN_NODE_WRITE) {//收到回复,提示开锁上锁、上下到位校准
                            if (type == ControlLockType.UP_CORRECT || type == ControlLockType.DOWN_CORRECT) {
                                mOnCorrectListener.onCorrectSuccess();
                            }
                        } else if (base.getFunCode() == FunCode.FUN_NODE_READ) {//F2读取设备状态
                            ProtocolParkingBaseLockState lockState = (ProtocolParkingBaseLockState) base;
                            switch (type) {
                                case ControlLockType.LOCK://上锁
                                    if (lockState.isLockOn()) {
                                        mOnLockOrUnLockListener.onLockOrUnLockNoNeed();
                                    } else {
                                        mOnLockOrUnLockListener.onLockOrUnLockWrite();
                                    }
                                    break;
                                case ControlLockType.UNLOCK://开锁
                                    if (!lockState.isLockOn()) {
                                        mOnLockOrUnLockListener.onLockOrUnLockNoNeed();
                                    } else {
                                        mOnLockOrUnLockListener.onLockOrUnLockWrite();
                                    }
                                    break;
                            }
                        } else if (base.getFunCode() == FunCode.FUN_NODE_REPORT) {//A4上报状态
                            ProtocolParkingBaseLockState lockState = (ProtocolParkingBaseLockState) base;
                            replyData(lockState.getID(), lockState.data[0]);
                            switch (type) {
                                case ControlLockType.LOCK://上锁
                                    if (lockState.isLockOn()) {
                                        mOnLockOrUnLockListener.onLockOrUnLockSuccess();
                                    } else {
                                        mOnLockOrUnLockListener.onLockOrUnLockFail();
                                    }
                                    disConnect();
                                    break;
                                case ControlLockType.UNLOCK://开锁
                                    if (!lockState.isLockOn()) {
                                        mOnLockOrUnLockListener.onLockOrUnLockSuccess();
                                    } else {
                                        mOnLockOrUnLockListener.onLockOrUnLockFail();
                                    }
                                    disConnect();
                                    break;
                            }
                        }
                    }
                }
            }
        }
    };

    public interface OnBleConnListener {
        void onConnSuccess();

        void onConnFail();
    }

    public interface OnLockOrUnLockListener {
        void onLockOrUnLockWrite();

        void onLockOrUnLockSuccess();

        void onLockOrUnLockFail();

        void onLockOrUnLockNoNeed();
    }

    public interface OnCorrectListener {
        void onCorrectSuccess();

        void onCorrectFail();
    }

    /**
     * 读数据
     *
     * @param nodeId 节点id(云锁编号)
     */
    private void readData(String nodeId) {
        ProtocolParkingBaseLockRead p = new ProtocolParkingBaseLockRead(nodeId);
        byte[] dataArray = p.buildCmd();
        if (dataArray != null) {
            for (byte[] data : MISC.splitByteArray(dataArray)) {
                try {
                    if (mBluetoothGattCharacteristic != null) {
                        mBluetoothGattCharacteristic.setValue(data);
                    }
                    Thread.sleep(10);
                    if (mBluetoothGatt != null) {
                        mBluetoothGatt.writeCharacteristic(mBluetoothGattCharacteristic);
                    }
                    Log.d(TAG, "发送：" + MISC.byteArray2String(data));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 写数据
     *
     * @param nodeId 节点id(云锁编号)
     */
    public void writeData(String nodeId) {
        ProtocolParkingBaseLockWrite p = new ProtocolParkingBaseLockWrite(nodeId);
        byte[] dataArray = p.buildCmd(type);
        if (dataArray != null) {
            for (byte[] data : MISC.splitByteArray(dataArray)) {
                try {
                    if (mBluetoothGattCharacteristic != null) {
                        mBluetoothGattCharacteristic.setValue(data);
                    }
                    Thread.sleep(10);
                    if (mBluetoothGatt != null) {
                        mBluetoothGatt.writeCharacteristic(mBluetoothGattCharacteristic);
                    }
                    Log.d(TAG, "发送：" + MISC.byteArray2String(data));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 向云锁回复收到A4数据
     *
     * @param nodeId 节点id(云锁编号)
     */
    private void replyData(String nodeId, byte index) {
        ProtocolParkingBaseLockReply p = new ProtocolParkingBaseLockReply(nodeId);
        byte[] dataArray = p.buildCmd(index);
        if (dataArray != null) {
            for (byte[] data : MISC.splitByteArray(dataArray)) {
                if (mBluetoothGattCharacteristic != null) {
                    mBluetoothGattCharacteristic.setValue(data);
                }
                if (mBluetoothGatt != null) {
                    mBluetoothGatt.writeCharacteristic(mBluetoothGattCharacteristic);
                }
                Log.d(TAG, "发送：" + MISC.byteArray2String(data));
            }
        }
    }

    /**
     * 去掉重复list
     *
     * @param list
     * @return
     */
    public static List<BluetoothDevice> removeDuplicate(List<BluetoothDevice> list) {
        Set set = new LinkedHashSet<>();
        set.addAll(list);
        list.clear();
        list.addAll(set);
        return list;
    }

    //设备列表
    public List<BluetoothDevice> getDeviceList() {
        return deviceList;
    }

    /**
     * 清除蓝牙缓存
     */
    private boolean refreshDeviceCache() {
        if (mBluetoothGatt != null) {
            try {
                BluetoothGatt localBluetoothGatt = mBluetoothGatt;
                Method localMethod = localBluetoothGatt.getClass().getMethod("refresh", new Class[0]);
                if (localMethod != null) {
                    boolean bool = (Boolean) localMethod.invoke(
                            localBluetoothGatt, new Object[0]);
                    return bool;
                }
            } catch (Exception localException) {
                Log.d(TAG, "localException:" + localException.toString());
            }
        }
        return false;
    }

    /**
     * 断开连接
     */
    public void disConnect() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.disconnect();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    /**
     * 关闭连接
     */
    public void closeConnect() {
        if (mBluetoothGatt == null) {
            return;
        }
        refreshDeviceCache();
        mBluetoothGatt.close();
        mBluetoothGatt = null;
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
//        disableBluetooth();
    }
}
