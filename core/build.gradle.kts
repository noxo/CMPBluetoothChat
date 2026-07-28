plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.androidLint)
}

kotlin {
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "core"
            isStatic = true
        }
    }
    // Target declarations - add or remove as needed below. These define
    // which platforms this KMP module supports.
    // See: https://kotlinlang.org/docs/multiplatform-discover-project.html#targets
    android {
        namespace = "org.noxo.core"
        compileSdk {
            version = release(36) {
                minorApiLevel = 1
            }
        }
        minSdk = 24

        withHostTestBuilder {
        }

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }
    val falconVersion = "3.7.0"
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                implementation("dev.bluefalcon:blue-falcon-core:${falconVersion}")
                implementation("dev.bluefalcon:blue-falcon-peripheral:${falconVersion}")
                implementation("dev.bluefalcon:blue-falcon-plugin-logging:${falconVersion}")
                implementation("dev.bluefalcon:blue-falcon-plugin-retry:${falconVersion}")
                implementation("dev.bluefalcon:blue-falcon-plugin-nordic-fota:${falconVersion}")
                implementation("dev.bluefalcon:blue-falcon-plugin-clone:${falconVersion}")
                implementation("dev.bluefalcon:blue-falcon-plugin-broadcast:${falconVersion}")
                api(libs.koin.core)
                implementation(libs.koin.compose)
                implementation(libs.koin.compose.viewmodel)
                implementation("co.touchlab:kermit:2.1.0")
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        androidMain {
            dependencies {
                implementation("androidx.appcompat:appcompat:1.6.1")
                implementation("androidx.activity:activity-compose:1.7.2")
                implementation("dev.bluefalcon:blue-falcon-engine-android:$falconVersion")

            }
        }

        getByName("androidDeviceTest") {
            dependencies {
                implementation(libs.androidx.core)
                implementation(libs.androidx.runner)
                implementation(libs.androidx.testExt.junit)
            }
        }

        iosMain {
            dependencies {
                dependencies {
                    // Blue Falcon iOS Engine
                    implementation("dev.bluefalcon:blue-falcon-engine-ios:$falconVersion")
                }
            }
        }
    }

}