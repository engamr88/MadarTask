plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "org.qxteam.madartask.core.navigation"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // No extra dependencies needed for simple navigation paths
}
