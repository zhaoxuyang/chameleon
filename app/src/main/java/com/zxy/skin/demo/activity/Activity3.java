package com.zxy.skin.demo.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;


import com.zxy.skin.demo.fragment.Fragment1;
import com.zxy.skin.demo.fragment.Fragment2;
import com.zxy.skin.demo.R;
import com.zxy.skin.sdk.SkinActivity;
import com.zxy.skin.sdk.SkinEngine;

import java.util.ArrayList;


public class Activity3 extends SkinActivity {

    private ArrayList<Fragment> mData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity3);
        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(2);
        mData.add(new Fragment1());
        mData.add(new Fragment2());
        viewPager.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager()));
    }

    public void setDaySkin(View view){
        SkinEngine.getInstance().changeSkin(R.style.AppTheme);
    }

    public void setNightSkin(View view){
        SkinEngine.getInstance().changeSkin(R.style.AppNightTheme);
    }


    public class MyViewPagerAdapter extends FragmentPagerAdapter {

        public MyViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mData.get(position);
        }

        @Override
        public int getCount() {
            return mData.size();
        }


    }

}
