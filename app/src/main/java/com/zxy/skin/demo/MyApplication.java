package com.zxy.skin.demo;

import android.app.Application;

import com.zxy.skin.demo.skinapplicator.SkinCustomViewApplicator;
import com.zxy.skin.demo.widget.CustomView;
import com.zxy.skin.sdk.SkinEngine;
import com.zxy.skin.sdk.applicator.SkinApplicatorManager;


public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SkinEngine.getInstance().changeSkin(R.style.AppTheme);
        SkinApplicatorManager.register(CustomView.class, new SkinCustomViewApplicator());
    }
}
