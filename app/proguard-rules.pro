# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

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