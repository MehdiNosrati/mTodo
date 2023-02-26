plugins {
    id("mtodo.android.library")
    id("mtodo.android.hilt")
}

android {
    namespace = "dev.mahdins.core.date"
}

dependencies {
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    kapt(libs.room.compiler)
}