package com.hengda.blemanager;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

/**
 * Application
 * Created by WHHD on 2016/10/11.
 */
public class MainApplication extends MultiDexApplication {
    private static MainApplication mainApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        mainApplication = this;
    }

    public static MainApplication getInstance() {
        return mainApplication;
    }

    /**
     * 分割 Dex 支持
     *
     * @param base
     */
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
