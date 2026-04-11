object Sdk {
    const val MIN_SDK_VERSION = 21
    const val COMPILE_SDK_VERSION = 36
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
    const val NAVIGATION = "2.8.5"
    const val ROOM = "2.7.2"
    const val ESPRESSO_CORE = "3.2.0"
    const val JUNIT = "4.13"
    const val KTLINT = "0.36.0"
    const val LIFE_CYCLE = "2.2.0"
    const val ARCH = "2.1.0"
    const val MULTI_DEX = "2.0.1"
    const val KOIN = "3.5.6"
    const val MATERIAL_DESIGN = "1.2.0-alpha06"
    const val CHECK_BOX = "1.0.1"
    const val BOTTOM_BAR = "1.7.7"
    const val COMPOSE = "1.7.6"
    const val COMPOSE_MATERIAL3 = "1.3.1"
    const val ACTIVITY_COMPOSE = "1.9.3"
    const val NAVIGATION_COMPOSE = "2.8.5"
    const val COMPOSE_LIVEDATA = "1.7.6"
    const val KOIN_COMPOSE = "3.5.6"
}

object BuildPluginsVersion {
    const val AGP = "9.1.0"
    const val DETEKT = "1.23.7"
    const val KOTLIN = "2.3.20"
    const val KTLINT = "12.1.1"
    const val VERSIONS_PLUGIN = "0.51.0"
    const val KSP = "2.3.6"
}

object SupportLibs {
    const val ANDROIDX_APPCOMPAT = "androidx.appcompat:appcompat:${Versions.APPCOMPAT}"
    const val DESIGN = "com.google.android.material:material:${Versions.MATERIAL_DESIGN}"
    const val ANDROIDX_CONSTRAINT_LAYOUT = "com.android.support.constraint:constraint-layout:${Versions.CONSTRAINT_LAYOUT}"
    const val ANDROIDX_CORE_KTX = "androidx.core:core-ktx:${Versions.CORE_KTX}"
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
}

object Koin {
    const val ANDROID = "io.insert-koin:koin-android:${Versions.KOIN}"
    const val CORE = "io.insert-koin:koin-core:${Versions.KOIN}"
    const val TEST = "io.insert-koin:koin-test:${Versions.KOIN}"
}

object Compose {
    const val UI = "androidx.compose.ui:ui:${Versions.COMPOSE}"
    const val UI_TOOLING = "androidx.compose.ui:ui-tooling:${Versions.COMPOSE}"
    const val UI_TOOLING_PREVIEW = "androidx.compose.ui:ui-tooling-preview:${Versions.COMPOSE}"
    const val MATERIAL3 = "androidx.compose.material3:material3:${Versions.COMPOSE_MATERIAL3}"
    const val ACTIVITY = "androidx.activity:activity-compose:${Versions.ACTIVITY_COMPOSE}"
    const val NAVIGATION = "androidx.navigation:navigation-compose:${Versions.NAVIGATION_COMPOSE}"
    const val LIVEDATA = "androidx.compose.runtime:runtime-livedata:${Versions.COMPOSE_LIVEDATA}"
    const val KOIN = "io.insert-koin:koin-androidx-compose:${Versions.KOIN_COMPOSE}"
}
