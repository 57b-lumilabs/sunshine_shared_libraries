plugins {
    alias(libs.plugins.depsmanagement.android.application)
    alias(libs.plugins.depsmanagement.android.application.compose)
    alias(libs.plugins.depsmanagement.android.hilt)
}

android {
    namespace = "com.lumilabs.sunshine.sharedlibraries"

    defaultConfig {
        applicationId = "com.lumilabs.sunshine.sharedlibraries"
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}