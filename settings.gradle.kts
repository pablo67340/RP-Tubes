pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/") { name = "FabricMC" }
        maven("https://maven.neoforged.net/releases/") { name = "NeoForged" }
        maven("https://maven.minecraftforge.net/") { name = "MinecraftForge" }
        maven("https://maven.kikugie.dev/releases") { name = "KikuGie Releases" }
        maven("https://maven.kikugie.dev/snapshots") { name = "KikuGie Snapshots" }
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.10"
    id("dev.kikugie.loom-back-compat") version "0.4.3"
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

// Minecraft ships unobfuscated from 26.1, which Loom builds without remapping.
loomx {
    unobfuscated { it.name.startsWith("26.") }
}

stonecutter {
    create(rootProject) {
        /**
         * One node per Minecraft version and loader, in `versions/{version}-{loader}`.
         * Forge uses ModDevGradle's legacy plugin up to 1.20.1, where Forge still ran on
         * SRG names and jars must be reobfuscated, and ForgeGradle 7 from 1.20.6 on.
         * The 1.20.1 Forge jar also runs on NeoForge 1.20.1, so that pair has no node of its own.
         */
        fun node(version: String, vararg loaders: String) {
            for (loader in loaders) {
                val script = when {
                    loader == "forge" && legacyForge(version) -> "build.legacyforge.gradle.kts"
                    else -> "build.$loader.gradle.kts"
                }
                version("$version-$loader", version).buildscript(script)
            }
        }

        node("1.19.2", "forge")
        node("1.20.1", "fabric", "forge")
        node("1.20.6", "fabric", "forge", "neoforge")
        node("1.21.1", "fabric", "forge", "neoforge")
        node("1.21.11", "fabric", "forge", "neoforge")
        node("26.1.2", "fabric", "forge", "neoforge")
        node("26.2", "fabric", "forge", "neoforge")
        vcsVersion = "1.21.1-neoforge"
    }
}

fun legacyForge(version: String): Boolean {
    val parts = version.split('.').map { it.toInt() }
    val minor = parts.getOrElse(1) { 0 }
    val patch = parts.getOrElse(2) { 0 }
    return parts[0] == 1 && (minor < 20 || (minor == 20 && patch <= 1))
}

rootProject.name = "rptubes"
