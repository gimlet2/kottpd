plugins {
    kotlin("multiplatform") version "1.9.21"
}

group = "com.github.gimlet2.kottpd"
version = "0.2.1"

repositories {
    mavenCentral()
}

kotlin {
    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = "20"
        }
        withJava()
    }
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")

    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    nativeTarget.apply {
        binaries {
            executable {
                baseName = "kottpd"
            }
            staticLib{
                baseName = "kottpd"
            }
        }
    }
    sourceSets {
        val commonMain by getting
        val nativeMain by getting
        val desktopMain by getting
        val desktopTest by getting

    }
}
