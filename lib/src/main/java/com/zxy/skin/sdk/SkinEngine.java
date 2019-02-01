package com.zxy.skin.sdk;


import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * @Description: 换肤引擎，管理当前使用的主题及换肤监听器
 * @author: zhaoxuyang
 * @Date: 2019/1/31
 */
public class SkinEngine {


    private static ArrayList<ISkinObserver> mSkinObservers = new ArrayList<>();

    private static int mThemeId;

    private SkinEngine() {

    }

    public static void changeSkin(int themeId) {
        if (mThemeId != themeId) {
            mThemeId = themeId;
            if (mThemeId != 0) {
                for (int i = 0; i < mSkinObservers.size(); i++) {
                    mSkinObservers.get(i).onChangeSkin(mThemeId);
                }
            }
        }

    }

    public static int getSkin() {
        return mThemeId;
    }

    public static void register(ISkinObserver observer) {
        if (observer != null && !mSkinObservers.contains(observer)) {
            mSkinObservers.add(observer);
        }
    }

    public static void unRegister(ISkinObserver observer) {
        if (observer != null) {
            mSkinObservers.remove(observer);
        }
    }

    /**
     * @Description: 换肤监听器
     * @author: zhaoxuyang
     * @Date: 2019/1/31
     */
    public interface ISkinObserver {

        void onChangeSkin(int themeId);

    }

    /**
     * SkinLayoutInflaterWrapper
     * @Description:
     * @author: zhaoxuyang
     * @Date: 2019/2/1
     */
    public static class SkinLayoutInflaterWrapper extends WeakReference<SkinLayoutInflater> implements ISkinObserver {

        public SkinLayoutInflaterWrapper(SkinLayoutInflater referent) {
            super(referent);
        }

        @Override
        public void onChangeSkin(int themeId) {
            SkinLayoutInflater skinLayoutInflater = get();
            if (skinLayoutInflater == null) {
                mSkinObservers.remove(this);
            } else {
                skinLayoutInflater.changeSkin(themeId);
            }
        }
    }

}
