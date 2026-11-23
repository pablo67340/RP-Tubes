package com.rptubes.universal;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.List;

/**
 * Puts the RP Tubes build for Forge 1.7.10 or 1.12.2 on the game's class path. FML runs the
 * tweaker named in a mods folder jar's manifest while the game starts, and later loads every mod
 * it finds on that class path, so the build loads as if it were in the mods folder.
 */
public final class LegacyTweaker implements ITweaker {
    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        Path jar = Universal.extract("legacy", mcVersion(classLoader));
        if (jar == null) {
            return;
        }
        try {
            classLoader.addURL(jar.toUri().toURL());
        } catch (Throwable t) {
            Log.error("RP Tubes could not add its build to the class path", t);
        }
    }

    /** {@code FMLInjectionData.mccversion}, under {@code cpw.mods.fml} on 1.7.10 and {@code net.minecraftforge.fml} later. */
    private static String mcVersion(ClassLoader classLoader) {
        for (String name : new String[]{"net.minecraftforge.fml.relauncher.FMLInjectionData", "cpw.mods.fml.relauncher.FMLInjectionData"}) {
            try {
                Field field = Class.forName(name, false, classLoader).getDeclaredField("mccversion");
                field.setAccessible(true);
                Object value = field.get(null);
                if (value instanceof String) {
                    return (String) value;
                }
            } catch (Throwable ignored) {
                // Not this generation of FML
            }
        }
        return null;
    }

    @Override
    public String getLaunchTarget() {
        return null;
    }

    @Override
    public String[] getLaunchArguments() {
        return new String[0];
    }
}
