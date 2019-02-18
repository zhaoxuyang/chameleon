package com.zxy.skin.sdk;


import android.view.View;

import com.zxy.skin.sdk.applicator.SkinApplicatorManager;
import com.zxy.skin.sdk.applicator.SkinViewApplicator;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;

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

    /**
     * 变更皮肤
     * @param themeId
     */
    public static void changeSkin(int themeId) {
        if (mThemeId != themeId) {
            mThemeId = themeId;
            if (mThemeId != 0) {
                Iterator<ISkinObserver> iterator = mSkinObservers.iterator();
                while (iterator.hasNext()) {
                    ISkinObserver skinObserver = iterator.next();
                    if (!skinObserver.onChangeSkin(themeId)) {
                        iterator.remove();
                    }
                }
            }
        }

    }

    /**
     * 获取当前皮肤
     * @return
     */
    public static int getSkin() {
        return mThemeId;
    }

    /**
     * 注册皮肤变化监听器
     * @param observer
     */
    public static void registerSkinObserver(ISkinObserver observer) {
        if (observer != null && !mSkinObservers.contains(observer)) {
            mSkinObservers.add(observer);
        }
    }

    /**
     * 解除注册皮肤变化监听器
     * @param observer
     */
    public static void unRegisterSkinObserver(ISkinObserver observer) {
        if (observer != null) {
            mSkinObservers.remove(observer);
        }
    }

    /**
     * 注册skinapplicator
     * @param viewClass
     * @param applicator
     */
    public static void registerSkinApplicator(Class<? extends View> viewClass, SkinViewApplicator applicator) {
        if (viewClass == null || applicator == null) {
            return;
        }
        SkinApplicatorManager.register(viewClass, applicator);
    }


    /**
     * @Description: 换肤监听器
     * @author: zhaoxuyang
     * @Date: 2019/1/31
     */
    public interface ISkinObserver {

        boolean onChangeSkin(int themeId);

    }

    /**
     * SkinLayoutInflaterWrapper
     *
     * @Description:
     * @author: zhaoxuyang
     * @Date: 2019/2/1
     */
    public static class SkinLayoutInflaterWrapper extends WeakReference<SkinLayoutInflater> implements ISkinObserver {

        public SkinLayoutInflaterWrapper(SkinLayoutInflater referent) {
            super(referent);
        }

        @Override
        public boolean onChangeSkin(int themeId) {
            SkinLayoutInflater skinLayoutInflater = get();
            if (skinLayoutInflater == null) {
                return false;
            }
            skinLayoutInflater.changeSkin(themeId);

            return true;
        }
    }

}
