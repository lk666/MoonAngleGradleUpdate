# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\SDK/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
# 基本配置
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-dontwarn
-dontskipnonpubliclibraryclassmembers
-ignorewarnings
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod

# 保持 native 方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# 泛型与反射
-keepattributes Signature
-keepattributes EnclosingMethod
-keepattributes *Annotation*

#四大组件
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-keep public class * extends android.os.IInterface

######################################

# 数据类
-keep class cn.com.bluemoon.delivery.app.api.model.** {*;}

# 实体数据
-keep class bluemoon.com.cn.libasynchttp.ResultBase
-dontwarn bluemoon.com.cn.libasynchttp.ResultBase

# 百度定位
-keep class com.baidu.** {*;}

# 友盟
-keep class com.umeng.analytics.** {*;}
-dontwarn com.umeng.analytics.**

#-keep public class * extends com.umeng.**
#-keep public class * extends com.umeng.analytics.**
#-keep public class * extends com.umeng.common.**
#-keep public class * extends com.umeng.newxp.**
-keep class com.umeng.** { *; }
-keep class com.umeng.analytics.** { *; }
-keep class com.umeng.common.** { *; }
-keep class com.umeng.newxp.** { *; }

-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}
-keep class com.umeng.**

-keep public class com.idea.fifaalarmclock.app.R$*{
    public static final int *;
}

-keep public class com.umeng.fb.ui.ThreadView {
}

-dontwarn com.umeng.**

-dontwarn org.apache.commons.**

-keep public class * extends com.umeng.**

-keep class com.umeng.** {*; }

# RoundImageview
-keep class com.squareup.** {*; }
-dontwarn  com.squareup.**

### greenDAO 3
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
public static java.lang.String TABLENAME;
}
-keep class **$Properties

# If you do not use SQLCipher:
-dontwarn org.greenrobot.greendao.database.**
# If you do not use RxJava:
-dontwarn rx.**

### greenDAO 2
-keepclassmembers class * extends de.greenrobot.dao.AbstractDao {
public static java.lang.String TABLENAME;
}
-keep class **$Properties
# 实体类所在的包
-keep class cn.com.bluemoon.delivery.db.** {*;}

# 微信支付
-dontwarn com.tencent.**
-keep class com.tencent.** {*;}
-keep class cn.com.bluemoon.bluehouse.wxapi.** {*;}

# 支付宝
-keepattributes Signature
-keep class sun.misc.Unsafe { *; }

-keep class com.taobao.** {*;}
-keep class com.alibaba.** {*;}
-keep class com.alipay.** {*;}
-dontwarn com.taobao.**
-dontwarn com.alibaba.**
-dontwarn com.alipay.**

-keep class com.ut.** {*;}
-dontwarn com.ut.**

-keep class com.ta.** {*;}
-dontwarn com.ta.**

-keep class anet.**{*;}
-keep class org.android.spdy.**{*;}
-keep class org.android.agoo.**{*;}
-dontwarn anet.**
-dontwarn org.android.spdy.**
-dontwarn org.android.agoo.**

# 银联
-dontwarn com.unionpay.**
-keep class com.unionpay.** { *; }

# 杂项
-dontwarn  java.**
-keep class java.** {*; }

-dontwarn  javax.**
-keep class javax.** {*; }

-dontwarn org.**
-keep class org.** {*; }

-dontwarn sun.**
-keep class sun.** {*; }

-dontwarn com.sun.org.**
-keep class com.sun.org.** {*; }

-dontwarn com.sun.org.**
-keep class com.sun.org.** {*; }

-dontwarn com.sun.org.**
-keep class com.sun.org.** {*; }

-dontwarn freemarker.**
-keep class freemarker.** {*; }

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
# for DexGuard only
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule

# BaseRecyclerViewAdapterHelper
-keep class com.chad.library.adapter.** {
*;
}
-keep public class * extends com.chad.library.adapter.base.BaseQuickAdapter
-keep public class * extends com.chad.library.adapter.base.BaseViewHolder
-keepclassmembers public class * extends com.chad.library.adapter.base.BaseViewHolder {
     <init>(android.view.View);
}

# ButterKnife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * { @butterknife.* <fields>;}
-keepclasseswithmembernames class * { @butterknife.* <methods>;}

# EventBus
-keepclassmembers class ** {
    public void onEvent*(**);
}
# Only required if you use AsyncExecutor
-keepclassmembers class * extends de.greenrobot.event.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(Java.lang.Throwable);
}

# asyhttp
-keep class cz.msebera.android.httpclient.** { *; }
-keep class com.loopj.android.http.** { *; }

#fastjson
-dontwarn com.alibaba.fastjson.**

-dontskipnonpubliclibraryclassmembers
-dontskipnonpubliclibraryclasses

-keep class com.alibaba.fastjson.**{*;}
-keep class * implements java.io.Serializable { *; }

# LeakCanary
-keep class org.eclipse.mat.** { *; }
-keep class com.squareup.leakcanary.** { *; }

# 个推
-dontwarn com.igexin.**
-keep class com.igexin.** { *; }
-keep class org.json.** { *; }

# pdfview
-keep class com.shockwave.**