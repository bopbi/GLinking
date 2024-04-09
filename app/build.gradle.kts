import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
}

val localProperties = Properties().apply {
    FileInputStream(rootProject.file("local.properties")).use { load(it) }
}

android {
    namespace = "com.bobbyprabowo.android.googlelinking"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.bobbyprabowo.android.googlelinking"
        minSdk = 26
        targetSdk = 34
        versionCode = 3
        versionName = "1.0 - $versionCode"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val serviceId = localProperties.getProperty("SERVICE_ID") ?: ""
        buildConfigField("String", "SERVICE_ID", "\"$serviceId\"")
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
    // Enable generation of BuildConfig class
    buildFeatures {
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.play.auth)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
