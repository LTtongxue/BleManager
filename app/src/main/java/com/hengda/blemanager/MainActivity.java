package com.hengda.blemanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "BleManager";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //模拟{"userOperate":2,"prId":3,"baseLockInstruct":"F41011","bluetoothMacAddress":"00:15:87:00:86:CF","type":10}
        //真实{"userOperate":2,"prId":3,"baseLockInstruct":"F40010","bluetoothMacAddress":"34:15:13:DD:2C:7F","type":10}
        //连接蓝牙
        findViewById(R.id.tv_ble_conn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BleManager.getInstance().init(MainActivity.this);
                BleManager.getInstance().connectBle("34:15:13:DD:2C:7F", new BleManager.OnBleConnListener() {
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

        //开锁
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

        //上锁
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

        //下到位
        findViewById(R.id.tv_down_correct).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BleManager.getInstance().downCorrect("F40010", new BleManager.OnCorrectListener() {
                    @Override
                    public void onCorrectSuccess() {
                        Log.d(TAG, "onCorrectSuccess: ");
                    }

                    @Override
                    public void onCorrectFail() {
                        Log.d(TAG, "onCorrectFail: ");
                    }
                });
            }
        });

        //上到位
        findViewById(R.id.tv_up_correct).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BleManager.getInstance().upCorrect("F40010", new BleManager.OnCorrectListener() {
                    @Override
                    public void onCorrectSuccess() {
                        Log.d(TAG, "onCorrectSuccess: ");
                    }

                    @Override
                    public void onCorrectFail() {
                        Log.d(TAG, "onCorrectFail: ");
                    }
                });
            }
        });

        //断开连接
        findViewById(R.id.tv_close_connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BleManager.getInstance().closeConnect();
            }
        });
    }
}
