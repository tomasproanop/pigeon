plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.app.pigeon"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.app.pigeon"
        minSdk = 24
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

    implementation("commons-io:commons-io:2.13.0")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.core)
    implementation(libs.espresso.intents)
    implementation(libs.swiperefreshlayout)
    testImplementation(libs.junit)

    // Test
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
    testImplementation("org.mockito:mockito-core:4.2.0")
    testImplementation("org.mockito:mockito-inline:4.2.0")
    //testImplementation("org.mockito:mockito-kotlin:4.0.0")

    // Mockito
    testImplementation("org.mockito:mockito-core:5.12.0")
    //testImplementation ("androidx.test.ext:junit:4.12")
    testImplementation ("androidx.test.espresso:espresso-core:3.5.1")
    testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.2")
    testImplementation ("org.mockito:mockito-core:4.0.0")
    testImplementation ("org.mockito:mockito-inline:4.0.0")
    //testImplementation ("org.mockito.kotlin:mockito-kotlin:4.0.0")
    //testImplementation ("io.mockk:mockk:4.0.0")
    androidTestImplementation ("androidx.test:runner:1.4.0")
    androidTestImplementation ("androidx.test:rules:1.4.0")

    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation ("androidx.core:core-ktx:1.10.1")
    implementation ("com.google.android.material:material:1.9.0")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")

    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0-alpha01")

    // Unit testing dependencies
    //testImplementation ("junit:junit:4.13.2")
    //testImplementation ("junit.runners:AndroidJUnit4")
    testImplementation ("org.mockito:mockito-core:3.11.2")
    testImplementation ("org.mockito:mockito-inline:3.11.2")
    //testImplementation ("org.mockito:mockito-kotlin:3.2.0")

    // AndroidX Test - Instrumented testing dependencies
    androidTestImplementation ("androidx.test.ext:junit:1.1.3v")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.4.0")

    // MockK for Kotlin
    //testImplementation ("io.mockk:mockk:1.12.0")

}