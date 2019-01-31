package com.zxy.skin.sdk;

import android.content.Context;

import java.util.HashSet;

/**
 *
 * @Description: 换肤引擎，管理当前使用的主题及换肤监听器
 * @author: zhaoxuyang
 * @Date: 2019/1/31
 */
public class SkinEngine {

    private static SkinEngine instance = new SkinEngine();

    private HashSet<ISkinObserver> mSkinObservers = new HashSet<>();

    private int mThemeId;

    private SkinEngine() {

    }

    public static SkinEngine getInstance() {
        return instance;
    }

    public void changeSkin(int themeId) {
        if (mThemeId != themeId) {
            mThemeId = themeId;
            if (mThemeId != 0) {
                for (ISkinObserver observer : mSkinObservers) {
                    observer.onChangeSkin(mThemeId);
                }
            }
        }

    }

    public void applySkin(Context context) {
        if (mThemeId != 0) {
            context.setTheme(mThemeId);
        }
    }

    public void register(ISkinObserver observer) {
        if (observer != null) {
            mSkinObservers.add(observer);
        }
    }

    public void unRegister(ISkinObserver observer) {
        if (observer != null) {
            mSkinObservers.remove(observer);
        }
    }

    /**
     *
     * @Description: 换肤监听器
     * @author: zhaoxuyang
     * @Date: 2019/1/31
     */
    public interface ISkinObserver {

        void onChangeSkin(int themeId);

    }

}
