package com.rptubes.universal;

import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.forgespi.locating.IModLocator;

import java.lang.reflect.Constructor;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.jar.Manifest;

/**
 * Hands Forge the RP Tubes build for the running Minecraft version, on Forge 1.16.5 through 26.2
 * and on NeoForge 1.20.1, which is still Forge underneath.
 *
 * <p>Forge loads any jar in the mods folder that declares a mod locator before it looks for mods.
 * This one unpacks the matching build into a folder of its own and lets Forge's own mods folder
 * locator read that folder, so the build is found, checked and loaded exactly like a jar dropped
 * into the mods folder. That constructor has the same shape on every version from 1.16.5 to 26.2.
 *
 * <p>The locator interface lost {@code findPath} and {@code findManifest} in 1.17; the methods
 * the two versions share have the same signatures, so one class serves both.
 */
public final class ForgeLocator implements IModLocator {
    private static final String FOLDER_LOCATOR = "net.minecraftforge.fml.loading.moddiscovery.ModsFolderLocator";

    private Map<String, ?> arguments = Collections.emptyMap();
    private IModLocator delegate;
    private boolean resolved;

    private synchronized IModLocator delegate() {
        if (!resolved) {
            resolved = true;
            String mcVersion = Universal.fmlMcVersion("net.minecraftforge.fml.loading");
            if (mcVersion == null && arguments.get("mcVersion") instanceof String) {
                mcVersion = (String) arguments.get("mcVersion");
            }
            Path jar = Universal.extract("forge", mcVersion);
            if (jar != null) {
                try {
                    Constructor<?> constructor = Class.forName(FOLDER_LOCATOR).getDeclaredConstructor(Path.class, String.class);
                    constructor.setAccessible(true);
                    delegate = (IModLocator) constructor.newInstance(jar.getParent(), "rptubes universal");
                    delegate.initArguments(arguments);
                } catch (Throwable t) {
                    Log.error("RP Tubes could not hand its build to Forge", t);
                }
            }
        }
        return delegate;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public List scanMods() {
        IModLocator d = delegate();
        return d == null ? Collections.emptyList() : d.scanMods();
    }

    @Override
    public String name() {
        return "rptubes universal";
    }

    @Override
    public Path findPath(IModFile modFile, String... path) {
        return delegate().findPath(modFile, path);
    }

    @Override
    public void scanFile(IModFile modFile, Consumer<Path> pathConsumer) {
        delegate().scanFile(modFile, pathConsumer);
    }

    @Override
    public Optional<Manifest> findManifest(Path file) {
        return delegate().findManifest(file);
    }

    @Override
    public void initArguments(Map<String, ?> arguments) {
        this.arguments = arguments;
    }

    @Override
    public boolean isValid(IModFile modFile) {
        IModLocator d = delegate();
        return d != null && d.isValid(modFile);
    }

    @Override
    public String toString() {
        return name();
    }
}
