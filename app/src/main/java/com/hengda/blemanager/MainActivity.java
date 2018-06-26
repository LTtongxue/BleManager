package com.hengda.blemanager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //模拟{"userOperate":2,"prId":3,"baseLockInstruct":"F41011","bluetoothMacAddress":"00:15:87:00:86:CF","type":10}
        //真实{"userOperate":2,"prId":3,"baseLockInstruct":"F40002","bluetoothMacAddress":"34:15:13:DD:8F:40","type":10}
        BleManager.getInstance().init(MainActivity.this);
        BleManager.getInstance().connect("34:15:13:DD:8F:40", new BleManager.LockResultHandler() {
            @Override
            public void onLockRead() {
                BleManager.getInstance().readData("F40002");
            }

            @Override
            public void onLockWrite() {
                BleManager.getInstance().writeData("F40002");
            }

            @Override
            public void onLockNoNeed() {
                Log.d(TAG, "无需开锁");
            }

            @Override
            public void onLockSuccess() {
                Log.d(TAG, "开锁成功");
            }

            @Override
            public void onLockFail() {
                Log.d(TAG, "开锁失败");
            }
        });
    }
}
