package com.zxy.skin.sdk;


import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;


/**
 * @Description: 使用方可以继承该Activity，或者将内部代码拷贝到自定义的Activity
 * @author: zhaoxuyang
 * @Date: 2019/1/31
 */
public class SkinActivity extends FragmentActivity {

    private SkinLayoutInflater mLayoutInfalter;

    @Override
    public final LayoutInflater getLayoutInflater() {
        if (mLayoutInfalter == null) {
            mLayoutInfalter = new SkinLayoutInflater(this);
        }
        return mLayoutInfalter;
    }

    @Override
    public final Object getSystemService(String name) {
        if (Context.LAYOUT_INFLATER_SERVICE.equals(name)) {
            if (mLayoutInfalter == null) {
                mLayoutInfalter = new SkinLayoutInflater(this);
            }
            return mLayoutInfalter;
        }
        return super.getSystemService(name);
    }

}
