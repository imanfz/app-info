import org.jetbrains.kotlin.util.capitalizeDecapitalize.capitalizeAsciiOnly
import java.io.FileInputStream
import java.util.Properties

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
     alias(libs.plugins.application)
     alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.firebase.pref)
}

android {
    bundle {
        storeArchive {
            enable = false
        }
    }
    signingConfigs {
        register("release") {
            val keystorePropertiesFile = rootProject.file("keystore/keystore.properties")
            if (!keystorePropertiesFile.exists()) {
                logger.warn("Release builds may not work: signing config not found.")
                return@register
            }
            val prop = Properties()
            prop.load(FileInputStream(keystorePropertiesFile))
            storeFile = file(prop.getProperty("storeFile"))
            storePassword = prop.getProperty("storePassword")
            keyAlias = prop.getProperty("keyAlias")
            keyPassword = prop.getProperty("keyPassword")
            enableV1Signing = true
            enableV2Signing = true
            enableV3Signing = true
            enableV4Signing = true
        }
    }

    namespace = "dev.fztech.app.info"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "dev.fztech.app.info"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        multiDexEnabled = true
        resValue("string", "app_ad_id", "ca-app-pub-9650203619472107~5817335904")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
    packaging {
        dex.useLegacyPackaging = false
        resources.excludes.addAll(
            setOf(
                "/META-INF/{AL2.0,LGPL2.1,gradle-plugins}",
                "META-INF/MANIFEST.MF",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt",
                "META-INF/notice.txt",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/license.txt",
                "META-INF/DEPENDENCIES",
                "META-INF/ASL2.0",
                "META-INF/*.kotlin_module",
                "META-INF/proguard/coroutines.pro",
                "META-INF/com.android.tools/proguard/coroutines.pro",
                "META-INF/maven/com.google.protobuf/protobuf-javalite/pom.properties",
                "META-INF/maven/com.google.protobuf/protobuf-javalite/pom.xml",
                "protobuf.meta"
            )
        )
    }
    flavorDimensions += listOf("default")
    productFlavors {
        create("dev") {
            dimension = "default"
            applicationIdSuffix = ".dev"
            versionCode = 1
            versionName = "1.0.0"
            versionNameSuffix = "-dev"
            resValue("string", "app_name", "Dev App Info: Inspector")
            resValue("string", "open_ad_id", "ca-app-pub-3940256099942544/3419835294")
            resValue("string", "banner_ad_id", "ca-app-pub-3940256099942544/6300978111")
            resValue("string", "banner2_ad_id", "ca-app-pub-3940256099942544/6300978111")
        }
        create("prod") {
            dimension = "default"
            versionCode = 6
            versionName = "1.0.1"
            resValue("string", "app_name", "App Info: Inspector")
            resValue("string", "open_ad_id", "ca-app-pub-9650203619472107/6506196074")
            resValue("string", "banner_ad_id", "ca-app-pub-9650203619472107/8891055952")
            resValue("string", "banner2_ad_id", "ca-app-pub-9650203619472107/4150978550")
        }
    }
    applicationVariants.all {
        outputs.map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
            .forEach { output ->
                val outputFileName = "App_Info_Inspector-${versionName}($versionCode)-${buildType.name}.apk"
                output.outputFileName = outputFileName
            }
        outputs.all {
            // AAB file name that You want. Flavour name also can be accessed here.
            val aabPackageName = "App_Info_Inspector-${versionName}($versionCode)-${buildType.name}.aab"
            // Get final bundle task name for this variant
            val bundleFinalizeTaskName = StringBuilder("sign").run {
                // Add each flavor dimension for this variant here
                productFlavors.forEach {
                    append(it.name.capitalizeAsciiOnly())
                }
                // Add build type of this variant
                append(buildType.name.capitalizeAsciiOnly())
                append("Bundle")
                toString()
            }
            tasks.named(
                bundleFinalizeTaskName,
                com.android.build.gradle.internal.tasks.FinalizeBundleTask::class.java
            ) {
                val file = finalBundleFile.asFile.get()
                val finalFile = File(file.parentFile, aabPackageName)
                finalBundleFile.set(finalFile)
            }
        }
    }
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.core.splashscreen)
    implementation(libs.runtime.livedata)
//    implementation(libs.lifecycle.runtime.compose)
    // ViewModel
//    implementation(libs.lifecycle.viewmodel.ktx)
    // ViewModel utilities for Compose
//    implementation(libs.lifecycle.viewmodel.compose)
    // Saved state module for ViewModel
//    implementation(libs.lifecycle.viewmodel.savedstate)
    // Lifecycles only (without ViewModel or LiveData)
//    implementation(libs.lifecycle.runtime.ktx)
    // LiveData
//    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.activity.compose)
    implementation(libs.navigation.compose)
//    implementation(libs.constraintlayout.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)

    implementation(libs.app.update)
    implementation(libs.admob)
    implementation(libs.gdpr)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.pref)

    implementation(libs.coil.compose)
}