import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import org.gradle.internal.hash.Hashing
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform
import org.gradle.nativeplatform.platform.internal.DefaultOperatingSystem
import org.jetbrains.gradle.ext.Application
import org.jetbrains.gradle.ext.runConfigurations
import org.jetbrains.gradle.ext.settings
import java.io.FileWriter
import java.net.URL
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


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
configurations.implementation.get().isCanBeResolved = true
configurations.runtimeOnly.get().isCanBeResolved = true

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
        attributes(
            mapOf(
                "Main-Class" to "net.minecraft.client.main.Main",
                "Client-Version" to version
            )
        )
    }

    from({
        configurations.implementation.get().filter { it.name.equals("1.8.9.jar", true) }.map { zipTree(it) }
    })

    dependsOn("generateJson")
}

//minecraft dependencies
val lwjglVersion =
    if (DefaultNativePlatform.getCurrentOperatingSystem().isMacOsX) "2.9.2-nightly-20140822" else "2.9.4-nightly-20150209"
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
    "org.lwjgl.lwjgl:lwjgl:$lwjglVersion",
    "org.lwjgl.lwjgl:lwjgl_util:$lwjglVersion",
    "org.lwjgl.lwjgl:lwjgl-platform:$lwjglVersion",
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


tasks.register("generateJson") {
    val gson = GsonBuilder()
        .setPrettyPrinting()
        .create()

    val jsonObject = gson.fromJson(
        """
        {
          "assetIndex": {
            "id": "1.8",
            "sha1": "f6ad102bcaa53b1a58358f16e376d548d44933ec",
            "size": 78494,
            "totalSize": 114885064,
            "url": "https://launchermeta.mojang.com/v1/packages/f6ad102bcaa53b1a58358f16e376d548d44933ec/1.8.json"
          },
          "assets": "1.8",
          "complianceLevel": 0,
          "downloads": {},
          "javaVersion": {
            "component": "jre-legacy",
            "majorVersion": 8
          },
          "libraries": [
            {
              "downloads": {
                "artifact": {
                  "path": "org/lwjgl/lwjgl/lwjgl/2.9.4-nightly-20150209/lwjgl-2.9.4-nightly-20150209.jar",
                  "sha1": "697517568c68e78ae0b4544145af031c81082dfe",
                  "size": 1047168,
                  "url": "https://libraries.minecraft.net/org/lwjgl/lwjgl/lwjgl/2.9.4-nightly-20150209/lwjgl-2.9.4-nightly-20150209.jar"
                }
              },
              "name": "org.lwjgl.lwjgl:lwjgl:2.9.4-nightly-20150209",
              "rules": [
                {
                  "action": "allow"
                },
                {
                  "action": "disallow",
                  "os": {
                    "name": "osx"
                  }
                }
              ]
            },
            {
              "downloads": {
                "artifact": {
                  "path": "org/lwjgl/lwjgl/lwjgl_util/2.9.4-nightly-20150209/lwjgl_util-2.9.4-nightly-20150209.jar",
                  "sha1": "d51a7c040a721d13efdfbd34f8b257b2df882ad0",
                  "size": 173887,
                  "url": "https://libraries.minecraft.net/org/lwjgl/lwjgl/lwjgl_util/2.9.4-nightly-20150209/lwjgl_util-2.9.4-nightly-20150209.jar"
                }
              },
              "name": "org.lwjgl.lwjgl:lwjgl_util:2.9.4-nightly-20150209",
              "rules": [
                {
                  "action": "allow"
                },
                {
                  "action": "disallow",
                  "os": {
                    "name": "osx"
                  }
                }
              ]
            },
            {
              "downloads": {
                "artifact": {
                  "path": "org/lwjgl/lwjgl/lwjgl-platform/2.9.4-nightly-20150209/lwjgl-platform-2.9.4-nightly-20150209.jar",
                  "sha1": "b04f3ee8f5e43fa3b162981b50bb72fe1acabb33",
                  "size": 22,
                  "url": "https://libraries.minecraft.net/org/lwjgl/lwjgl/lwjgl-platform/2.9.4-nightly-20150209/lwjgl-platform-2.9.4-nightly-20150209.jar"
                },
                "classifiers": {
                  "natives-linux": {
                    "path": "org/lwjgl/lwjgl/lwjgl-platform/2.9.4-nightly-20150209/lwjgl-platform-2.9.4-nightly-20150209-natives-linux.jar",
                    "sha1": "931074f46c795d2f7b30ed6395df5715cfd7675b",
                    "size": 578680,
                    "url": "https://libraries.minecraft.net/org/lwjgl/lwjgl/lwjgl-platform/2.9.4-nightly-20150209/lwjgl-platform-2.9.4-nightly-20150209-natives-linux.jar"
                  },
                  "natives-osx": {
                    "path": "org/lwjgl/lwjgl/lwjgl-platform/2.9.4-nightly-20150209/lwjgl-platform-2.9.4-nightly-20150209-natives-osx.jar",
                    "sha1": "bcab850f8f487c3f4c4dbabde778bb82bd1a40ed",
                    "size": 426822,
                    "url": "https://libraries.minecraft.net/org/lwjgl/lwjgl/lwjgl-platform/2.9.4-nightly-20150209/lwjgl-platform-2.9.4-nightly-20150209-natives-osx.jar"
                  },
                  "natives-windows": {
                    "path": "org/lwjgl/lwjgl/lwjgl-platform/2.9.4-nightly-20150209/lwjgl-platform-2.9.4-nightly-20150209-natives-windows.jar",
                    "sha1": "b84d5102b9dbfabfeb5e43c7e2828d98a7fc80e0",
                    "size": 613748,
                    "url": "https://libraries.minecraft.net/org/lwjgl/lwjgl/lwjgl-platform/2.9.4-nightly-20150209/lwjgl-platform-2.9.4-nightly-20150209-natives-windows.jar"
                  }
                }
              },
              "extract": {
                "exclude": [
                  "META-INF/"
                ]
              },
              "name": "org.lwjgl.lwjgl:lwjgl-platform:2.9.4-nightly-20150209",
              "natives": {
                "linux": "natives-linux",
                "osx": "natives-osx",
                "windows": "natives-windows"
              },
              "rules": [
                {
                  "action": "allow"
                },
                {
                  "action": "disallow",
                  "os": {
                    "name": "osx"
                  }
                }
              ]
            },
            {
              "downloads": {
                "artifact": {
                  "path": "org/lwjgl/lwjgl/lwjgl/2.9.2-nightly-20140822/lwjgl-2.9.2-nightly-20140822.jar",
                  "sha1": "7707204c9ffa5d91662de95f0a224e2f721b22af",
                  "size": 1045632,
                  "url": "https://libraries.minecraft.net/org/lwjgl/lwjgl/lwjgl/2.9.2-nightly-20140822/lwjgl-2.9.2-nightly-20140822.jar"
                }
              },
              "name": "org.lwjgl.lwjgl:lwjgl:2.9.2-nightly-20140822",
              "rules": [
                {
                  "action": "allow",
                  "os": {
                    "name": "osx"
                  }
                }
              ]
            },
            {
              "downloads": {
                "artifact": {
                  "path": "org/lwjgl/lwjgl/lwjgl_util/2.9.2-nightly-20140822/lwjgl_util-2.9.2-nightly-20140822.jar",
                  "sha1": "f0e612c840a7639c1f77f68d72a28dae2f0c8490",
                  "size": 173887,
                  "url": "https://libraries.minecraft.net/org/lwjgl/lwjgl/lwjgl_util/2.9.2-nightly-20140822/lwjgl_util-2.9.2-nightly-20140822.jar"
                }
              },
              "name": "org.lwjgl.lwjgl:lwjgl_util:2.9.2-nightly-20140822",
              "rules": [
                {
                  "action": "allow",
                  "os": {
                    "name": "osx"
                  }
                }
              ]
            },
            {
              "downloads": {
                "classifiers": {
                  "natives-linux": {
                    "path": "org/lwjgl/lwjgl/lwjgl-platform/2.9.2-nightly-20140822/lwjgl-platform-2.9.2-nightly-20140822-natives-linux.jar",
                    "sha1": "d898a33b5d0a6ef3fed3a4ead506566dce6720a5",
                    "size": 578539,
                    "url": "https://libraries.minecraft.net/org/lwjgl/lwjgl/lwjgl-platform/2.9.2-nightly-20140822/lwjgl-platform-2.9.2-nightly-20140822-natives-linux.jar"
                  },
                  "natives-osx": {
                    "path": "org/lwjgl/lwjgl/lwjgl-platform/2.9.2-nightly-20140822/lwjgl-platform-2.9.2-nightly-20140822-natives-osx.jar",
                    "sha1": "79f5ce2fea02e77fe47a3c745219167a542121d7",
                    "size": 468116,
                    "url": "https://libraries.minecraft.net/org/lwjgl/lwjgl/lwjgl-platform/2.9.2-nightly-20140822/lwjgl-platform-2.9.2-nightly-20140822-natives-osx.jar"
                  },
                  "natives-windows": {
                    "path": "org/lwjgl/lwjgl/lwjgl-platform/2.9.2-nightly-20140822/lwjgl-platform-2.9.2-nightly-20140822-natives-windows.jar",
                    "sha1": "78b2a55ce4dc29c6b3ec4df8ca165eba05f9b341",
                    "size": 613680,
                    "url": "https://libraries.minecraft.net/org/lwjgl/lwjgl/lwjgl-platform/2.9.2-nightly-20140822/lwjgl-platform-2.9.2-nightly-20140822-natives-windows.jar"
                  }
                }
              },
              "extract": {
                "exclude": [
                  "META-INF/"
                ]
              },
              "name": "org.lwjgl.lwjgl:lwjgl-platform:2.9.2-nightly-20140822",
              "natives": {
                "linux": "natives-linux",
                "osx": "natives-osx",
                "windows": "natives-windows"
              },
              "rules": [
                {
                  "action": "allow",
                  "os": {
                    "name": "osx"
                  }
                }
              ]
            },
            {
              "downloads": {
                "classifiers": {
                  "natives-linux": {
                    "path": "net/java/jinput/jinput-platform/2.0.5/jinput-platform-2.0.5-natives-linux.jar",
                    "sha1": "7ff832a6eb9ab6a767f1ade2b548092d0fa64795",
                    "size": 10362,
                    "url": "https://libraries.minecraft.net/net/java/jinput/jinput-platform/2.0.5/jinput-platform-2.0.5-natives-linux.jar"
                  },
                  "natives-osx": {
                    "path": "net/java/jinput/jinput-platform/2.0.5/jinput-platform-2.0.5-natives-osx.jar",
                    "sha1": "53f9c919f34d2ca9de8c51fc4e1e8282029a9232",
                    "size": 12186,
                    "url": "https://libraries.minecraft.net/net/java/jinput/jinput-platform/2.0.5/jinput-platform-2.0.5-natives-osx.jar"
                  },
                  "natives-windows": {
                    "path": "net/java/jinput/jinput-platform/2.0.5/jinput-platform-2.0.5-natives-windows.jar",
                    "sha1": "385ee093e01f587f30ee1c8a2ee7d408fd732e16",
                    "size": 155179,
                    "url": "https://libraries.minecraft.net/net/java/jinput/jinput-platform/2.0.5/jinput-platform-2.0.5-natives-windows.jar"
                  }
                }
              },
              "extract": {
                "exclude": [
                  "META-INF/"
                ]
              },
              "name": "net.java.jinput:jinput-platform:2.0.5",
              "natives": {
                "linux": "natives-linux",
                "osx": "natives-osx",
                "windows": "natives-windows"
              }
            },
            {
              "downloads": {
                "classifiers": {
                  "natives-osx": {
                    "path": "tv/twitch/twitch-platform/6.5/twitch-platform-6.5-natives-osx.jar",
                    "sha1": "5f9d1ee26257b3a33f0ca06fed335ef462af659f",
                    "size": 455359,
                    "url": "https://libraries.minecraft.net/tv/twitch/twitch-platform/6.5/twitch-platform-6.5-natives-osx.jar"
                  },
                  "natives-windows-32": {
                    "path": "tv/twitch/twitch-platform/6.5/twitch-platform-6.5-natives-windows-32.jar",
                    "sha1": "206c4ccaecdbcfd2a1631150c69a97bbc9c20c11",
                    "size": 474225,
                    "url": "https://libraries.minecraft.net/tv/twitch/twitch-platform/6.5/twitch-platform-6.5-natives-windows-32.jar"
                  },
                  "natives-windows-64": {
                    "path": "tv/twitch/twitch-platform/6.5/twitch-platform-6.5-natives-windows-64.jar",
                    "sha1": "9fdd0fd5aed0817063dcf95b69349a171f447ebd",
                    "size": 580098,
                    "url": "https://libraries.minecraft.net/tv/twitch/twitch-platform/6.5/twitch-platform-6.5-natives-windows-64.jar"
                  }
                }
              },
              "extract": {
                "exclude": [
                  "META-INF/"
                ]
              },
              "name": "tv.twitch:twitch-platform:6.5",
              "natives": {
                "linux": "natives-linux",
                "osx": "natives-osx",
                "windows": "natives-windows-${'$'}{arch}"
              },
              "rules": [
                {
                  "action": "allow"
                },
                {
                  "action": "disallow",
                  "os": {
                    "name": "linux"
                  }
                }
              ]
            },
            {
              "downloads": {
                "classifiers": {
                  "natives-windows-32": {
                    "path": "tv/twitch/twitch-external-platform/4.5/twitch-external-platform-4.5-natives-windows-32.jar",
                    "sha1": "18215140f010c05b9f86ef6f0f8871954d2ccebf",
                    "size": 5654047,
                    "url": "https://libraries.minecraft.net/tv/twitch/twitch-external-platform/4.5/twitch-external-platform-4.5-natives-windows-32.jar"
                  },
                  "natives-windows-64": {
                    "path": "tv/twitch/twitch-external-platform/4.5/twitch-external-platform-4.5-natives-windows-64.jar",
                    "sha1": "c3cde57891b935d41b6680a9c5e1502eeab76d86",
                    "size": 7457619,
                    "url": "https://libraries.minecraft.net/tv/twitch/twitch-external-platform/4.5/twitch-external-platform-4.5-natives-windows-64.jar"
                  }
                }
              },
              "extract": {
                "exclude": [
                  "META-INF/"
                ]
              },
              "name": "tv.twitch:twitch-external-platform:4.5",
              "natives": {
                "windows": "natives-windows-${'$'}{arch}"
              },
              "rules": [
                {
                  "action": "allow",
                  "os": {
                    "name": "windows"
                  }
                }
              ]
            }
          ],
          "logging": {
            "client": {
              "argument": "-Dlog4j.configurationFile=${'$'}{path}",
              "file": {
                "id": "client-1.7.xml",
                "sha1": "50c9cc4af6d853d9fc137c84bcd153e2bd3a9a82",
                "size": 966,
                "url": "https://launcher.mojang.com/v1/objects/50c9cc4af6d853d9fc137c84bcd153e2bd3a9a82/client-1.7.xml"
              },
              "type": "log4j2-xml"
            }
          },
          "mainClass": "net.minecraft.client.main.Main",
          "minecraftArguments": "--username ${'$'}{auth_player_name} --version ${'$'}{version_name} --gameDir ${'$'}{game_directory} --assetsDir ${'$'}{assets_root} --assetIndex ${'$'}{assets_index_name} --uuid ${'$'}{auth_uuid} --accessToken ${'$'}{auth_access_token} --userProperties ${'$'}{user_properties} --userType ${'$'}{user_type}",
          "minimumLauncherVersion": 14,
          "type": "release"
        }
    """.trimIndent(), JsonObject::class.java
    )

    //from stackoverflow
    val dependenciesURLs = configurations.implementation.get().dependencies.asSequence().mapNotNull {
        it.run { it } to repositories.mapNotNull { repo ->
            (repo as? UrlArtifactRepository)?.url
        }.flatMap { repoUrl ->
            "%s/%s/%s/%s/%s-%s".format(
                repoUrl.toString().trimEnd('/'),
                it.group?.replace('.', '/') ?: "", it.name, it.version,
                it.name, it.version
            ).let { x -> listOf("$x.jar", "$x.aar") }
        }.firstNotNullOfOrNull { url ->
            runCatching {
                val conn = URL(url).openConnection()
                conn.getInputStream() ?: throw Exception()
                conn.url
            }.getOrNull()
        }
    }

    jsonObject.addProperty("id", "${project.name}-$version")
    jsonObject.addProperty("time", DateTimeFormatter.ISO_INSTANT.format(Instant.now().truncatedTo(ChronoUnit.SECONDS)))
    jsonObject.add("releaseTime", jsonObject.get("time"))

    dependenciesURLs.forEach { (dependency: Dependency, url: URL?) ->
        url ?: return@forEach

        val file = configurations.implementation.get().files(dependency)
            .firstOrNull { it.name.equals("${dependency.name}-${dependency.version}.jar", true) }

        file ?: throw GradleException("Failed to find file: ${dependency.name}-${dependency.version}.jar")

        val libraryObject = gson.toJsonTree(
            mapOf(
                "name" to "${dependency.group}:${dependency.name}:${dependency.version}",
                "downloads" to mapOf(
                    "artifact" to mapOf(
                        "url" to url.toString(),
                        "sha1" to Hashing.sha1().hashFile(file).toString(),
                        "size" to file.length()
                    )
                )
            )
        )

        jsonObject.getAsJsonArray("libraries").add(libraryObject)
    }

    val file = file("$buildDir/libs/${project.name}-$version.json")
    file.parentFile.mkdirs()

    gson.toJson(jsonObject, FileWriter(file))
    FileWriter(file).use { gson.toJson(jsonObject, it) }
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