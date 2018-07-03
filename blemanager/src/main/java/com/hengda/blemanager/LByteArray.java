package com.hengda.blemanager;


public class LByteArray {

    private byte[] data;

    LByteArray() {
        data = null;
    }

    LByteArray(LByteArray src) {
        data = new byte[src.length()];
        System.arraycopy(src.data(), 0, data, 0, src.length());
    }

    LByteArray(byte[] src) {
        append(src);
    }

    public void append(byte[] src) {

        byte[] newData = new byte[length() + src.length];
        if (data != null) {
            System.arraycopy(data, 0, newData, 0, length());
        }
        System.arraycopy(src, 0, newData, length(), src.length);
        data = newData;
    }

    public void append(byte value) {
        byte[] newData = new byte[length() + 1];
        if (data != null) {
            System.arraycopy(data, 0, newData, 0, length());
        }
        newData[length()] = value;
        data = newData;
    }

    public byte[] mid(int offset, int length) {
        if (length() <= 0 || length <= 0 || length() < length) {
            return null;
        }
        byte[] newData = new byte[length];

        System.arraycopy(data, offset, newData, 0, length);

        return newData;
    }

    public byte[] mid(int offset) {
        return mid(offset, length() - offset);
    }

    public void deleteFirstByte() {
        byte[] newData = new byte[length() - 1];
        if (data != null) {
            System.arraycopy(data, 1, newData, 0, length() - 1);
        } else {
            return;
        }
        data = newData;
    }

    public int length() {
        if (null == data) {
            return 0;
        } else {
            return data.length;
        }
    }

    public byte[] data() {
        return data;
    }
}

