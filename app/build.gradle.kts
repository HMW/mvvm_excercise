plugins {
  alias(libs.plugins.com.android.application)
  alias(libs.plugins.org.jetbrains.kotlin.android)
  id("com.google.devtools.ksp")
}

android {
  namespace = "com.hm.currencyexercisexml"
  compileSdk = 33

  defaultConfig {
    applicationId = "com.hm.currencyexercisexml"
    minSdk = 26
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
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  kotlinOptions {
    jvmTarget = "17"
  }

  buildFeatures {
    viewBinding = true
  }
}

dependencies {
  // material
  implementation(libs.material)

  // androidx
  implementation(libs.core.ktx)
  implementation(libs.appcompat)
  implementation(libs.constraintlayout)
  implementation(libs.view.model)
  implementation(libs.livedata)
  implementation(libs.fragment.ktx)
  implementation(libs.preference)

  // retrofit
  implementation(libs.retrofit)
  implementation(libs.retrofit.converter.gson)
  implementation(libs.retrofit.interceptor)

  // room
  implementation(libs.room)
  implementation(libs.room.ktx)
  ksp(libs.room.annotation.processor)

  implementation(libs.slf4j)

  testImplementation(libs.junit)
  testImplementation(libs.mockk)
  testImplementation(libs.coroutine.test)

  androidTestImplementation(libs.androidx.test.ext.junit)
  androidTestImplementation(libs.espresso.core)
  androidTestImplementation(libs.coroutine.test)
}