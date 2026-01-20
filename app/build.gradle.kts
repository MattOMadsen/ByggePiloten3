// Fil: app/build.gradle.kts
// Opdateret: Tilføjet multiDexEnabled = true for at eliminere debug-overlay duplicate class warnings
//            Beholdt dine danske kommentarer på Gemini API-key

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.dagger.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.firebase.perf)
}

android {
    namespace = "dk.byggepiloten.firma"
    compileSdk = 35

    defaultConfig {
        applicationId = "dk.byggepiloten.firma"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        // Tilføjet for at fjerne duplicate class / overlay warnings i debug (især med Hilt)
        multiDexEnabled = true

        testInstrumentationRunner = "dk.bygepiloten.firma.HiltTestRunner"
        vectorDrawables { useSupportLibrary = true }

        testInstrumentationRunnerArguments["disableAnalytics"] = "true"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Gemini API-key – loades sikkert fra gradle.properties
            buildConfigField("String", "GEMINI_API_KEY", "\"${project.findProperty("GEMINI_API_KEY") ?: ""}\"")
        }
        debug {
            isMinifyEnabled = false // Holder debug-builds hurtige og undgår ProGuard-problemer under udvikling
            // Gemini API-key – loades sikkert fra gradle.properties (samme i debug)
            buildConfigField("String", "GEMINI_API_KEY", "\"${project.findProperty("GEMINI_API_KEY") ?: ""}\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
}

dependencies {
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Core
    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(libs.coil.compose)

    // Compose
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.compose.animation)
    implementation(libs.compose.runtime)
    implementation(libs.compose.runtime.livedata)
    implementation(libs.navigation.compose)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.lifecycle.viewmodel.compose)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Firebase BOM
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.crashlytics)
    implementation("com.google.firebase:firebase-appcheck-playintegrity")
    debugImplementation(libs.firebase.appcheck.debug)
    releaseImplementation(libs.firebase.appcheck.playintegrity)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.perf)

    // Play Services
    implementation(libs.play.services.tasks)

    // Navigation (duplikat fjernet – allerede dækket ovenfor)
    // implementation(libs.navigation.compose) ← fjernet, da den allerede er tilføjet via Compose-sektionen

    // WorkManager
    implementation(libs.work.runtime.ktx)

    // Retrofit + Gson
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.gson)

    // Timber
    implementation(libs.timber)

    // DataStore
    implementation(libs.datastore.preferences)

    // ML Kit / Gemini Nano
    implementation("com.google.mlkit:vision-common:${libs.versions.mlkit.get()}")
    implementation("com.google.mlkit:genai-prompt:${libs.versions.genaiPrompt.get()}")
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")

    // Hilt WorkManager
    implementation(libs.hilt.work)
    ksp(libs.hilt.work.compiler)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.espresso.intents)

    testImplementation(libs.dagger.hilt.android.testing)
    kspTest(libs.dagger.hilt.android.compiler.test)

    androidTestImplementation(libs.dagger.hilt.android.testing)
    kspAndroidTest(libs.dagger.hilt.android.compiler.test)

    androidTestImplementation("io.mockk:mockk-android:1.13.12")
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")

    androidTestImplementation(libs.mockito.core)
    androidTestImplementation(libs.mockito.android)
    androidTestImplementation(libs.mockito.kotlin)

    testImplementation("androidx.navigation:navigation-testing:2.8.3")

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.6.8")

    debugImplementation("androidx.compose.ui:ui-tooling:1.6.8")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.6.8")

    testImplementation("app.cash.paparazzi:paparazzi:1.3.4")
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.generateKotlin", "true")
    arg("room.incremental", "true")
}

hilt {
    enableAggregatingTask = true
}