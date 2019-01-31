package com.zxy.skin.sdk;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;


import com.zxy.skin.sdk.adapter.SkinViewApplicator;
import com.zxy.skin.sdk.adapter.SkinApplicatorManager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @Description: 自定义的LayoutInflater，会收集需要换肤的控件及属性集，部分代码 copy from AOSP
 * @author: zhaoxuyang
 * @Date: 2019/1/31
 */
public class SkinLayoutInflater extends LayoutInflater implements LayoutInflater.Factory2 {

    private static String TAG = "SkinLayoutInflater";

    private static final String[] sClassPrefixList = {
            "android.widget.",
            "android.webkit.",
            "android.app."
    };

    private static final StackTraceElement[] EMPTY_STACK_TRACE = new StackTraceElement[0];

    private static Field mConstructorArgsField;

    static {
        try {
            mConstructorArgsField = LayoutInflater.class.getDeclaredField("mConstructorArgs");
            mConstructorArgsField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<SkinElement> skinElements = new ArrayList<>();

    private Object[] mConstructorArgs;

    private volatile boolean mFactorySet;

    private volatile boolean mSelfInit = true;

    private Factory mFactory;

    private Factory2 mFactory2;

    public SkinLayoutInflater(Context context) {
        super(context);
        init();
    }

    private void init() {
        //将自己设置为LayoutInflaterFactory，接管view的创建
        setFactory2(this);
        if (mConstructorArgsField != null) {
            try {
                mConstructorArgs = (Object[]) mConstructorArgsField.get(this);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected View onCreateView(String name, AttributeSet attrs) throws ClassNotFoundException {
        for (String prefix : sClassPrefixList) {
            try {
                View view = createView(name, prefix, attrs);
                if (view != null) {
                    return view;
                }
            } catch (ClassNotFoundException e) {
                // In this case we want to let the base class take a crack
                // at it.
            }
        }

        return super.onCreateView(name, attrs);
    }

    @Override
    public LayoutInflater cloneInContext(Context context) {
        SkinLayoutInflater skinLayoutInflater = new SkinLayoutInflater(context);
        skinLayoutInflater.mFactory = this.mFactory;
        skinLayoutInflater.mFactory2 = this.mFactory2;
        return skinLayoutInflater;
    }

    @Override
    public void setFactory(Factory factory) {
        if (mFactorySet) {
            throw new IllegalStateException("A factory has already been set on this LayoutInflater");
        }
        if (factory == null) {
            throw new NullPointerException("Given factory can not be null");
        }
        mFactorySet = true;
        if (mFactory == null) {
            mFactory = factory;
        } else {
            mFactory = new FactoryMerger(factory, null, mFactory, mFactory2);
        }
    }

    @Override
    public void setFactory2(Factory2 factory) {
        if (mSelfInit) {
            super.setFactory2(factory);
            mSelfInit = false;
        } else {
            if (mFactorySet) {
                throw new IllegalStateException("A factory has already been set on this LayoutInflater");
            }
            if (factory == null) {
                throw new NullPointerException("Given factory can not be null");
            }
            mFactorySet = true;
            if (mFactory == null) {
                mFactory = mFactory2 = factory;
            } else {
                mFactory = mFactory2 = new FactoryMerger(factory, factory, mFactory, mFactory2);
            }
        }
    }

    /**
     * 对收集的控件执行换肤操作
     */
    public void changeSkin() {
        for (SkinElement skinElement : skinElements) {
            skinElement.changeSkin();
        }
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {

        try {

            View view = null;

            if (mFactory2 != null) {
                view = mFactory2.onCreateView(parent, name, context, attrs);
            } else if (mFactory != null) {
                view = mFactory.onCreateView(name, context, attrs);
            } else {
                view = null;
            }

            if (view == null) {
                if ("fragment".equals(name)) { // 处理fragment标签，原有PrivateFactory的操作
                    view = null;
                } else {
                    Object lastContext = null;
                    if (mConstructorArgs != null) {
                        lastContext = mConstructorArgs[0];
                        mConstructorArgs[0] = context;
                    }
                    try {
                        if (-1 == name.indexOf('.')) {
                            view = onCreateView(parent, name, attrs);
                        } else {
                            view = createView(name, null, attrs);
                        }
                    } finally {
                        if (mConstructorArgs != null) {
                            mConstructorArgs[0] = lastContext;
                        }
                    }
                }

            }

            //检查该view是否需要进行换肤操作
            if (view != null) {
                SkinElement skinElement = SkinElement.create(view, attrs);
                if (skinElement != null) {
                    skinElements.add(skinElement);
                }
            }

            return view;
        } catch (InflateException e) {
            throw e;

        } catch (ClassNotFoundException e) {
            final InflateException ie = new InflateException(attrs.getPositionDescription()
                    + ": Error inflating class " + name, e);
            ie.setStackTrace(EMPTY_STACK_TRACE);
            throw ie;

        } catch (Exception e) {
            final InflateException ie = new InflateException(attrs.getPositionDescription()
                    + ": Error inflating class " + name, e);
            ie.setStackTrace(EMPTY_STACK_TRACE);
            throw ie;
        }

    }

    @Override
    public View onCreateView(String s, Context context, AttributeSet attributeSet) {
        return this.onCreateView(null, s, context, attributeSet);
    }

    private static class FactoryMerger implements Factory2 {
        private final Factory mF1, mF2;
        private final Factory2 mF12, mF22;

        FactoryMerger(Factory f1, Factory2 f12, Factory f2, Factory2 f22) {
            mF1 = f1;
            mF2 = f2;
            mF12 = f12;
            mF22 = f22;
        }

        public View onCreateView(String name, Context context, AttributeSet attrs) {
            View v = mF1.onCreateView(name, context, attrs);
            if (v != null) return v;
            return mF2.onCreateView(name, context, attrs);
        }

        public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
            View v = mF12 != null ? mF12.onCreateView(parent, name, context, attrs)
                    : mF1.onCreateView(name, context, attrs);
            if (v != null) return v;
            return mF22 != null ? mF22.onCreateView(parent, name, context, attrs)
                    : mF2.onCreateView(name, context, attrs);
        }
    }


    /**
     * @Description: 需要换肤控件的抽象表示
     * @author: zhaoxuyang
     * @Date: 2019/1/31
     */
    private static class SkinElement {

        private static String TAG = "SkinElement";

        public View view;

        public HashMap<String, Integer> changeAttrs = new HashMap<>();

        /**
         * 对控件执行换肤
         */
        public void changeSkin() {
            if (changeAttrs.size() > 0) {
                SkinViewApplicator adapter = SkinApplicatorManager.getApplicator(view.getClass());
                if (adapter != null) {
                    adapter.apply(view, changeAttrs);
                }
            }
        }

        public void dump() {
            Logger.d(TAG, "------ dump view:" + view);
            Set<Map.Entry<String, Integer>> entrySet = changeAttrs.entrySet();
            for (Map.Entry<String, Integer> entry : entrySet) {
                Log.d(TAG, "attr:" + entry.getKey() + ",value:" + entry.getValue());
            }
            Logger.d(TAG, "------ dump end");
        }

        /**
         * 检查控件的属性值是否有对主题属性的引用，如果有则需要换肤
         * @param view
         * @param attrs
         * @return
         */
        public static SkinElement create(View view, AttributeSet attrs) {
            SkinElement skinElement = new SkinElement();
            skinElement.view = view;
            int count = attrs.getAttributeCount();
            for (int i = 0; i < count; i++) {
                String name = attrs.getAttributeName(i);
                String value = attrs.getAttributeValue(i);
                if (!TextUtils.isEmpty(value) && value.startsWith("?")) {
                    try {
                        String temp = value.substring(1);
                        int attrId = Integer.parseInt(temp);
                        skinElement.changeAttrs.put(name, attrId);
                    } catch (Exception e) {

                    }

                }

            }
            if (skinElement.changeAttrs.size() == 0) {
                return null;
            }
            return skinElement;
        }
    }
}
