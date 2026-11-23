// Forge from 1.20.2 on runs on Mojang names, so the compiled jar ships as is.
plugins {
    java
    id("net.minecraftforge.gradle") version "[7.0.3,8)"
}

val node = project.name
val target = RPTubesTarget(sc.current.version, "forge", { sc.current.parsed >= it }, { runCatching { sc.properties[it] }.getOrNull() })
// Forge 61 moved to EventBus 7, which checks listeners at compile time and boots dev runs through bootstrap-dev.
val eventBus7 = target.atLeast("1.21.11")

rpTubes(target, mapOf(
    "loader_range" to target.prop("deps.forge_range"),
    "forge_range" to target.prop("deps.forge_range"),
))

minecraft {
    mappings("official", sc.current.version)

    runs {
        configureEach {
            workingDir.set(rootProject.layout.projectDirectory.dir("run/$node"))
            jvmArgs("-Xmx3G")
            mods {
                register(target.modId) {
                    source(sourceSets.main.get())
                }
            }
            if (eventBus7) {
                val main = sourceSets.main.get()
                environment("MOD_CLASSES", "${target.modId}%%${main.output.classesDirs.asPath};${target.modId}%%${main.output.resourcesDir}")
            }
        }
        register("client")
        register("server") {
            args("--nogui")
        }
    }
}

repositories {
    minecraft.mavenizer(this)
    maven(fg.forgeMaven)
    maven(fg.minecraftLibsMaven)
    mavenCentral()
    maven("https://maven.blamejared.com/") { name = "BlameJared" }
}

dependencies {
    implementation(minecraft.dependency("net.minecraftforge:forge:${sc.current.version}-${target.prop("deps.forge")}"))
    if (eventBus7) {
        annotationProcessor("net.minecraftforge:eventbus-validator:7.0.5")
        runtimeOnly("net.minecraftforge:bootstrap-dev:2.2.1")
    }
    if (target.hasJei) {
        val jei = target.prop("deps.jei")
        compileOnly("mezz.jei:jei-${sc.current.version}-common-api:$jei")
        compileOnly("mezz.jei:jei-${sc.current.version}-forge-api:$jei")
        runtimeOnly("mezz.jei:jei-${sc.current.version}-forge:$jei")
    }
}

collectJar(target, tasks.jar.flatMap { it.archiveFile })
