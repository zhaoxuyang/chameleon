package com.zxy.skin.sdk.applicator;

import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.ArrayMap;
import android.view.View;

import com.zxy.skin.sdk.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

/**
 * @Description: View 的换肤器，其他换肤器需要继承该类
 * @author: zhaoxuyang
 * @Date: 2019/1/31
 */
public class SkinViewApplicator {

    private static final String TAG = "SkinViewApplicator";

    private HashMap<String, IAttributeApplicator<? extends View>> supportAttrs = new HashMap<>();

    private HashMap<String, Integer> mAttrIndexMap = new HashMap<>();

    private int[] attrArr;

    public SkinViewApplicator() {
        addAttributeApplicator("background", new IAttributeApplicator<View>() {

            @Override
            public void onApply(View view, TypedArray typedArray, int typedArrayIndex) {
                view.setBackground(typedArray.getDrawable(typedArrayIndex));
            }
        });

        addAttributeApplicator("foreground", new IAttributeApplicator<View>() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onApply(View view, TypedArray typedArray, int typedArrayIndex) {
                view.setForeground(typedArray.getDrawable(typedArrayIndex));
            }
        });
    }

    protected void addAttributeApplicator(String attrName, IAttributeApplicator<? extends View> applicator) {
        supportAttrs.put(attrName, applicator);
    }

    /**
     * 换肤核心逻辑
     * @param view
     * @param attrsMap
     */
    public void apply(View view, ArrayMap<String, Integer> attrsMap) {

        if (view == null || attrsMap == null || attrsMap.size() == 0) {
            return;
        }

        Logger.d(TAG, "------ " + view + " start apply skin");

        try {
            int index = 0;
            mAttrIndexMap.clear();
            if (attrArr == null) {
                attrArr = new int[supportAttrs.size()];
            } else {
                Arrays.fill(attrArr, 0);
            }

            Set<String> attrNameSet = supportAttrs.keySet();
            for (String attrName : attrNameSet) {
                Integer value = attrsMap.get(attrName);
                if (value != null) {
                    attrArr[index] = value;
                    mAttrIndexMap.put(attrName, index);
                    index++;
                }
            }

            //从主题中获取属性值，执行换肤
            TypedArray typedArray = view.getContext().getTheme().obtainStyledAttributes(attrArr);
            if (typedArray == null) {
                throw new Exception("typedArray is null!, view:" + view);
            }

            for (String attrName : attrNameSet) {
                Integer typedArrayIndex = mAttrIndexMap.get(attrName);
                IAttributeApplicator attributeApplicator = supportAttrs.get(attrName);
                if (typedArrayIndex != null && attributeApplicator != null) {
                    attributeApplicator.onApply(view, typedArray, typedArrayIndex);
                    Logger.d(TAG, "attrName: " + attrName);
                }
            }

            Logger.d(TAG, "------ " + view + " apply skin success");
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            Logger.d(TAG, "------ " + view + " apply skin failed");
        }


    }


    /**
     * @Description: 每个属性的设置器
     * @author: zhaoxuyang
     * @Date: 2019/1/31
     */
    public interface IAttributeApplicator<T extends View> {

        void onApply(T view, TypedArray typedArray, int typedArrayIndex);
    }

}
