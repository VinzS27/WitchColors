// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("androidx.navigation.safeargs.kotlin") version "2.8.3" apply false
    id("com.google.devtools.ksp") version "1.9.24-1.0.20" apply false
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
}