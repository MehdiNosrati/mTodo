# preserve the line number information for debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# AGP 9.1 enables R8 repackaging to unnamed package by default; opt out to preserve stack traces
-dontrepackage

# Retrofit
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# Room - keep entity field names so column mapping survives R8
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }
-keep class * extends androidx.room.RoomDatabase { *; }
-keepclassmembers class * extends androidx.room.RoomDatabase {
    abstract *;
}

# ViewModels - Koin resolves these reflectively
-keep class * extends androidx.lifecycle.ViewModel { *; }
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# App classes - keep all constructors for Koin DI
-keep class io.mns.base.app.data.** { *; }
-keep class io.mns.base.app.di.** { *; }

# Missing classes from android-utilities
-dontwarn io.mns.androidlib.ActivityExtensionsKt
-dontwarn io.mns.androidlib.NotificationUtil
-dontwarn io.mns.androidlib.ViewExtensionsKt