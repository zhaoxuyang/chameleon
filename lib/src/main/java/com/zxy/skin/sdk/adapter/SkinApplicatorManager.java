package com.zxy.skin.sdk.adapter;


import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

/**
 *
 * @Description: 换肤器管理中心
 * @author: zhaoxuyang
 * @Date: 2019/1/31
 */
public class SkinApplicatorManager {

    private static final String TAG = "SkinApplicatorManager";

    private static SkinViewApplicator defaultSkinViewAdapter = new SkinViewApplicator();

    private static HashMap<Class, SkinViewApplicator> adaptersMap = new HashMap<>();

    static {

        SkinViewApplicator textViewSkinViewAdapter = new SkinTextViewApplicator();
        adaptersMap.put(TextView.class, textViewSkinViewAdapter);
        adaptersMap.put(Button.class, textViewSkinViewAdapter);

    }

    /**
     *  获取某个控件的换肤器
     * @param viewClass
     * @return
     */
    public static SkinViewApplicator getAdapter(Class<? extends View> viewClass) {
        SkinViewApplicator skinViewAdapter = adaptersMap.get(viewClass);
        return skinViewAdapter == null ? defaultSkinViewAdapter : skinViewAdapter;
    }
}
