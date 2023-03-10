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
-dontnote "**"
# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile


-keep class com.stripe.android.** { *; }
-dontwarn com.stripe.android.**
#-keep class com.nimbusds.jose.** { *; }
-dontwarn com.nimbusds.jose.**
#-keep class net.minidev.asm.** { *; }
-dontwarn net.minidev.asm.**
#-keep class org.bouncycastle.** { *; }
-dontwarn org.bouncycastle.**

#-keep public class com.bupp.wood_spoon_eaters.model.** {*;}

#-keep class com.appsee.** { *; }
-dontwarn com.appsee.**
#-keep class android.support.** { *; }
#-keep interface android.support.** { *; }
-keep class androidx.** { *; }
-keep interface androidx.** { *; }
-keepattributes SourceFile,LineNumberTable
-keepclassmembers enum * { *; }

-keep class com.bupp.wood_spoon_eaters.model.* { *; }
-keep public class com.bupp.wood_spoon_eaters.network.google.models.* {*;}



-dontwarn module-info


#-keep class com.uxcam.** { *; }
-dontwarn com.uxcam.**

#branch
-keep class com.google.android.gms.** { *; }

#AWS
-keep class com.amazon.** { *; }
-keep class com.amazonaws.** { *; }
-keep class com.amplifyframework.** { *; }