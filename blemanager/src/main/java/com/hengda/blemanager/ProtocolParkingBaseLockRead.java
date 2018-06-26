package com.hengda.blemanager;

public class ProtocolParkingBaseLockRead extends ProtocolBase {

    public ProtocolParkingBaseLockRead(String nodeId) {
        super((byte) 0xF2, nodeId);
    }

    //00 04 7E 00 0A
    public byte[] buildCmd() {
        LByteArray ldata = new LByteArray();
        ldata.append((byte) 0x00);  // 序号

        ldata.append((byte) 0x04);  // 寄存器地址
        ldata.append((byte) 0x7E);

        ldata.append((byte) 0x00);  // 写入的数据长度
        ldata.append((byte) 0x0A);

        data = ldata.data();
        return encode();
    }
}
