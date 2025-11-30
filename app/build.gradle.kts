plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.jobapplicationportal"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.jobapplicationportal"
        minSdk = 26
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    // --- Android Core & UI ---
    implementation("androidx.appcompat:appcompat:1.7.0")
    // IMPORTANT: This version is needed for the new layouts
    implementation("com.google.android.material:material:1.12.0") 
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // --- Firebase (Cleaned Up) ---
    // We use the BOM to manage versions. 
    // Notice we REMOVED the version numbers from auth and database below.
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))

    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-analytics")

    // --- Firebase UI ---
    implementation("com.firebaseui:firebase-ui-database:8.0.2")

    // --- DataStore ---
    implementation("androidx.datastore:datastore-core-android:1.1.0-alpha06")

    // --- Testing ---
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}