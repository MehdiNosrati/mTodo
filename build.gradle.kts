import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    id("com.android.application") version BuildPluginsVersion.AGP apply false
    id("com.android.library") version BuildPluginsVersion.AGP apply false
    kotlin("jvm") version BuildPluginsVersion.KOTLIN apply false
    id("org.jetbrains.kotlin.plugin.parcelize") version BuildPluginsVersion.KOTLIN apply false
    id("org.jetbrains.kotlin.plugin.compose") version BuildPluginsVersion.KOTLIN apply false
    id("com.google.devtools.ksp") version BuildPluginsVersion.KSP apply false
    id("io.gitlab.arturbosch.detekt") version BuildPluginsVersion.DETEKT
    id("org.jlleitschuh.gradle.ktlint") version BuildPluginsVersion.KTLINT
    id("com.github.ben-manes.versions") version BuildPluginsVersion.VERSIONS_PLUGIN
}

allprojects {
    group = PUBLISHING_GROUP
    repositories {
        google()
        mavenCentral()
        maven {
            setUrl("https://jitpack.io")
        }
    }
}

buildscript {
    repositories {
        google()
        maven {
            setUrl("https://jitpack.io")
        }
    }

    dependencies {
        classpath(Navigation.SAFE_ARGS)
    }
}

subprojects {
    // Only apply quality plugins to subprojects that have Android or Kotlin applied
    pluginManager.withPlugin("com.android.application") {
        apply(plugin = "io.gitlab.arturbosch.detekt")
        apply(plugin = "org.jlleitschuh.gradle.ktlint")
        configureQualityPlugins(this@subprojects)
    }
    pluginManager.withPlugin("com.android.library") {
        apply(plugin = "io.gitlab.arturbosch.detekt")
        apply(plugin = "org.jlleitschuh.gradle.ktlint")
        configureQualityPlugins(this@subprojects)
    }
    pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
        apply(plugin = "io.gitlab.arturbosch.detekt")
        apply(plugin = "org.jlleitschuh.gradle.ktlint")
        configureQualityPlugins(this@subprojects)
    }
}

fun configureQualityPlugins(project: Project) {
    project.configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        debug.set(false)
        version.set(Versions.KTLINT)
        verbose.set(true)
        android.set(false)
        outputToConsole.set(true)
        ignoreFailures.set(false)
        enableExperimentalRules.set(true)
        filter {
            exclude("**/generated/**")
            include("**/kotlin/**")
        }
    }

    project.configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
        config = project.rootProject.files("config/detekt/detekt.yml")
        reports {
            html {
                required.set(true)
                outputLocation.set(project.file("build/reports/detekt.html"))
            }
        }
    }
}

tasks.register("clean", Delete::class.java) {
    delete(rootProject.layout.buildDirectory)
}

tasks.withType<DependencyUpdatesTask> {
    rejectVersionIf {
        isNonStable(candidate.version)
    }
}

fun isNonStable(version: String) = "^[0-9,.v-]+(-r)?$".toRegex().matches(version).not()
