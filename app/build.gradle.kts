plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("com.google.devtools.ksp")
}

android {
    namespace = "es.unex.giiis.asee.totalemergency"
    compileSdk = 33

    defaultConfig {
        applicationId = "es.unex.giiis.asee.totalemergency"
        minSdk = 24
        targetSdk = 33
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

    viewBinding {
        enable = true
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

    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    //implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.11")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.1")
    implementation("com.github.bumptech.glide:glide:5.0.0-rc01")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("androidx.preference:preference:1.2.1")

    //IO dependencies
    implementation("commons-io:commons-io:2.11.0")

    // Room dependencies
    val room_version = "2.5.0"

    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")

    ksp("androidx.room:room-compiler:$room_version")

    implementation("androidx.room:room-ktx:$room_version")

    testImplementation("androidx.room:room-testing:$room_version")

    // Navigation Component
    val navVersion = "2.5.3"


    // Kotlin
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")

    // Feature module Support
    implementation("androidx.navigation:navigation-dynamic-features-fragment:$navVersion")

    // Testing Navigation
    androidTestImplementation("androidx.navigation:navigation-testing:$navVersion")

    // Jetpack Preferences
    implementation("androidx.preference:preference-ktx:1.2.0")

    // Jetpack Compose Integration
    // implementation("androidx.navigation:navigation-compose:$navVersion")

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")


    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:4.0.0") // use the latest version
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")

    testImplementation("com.squareup.okhttp3:okhttp:4.9.1")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.9.1")
    testImplementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.10")


    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.5.0")
    testImplementation ("org.robolectric:robolectric:4.6.1")

    testImplementation("androidx.arch.core:core-testing:2.1.0")
    testImplementation("io.mockk:mockk:1.12.0")


    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.5.1")
    androidTestImplementation("org.mockito:mockito-android:4.0.0") // use the latest version
    androidTestImplementation ("io.mockk:mockk-android:1.12.0")
    androidTestImplementation("androidx.arch.core:core-testing:2.1.0") // use the latest version

    androidTestImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.5.0") // For coroutine testing
    androidTestImplementation ("androidx.test:core:1.3.0") // For ApplicationProvider

    androidTestImplementation("androidx.room:room-testing:2.3.0") // use the appropriate version



}