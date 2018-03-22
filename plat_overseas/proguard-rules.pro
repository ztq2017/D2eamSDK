# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Android\sdk/tools/proguard/proguard-android.txt
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

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
# ----- 公用部分 START ----- #
#-optimizationpasses 5
#-dontusemixedcaseclassnames
#-dontskipnonpubliclibraryclasses
#-allowaccessmodification
#-dontoptimize
#-dontshrink
#-dontpreverify
#-verbose
#-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
#-keepattributes *Annotation*, Exceptions, InnerClasses, Signature, Deprecated, EnclosingMethod, SourceFile, LineNumberTable
#-dontwarn android.support.**
#-keep class android.support.annotation.Keep # keep 注解的支持
#-keepclasseswithmembers class * { @android.support.annotation.Keep <methods>; }
#-keepclasseswithmembers class * { @android.support.annotation.Keep <fields>; }
#-keepclasseswithmembers class * { @android.support.annotation.Keep <init>(...); }
#-keepclassmembers class * { @android.webkit.JavascriptInterface <methods>; }
#-keepclassmembers class * implements android.os.Parcelable { public static final android.os.Parcelable$Creator CREATOR; }
#-keepclasseswithmembernames class * { native <methods>; }
#-keepclassmembers enum * {
#    public static **[] values();
#    public static ** valueOf(java.lang.String);
#}
#-keep public class * extends android.app.Activity
#
#-keep public class * extends android.app.Application
#-keep public class * extends android.app.Service
#-keep public class * extends android.content.BroadcastReceiver
#-keep public class * extends android.content.ContentProvider
#-keep public class * extends android.support.v4.app.Fragment
#-keep public class * extends import android.support.v4.app.FragmentActivity;
#-keep public class * extends android.app.Fragment
## ----- 公用部分 END ----- #
#
#
#
## ----- 项目自有部分 START ----- #
#-keep class com.overseas.exports.** { public *; }
#-keep class com.overseas.exports.common.util.UtilDPI { public *; }
#-keep class com.overseas.exports.common.util.UtilResources { public *; }
#-keep class com.overseas.exports.common.CoreRestClient { public *; }
#-keep class com.overseas.exports.common.TwitterRestClient { public *; }
#-keep class com.overseas.exports.common.BaseHttpResponseHandler { public *; }
#-keep class com.overseas.exports.common.util.Md5 { public *; }
#
#-keep class com.overseas.exports.* { public *; }
#-keep class com.overseas.exports.dialog.SdkDialog { public *; }
#-keep class com.overseas.exports.dialog.WaitingDialog { public *; }
#-keep class com.overseas.exports.task.* { public *; }
#-keep class com.overseas.exports.utils.* { public *; }
## ----- 项目自有部分 END ----- #