package com.hengda.blemanager;

public interface FunCode {
    byte FUN_NODE_REPORT = (byte) 0xA4;//上报状态
    byte FUN_NODE_WRITE = (byte) 0xF3;//发送接收反馈
    byte FUN_NODE_READ = (byte) 0xF2;//读取设备状态
}
