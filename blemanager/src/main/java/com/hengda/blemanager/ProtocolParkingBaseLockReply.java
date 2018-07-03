package com.hengda.blemanager;

/**
 * 向云锁回复收到A4数据
 */
public class ProtocolParkingBaseLockReply extends ProtocolBase {

    ProtocolParkingBaseLockReply(String nodeId) {
        super((byte) 0xA4, nodeId);
    }

    public byte[] buildCmd(byte index) {
        LByteArray ldata = new LByteArray();
        ldata.append(index);  // 序号

        data = ldata.data();
        return encode();
    }
}

