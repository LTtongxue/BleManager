package com.hengda.blemanager;

public class ProtocolParkingBaseLockState extends ProtocolBase {
    private int lockState = 1;//云锁的状态

    public int getLockState() {
        return lockState;
    }

    public ProtocolParkingBaseLockState(LByteArray lframe) {
        super(lframe);

        LByteArray ldata = new LByteArray(data);
        //开锁操作00 00 00 21 04 7E 00 0A 00 00 18 00 80 00 A0 00 40 00
        //读取操作            04 7E 00 0A 00 00 18 00 80 00 A0 00 40 00
        if (funCode == FunCode.FUN_NODE_REPORT
                && ldata.length() > 13
                && data[5] == (byte) 0x04
                && data[6] == (byte) 0x7E
                && data[7] == (byte) 0x00
                && data[8] == (byte) 0x0A) {
            if ((data[13] & (byte) 0x80) == (byte) 0x80) {//开锁成功
                lockState = 0;
            } else {//开锁失败
                lockState = 1;
            }
        } else if (funCode == FunCode.FUN_NODE_READ
                && ldata.length() > 9
                && data[1] == (byte) 0x04
                && data[2] == (byte) 0x7E
                && data[3] == (byte) 0x00
                && data[4] == (byte) 0x0A) {
            if ((data[9] & (byte) 0x80) == (byte) 0x80) {//开锁状态
                lockState = 2;
            } else {//上锁状态
                lockState = 3;
            }
        } else {
            valid = false;
        }
    }
}
