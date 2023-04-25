plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.github.domyn.colorfulcalendar.sample"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.github.domyn.colorfulcalendar.sample"
        minSdk = 21
        //noinspection OldTargetApi
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
        languageVersion = "1.8"
    }
}

dependencies {
    implementation(project(":library"))
    implementation("androidx.appcompat:appcompat:1.6.1")
}
