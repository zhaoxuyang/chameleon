package com.zxy.skin.sdk;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;


/**
 *
 * @Description: 使用方可以继承该Fragment，或者将内部代码拷贝到自定义的Fragment
 * @author: zhaoxuyang
 * @Date: 2019/1/31
 */
public class SkinFragment extends Fragment implements SkinEngine.ISkinObserver {


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        SkinEngine.getInstance().applySkin(getLayoutInflater().getContext());
        SkinEngine.getInstance().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        SkinEngine.getInstance().unRegister(this);

    }

    @Override
    public void onChangeSkin(int themeId) {
        LayoutInflater layoutInflater = getLayoutInflater();
        layoutInflater.getContext().setTheme(themeId);
        if(layoutInflater!=null && layoutInflater instanceof SkinLayoutInflater){
            SkinLayoutInflater skinLayoutInflater = (SkinLayoutInflater) layoutInflater;
            skinLayoutInflater.changeSkin();
        }
    }
}
