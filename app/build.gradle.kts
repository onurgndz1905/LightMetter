plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.lux.light.meter.luminosity"
    compileSdk = 34

    buildFeatures {
        dataBinding = true
        viewBinding = true
    }
    defaultConfig {
        applicationId = "com.lux.light.meter.luminosity"
        minSdk = 24
        targetSdk = 34
        versionCode = 3
        versionName = "1.0.2"

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}



dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")

    implementation ("com.google.android.material:material:1.11.0")

    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    implementation("androidx.compose.ui:ui-android:1.6.3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")


    // Grafik
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    // Room
    implementation ("androidx.room:room-runtime:2.6.0-beta01")
    implementation ("androidx.room:room-ktx:2.6.0-beta01")
    kapt ("androidx.room:room-compiler:2.6.0-beta01")

    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.3.1")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")


    //Aplovin
    implementation("com.applovin:applovin-sdk:12.1.0")
    implementation("com.google.android.gms:play-services-base:17.1.0")
    implementation("androidx.lifecycle:lifecycle-process:2.2.0")

    implementation ("com.jakewharton.timber:timber:4.7.1")



    implementation("com.google.code.gson:gson:2.8.8")


    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:32.8.0"))

    // Add the dependencies for the Crashlytics and Analytics libraries
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-crashlytics")

    implementation("com.google.firebase:firebase-config")


    implementation("com.revenuecat.purchases:purchases:7.1.0")


    implementation("com.android.billingclient:billing:6.2.0")
    implementation("com.android.billingclient:billing-ktx:6.2.0")

}
