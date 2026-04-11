plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("com.google.devtools.ksp")
}

android {
    namespace = "io.mns.base.app"
    compileSdk = Sdk.COMPILE_SDK_VERSION

    kotlin {
        jvmToolchain(17)
    }

    defaultConfig {
        minSdk = Sdk.MIN_SDK_VERSION
        targetSdk = Sdk.COMPILE_SDK_VERSION
        multiDexEnabled = true
        applicationId = AppCoordinates.APP_ID
        versionCode = AppCoordinates.APP_VERSION_CODE
        versionName = AppCoordinates.APP_VERSION_NAME
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk7"))

    // local modules
    implementation(project(":android-utilities"))

    // support
    implementation(SupportLibs.ANDROIDX_APPCOMPAT)
    implementation(SupportLibs.ANDROIDX_CONSTRAINT_LAYOUT)
    implementation(SupportLibs.ANDROIDX_CORE_KTX)
    implementation(SupportLibs.ANDROIDX_FRAGMENT_KTX)
    implementation(SupportLibs.DESIGN)

    // multi dex
    implementation(MultiDex.MULTI_DEX)

    // navigation
    implementation(Navigation.FRAGMENT_KTX)
    implementation(Navigation.UI_KTX)
    implementation(Navigation.RUNTIME_KTX)

    // room
    implementation(Room.RUNTIME)
    ksp(Room.COMPILER)
    implementation(Room.KTX)

    // view model
    implementation(LifeCycle.VIEW_MODEL)
    implementation(LifeCycle.LIVE_DATA)
    implementation(LifeCycle.COMMON)
    implementation(LifeCycle.COMPILER)

    // koin
    implementation(Koin.ANDROID)
    implementation(Koin.CORE)

    // compose
    implementation(Compose.UI)
    implementation(Compose.UI_TOOLING_PREVIEW)
    debugImplementation(Compose.UI_TOOLING)
    implementation(Compose.MATERIAL3)
    implementation(Compose.ACTIVITY)
    implementation(Compose.NAVIGATION)
    implementation(Compose.LIVEDATA)
    implementation(Compose.KOIN)

    // test
    testImplementation(TestingLib.JUNIT)
    androidTestImplementation(AndroidTestingLib.ANDROIDX_TEST_EXT_JUNIT)
    androidTestImplementation(AndroidTestingLib.ANDROIDX_TEST_RULES)
    androidTestImplementation(AndroidTestingLib.ESPRESSO_CORE)
}
