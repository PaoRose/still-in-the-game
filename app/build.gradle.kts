import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

// RevenueCat API key lives in local.properties (never in git).
// For the demo use the Test Store key from the RevenueCat dashboard:
//   revenuecat.apiKey=test_XXXXXXXX
val localProps = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}
val revenueCatKey: String = localProps.getProperty("revenuecat.apiKey") ?: ""

android {
    namespace = "com.paorose.stillinthegame"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.paorose.stillinthegame"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        buildConfigField("String", "REVENUECAT_API_KEY", "\"$revenueCatKey\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            // Signed with the debug key so the release APK installs anywhere for judging.
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
        // RevenueCat 9.x may be compiled with a newer Kotlin than ours.
        freeCompilerArgs += "-Xskip-metadata-version-check"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" }
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.06.00")
    implementation(composeBom)

    implementation("androidx.core:core-ktx:1.13.1")
    // Installs Compose's baseline profiles, so the app opens faster.
    implementation("androidx.profileinstaller:profileinstaller:1.3.1")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.2")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.animation:animation")
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("com.revenuecat.purchases:purchases:9.9.0")

    debugImplementation("androidx.compose.ui:ui-tooling")
}
