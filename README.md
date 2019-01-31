# chameleon
Android Change Skin,  Android Night Mode, 安卓换肤，安卓夜间模式

### 1、皮肤资源组织形式

框架中的皮肤是以主题的形式存在的，在 values 目录下建立 skins.xml，文件内容如下样例：

```<resources>

        <!--定义皮肤的全部属性，例如全局的背景色，全局的字体色等-->
       <declare-styleable name="skin_attr">
           <attr name="main_bg" format="color|reference"></attr>
           <attr name="button_bg" format="color|reference"></attr>
           <attr name="button_text_color" format="color|reference"></attr>
           <attr name="text_color" format="color|reference"></attr>
           <attr name="line_color" format="color|reference"></attr>
       </declare-styleable>

       <!--定义皮肤主题，给皮肤的属性赋值-->
       <!--白天皮肤-->
       <style name="AppTheme" parent="android:Theme.Light.NoTitleBar">
           <item name="main_bg">@color/main_bg</item>
           <item name="button_bg">@color/button_bg</item>
           <item name="button_text_color">@color/button_text_color</item>
           <item name="text_color">@color/text_color</item>
           <item name="line_color">@color/line_color</item>
       </style>

       <!--夜间皮肤-->
       <style name="AppNightTheme" parent="android:Theme.Light.NoTitleBar">
           <item name="main_bg">@color/night_main_bg</item>
           <item name="button_bg">@color/night_button_bg</item>
           <item name="button_text_color">@color/night_button_text_color</item>
           <item name="text_color">@color/night_text_color</item>
           <item name="line_color">@color/night_line_color</item>
       </style>

   </resources>
```

如果某个控件需要换肤，则该控件的一些属性的值必须引用皮肤属性，例如：

```
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
       xmlns:app="http://schemas.android.com/apk/res-auto"
       android:layout_width="match_parent"
       android:layout_height="match_parent"
       android:orientation="vertical"
       android:background="?attr/main_bg">
       <TextView
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:textColor="?attr/text_color" />
</LinearLayout>

```

### 2、接入 SDK

将 lib 项目的代码引入到自己的项目中。

Application 初始化：

```
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //设置当前皮肤
        SkinEngine.getInstance().changeSkin(R.style.AppTheme);
    }
}
```

Activity & Fragment 改造：

可以将自己项目的 Activity（Fragment） 统一继承框架的 SkinActivity（SkinFragment）。
如果不能以继承的形式，则需将 SkinActivity（SkinFragment）内部的代码拷贝到自己项目相应的类中。

### 3、扩展 SkinApplicator

lib 项目中只写了 SkinViewApplicator 和 SkinTextViewApplicator ，支持 backgroud， textColor
等属性的换肤操作，其他属性的 Applicator 编写可以参照这两个以及样例中的 SkinCustomViewApplicator。
新增的 Applicator 需要注册到框架中。

```
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SkinEngine.getInstance().changeSkin(R.style.AppTheme);
        SkinApplicatorManager.register(CustomView.class, new SkinCustomViewApplicator());
    }
}
```