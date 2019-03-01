package com.zxy.skin.sdk;

import android.content.Context;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;


import com.zxy.skin.sdk.applicator.SkinViewApplicator;
import com.zxy.skin.sdk.applicator.SkinApplicatorManager;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @Description: 自定义的LayoutInflater，会收集需要换肤的控件及属性集，部分代码 copy from AOSP
 * @author: zhaoxuyang
 * @Date: 2019/1/31
 */
public class SkinLayoutInflater extends LayoutInflater implements LayoutInflater.Factory2,SkinEngine.ISkinObserver {

    private static String TAG = "SkinLayoutInflater";

    // copy from AOSP PhoneLayoutInflater.java
    private static final String[] sClassPrefixList = {
            "android.widget.",
            "android.webkit.",
            "android.app."
    };

    // copy from AOSP LayoutInflater.java
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
        this(null, context);
    }

    public SkinLayoutInflater(LayoutInflater original, Context newContext) {
        super(newContext);
        init(original);
    }

    /**
     * 初始化
     *
     * @param original
     */
    private void init(LayoutInflater original) {

        //将自己设置为LayoutInflaterFactory，接管view的创建
        setFactory2(this);
        if (mConstructorArgsField != null) {
            try {
                mConstructorArgs = (Object[]) mConstructorArgsField.get(this);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        //将自己注册到换肤监听
        SkinEngine.registerSkinObserver(this);

        if (original == null) {
            return;
        }

        if (original instanceof SkinLayoutInflater) {
            SkinLayoutInflater skinLayoutInflater = (SkinLayoutInflater) original;
            this.mFactory = skinLayoutInflater.mFactory;
            this.mFactory2 = skinLayoutInflater.mFactory2;
        } else {
            LayoutInflater.Factory factory = original.getFactory();
            if (factory instanceof LayoutInflater.Factory2) {
                mFactory2 = (Factory2) factory;
            } else {
                mFactory = factory;
            }
        }
    }


    public void destory(){
        skinElements.clear();
        SkinEngine.unRegisterSkinObserver(this);
    }

    /**
     * 设置当前皮肤
     */
    public void applyCurrentSkin(){
        int themeId = SkinEngine.getSkin();
        if (themeId != 0) {
            getContext().setTheme(themeId);
        }
    }

    /**
     * copy from AOSP PhoneLayoutInflater.java
     *
     * @param name
     * @param attrs
     * @return
     * @throws ClassNotFoundException
     */
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
        SkinLayoutInflater skinLayoutInflater = new SkinLayoutInflater(this, context);
        return skinLayoutInflater;
    }

    /**
     * copy from AOSP LayoutInflater.java
     *
     * @param factory
     */
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

    /**
     * copy from AOSP LayoutInflater.java
     *
     * @param factory
     */
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
     * 接管view的创建，copy from AOSP LayoutInflater.java
     * 收集需要换肤的view及属性
     *
     * @param parent
     * @param name
     * @param context
     * @param attrs
     * @return
     */
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
                if ("fragment".equals(name)) { // 跳过fragment标签,回到原有的操作
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
            this.addElement(view, attrs);

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

    /**
     * 检查控件的属性值是否有对主题属性的引用，如果有则需要换肤
     *
     * @param view
     * @param attrs
     * @return
     */
    public boolean addElement(View view, AttributeSet attrs) {
        if (view == null) {
            return false;
        }
        SkinElement skinElement = new SkinElement(view);
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
            return false;
        }
        skinElements.add(skinElement);
        return true;
    }

    @Override
    public boolean onChangeSkin() {
        getContext().setTheme(SkinEngine.getSkin());
        Iterator<SkinElement> iterator = skinElements.iterator();
        while (iterator.hasNext()) {
            SkinElement skinElement = iterator.next();
            if (!skinElement.changeSkin()) {
                iterator.remove();
            }
        }
        return true;
    }

    /**
     * copy from AOSP LayoutInflater.java
     *
     * @Description:
     * @author: zhaoxuyang
     * @Date: 2019/2/12
     */
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
    private class SkinElement extends WeakReference<View> {

        public ArrayMap<String, Integer> changeAttrs = new ArrayMap<>();

        public SkinElement(View referent) {
            super(referent);
        }

        /**
         * 对控件执行换肤
         */
        public boolean changeSkin() {
            View view = get();
            int attrsSize = changeAttrs.size();
            if (view == null || attrsSize == 0) {
                return false;
            }
            SkinViewApplicator applicator = SkinApplicatorManager.getApplicator(view.getClass());
            if (applicator != null) {
                applicator.apply(view, changeAttrs);
            }
            return true;
        }

        public void dump() {
            View view = get();
            if (view == null) {
                return;
            }
            Logger.d(TAG, "------ dump view:" + get());
            Set<Map.Entry<String, Integer>> entrySet = changeAttrs.entrySet();
            for (Map.Entry<String, Integer> entry : entrySet) {
                Log.d(TAG, "attr:" + entry.getKey() + ",value:" + entry.getValue());
            }
            Logger.d(TAG, "------ dump end");
        }

    }
}
