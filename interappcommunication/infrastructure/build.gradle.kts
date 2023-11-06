plugins {
    alias(libs.plugins.sharedlibraries.jvm.library)
}

dependencies {
    implementation(project(":interappcommunication:model"))
}