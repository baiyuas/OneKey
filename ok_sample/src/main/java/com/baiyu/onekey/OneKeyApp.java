package com.baiyu.onekey;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.fm.openinstall.OpenInstall;

/**
 * @author baiyu
 */
public class OneKeyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        OpenInstall.init(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

}
