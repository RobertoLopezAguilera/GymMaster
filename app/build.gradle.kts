plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")
    //id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.robertolopezaguilera.gimnasio"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.robertolopezaguilera.gimnasio"
        minSdk = 28
        targetSdk = 35
        versionCode = 4
        versionName = "1.4"

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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // ✅ Room
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    // (Opcional) Paging con Room
    implementation("androidx.room:room-paging:2.6.1")

    // Soporte para fechas modernas (java.time.*)
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    // Material3
    implementation("androidx.compose.material3:material3:1.2.1")

    // Compose Navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")

    //FireBase
    implementation(platform("com.google.firebase:firebase-bom:33.15.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-firestore")

    implementation ("com.google.firebase:firebase-auth:24.0.1")
    implementation ("com.google.firebase:firebase-firestore:26.0.0")
    implementation ("com.google.android.gms:play-services-auth:20.7.0")

    //Facebook
    //implementation ("com.facebook.android:facebook-android-sdk:[4,5)")
    implementation("com.facebook.android:facebook-login:16.3.0")

    // Lottie para Jetpack Compose
    //implementation("com.airbnb.android:lottie-compose:6.4.0")

    implementation("androidx.media3:media3-exoplayer:1.2.1")
    implementation("androidx.media3:media3-ui:1.2.1")
    implementation("io.coil-kt:coil-compose:2.4.0")

    // Graficas
    implementation("com.github.tehras:charts:0.2.4-alpha")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    //"C:\Users\Dell\Desktop\keystoreGymManager.jks"
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    //AdMob
    implementation("com.google.android.gms:play-services-ads:24.4.0")
    //implementation("com.google.android.gms:play-services-ads-lite:22.6.0")
}