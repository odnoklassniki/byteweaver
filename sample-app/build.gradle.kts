plugins {
    id("ru.ok.byteweaver")
    id("com.android.application").version("8.5.0")
    id("org.jetbrains.kotlin.android").version("1.9.20")
}

byteweaver {
    create("debug") {
        srcFiles += "byteweaver/ac_main.conf"
    }
}

android {
    namespace = "com.example.byteweaver"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.byteweaver"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("com.android.support:appcompat-v7:28.0.0")
    testImplementation("junit:junit:4.14")
    androidTestImplementation("com.android.support.test:runner:1.0.2")
    androidTestImplementation("com.android.support.test.espresso:espresso-core:3.0.2")
}