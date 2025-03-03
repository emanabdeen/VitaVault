plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.insight"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.insight"
        minSdk = 31
        targetSdk = 34
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

    buildFeatures{
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    packaging {
        resources.excludes.add("META-INF/*")
    }
}

dependencies {
    // Core
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)

    //Google
    implementation(platform("com.google.cloud:libraries-bom-protobuf3:26.55.0"))
    implementation("com.google.cloud:google-cloud-vision")
    implementation("com.google.auth:google-auth-library-oauth2-http")

    // gRPC dependencies
    implementation("io.grpc:grpc-okhttp:1.42.1")
    implementation("io.grpc:grpc-stub:1.42.1")
    implementation("javax.annotation:javax.annotation-api:1.3.2")

    // Camerax
    implementation("androidx.camera:camera-core:1.4.1")
    implementation("androidx.camera:camera-camera2:1.4.1")
    implementation("androidx.camera:camera-lifecycle:1.4.1")
    implementation("androidx.camera:camera-view:1.4.1")


    // tesseract
    //implementation("cz.adaptech.tesseract4android:tesseract4android:4.8.0")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}