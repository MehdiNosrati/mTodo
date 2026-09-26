plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.room)
    alias(libs.plugins.ksp)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "mTodoShared"
            isStatic = true
            linkerOpts.add("-lsqlite3")
            binaryOption("bundleId", "dev.mahdins.mtodo.shared")
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.materialIconsExtended)

            implementation(libs.lifecycle.viewmodel)
            implementation(libs.lifecycle.runtime.compose)

            implementation(libs.coroutines.core)

            // Room KMP
            implementation(libs.room.runtime)
            implementation(libs.sqlite.bundled)

            // Koin KMP
            implementation(libs.koin.core)
            implementation(libs.koin.compose.multiplatform)
            implementation(libs.koin.compose.viewmodel)

            // Kotlinx
            api(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization.json)
        }

        androidMain.dependencies {
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.appcompat)
            implementation(libs.koin)
            implementation(libs.lifecycle.viewmodel.ktx)
            implementation(libs.lifecycle.livedata.ktx)
        }

        iosMain.dependencies {
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.coroutines.test)
        }
    }
}

android {
    namespace = "io.mns.base.app.shared"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    add("kspAndroid", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)
    add("kspIosX64", libs.room.compiler)
    add("kspIosArm64", libs.room.compiler)
}
