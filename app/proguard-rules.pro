# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

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

# Keep custom model classes
#-keep class sembako.sayunara.android.ui.component.product.listproduct.model.Product1.** { *; }
#-keepclassmembers sembako.sayunara.android.ui.component.product.listproduct.model.Product1.** {*;}
-keepclassmembers class sembako.sayunara.android.ui.component.product.listproduct.model.Product.** { *; }
-keepclassmembers class sembako.sayunara.android.ui.component.account.login.data.model.User.**{ *; }
-keepclassmembers class sembako.sayunara.android.ui.component.basket.model.Basket.** { *; }
-keepclassmembers class sembako.sayunara.android.ui.component.splashcreen.model.ConfigSetup.** { *; }
-keepclassmembers class sembako.sayunara.android.ui.component.splashcreen.model.** { *; }
-keepattributes Signature
-keep class androidx.multidex.MultiDexApplication

# Keep custom model classes
-keepclassmembers class com.mypackage.model.** { *; }
-keepclassmembers class sembako.sayunara.android.ui.component.account.login.data.model.** { *; }
-keep class com.mypackage.model.** { *; }
-keep class com.mypackage.util.** { *; }
