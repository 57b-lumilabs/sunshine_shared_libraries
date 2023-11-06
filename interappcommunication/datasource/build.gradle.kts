plugins {
    alias(libs.plugins.sharedlibraries.android.library)
}

android {
    namespace = "com.lumilabs.sunshine.sharedlibraries.interappcommunication.datasource"
}

dependencies {
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.play.services.auth.blockstore)
    implementation(libs.kotlinx.coroutines)

    implementation(project(":interappcommunication:infrastructure"))
    implementation(project(":interappcommunication:model"))
}