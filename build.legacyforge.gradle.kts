import dev.kikugie.stonecutter.controller.task.StonecutterTaskMutexService

// Forge up to 1.20.1 runs on SRG names, so the release jar is the reobfuscated one.
plugins {
    id("net.neoforged.moddev.legacyforge") version "2.0.148"
}

val node = project.name
val target = RPTubesTarget(sc.current.version, "forge", { sc.current.parsed >= it }, { runCatching { sc.properties[it] }.getOrNull() })

rpTubes(target, mapOf(
    "loader_range" to target.prop("deps.forge_range"),
    "forge_range" to target.prop("deps.forge_range"),
))

repositories {
    mavenCentral()
    maven("https://maven.blamejared.com/") { name = "BlameJared" }
    maven("https://maven.covers1624.net/") { name = "covers1624" }
}

dependencies {
    if (target.hasJei) {
        val jei = target.prop("deps.jei")
        "modCompileOnly"("mezz.jei:jei-${sc.current.version}-common-api:$jei")
        "modCompileOnly"("mezz.jei:jei-${sc.current.version}-forge-api:$jei")
        "modRuntimeOnly"("mezz.jei:jei-${sc.current.version}-forge:$jei")
    }
    if (target.hasCbm) {
        for (dep in listOf(target.prop("deps.cbm"), target.prop("deps.ccl"))) {
            "modCompileOnly"(dep) { isTransitive = false }
            if (providers.gradleProperty("rptubes.cbmRuntime").isPresent) {
                "modRuntimeOnly"(dep) { isTransitive = false }
            }
        }
    }
}

legacyForge {
    version = "${sc.current.version}-${target.prop("deps.forge")}"

    mods {
        register(target.modId) {
            sourceSet(sourceSets.main.get())
        }
    }

    runs {
        configureEach {
            gameDirectory = rootProject.file("run/$node")
            jvmArgument("-Xmx3G")
        }
        register("client") {
            client()
        }
        register("server") {
            server()
            programArgument("--nogui")
        }
    }
}

tasks.named("createMinecraftArtifacts") {
    dependsOn("stonecutterGenerate")
    usesService(StonecutterTaskMutexService.create(gradle, name))
}

collectJar(target, tasks.named("reobfJar").map { it.outputs.files.singleFile })
