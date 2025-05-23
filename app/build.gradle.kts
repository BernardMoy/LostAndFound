import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    id("kotlin-parcelize")
    jacoco
}

val properties = Properties()
properties.load(FileInputStream(project.rootProject.file("local.properties")))

android {
    namespace = "com.example.lostandfound"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.lostandfound"
        minSdk = 29
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        buildConfigField("String", "MAPS_API_KEY", properties.getProperty("MAPS_API_KEY"))
        buildConfigField("String", "SENDER_EMAIL", properties.getProperty("SENDER_EMAIL"))
        buildConfigField("String", "SENDER_PASSWORD", properties.getProperty("SENDER_PASSWORD"))

        manifestPlaceholders["MAPS_API_KEY"] = properties.getProperty("MAPS_API_KEY")
    }

    packaging {
        resources {
            excludes += "META-INF/DEPENDENCIES"
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
        debug{
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
        compose = true
        mlModelBinding = true
    }

    packaging {
        resources {
            excludes += listOf("META-INF/NOTICE.md", "META-INF/LICENSE.md")
        }
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.activity)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.firebase.firestore)
    implementation(libs.jakarta.mail)
    implementation(libs.jakarta.activation)
    implementation(libs.core.ktx)
    implementation(libs.tensorflow.lite.support)
    implementation(libs.tensorflow.lite.metadata)
    implementation(libs.tensorflow.lite.gpu)
    implementation(libs.volley)
    testImplementation(libs.junit)
    testImplementation (libs.mockito.core)
    testImplementation (libs.coreTesting)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    val composeBom = platform("androidx.compose:compose-bom:2024.10.01")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation ("androidx.compose.runtime:runtime")
    implementation ("androidx.compose.ui:ui")
    implementation ("androidx.compose.foundation:foundation")
    implementation ("androidx.compose.foundation:foundation-layout")
    implementation ("androidx.compose.material3:material3")
    implementation ("androidx.compose.runtime:runtime-livedata")
    implementation ("androidx.compose.ui:ui-tooling")
    implementation ("androidx.activity:activity-compose:1.3.0-alpha07")
    implementation("androidx.navigation:navigation-compose: 2.7.6")
    implementation ("androidx.compose.material:material-icons-extended:1.7.6")
    implementation("io.coil-kt.coil3:coil-compose:3.0.4")

    implementation(platform("com.google.firebase:firebase-bom:33.12.0"))
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-functions")
    implementation("com.google.firebase:firebase-messaging")
    implementation ("com.github.bumptech.glide:glide:4.16.0")

    androidTestImplementation("androidx.compose.ui:ui-test-junit4-android:1.7.6")
    androidTestImplementation("io.github.aungthiha:compose-ui-test:1.0.0")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.7.6")

    implementation ("com.github.bumptech.glide:compose:1.0.0-beta01")

    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("com.google.maps.android:maps-compose:6.4.1")
    implementation("com.google.maps.android:maps-compose-utils:6.4.1")
    implementation("com.google.maps.android:maps-compose-widgets:6.4.1")
    implementation ("com.google.android.gms:play-services-location:21.3.0")

    implementation ("org.tensorflow:tensorflow-lite:2.12.0")
    implementation ("org.tensorflow:tensorflow-lite-support:0.4.4")

    implementation ("androidx.core:core-ktx:1.15.0")

    implementation ("org.mockito:mockito-core:5.15.2")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")

    implementation("com.google.auth:google-auth-library-oauth2-http:1.17.0")
    implementation("com.android.support.test.uiautomator:uiautomator-v18:2.1.1")


}
