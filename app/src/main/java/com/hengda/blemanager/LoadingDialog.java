package com.hengda.blemanager;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.TextView;

/**
 * 加载进度
 * Created by WHHD on 2018/3/29.
 */
public class LoadingDialog extends Dialog {
    private TextView mTvLockState;
    private ATProgressView mProgressView;

    public LoadingDialog(@NonNull Context context) {
        super(context, R.style.loadingDialogStyle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.countdown_loading);
        mTvLockState = findViewById(R.id.tv_lock_state);
        mProgressView = findViewById(R.id.progress_view);
        setCanceledOnTouchOutside(false);
    }

    /**
     * 显示dialog
     *
     * @param state 蓝牙解锁
     */
    public void showDialog(String state) {
        if (!isShowing()) {
            show();
        }
        if (mTvLockState != null) {
            mTvLockState.setText(state);
        }
        if (mProgressView != null) {
            mProgressView.setCountdownTime(5);
            mProgressView.startCountdown(new ATProgressView.OnCountDownFinishListener() {
                @Override
                public void countDownFinished() {
                    dismissDialog();
                }
            });
        }
    }

    /**
     * 隐藏dialog
     */
    public void dismissDialog() {
        if (isShowing()) {
            dismiss();
        }
    }
}
