@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import com.vanniktech.maven.publish.SonatypeHost
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform.getCurrentOperatingSystem
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.android.library)
//    alias(libs.plugins.compose.compiler)
//    alias(libs.plugins.compose)
    id("maven-publish")
    id("signing")
    alias(libs.plugins.maven.publish)
    id("io.github.ttypic.swiftklib") version "0.6.4"

}




apply(plugin = "maven-publish")
apply(plugin = "signing")


tasks.withType<PublishToMavenRepository> {
    val isMac = getCurrentOperatingSystem().isMacOsX
    onlyIf {
        isMac.also {
            if (!isMac) logger.error(
                """
                    Publishing the library requires macOS to be able to generate iOS artifacts.
                    Run the task on a mac or use the project GitHub workflows for publication and release.
                """
            )
        }
    }
}


extra["packageNameSpace"] = "io.tbib.klocal_notification"
extra["groupId"] = "io.github.the-best-is-best"
extra["artifactId"] = "klocal-notification"
extra["version"] = "1.0.0-6-rc"
extra["packageName"] = "KLocalNotification"
extra["packageUrl"] = "https://github.com/the-best-is-best/KLocalNotification"
extra["packageDescription"] =
    "KLocalNotification package provides a streamlined way to create and schedule local notifications for both Android and iOS platforms within Kotlin Multiplatform projects. It allows developers to set up notifications that inform or alert users at specified times or intervals, enhancing user engagement and facilitating reminders or updates."
extra["system"] = "GITHUB"
extra["issueUrl"] = "https://github.com/the-best-is-best/KLocalNotification/issues"
extra["connectionGit"] = "https://github.com/the-best-is-best/KLocalNotification.git"

extra["developerName"] = "Michelle Raouf"
extra["developerNameId"] = "MichelleRaouf"
extra["developerEmail"] = "eng.michelle.raouf@gmail.com"


mavenPublishing {
    coordinates(
        extra["groupId"].toString(),
        extra["artifactId"].toString(),
        extra["version"].toString()
    )

    publishToMavenCentral(SonatypeHost.S01, true)
    signAllPublications()

    pom {
        name.set(extra["packageName"].toString())
        description.set(extra["packageDescription"].toString())
        url.set(extra["packageUrl"].toString())
        licenses {
            license {
                name.set("Apache-2.0")
                url.set("https://opensource.org/licenses/Apache-2.0")
            }
        }
        issueManagement {
            system.set(extra["system"].toString())
            url.set(extra["issueUrl"].toString())
        }
        scm {
            connection.set(extra["connectionGit"].toString())
            url.set(extra["packageUrl"].toString())
        }
        developers {
            developer {
                id.set(extra["developerNameId"].toString())
                name.set(extra["developerName"].toString())
                email.set(extra["developerEmail"].toString())
            }
        }
    }

}


signing {
    useGpgCmd()
    sign(publishing.publications)
}

val packageName = extra["packageName"].toString()


kotlin {
    jvmToolchain(17)
    androidTarget {
        //https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-test.html
        instrumentedTestVariant.sourceSetTree.set(KotlinSourceSetTree.test)
    }

//    jvm()
//
//    js {
//        browser()
//        binaries.executable()
//    }
//
//    @OptIn(ExperimentalWasmDsl::class)
//    wasmJs {
//        browser()
//        binaries.executable()
//    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
        macosX64(),
        macosArm64(),
        tvosX64(),
        tvosArm64(),
        tvosSimulatorArm64(),
        watchosX64(),
        watchosArm64(),
        watchosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = packageName
            isStatic = true
        }
        it.compilations {
            val main by getting {
                cinterops {
                    create("MyLocalNotification")
                }
            }
        }
    }

    sourceSets {
        commonMain.dependencies {
//            implementation(compose.runtime)
//            implementation(compose.foundation)
//            implementation(compose.material3)
//            implementation(compose.components.resources)
//            implementation(compose.components.uiToolingPreview)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.coroutines.core)


        }

        commonTest.dependencies {
            implementation(kotlin("test"))
//            @OptIn(ExperimentalComposeLibrary::class)
//            implementation(compose.uiTest)
        }

        androidMain.dependencies {
//            implementation(compose.uiTooling)
//            implementation(libs.androidx.activityCompose)
            implementation(libs.kpermissions)
            implementation(libs.gson)
            implementation(libs.androidx.core.ktx)

        }

//        jvmMain.dependencies {
//            implementation(compose.desktop.currentOs)
//        }
//
//        jsMain.dependencies {
//            implementation(compose.html.core)
//        }

        iosMain.dependencies {
        }

    }
}


android {
    namespace = extra["packageNameSpace"].toString()
    compileSdk = 35

    defaultConfig {
        minSdk = 23
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }
//        buildFeatures {
//            //enables a Compose tooling support in the AndroidStudio
//            compose = true
//        }
    }
}

//https://developer.android.com/develop/ui/compose/testing#setup
dependencies {
    androidTestImplementation(libs.androidx.uitest.junit4)
    debugImplementation(libs.androidx.uitest.testManifest)
    //temporary fix: https://youtrack.jetbrains.com/issue/CMP-5864
    androidTestImplementation("androidx.test:monitor") {
        version { strictly("1.6.1") }
    }
}
//val composePackage = extra["packageNameSpace"].toString()
//compose.desktop {
//    application {
//        mainClass = "MainKt"
//
//        nativeDistributions {
//            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
//            packageName = composePackage
//            packageVersion = "1.0.0"
//
//            linux {
//                iconFile.set(project.file("desktopAppIcons/LinuxIcon.png"))
//            }
//            windows {
//                iconFile.set(project.file("desktopAppIcons/WindowsIcon.ico"))
//            }
//            macOS {
//                iconFile.set(project.file("desktopAppIcons/MacosIcon.icns"))
//                bundleID = "org.company.app.desktopApp"
//            }
//        }
//    }
//}

swiftklib {
    create("MyLocalNotification") {
        path = file("native/my_local_notification")
        packageName("io.github.native.my_local_notification")
    }
}