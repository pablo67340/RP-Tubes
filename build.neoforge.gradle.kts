import dev.kikugie.stonecutter.controller.task.StonecutterTaskMutexService

plugins {
    id("net.neoforged.moddev") version "2.0.148"
}

val node = project.name
val target = RPTubesTarget(sc.current.version, "neoforge", { sc.current.parsed >= it }, { runCatching { sc.properties[it] }.getOrNull() })

rpTubes(target, mapOf(
    "loader_range" to target.prop("deps.fml_range"),
    "neoforge_range" to target.prop("deps.neoforge_range"),
))

repositories {
    mavenCentral()
    maven("https://maven.blamejared.com/") { name = "BlameJared" }
}

dependencies {
    if (target.hasJei) {
        val jei = target.prop("deps.jei")
        compileOnly("mezz.jei:jei-${sc.current.version}-common-api:$jei")
        compileOnly("mezz.jei:jei-${sc.current.version}-neoforge-api:$jei")
        runtimeOnly("mezz.jei:jei-${sc.current.version}-neoforge:$jei")
    }
}

neoForge {
    version = target.prop("deps.neoforge")

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

collectJar(target, tasks.jar.flatMap { it.archiveFile })
