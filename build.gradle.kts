import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.github.jengelman.gradle.plugins.shadow.transformers.Log4j2PluginsCacheFileTransformer
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.serialization") version "1.7.22"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "net.geekmc.turingcore"
version = "0.1.0-SNAPSHOT"
val outputName = "${project.name}-$version.jar"

repositories {
    maven(url = "https://jitpack.io")
    mavenCentral()
    mavenLocal()
}

dependencies {
    if (parent?.name == "swork-fire-workspace") {
        implementation(project(":kstom"))
    } else {
        // TODO: pin version
        implementation("com.github.Project-Cepi:KStom:${project.ext["version.KStom"]}")
    }

    // TODO: pin version
    compileOnly("com.github.Minestom:Minestom:-SNAPSHOT") {
        exclude(group = "org.tinylog")
    }

    // TODO: 把包名合并到workspace的build.gradle.kts中
    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:${project.ext["version.kotlinx-coroutines-core"]}")
    compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-core:${project.ext["version.kotlinx-serialization-core"]}")
    compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-json:${project.ext["version.kotlinx-serialization-json"]}")
    compileOnly("org.yaml:snakeyaml:${project.ext["version.snakeyaml"]}")
    compileOnly("net.kyori:adventure-text-minimessage:${project.ext["version.adventure-text-minimessage"]}")
    compileOnly("com.charleskorn.kaml:kaml:${project.ext["version.kaml"]}")
    compileOnly("org.kodein.di:kodein-di-jvm:${project.ext["version.kodein-di-jvm"]}")
    compileOnly("org.reflections:reflections:${project.ext["version.reflections"]}")
}

@Suppress("UnstableApiUsage")
tasks.withType<ProcessResources> {
    filesMatching("extension.json") {
        filter {

            val extensionVersionPlaceholder = "@!extensionVersion@"
            val dependencyVersionPlaceholder = "@!dependencyVersion@"

            // 替换拓展版本。
            var ret = it
            if (extensionVersionPlaceholder in ret) {
                ret = ret.replace(extensionVersionPlaceholder, version as String)
            }

            // 替换Maven依赖版本。
            @Suppress("UNCHECKED_CAST")
            val dependencyToVersionMap = project.ext["version.dependencyToVersionMap"] as Map<String, String>

            if ("@!dependencyVersion@" in ret) {
                var replaced = false
                for ((dependency, version) in dependencyToVersionMap) {
                    if (dependency in ret) {
                        ret = ret.replace(dependencyVersionPlaceholder, version)
                        replaced = true
                        break
                    }
                }
                if (!replaced) {
                    throw IllegalStateException("Dependency not found in dependencyToVersionMap: $ret")
                }
            }
            return@filter ret
        }
    }
}

tasks.withType<ShadowJar> {
    transform(Log4j2PluginsCacheFileTransformer::class.java)
    archiveFileName.set(outputName)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

tasks.withType<Jar> {
    archiveFileName.set(outputName)
}