package com.hengda.blemanager;

public interface ControlLockType {
    byte UNLOCK = 1;  // 开锁
    byte LOCK = 2;  // 上锁
    byte DOWN_CORRECT = 3;  // 下到位
    byte UP_CORRECT = 4;  // 上到位
}
