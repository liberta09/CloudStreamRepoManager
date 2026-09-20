plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.kaan.cloudstreamrepomanager"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.kaan.cloudstreamrepomanager"
        minSdk = 24
        targetSdk = 37
        versionCode = 2
        versionName = "1.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    flavorDimensions += "version"
    productFlavors {
        create("user") {
            dimension = "version"
            applicationId = "com.kaan.cloudstreamrepomanager.user"
            manifestPlaceholders["appName"] = "CloudStream Repo Manager"
            buildConfigField("Boolean", "ENABLE_ADMIN_PANEL", "false")
        }
        create("admin") {
            dimension = "version"
            applicationId = "com.kaan.cloudstreamrepomanager.admin"
            manifestPlaceholders["appName"] = "CloudStream Repo Manager Admin"
            buildConfigField("Boolean", "ENABLE_ADMIN_PANEL", "true")
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("debug")
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
