import android.annotation.SuppressLint

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    id("kotlin-kapt")
}

android {
    namespace = "com.example.fuuplugins"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.fuuplugins"
        minSdk = 27
        targetSdk = 33
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    //    packaging {
//        resources {
//            excludes += "/META-INF/{AL2.0,LGPL2.1}"
//        }
//    }
}

dependencies {
implementation(platform(libs.compose.bom))
    androidTestImplementation(platform(libs.compose.bom))
    //    implementation(libs.androidx.material3.android)
    val nav_version = "2.5.3"
    val room_version = "2.5.0"

    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    implementation(platform(libs.compose.bom))
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    androidTestImplementation(platform(libs.compose.bom))
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")
    implementation("androidx.navigation:navigation-compose:$nav_version")
    implementation("androidx.palette:palette:1.0.0")
    implementation("com.github.jeziellago:compose-markdown:0.3.4")
    implementation("io.coil-kt:coil-compose:2.4.0")

    implementation ("androidx.lifecycle:lifecycle-runtime-compose:2.6.0")
    // network
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")
    implementation("com.squareup.okio:okio:2.10.0")


    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")

    implementation("org.jsoup:jsoup:1.13.1")

//    implementation("androidx.paging:paging-runtime:3.2.0")
//    implementation("androidx.paging：paging-compose:1.0.0")
    implementation("com.github.theonenull:AndroidComposeMaterial:0.0.5")

    implementation("androidx.datastore:datastore-preferences-core:1.0.0")
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.datastore:datastore-core:1.0.0")

    implementation ("androidx.room:room-ktx:$room_version")
    kapt ("androidx.room:room-compiler:$room_version")

    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")

    implementation("com.google.accompanist:accompanist-systemuicontroller:0.31.0-alpha")

    implementation ("androidx.glance:glance:1.0.0-alpha05")
    implementation("androidx.glance:glance-appwidget:1.0.0-alpha05")

//    // For AppWidgets support
//    implementation ("androidx.glance:glance-appwidget:1.0.0-rc01")

//    // For interop APIs with Material 2
//    implementation ("androidx.glance:glance-material:1.0.0-rc01")
//
//    // For interop APIs with Material 3
//    implementation ("androidx.glance:glance-material3:1.0.0-rc01")

    implementation("com.github.skydoves:colorpicker-compose:1.0.4")

    implementation("com.github.theonenull:FuuInject:0.0.6")

}