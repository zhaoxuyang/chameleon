package com.zxy.skin.sdk.adapter;


import android.content.res.TypedArray;
import android.widget.TextView;
/**
 *
 * @Description: TextView 的换肤器
 * @author: zhaoxuyang
 * @Date: 2019/1/31
 */
public class SkinTextViewApplicator extends SkinViewApplicator {

    private static final String TAG = "SkinTextViewApplicator";

    public SkinTextViewApplicator() {
        //super必须调用
        super();
        supportAttrs.put("textColor", new IAttributeApplicator<TextView>(){

            @Override
            public void onApply(TextView view, TypedArray typedArray, int typedArrayIndex) {
                view.setTextColor(typedArray.getColorStateList(typedArrayIndex));
            }
        });

    }

}


