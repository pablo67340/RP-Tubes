package com.rptubes.universal;

import net.neoforged.neoforgespi.ILaunchContext;
import net.neoforged.neoforgespi.locating.IDiscoveryPipeline;
import net.neoforged.neoforgespi.locating.IModFileCandidateLocator;

import java.nio.file.Path;

/**
 * Hands NeoForge the RP Tubes build for the running Minecraft version, on NeoForge 20.6 through
 * 26.2. NeoForge asks every mod file locator in the mods folder for candidates while it discovers
 * mods; this one unpacks the matching build into a folder of its own and passes that folder to
 * NeoForge's own folder locator, so the build is read like a jar in the mods folder.
 */
public final class NeoForgeLocator implements IModFileCandidateLocator {
    private boolean ran;

    @Override
    public void findCandidates(ILaunchContext context, IDiscoveryPipeline pipeline) {
        synchronized (this) {
            if (ran) {
                return;
            }
            ran = true;
        }
        Path jar = Universal.extract("neoforge", Universal.fmlMcVersion("net.neoforged.fml.loading"));
        if (jar != null) {
            IModFileCandidateLocator.forFolder(jar.getParent().toFile(), "rptubes universal").findCandidates(context, pipeline);
        }
    }

    @Override
    public String toString() {
        return "rptubes universal";
    }
}
