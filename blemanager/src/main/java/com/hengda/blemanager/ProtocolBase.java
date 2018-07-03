package com.hengda.blemanager;

import java.io.Serializable;

public class ProtocolBase implements Serializable {
    byte funCode;
    private byte[] frame;
    protected byte[] data;
    private String deviceId;
    boolean valid;

    ProtocolBase(LByteArray lframe) {
        this.frame = lframe.data();

        data = lframe.mid(8);
        deviceId = MISC.byteArray2String(frame, 2, 3, false);

        funCode = frame[5];
        valid = true;
    }

    ProtocolBase(byte funCode, String deviceId) {
        this.funCode = funCode;
        if (deviceId.length() == 4) {
            this.deviceId = "00" + deviceId;
        } else {
            this.deviceId = deviceId;
        }
    }

    byte[] encode() {
        byte frameHead = (byte) 0xAA;

        LByteArray frame = new LByteArray();
        frame.append(frameHead);
        frame.append(frameHead);
        frame.append(MISC.string2ByteArray(deviceId));
        frame.append(funCode);

        frame.append((byte) data.length);

        LByteArray temp = new LByteArray(frame);
        temp.append(data);
        byte xor = MISC.xorCheck(temp.data());
        frame.append(xor);
        frame.append(data);
        return frame.data();
    }

    public byte getFunCode() {
        return funCode;
    }

    public String getID() {
        return deviceId;
    }

    public boolean isValid() {
        return valid;
    }

    public byte[] getData() {
        return data;
    }

}
