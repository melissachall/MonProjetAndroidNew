import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig
import com.codingfeline.buildkonfig.compiler.FieldSpec
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    // alias(libs.plugins.androidApplication) // déjà activé avec id plus bas
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.buildkonfig)
    id("com.android.application")
    id("com.google.gms.google-services")
}

kotlin {
    @OptIn(ExperimentalWasmDsl::class)

    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }


    sourceSets {

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.ktor.core)
            implementation(libs.coil.compose.core)
            implementation(libs.coil.compose)
            implementation(libs.coil.mp)
            implementation(libs.coil.network.ktor)
            implementation(libs.voyager.navigator)
            implementation(libs.voyager.tab.navigator)
            implementation(libs.voyager.screenmodel)
            implementation(libs.lifecycle.viewmodel.compose)
            implementation(libs.markdown.renderer)
            api(libs.compose.window.size)
            api(libs.generativeai)
            implementation(libs.filekit.compose)

        }


        commonTest.dependencies {
            implementation(kotlin("test"))
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
        }

        androidMain.dependencies {
            implementation(platform("com.google.firebase:firebase-bom:33.16.0"))
            implementation("com.google.firebase:firebase-auth")
            implementation("com.google.firebase:firebase-auth-ktx")
            implementation("com.google.firebase:firebase-common")
            implementation("com.google.firebase:firebase-firestore")
            implementation("com.google.firebase:firebase-firestore-ktx")
            implementation("com.google.android.gms:play-services-auth:21.0.0")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android")
            implementation("androidx.credentials:credentials:1.3.0")
            implementation("androidx.credentials:credentials-play-services-auth:1.3.0")
            implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")
            implementation("androidx.compose.material3:material3")
            implementation("androidx.compose.material:material")
            //implementation("androidx.compose.material:material-pullrefresh:1.6.1")
            //implementation("cafe.adriel.voyager:voyager-navigator")
            //implementation("cafe.adriel.voyager:voyager-tab:1.0.0")

            
            implementation(compose.uiTooling)
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.okhttp)
        }

    }
}

android {
    namespace = "com.travel.buddy"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "com.travel.buddy"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
    dependencies {
        debugImplementation(compose.uiTooling)
        implementation(platform("com.google.firebase:firebase-bom:33.16.0"))
    }
}


buildkonfig {
    packageName = "com.travel.buddy"

    val geminiApiKey = project.findProperty("gemini_api_key") as? String ?: ""

    defaultConfigs {
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "GEMINI_API_KEY",
            geminiApiKey
        )
    }
}
