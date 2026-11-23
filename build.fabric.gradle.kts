plugins {
    // Applies the Loom variant that matches the Minecraft version.
    id("dev.kikugie.loom-back-compat")
}

val node = project.name
val target = RPTubesTarget(sc.current.version, "fabric", { sc.current.parsed >= it }, { runCatching { sc.properties[it] }.getOrNull() })
// Unobfuscated versions have no remapping, so mods go on the plain configurations.
val unobfuscated = loomx.isUnobfuscated
val modImplementation = if (unobfuscated) "implementation" else "modImplementation"
val modCompileOnly = if (unobfuscated) "compileOnly" else "modCompileOnly"
val modLocalRuntime = if (unobfuscated) "localRuntime" else "modLocalRuntime"

rpTubes(target, mapOf(
    "loader_range" to target.prop("deps.fabric_loader"),
))

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/") { name = "FabricMC" }
    maven("https://maven.blamejared.com/") { name = "BlameJared" }
}

dependencies {
    minecraft("com.mojang:minecraft:${sc.current.version}")
    loomx.applyMojangMappings()

    modImplementation("net.fabricmc:fabric-loader:${target.prop("deps.fabric_loader")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${target.prop("deps.fabric_api")}")

    target.propOrNull("deps.energy")?.let { energy ->
        modCompileOnly("teamreborn:energy:$energy")
        modLocalRuntime("teamreborn:energy:$energy")
    }
    if (target.hasJei) {
        val jei = target.prop("deps.jei")
        modCompileOnly("mezz.jei:jei-${sc.current.version}-fabric-api:$jei")
        modLocalRuntime("mezz.jei:jei-${sc.current.version}-fabric:$jei")
    }
}

loom {
    fabricModJsonPath = rootProject.file("src/overlays/fabric/fabric.mod.json")
    if (target.minecraft == "1.21.11") {
        accessWidenerPath = rootProject.file("src/overlays/fabric/rptubes.accesswidener")
    }

    runConfigs.all {
        runDirectory = rootProject.file("run/$node")
        vmArgs("-Xmx3G")
    }
}

collectJar(target, loomx.modJar.flatMap { it.archiveFile })
