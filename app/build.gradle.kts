plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.codewithngoc.instagallery"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.codewithngoc.instagallery"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.core.splashscreen)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation (libs.logging.interceptor)

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.lifecycle.runtime.compose)

    implementation (libs.coil.compose)

    implementation("androidx.compose.material3:material3-window-size-class:1.2.1")

    implementation("com.google.accompanist:accompanist-permissions:0.34.0") // hoặc phiên bản mới hơn


}

kapt {
    correctErrorTypes = true
}

// ✅ Ép buộc version thư viện serialization để tránh lỗi Kotlin version mismatch
configurations.all {
    resolutionStrategy {
        force("org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:1.6.3")
        force("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.6.3")
    }
}