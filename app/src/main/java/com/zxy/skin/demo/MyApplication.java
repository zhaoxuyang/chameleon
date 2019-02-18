package com.zxy.skin.demo;

import android.app.Application;

import com.zxy.skin.demo.skinapplicator.SkinCustomViewApplicator;
import com.zxy.skin.demo.widget.CustomView;
import com.zxy.skin.sdk.SkinEngine;


public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SkinEngine.changeSkin(R.style.AppTheme);
        SkinEngine.registerSkinApplicator(CustomView.class, new SkinCustomViewApplicator());
    }
}
