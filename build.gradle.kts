import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform
import org.gradle.nativeplatform.platform.internal.DefaultOperatingSystem
import org.jetbrains.gradle.ext.Application
import org.jetbrains.gradle.ext.runConfigurations
import org.jetbrains.gradle.ext.settings

plugins {
    id("java")
    id("application")
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.1.7"
}

group = "net.minecraft"
version = "1.8.9-OFM5"

repositories {
    mavenCentral()
}

//client dependencies
dependencies {
    compileOnly("org.projectlombok:lombok:1.18.24")
    annotationProcessor("org.projectlombok:lombok:1.18.24")

    testCompileOnly("org.projectlombok:lombok:1.18.24")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.24")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

sourceSets {
    main {
        java.srcDirs("src/minecraft")
    }
}

tasks.withType<Jar> {
    manifest {
        attributes(mapOf("Main-Class" to "net.minecraft.client.main.Main"))
    }

    configurations.implementation.get().isCanBeResolved = true
    from({
        configurations.implementation.get().filter { it.name.equals("1.8.9.jar", true) }.map { zipTree(it) }
    })
}

//minecraft dependencies
val minecraftLibraries = arrayOf(
    "oshi-project:oshi-core:1.1",
    "net.java.dev.jna:jna:3.4.0",
    "net.java.dev.jna:platform:3.4.0",
    "com.ibm.icu:icu4j-core-mojang:51.2",
    "net.sf.jopt-simple:jopt-simple:4.6",
    "com.paulscode:codecjorbis:20101023",
    "com.paulscode:codecwav:20101023",
    "com.paulscode:libraryjavasound:20101123",
    "com.paulscode:librarylwjglopenal:20100824",
    "com.paulscode:soundsystem:20120107",
    "io.netty:netty-all:4.0.23.Final",
    "com.google.guava:guava:17.0",
    "org.apache.commons:commons-lang3:3.3.2",
    "commons-io:commons-io:2.4",
    "commons-codec:commons-codec:1.9",
    "net.java.jinput:jinput:2.0.5",
    "net.java.jutils:jutils:1.0.0",
    "com.google.code.gson:gson:2.2.4",
    "com.mojang:authlib:1.5.21",
    "com.mojang:realms:1.7.59",
    "org.apache.commons:commons-compress:1.8.1",
    "org.apache.httpcomponents:httpclient:4.3.3",
    "commons-logging:commons-logging:1.1.3",
    "org.apache.httpcomponents:httpcore:4.3.2",
    "org.lwjgl.lwjgl:lwjgl:2.9.4-nightly-20150209",
    "org.lwjgl.lwjgl:lwjgl_util:2.9.4-nightly-20150209",
    "org.lwjgl.lwjgl:lwjgl-platform:2.9.4-nightly-20150209",
    "net.java.jinput:jinput-platform:2.0.5",
    "tv.twitch:twitch:6.5",

    //updated log4j
    "org.apache.logging.log4j:log4j-api:2.20.0",
    "org.apache.logging.log4j:log4j-core:2.20.0",

//    "com.mojang:netty:1.8.8",
)

val lwjglNatives = "natives-${getCurrentOs()}"
val minecraftNativeLibraries = arrayOf(
    "org.lwjgl.lwjgl:lwjgl-platform:2.9.4-nightly-20150209:$lwjglNatives",
    "net.java.jinput:jinput-platform:2.0.5:$lwjglNatives"
)

repositories {
    maven(url = "https://libraries.minecraft.net")

    flatDir {
        dirs("libs")
    }
}

dependencies {
    implementation(":1.8.9")

    minecraftLibraries.forEach { implementation(it) }
    minecraftNativeLibraries.forEach { runtimeOnly(it) }
}

configurations.runtimeOnly.get().isCanBeResolved = true
configurations.runtimeOnly.get().asFileTree.forEach { file ->
    val os: OperatingSystem = DefaultNativePlatform.getCurrentOperatingSystem()
    val zip: FileTree = zipTree(file)

    zip.filter {
        val name = it.name.lowercase()
        (os.isWindows && name.endsWith(".dll")) ||
                (os.isLinux && name.endsWith(".so")) ||
                (os.isMacOsX && (name.endsWith(".jnilib") || name.endsWith(".dylib")))
    }.forEach {
        val f = file("$buildDir/natives/${it.name}")
        if (!f.exists()) {
            f.parentFile.mkdirs()
            it.copyTo(f)
        }
    }
}

val workingDir = file("$projectDir/run")
workingDir.mkdirs()

application {
    mainClass.set("Start")

    val listArgs = mutableListOf("-Djava.library.path=${file("$buildDir/natives").absolutePath}")
    if (DefaultNativePlatform.getCurrentOperatingSystem().isMacOsX) {
        listArgs.add("-XstartOnFirstThread")
    }

    applicationDefaultJvmArgs = listArgs
    tasks.run.get().workingDir = workingDir
}

idea.project.settings {
    runConfigurations {
        register<Application>("Minecraft") {
            mainClass = "Start"
            moduleName = idea.module.name + ".main"
            workingDirectory = workingDir.absolutePath
            jvmArgs = application.applicationDefaultJvmArgs.joinToString(" ")
        }
    }
}

fun getCurrentOs(): String {
    val os: DefaultOperatingSystem = DefaultNativePlatform.getCurrentOperatingSystem()

    return if (os.isWindows) {
        "windows"
    } else if (os.isLinux) {
        "linux"
    } else if (os.isMacOsX) {
        "osx"
    } else {
        throw GradleException("Your OS does not seem to be supported!")
    }
}