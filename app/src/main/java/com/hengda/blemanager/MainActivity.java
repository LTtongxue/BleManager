package com.hengda.blemanager;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private CarPortSpinner mCarPortSpinner;
    private TextView mTvCarPort, mTvBleConn, mTvUnlock, mTvLock;
    private LoadingDialog mLoadingDialog;
    private BleManager mBleManager;
    private String[] carPortNum;
    private List<BluetoothDevice> mBluetoothDeviceList;
    private String mMacAddress = "";
    private String mNodeId = "";
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setListener();
    }

    private void initView() {
        PermissionUtils.setPermisson(this, "定位",
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
        mTvCarPort = findViewById(R.id.tv_car_port);
        mTvBleConn = findViewById(R.id.tv_ble_conn);
        mTvUnlock = findViewById(R.id.tv_unlock);
        mTvLock = findViewById(R.id.tv_lock);
        mBleManager = BleManager.getInstance();
        mBleManager.init(MainActivity.this);
        mBleManager.scanBluetoothDevice(true);
    }

    private void setListener() {
        mTvCarPort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseCarPort();
            }
        });

        //连接蓝牙
        mTvBleConn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(mMacAddress)) {
                    Toast.makeText(MainActivity.this, "请选择车位连接蓝牙", Toast.LENGTH_SHORT).show();
                    return;
                }
                getLoadDialog().showDialog("蓝牙连接中,请等待");
                mBleManager.connectBle(mMacAddress, new BleManager.OnBleConnListener() {
                    @Override
                    public void onConnSuccess() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getLoadDialog().dismissDialog();
                                mTvBleConn.setText("连接成功");
                            }
                        });
                    }

                    @Override
                    public void onConnFail() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getLoadDialog().dismissDialog();
                                mTvBleConn.setText("断开连接,重连");
                            }
                        });
                    }
                });
            }
        });

        //开锁
        mTvUnlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(mNodeId)) {
                    Toast.makeText(MainActivity.this, "请选择车位开锁", Toast.LENGTH_SHORT).show();
                    return;
                }
                getLoadDialog().showDialog("请等待开锁");
                mBleManager.unLock(mNodeId, new BleManager.OnLockOrUnLockListener() {
                    @Override
                    public void onLockOrUnLockWrite() {
                        mBleManager.writeData(mNodeId);
                    }

                    @Override
                    public void onLockOrUnLockSuccess() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getLoadDialog().dismissDialog();
                                Toast.makeText(MainActivity.this, "开锁成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onLockOrUnLockFail() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getLoadDialog().dismissDialog();
                                Toast.makeText(MainActivity.this, "开锁失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onLockOrUnLockNoNeed() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getLoadDialog().dismissDialog();
                                Toast.makeText(MainActivity.this, "无需开锁", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });

        //上锁
        mTvLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(mNodeId)) {
                    Toast.makeText(MainActivity.this, "请选择车位上锁", Toast.LENGTH_SHORT).show();
                    return;
                }
                getLoadDialog().showDialog("请等待上锁");
                mBleManager.lock(mNodeId, new BleManager.OnLockOrUnLockListener() {
                    @Override
                    public void onLockOrUnLockWrite() {
                        mBleManager.writeData(mNodeId);
                    }

                    @Override
                    public void onLockOrUnLockSuccess() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getLoadDialog().dismissDialog();
                                Toast.makeText(MainActivity.this, "上锁成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onLockOrUnLockFail() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getLoadDialog().dismissDialog();
                                Toast.makeText(MainActivity.this, "上锁失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onLockOrUnLockNoNeed() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getLoadDialog().dismissDialog();
                                Toast.makeText(MainActivity.this, "无需上锁", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });
    }

    public LoadingDialog getLoadDialog() {
        if (mLoadingDialog == null) {
            synchronized (this) {
                if (mLoadingDialog == null) {
                    mLoadingDialog = new LoadingDialog(this);
                }
            }
        }
        return mLoadingDialog;
    }

    /**
     * 选择车位
     */
    private void chooseCarPort() {
        try {
            getLoadDialog().showDialog("扫描设备,请等待");
            mTvBleConn.setText("连接蓝牙");
            mBleManager.scanBluetoothDevice(true);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBluetoothDeviceList = mBleManager.getDeviceList();
                    carPortNum = new String[mBluetoothDeviceList.size()];
                    for (int i = 0; i < mBluetoothDeviceList.size(); i++) {
                        switch (mBluetoothDeviceList.get(i).getName().substring(4, 10)) {
                            case "B40001":
                                carPortNum[i] = "GX-0000";
                                break;
                            case "B40003":
                                carPortNum[i] = "GX-0001";
                                break;
                            case "B40005":
                                carPortNum[i] = "GX-0002";
                                break;
                            case "B40006":
                                carPortNum[i] = "GX-0003";
                                break;
                            case "B40007":
                                carPortNum[i] = "GX-0004";
                                break;
                            default:
                                carPortNum[i] = mBluetoothDeviceList.get(i).getName().substring(4, 10);
                                break;
                        }
                    }
                    mCarPortSpinner = new CarPortSpinner(MainActivity.this, mTvCarPort.getWidth(), carPortNum);
                    mCarPortSpinner.showAsDropDown(mTvCarPort, 0, 2);//显示在rl_spinner的下方
                    mCarPortSpinner.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            mCarPortSpinner.dismiss();
                        }
                    });
                    //点击别处关闭spinner
                    mCarPortSpinner.setTouchInterceptor(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                                mCarPortSpinner.dismiss();
                                return true;
                            }
                            return false;
                        }
                    });
                    //选择车位
                    mCarPortSpinner.setOnItemClickListener(new SpinnerListAdapter.onItemClickListener() {
                        @Override
                        public void click(int position, View view) {
                            mTvCarPort.setText(carPortNum[position]);
                            mMacAddress = mBluetoothDeviceList.get(position).getAddress();
                            mNodeId = mBluetoothDeviceList.get(position).getName().substring(4, 10);
                            mBleManager.disConnect();
                        }
                    });
                }
            }, 5000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBleManager.disConnect();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }
}
