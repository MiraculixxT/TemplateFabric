import dex.plugins.outlet.v2.util.ReleaseType

plugins {
    kotlin("jvm") version "1.9.+"
    kotlin("plugin.serialization") version "1.9.+"
    id("fabric-loom") version "1.4-SNAPSHOT"
    id("com.modrinth.minotaur") version "2.+"
    id("io.github.dexman545.outlet") version "1.6.1"
}

group = properties["group"] as String
version = properties["version"] as String
description = properties["description"] as String

val gameVersion by properties
val projectName = properties["name"] as String

repositories {
    mavenCentral()
    maven("https://maven.shedaniel.me/")
    maven("https://maven.terraformersmc.com/releases/")
}

dependencies {
    outlet.mcVersionRange = properties["supportedVersions"] as String

    // Fabric configuration
    minecraft("com.mojang:minecraft:$gameVersion")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${outlet.loaderVersion()}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${outlet.fapiVersion()}")

    // Kotlin libraries
    val flkVersion = outlet.latestModrinthModVersion("fabric-language-kotlin", outlet.mcVersions())
    modImplementation("net.fabricmc:fabric-language-kotlin:$flkVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.+")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.+")

    // Ingame configuration (optional)
    modApi("com.terraformersmc:modmenu:9.+")
    modApi("me.shedaniel.cloth:cloth-config-fabric:13.+") {
        exclude("net.fabricmc.fabric-api")
    }

    // Silk configuration (optional)
    val silkVersion = outlet.latestModrinthModVersion("silk", outlet.mcVersions())
    modImplementation("net.silkmc:silk-core:$silkVersion")
    modImplementation("net.silkmc:silk-commands:$silkVersion") // easy command registration
    modImplementation("net.silkmc:silk-nbt:$silkVersion") // item simplification
}

tasks.processResources {
    filesMatching("fabric.mod.json") {
        val modrinthSlug = properties["modrinthProjectId"] as? String ?: properties["modid"] as String
        expand(
            mapOf(
                "modid" to properties["modid"] as String,
                "version" to version,
                "name" to projectName,
                "description" to description,
                "author" to properties["authors"] as String,
                "license" to properties["license"] as String,
                "modrinth" to modrinthSlug,
                "environment" to properties["environment"] as String,
            )
        )
    }
}

modrinth {
    token.set(properties["modrinthToken"] as String)
    projectId.set(properties["modrinthProjectId"] as? String ?: projectName)
    versionNumber.set(version as String)
    versionType.set("release") // Can also be `beta` or `alpha`
    uploadFile.set(tasks.jar)
    outlet.mcVersionRange = properties["supportedVersions"] as String
    outlet.allowedReleaseTypes = setOf(ReleaseType.RELEASE)
    gameVersions.addAll(outlet.mcVersions())
    loaders.addAll(buildList {
        add("fabric")
        add("quilt")
    })
    dependencies {
        // The scope can be `required`, `optional`, `incompatible`, or `embedded`
        // The type can either be `project` or `version`
//        required.project("fabric-api")
    }

    // Project sync
    syncBodyFrom = rootProject.file("README.md").readText()
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
        options.release.set(17)
    }
    compileKotlin {
        kotlinOptions.jvmTarget = "17"
    }
}