package com.hengda.blemanager;

public class ProtocolParkingBaseLockState extends ProtocolBase {

    private boolean lockOn = false;//是否上锁

    ProtocolParkingBaseLockState(LByteArray lframe) {
        super(lframe);

        LByteArray ldata = new LByteArray(data);
        //云锁操作00 00 00 21 04 7E 00 0A 00 00 18 00 80 00 A0 00 40 00
        //读取操作            04 7E 00 0A 00 00 18 00 80 00 A0 00 40 00
        if (funCode == FunCode.FUN_NODE_REPORT
                && ldata.length() > 13
                && data[5] == (byte) 0x04
                && data[6] == (byte) 0x7E
                && data[7] == (byte) 0x00
                && data[8] == (byte) 0x0A) {
            int offset = -126 + 9;
            lockOn = ((data[offset + 130] >> 6) & 1) == 1;
        } else if (funCode == FunCode.FUN_NODE_READ
                && ldata.length() > 9
                && data[1] == (byte) 0x04
                && data[2] == (byte) 0x7E
                && data[3] == (byte) 0x00
                && data[4] == (byte) 0x0A) {
            int offset = -126 + 5;
            lockOn = ((data[offset + 130] >> 6) & 1) == 1;
        } else {
            valid = false;
        }
    }

    public boolean isLockOn() {
        return lockOn;
    }
}
