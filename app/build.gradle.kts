plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.studyflow"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.studyflow"
        minSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    //google ar code implementation
    implementation("com.google.ar:core:1.27.0")

    //cardview
    implementation("androidx.cardview:cardview:1.0.0")


    //firebase boom
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))

    //firebase services that we want to use - analytics as example
    implementation("com.google.firebase:firebase-database-ktx")
    implementation(libs.firebase.auth.ktx)
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation ("com.google.firebase:firebase-auth:23.1.0")

    implementation ("com.mapbox.maps:android:11.7.1")


    //chart
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")






    //swiping animation
    implementation ("androidx.recyclerview:recyclerview:1.3.1")
    implementation ("androidx.appcompat:appcompat:1.6.1")

    //material design
    implementation ("com.google.android.material:material:1.9.0")

    implementation ("io.github.wojciechosak:calendar:1.0.1")



    implementation(libs.mapbox.android)


    implementation(libs.mapbox.android)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.firestore.ktx) //firestore for firebase
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.firebase.auth)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

