plugins {
    `kotlin-dsl`
}

group = "com.lumilabs.sunshine.sharedlibraries.convention"

// Configure the build-logic plugins to target JDK 17
// This matches the JDK used to build the project, and is not related to what is running on device.
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}
dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.ksp.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("android-application-compose") {
            id = "sharedlibraries.android.application.compose"
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }
        register("android-application") {
            id = "sharedlibraries.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("android-library-compose") {
            id = "sharedlibraries.android.library.compose"
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }
        register("android-library") {
            id = "sharedlibraries.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("android-test") {
            id = "sharedlibraries.android.test"
            implementationClass = "AndroidTestConventionPlugin"
        }
        register("android-hilt") {
            id = "sharedlibraries.android.hilt"
            implementationClass = "AndroidHiltConventionPlugin"
        }
        register("jvm-library") {
            id = "sharedlibraries.jvm.library"
            implementationClass = "JvmLibraryConventionPlugin"
        }

        // Architecture
        /*register("arch-view") {
            id = "sharedlibraries.arch.view"
            implementationClass = "ArchViewConventionPlugin"
        }
        register("arch-domain") {
            id = "sharedlibraries.arch.domain"
            implementationClass = "ArchDomainConventionPlugin"
        }
        register("arch-infrastructure") {
            id = "sharedlibraries.arch.infrastructure"
            implementationClass = "ArchInfrastructureConventionPlugin"
        }*/
    }
}