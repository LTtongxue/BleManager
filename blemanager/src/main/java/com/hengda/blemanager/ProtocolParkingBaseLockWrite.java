package com.hengda.blemanager;

import android.util.Log;

public class ProtocolParkingBaseLockWrite extends ProtocolBase {

    ProtocolParkingBaseLockWrite(LByteArray lframe) {
        super(lframe);

        LByteArray ldata = new LByteArray(data);
        if (funCode == FunCode.FUN_NODE_WRITE
                && ldata.length() == 5
                && data[1] == (byte) 0x02
                && data[3] == (byte) 0x00
                && data[4] == (byte) 0x01) {
            if (data[2] == (byte) 0x01) {
                Log.d("BleManager", "开锁上锁返回");
            } else if (data[2] == (byte) 0x02) {
                Log.d("BleManager", "上下到位校准返回");
            }
        } else {
            valid = false;
        }
    }

    ProtocolParkingBaseLockWrite(String nodeId) {
        super((byte) 0xF3, nodeId);
    }

    public byte[] buildCmd(int type) {
        LByteArray ldata = new LByteArray();
        ldata.append((byte) 0x00);  // 序号

        if (type == ControlLockType.UNLOCK) {
            ldata.append((byte) 0x02);  // 寄存器地址
            ldata.append((byte) 0x01);

            ldata.append((byte) 0x00);  // 写入的数据长度
            ldata.append((byte) 0x01);

            ldata.append((byte) 0x90);  // 服务器开锁命令
        } else if (type == ControlLockType.LOCK) {
            ldata.append((byte) 0x02);  // 寄存器地址
            ldata.append((byte) 0x01);

            ldata.append((byte) 0x00);  // 写入的数据长度
            ldata.append((byte) 0x01);

            ldata.append((byte) 0x50);  // 服务器上锁命令
        } else if (type == ControlLockType.DOWN_CORRECT) {
            ldata.append((byte) 0x02);  // 寄存器地址
            ldata.append((byte) 0x02);

            ldata.append((byte) 0x00);  // 写入的数据长度
            ldata.append((byte) 0x01);

            ldata.append((byte) 0x11);  // 下到位校准命令
        } else if (type == ControlLockType.UP_CORRECT) {
            ldata.append((byte) 0x02);  // 寄存器地址
            ldata.append((byte) 0x02);

            ldata.append((byte) 0x00);  // 写入的数据长度
            ldata.append((byte) 0x01);

            ldata.append((byte) 0x12);  // 上到位校准命令
        }

        data = ldata.data();
        return encode();
    }
}

