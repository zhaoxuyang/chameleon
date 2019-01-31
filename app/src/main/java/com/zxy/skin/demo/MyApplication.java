package com.zxy.skin.demo;

import android.app.Application;

import com.zxy.skin.sdk.SkinEngine;


public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SkinEngine.getInstance().changeSkin(R.style.AppTheme);
    }
}
