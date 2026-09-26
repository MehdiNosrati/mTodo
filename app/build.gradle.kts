import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.roborazzi)
}

android {
    namespace = "io.mns.base.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "dev.mahdins.mtodo"
        minSdk = 24
        targetSdk = 36
        versionCode = 26042007
        versionName = "2.5.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    val localProps = Properties().apply {
        val f = rootProject.file("local.properties")
        if (f.exists()) {
            f.inputStream().use { load(it) }
        }
    }

    signingConfigs {
        create("release") {
            val keystoreFile = localProps.getProperty("KEYSTORE_FILE")
                ?: (project.findProperty("KEYSTORE_FILE") as? String)
                ?: System.getenv("KEYSTORE_FILE")
                ?: "/Users/mns/Downloads/mns.jks"
            val keystorePassword = localProps.getProperty("KEYSTORE_PASSWORD")
                ?: (project.findProperty("KEYSTORE_PASSWORD") as? String)
                ?: System.getenv("KEYSTORE_PASSWORD")
            val keyAliasName = localProps.getProperty("KEY_ALIAS")
                ?: (project.findProperty("KEY_ALIAS") as? String)
                ?: System.getenv("KEY_ALIAS")
                ?: "mns"
            val keyPasswordValue = localProps.getProperty("KEY_PASSWORD")
                ?: (project.findProperty("KEY_PASSWORD") as? String)
                ?: System.getenv("KEY_PASSWORD")

            if (keystorePassword != null && keyPasswordValue != null && file(keystoreFile).exists()) {
                storeFile = file(keystoreFile)
                storePassword = keystorePassword
                keyAlias = keyAliasName
                keyPassword = keyPasswordValue
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            ndk {
                debugSymbolLevel = "SYMBOL_TABLE"
            }
            val releaseSigning = signingConfigs.getByName("release")
            if (releaseSigning.storeFile != null) {
                signingConfig = releaseSigning
            }
        }
    }
    buildFeatures {
        compose = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation(project(":android-utilities"))
    implementation(project(":shared"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.multidex)
    implementation(libs.material)
    implementation(libs.kotlinx.datetime)

    // Room
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)
    implementation(libs.room.ktx)

    // Lifecycle
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.runtime.ktx)

    // Koin
    implementation(libs.koin)
    implementation(libs.koin.compose)

    // Jetpack Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.compose.runtime.livedata)
    implementation(libs.activity.compose)
    implementation(libs.navigation.compose)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)

    // Tests
    testImplementation(libs.junit)
    testImplementation(libs.koin.test)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.androidx.arch.core.testing)
    testImplementation(libs.room.testing)
    testImplementation(libs.robolectric)
    testImplementation(libs.roborazzi)
    testImplementation(libs.roborazzi.compose)
    testImplementation(libs.roborazzi.junit.rule)
    testImplementation(libs.compose.ui.test.junit4)
    testImplementation(libs.androidx.junit)

    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.room.testing)
    androidTestImplementation(libs.compose.ui.test.junit4)
}