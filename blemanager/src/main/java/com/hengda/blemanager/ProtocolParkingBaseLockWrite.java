package com.hengda.blemanager;

public class ProtocolParkingBaseLockWrite extends ProtocolBase {

    public ProtocolParkingBaseLockWrite(LByteArray lframe) {
        super(lframe);

        LByteArray ldata = new LByteArray(data);
        if (funCode == FunCode.FUN_NODE_WRITE
                && ldata.length() == 5
                && data[1] == (byte) 0x02
                && data[2] == (byte) 0x01
                && data[3] == (byte) 0x00
                && data[4] == (byte) 0x01) {
        } else {
            valid = false;
        }
    }

    public ProtocolParkingBaseLockWrite(String nodeId) {
        super((byte) 0xF3, nodeId);
    }

    public byte[] buildCmd(int type) {
        LByteArray ldata = new LByteArray();
        ldata.append((byte) 0x00);  // 序号

        ldata.append((byte) 0x02);  // 寄存器地址
        ldata.append((byte) 0x01);

        ldata.append((byte) 0x00);  // 写入的数据长度
        ldata.append((byte) 0x01);

        if (type == 1) {
            ldata.append((byte) 0x90);  // 服务器开锁命令
        } else {
            ldata.append((byte) 0x50);  // 服务器上锁命令
        }

        data = ldata.data();
        return encode();
    }
}
