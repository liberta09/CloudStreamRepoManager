plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.kaan.cloudstreamrepomanager"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.kaan.cloudstreamrepomanager"
        minSdk = 24
        targetSdk = 35
        versionCode = 7
        versionName = "1.1.5"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    flavorDimensions += "version"
    productFlavors {
        create("user") {
            dimension = "version"
            applicationId = "com.kaan.cloudstreamrepomanager.user"
            manifestPlaceholders["appName"] = "Repo"
            buildConfigField("Boolean", "ENABLE_ADMIN_PANEL", "false")
        }
        create("admin") {
            dimension = "version"
            applicationId = "com.kaan.cloudstreamrepomanager.admin"
            manifestPlaceholders["appName"] = "Repo"
            buildConfigField("Boolean", "ENABLE_ADMIN_PANEL", "true")
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    signingConfigs {
        create("release") {
            val keystoreFile = System.getenv("KEYSTORE_FILE")
            if (keystoreFile != null && file(keystoreFile).exists()) {
                storeFile = file(keystoreFile)
                storePassword = System.getenv("KEYSTORE_PASSWORD")
                keyAlias = System.getenv("KEY_ALIAS")
                keyPassword = System.getenv("KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            if (System.getenv("KEYSTORE_FILE") != null) {
                signingConfig = signingConfigs.getByName("release")
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
    implementation("io.coil-kt:coil-compose:2.6.0")
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
