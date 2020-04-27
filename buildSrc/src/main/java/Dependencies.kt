object Sdk {
    const val MIN_SDK_VERSION = 21
    const val TARGET_SDK_VERSION = 29
    const val COMPILE_SDK_VERSION = 29
}

object Versions {
    const val ANDROIDX_TEST_EXT = "1.1.1"
    const val ANDROIDX_TEST = "1.2.0"
    const val APPCOMPAT = "1.1.0"
    const val CONSTRAINT_LAYOUT = "1.1.3"
    const val CORE_KTX = "1.2.0"
    const val LEGACY_SUPPORT = "1.0.0"
    const val COLLECTION_KTX = "1.1.0"
    const val FRAGMENT_KTX = "1.2.4"
    const val NAVIGATION = "2.3.0-alpha05"
    const val ROOM = "2.2.5"
    const val ESPRESSO_CORE = "3.2.0"
    const val JUNIT = "4.13"
    const val KTLINT = "0.36.0"
    const val LIFE_CYCLE = "2.2.0"
    const val ARCH = "2.1.0"
    const val MULTI_DEX = "2.0.1"
    const val RETROFIT = "2.8.1"
    const val GLIDE = "4.11.0"
    const val KOIN = "2.1.5"
    const val MATERIAL_DESIGN = "1.2.0-alpha06"
}

object BuildPluginsVersion {
    const val AGP = "3.6.3"
    const val DETEKT = "1.8.0"
    const val KOTLIN = "1.3.72"
    const val KTLINT = "9.2.1"
    const val VERSIONS_PLUGIN = "0.28.0"
}

object SupportLibs {
    const val ANDROIDX_APPCOMPAT = "androidx.appcompat:appcompat:${Versions.APPCOMPAT}"
    const val DESIGN = "com.google.android.material:material:${Versions.MATERIAL_DESIGN}"
    const val ANDROIDX_CONSTRAINT_LAYOUT = "com.android.support.constraint:constraint-layout:${Versions.CONSTRAINT_LAYOUT}"
    const val ANDROIDX_CORE_KTX = "androidx.core:core-ktx:${Versions.CORE_KTX}"
    const val LEGACY_SUPPORT = "androidx.legacy:legacy-support-v4:${Versions.LEGACY_SUPPORT}"
    const val ANDROIDX_COLLECTION_KTX = "androidx.collection:collection-ktx:${Versions.COLLECTION_KTX}"
    const val ANDROIDX_FRAGMENT_KTX = "androidx.fragment:fragment-ktx:${Versions.FRAGMENT_KTX}"
}

object TestingLib {
    const val JUNIT = "junit:junit:${Versions.JUNIT}"
}

object AndroidTestingLib {
    const val ANDROIDX_TEST_RULES = "androidx.test:rules:${Versions.ANDROIDX_TEST}"
    const val ANDROIDX_TEST_RUNNER = "androidx.test:runner:${Versions.ANDROIDX_TEST}"
    const val ANDROIDX_TEST_EXT_JUNIT = "androidx.test.ext:junit:${Versions.ANDROIDX_TEST_EXT}"
    const val ESPRESSO_CORE = "androidx.test.espresso:espresso-core:${Versions.ESPRESSO_CORE}"
}

object MultiDex {
    const val MULTI_DEX = "androidx.multidex:multidex:${Versions.MULTI_DEX}"
}

object Room {
    const val RUNTIME = "androidx.room:room-runtime:${Versions.ROOM}"
    const val COMPILER = "androidx.room:room-compiler:${Versions.ROOM}"
    const val TEST = "androidx.room:room-testing:${Versions.ROOM}"
    const val KTX = "androidx.room:room-ktx:${Versions.ROOM}"
}

object Navigation {
    const val SAFE_ARGS = "androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.NAVIGATION}"
    const val DYNAMIC = "androidx.navigation:navigation-dynamic-features-fragment:${Versions.NAVIGATION}"
    const val RUNTIME_KTX = "androidx.navigation:navigation-runtime-ktx:${Versions.NAVIGATION}"
    const val FRAGMENT_KTX = "androidx.navigation:navigation-fragment-ktx:${Versions.NAVIGATION}"
    const val UI_KTX = "androidx.navigation:navigation-ui-ktx:${Versions.NAVIGATION}"
    const val TEST = "androidx.navigation:navigation-testing:${Versions.NAVIGATION}"
}

object LifeCycle {
    const val VIEW_MODEL = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.LIFE_CYCLE}"
    const val LIVE_DATA = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.LIFE_CYCLE}"
    const val COMMON = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.LIFE_CYCLE}"
    const val COMPILER = "androidx.lifecycle:lifecycle-common-java8:${Versions.LIFE_CYCLE}"
    const val APPLICATION = "androidx.lifecycle:lifecycle-process:${Versions.LIFE_CYCLE}"
    const val SERVICE = "androidx.lifecycle:lifecycle-service:${Versions.LIFE_CYCLE}"
    const val TEST = "androidx.arch.core:core-testing:${Versions.ARCH}"
}

object Retrofit {
    const val CORE = "com.squareup.retrofit2:retrofit:${Versions.RETROFIT}"
    const val GSON_CONVERTER = "com.squareup.retrofit2:converter-gson:${Versions.RETROFIT}"
}

object Glide {
    const val CORE = "com.github.bumptech.glide:glide:${Versions.GLIDE}"
    const val COMPILER = "com.github.bumptech.glide:compiler:${Versions.GLIDE}"
}

object Koin {
    const val CORE = "org.koin:koin-android:${Versions.KOIN}"
    const val TEST = "org.koin:koin-test:$${Versions.KOIN}"
}