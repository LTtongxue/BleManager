package com.hengda.blemanager;

import java.util.ArrayList;
import java.util.List;

public class ParseFrame {
    private LByteArray ldata;

    ParseFrame() {
        ldata = new LByteArray();
    }

    public List<LByteArray> getFrameList(byte[] recvData) {
        return nodeFrame(recvData);
    }

    private List<LByteArray> nodeFrame(byte[] recvData) {
        ldata.append(recvData);
        List<LByteArray> resultList = new ArrayList<LByteArray>();

        while (ldata.length() >= 8) {
            if ((ldata.data()[0] == (byte) 0xAB
                    && ldata.data()[1] == (byte) 0xAB)
                    ) {

                int length = MISC.byte2Int(ldata.data()[6]);
                if (ldata.length() >= 8 + length) {
                    byte[] newFrame = ldata.mid(0, 8 + length);
                    if (MISC.xorCheck(newFrame) == (byte) 0x00) {
                        resultList.add(new LByteArray(newFrame));

                        ldata.deleteFirstByte();
                    } else {
                        ldata.deleteFirstByte();
                        continue;
                    }
                } else {
                    break;
                }
            } else {
                ldata.deleteFirstByte();
                continue;
            }
        }
        return resultList;
    }
}
