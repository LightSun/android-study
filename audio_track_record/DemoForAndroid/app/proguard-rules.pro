# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\cj\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
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

#保持 native 方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}
-keep class com.tt.SkEgn { *; }
-keep class com.tt.SkEgn$skegn_callback{
    public <fields>;
    public <methods>;
}
-dontwarn android.net.http.*
-dontwarn com.android.internal.http.multipart.*
-dontwarn org.apache.http.**