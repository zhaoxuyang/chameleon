package com.zxy.skin.demo.skinapplicator;

import android.content.res.TypedArray;
import android.graphics.Color;

import com.zxy.skin.demo.widget.CustomView;
import com.zxy.skin.sdk.adapter.SkinViewApplicator;

public class SkinCustomViewApplicator extends SkinViewApplicator {

    public SkinCustomViewApplicator() {
        super();
        supportAttrs.put("lineColor", new IAttributeApplicator<CustomView>() {
            @Override
            public void onApply(CustomView view, TypedArray typedArray, int typedArrayIndex) {
                view.setLineColor(typedArray.getColor(typedArrayIndex, Color.BLACK));
            }
        });
    }
}
