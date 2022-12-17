import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.transformers.Log4j2PluginsCacheFileTransformer

plugins {
    kotlin("jvm") version "1.7.22"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "net.geekmc.turingcore"
version = "0.1.0-SNAPSHOT"

repositories {
    maven(url = "https://jitpack.io")
    mavenCentral()
    mavenLocal()
}

dependencies {
    // TODO: pin version
    compileOnly("com.github.Minestom:Minestom:-SNAPSHOT") {
        exclude(group = "org.tinylog")
    }

    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    compileOnly("net.kyori:adventure-text-minimessage:4.12.0")

    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-core:1.4.1")
    compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")

    compileOnly("com.github.Project-Cepi:KStom:f02e4c21d4")

    compileOnly("org.yaml:snakeyaml:1.33")
}

// Process some props in extension.json
@Suppress("UnstableApiUsage")
tasks.withType<ProcessResources> {
    filter {
        return@filter if ("!@!version!@!" in it) {
            it.replace("!@!version!@!", version as String)
        } else it
    }
}

tasks.withType<ShadowJar> {
    transform(Log4j2PluginsCacheFileTransformer::class.java)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}