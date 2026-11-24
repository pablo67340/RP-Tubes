stonecutter active "1.21.1-neoforge"

stonecutter parameters {
    val (version, loader) = current.project.split('-', limit = 2)

    properties {
        tags(version, loader)
    }

    constants {
        match(loader, "fabric", "forge", "neoforge")
    }
    constants["forgelike"] = loader != "fabric"
    // Versions the JEI plugin is built for; deps.jei in stonecutter.properties.toml must agree.
    constants["jei"] = version in setOf("1.19.2", "1.20.1", "1.20.6", "1.21.1")
    // Versions CB Multipart was released for; deps.cbm in stonecutter.properties.toml must agree.
    constants["cbm"] = (loader == "forge" && current.parsed < "1.20.6") ||
        (loader == "neoforge" && (version == "1.21.1" || version == "1.21.11"))

    replacements {
        // Mojang renamed ResourceLocation in 1.21.11.
        string(current.parsed >= "1.21.11") {
            replace("ResourceLocation", "Identifier")
        }
    }
}
