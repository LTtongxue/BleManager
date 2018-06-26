package com.hengda.blemanager;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private ParseFrame nodeParser = new ParseFrame();

    public List<ProtocolBase> parsing(byte[] recvData) {
        List<ProtocolBase> baseList = new ArrayList<>();
        List<LByteArray> frameList = new ArrayList<>();
        List<LByteArray> tmpList = nodeParser.getFrameList(recvData);
        if (tmpList != null) {
            frameList.addAll(tmpList);
        }
        for (LByteArray lframe : frameList) {
            baseList.add(parseOne(lframe));
        }
        return baseList;
    }

    private ProtocolBase parseOne(LByteArray lframe) {
        ProtocolBase base = new ProtocolBase(lframe);
        if (base.getFunCode() == FunCode.FUN_NODE_WRITE) {
            base = new ProtocolParkingBaseLockWrite(lframe);
        } else if (base.getFunCode() == FunCode.FUN_NODE_REPORT || base.getFunCode() == FunCode.FUN_NODE_READ) {
            base = new ProtocolParkingBaseLockState(lframe);
        }
        return base;
    }
}
