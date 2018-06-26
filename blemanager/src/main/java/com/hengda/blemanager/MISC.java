package com.hengda.blemanager;

public class MISC {

    public static int byte2Int(byte value) {
        return ((int) value & 0x000000ff);
    }

    public static String byte2String(byte value) {
        String str = "";
        if (byte2Int(value) < 0x10) {
            str += "0";
        }

        str += Integer.toHexString(byte2Int(value));

        return str;
    }

    public static String byteArray2String(byte[] array) {
        return byteArray2String(array, 0, array.length, true);
    }

    public static String byteArray2String(byte[] array, boolean addSpace) {
        return byteArray2String(array, 0, array.length, addSpace);
    }

    public static String byteArray2String(byte[] array, int offset, int length, boolean addSpace) {
        String str = "";
        for (int i = 0; i < length; i++) {
            str += byte2String(array[offset + i]);
            if (addSpace) {
                str += " ";
            }
        }
        return str.toUpperCase();
    }

    public static byte xorCheck(byte[] dataArray) {
        byte xorCrc = 0;
        for (int i = 0; i < dataArray.length; i++) {
            xorCrc ^= dataArray[i];
        }
        return xorCrc;
    }

    public static byte[] int2ByteArray(int value, int length) {
        byte[] array = new byte[length];
        int offset = 0;
        for (int i = length - 1; i >= 0; i--) {
            array[offset] = (byte) ((value >> i * 8) & 0x000000ff);
            offset++;
        }
        return array;
    }

    public static int byteArray2Int(byte[] array, int offset, int length) {
        int d = 0;
        if (array == null || array.length < offset + length) {
            return -1;
        }
        for (int i = 0; i < length; i++) {
            d |= ((array[offset + i] & 0xff) << (length - (i + 1)) * 8);
        }
        return d;
    }

    public static byte[] string2ByteArray(String str) {
        int offset = 0;
        byte[] dataArray = new byte[(str.length() + 1) / 2];
        for (int i = 0; i < str.length(); i += 2) {
            String temp;
            if (i + 2 > str.length()) {
                temp = str.substring(i, i + 1);
            } else {
                temp = str.substring(i, i + 2);
            }
            dataArray[offset] = Integer.valueOf(temp, 16).byteValue();
            offset++;
        }
        return dataArray;
    }

    public static byte[][] splitByteArray(byte[] array) {
        int arrayLength = array.length % 20 == 0 ? (array.length / 20) : ((array.length / 20) + 1);
        byte[][] dataArray = new byte[arrayLength][];
        for (int i = 0; i < arrayLength; i++) {
            byte[] d = null;

            if (i == arrayLength - 1) {
                d = new byte[array.length - i * 20];
            } else {
                d = new byte[20];
            }

            System.arraycopy(array, i * 20, d, 0, d.length);
            dataArray[i] = d;
        }

        return dataArray;
    }

    public static byte[] ip2ByteArray(String ipstr) {
        String[] ipArray = ipstr.split("\\.");
        byte[] ipByteArray = new byte[4];

        ipByteArray[0] = (byte) ((Integer.valueOf(ipArray[0]).intValue() & 0x000000ff));
        ipByteArray[1] = (byte) ((Integer.valueOf(ipArray[1]).intValue() & 0x000000ff));
        ipByteArray[2] = (byte) ((Integer.valueOf(ipArray[2]).intValue() & 0x000000ff));
        ipByteArray[3] = (byte) ((Integer.valueOf(ipArray[3]).intValue() & 0x000000ff));
        return ipByteArray;
    }

    public static String byteArray2Ip(byte[] array, int offset) {
        String ipStr = new String();
        ipStr += Integer.toString(byte2Int(array[offset + 0]));
        ipStr += ".";
        ipStr += Integer.toString(byte2Int(array[offset + 1]));
        ipStr += ".";
        ipStr += Integer.toString(byte2Int(array[offset + 2]));
        ipStr += ".";
        ipStr += Integer.toString(byte2Int(array[offset + 3]));
        return ipStr;
    }
}
