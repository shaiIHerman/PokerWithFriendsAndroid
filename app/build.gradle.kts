plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("kapt")
    alias(libs.plugins.hilt)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.shai.pokerwithfriendsandroid"
    compileSdk = 35
    buildFeatures {
        buildConfig = true
    }
    defaultConfig {
        applicationId = "com.shai.pokerwithfriendsandroid"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "GOOGLE_SIGN_IN_CLIENT_ID",
            "\"${project.findProperty("googleSignInClientId")}\""
        )
    }

    buildTypes {
        debug {
            buildConfigField(
                "String",
                "GOOGLE_SIGN_IN_CLIENT_ID",
                "\"${project.findProperty("googleSignInClientId")}\""
            )
        }
        release {
            isMinifyEnabled = false
            buildConfigField(
                "String",
                "GOOGLE_SIGN_IN_CLIENT_ID",
                "\"${project.findProperty("googleSignInClientId")}\""
            )
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
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
    implementation(libs.material.icons.core)
    implementation(libs.androidx.runtime.livedata)

    implementation(libs.bundles.ktor)
    implementation(libs.bundles.hilt)
    kapt(libs.hilt.android.compiler)
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)
    implementation(libs.play.services.auth)
    implementation(libs.bundles.room)
    kapt(libs.androidx.room.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

kapt {
    correctErrorTypes = true
}