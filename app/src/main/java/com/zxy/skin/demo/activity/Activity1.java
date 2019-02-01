package com.zxy.skin.demo.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.zxy.skin.demo.R;
import com.zxy.skin.sdk.SkinActivity;
import com.zxy.skin.sdk.SkinEngine;


public class Activity1 extends SkinActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity1);

    }

    public void startActivity2(View view){
        Intent intent = new Intent(this, Activity2.class);
        startActivity(intent);
    }

    public void setDaySkin(View view){
        SkinEngine.changeSkin(R.style.AppTheme);
    }

    public void setNightSkin(View view){
        SkinEngine.changeSkin(R.style.AppNightTheme);
    }
}
