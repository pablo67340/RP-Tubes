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

    replacements {
        // Mojang renamed ResourceLocation in 1.21.11.
        string(current.parsed >= "1.21.11") {
            replace("ResourceLocation", "Identifier")
        }
    }
}
