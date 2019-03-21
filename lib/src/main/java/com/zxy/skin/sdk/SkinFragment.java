package com.zxy.skin.sdk;


import android.support.v4.app.Fragment;
import android.view.LayoutInflater;


/**
 * @Description: 使用方可以继承该Fragment，或者将内部代码拷贝到自定义的Fragment
 * @author: zhaoxuyang
 * @Date: 2019/1/31
 */
public class SkinFragment extends Fragment {


    @Override
    public void onDetach() {
        super.onDetach();
        LayoutInflater layoutInflater = getLayoutInflater();
        if(layoutInflater instanceof  SkinLayoutInflater){
            SkinLayoutInflater skinLayoutInflater = (SkinLayoutInflater) layoutInflater;
            skinLayoutInflater.destory();
        }
    }
}
