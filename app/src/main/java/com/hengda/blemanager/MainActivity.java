package com.hengda.blemanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //模拟{"userOperate":2,"prId":3,"baseLockInstruct":"F41011","bluetoothMacAddress":"00:15:87:00:86:CF","type":10}
        //真实{"userOperate":2,"prId":3,"baseLockInstruct":"F40002","bluetoothMacAddress":"34:15:13:DD:8F:40","type":10}
        BleManager.getInstance().init(MainActivity.this);
        findViewById(R.id.tv_ble_conn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BleManager.getInstance().connectBle("34:15:13:DD:8F:40", new BleManager.OnBleConnListener() {
                    @Override
                    public void onConnSuccess() {
                        Log.d(TAG, "onConnSuccess: ");
                    }

                    @Override
                    public void onConnFail() {
                        Log.d(TAG, "onConnFail: ");
                    }
                });
            }
        });

        findViewById(R.id.tv_unlock).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BleManager.getInstance().unLock("F40010", new BleManager.OnLockOrUnLockListener() {
                    @Override
                    public void onLockOrUnLockWrite() {
                        BleManager.getInstance().writeData("F40010");
                    }

                    @Override
                    public void onLockOrUnLockSuccess() {
                        Log.d(TAG, "onLockOrUnLockSuccess: ");
                    }

                    @Override
                    public void onLockOrUnLockFail() {
                        Log.d(TAG, "onLockOrUnLockFail: ");
                    }

                    @Override
                    public void onLockOrUnLockNoNeed() {
                        Log.d(TAG, "onLockOrUnLockNoNeed: ");
                    }
                });
            }
        });

        findViewById(R.id.tv_lock).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BleManager.getInstance().lock("F40010", new BleManager.OnLockOrUnLockListener() {
                    @Override
                    public void onLockOrUnLockWrite() {
                        BleManager.getInstance().writeData("F40010");
                    }

                    @Override
                    public void onLockOrUnLockSuccess() {
                        Log.d(TAG, "onLockOrUnLockSuccess: ");
                    }

                    @Override
                    public void onLockOrUnLockFail() {
                        Log.d(TAG, "onLockOrUnLockFail: ");
                    }

                    @Override
                    public void onLockOrUnLockNoNeed() {
                        Log.d(TAG, "onLockOrUnLockNoNeed: ");
                    }
                });
            }
        });
    }
}
