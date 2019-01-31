package com.zxy.skin.sdk;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;


/**
 * @Description: 使用方可以继承该Activity，或者将内部代码拷贝到自定义的Activity
 * @author: zhaoxuyang
 * @Date: 2019/1/31
 */
public class SkinActivity extends FragmentActivity implements SkinEngine.ISkinObserver {

    private SkinLayoutInflater mLayoutInfalter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SkinEngine.getInstance().applySkin(getLayoutInflater().getContext());
        SkinEngine.getInstance().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SkinEngine.getInstance().unRegister(this);
    }

    @Override
    public void onChangeSkin(int themeId) {
        LayoutInflater layoutInflater = getLayoutInflater();
        layoutInflater.getContext().setTheme(themeId);
        if (layoutInflater != null && layoutInflater instanceof SkinLayoutInflater) {
            SkinLayoutInflater skinLayoutInflater = (SkinLayoutInflater) layoutInflater;
            skinLayoutInflater.changeSkin();
        }
    }

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
