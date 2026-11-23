package com.rptubes.universal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Picks the RP Tubes build that matches the running loader and Minecraft version out of this jar,
 * and copies it to a cache folder of its own so the loader can read it like any jar in the mods
 * folder. The builds and the Minecraft range each supports are listed in {@code index.txt}, which
 * the build writes alongside them.
 *
 * <p>Fabric needs none of this: it reads the nested Fabric builds itself and keeps the one whose
 * Minecraft dependency is met.
 */
public final class Universal {
    static final String INDEX = "/META-INF/rptubes/index.txt";

    private Universal() {
    }

    /** The extracted build for this loader and Minecraft version, or {@code null} when there is none. */
    static Path extract(String loader, String mcVersion) {
        if (mcVersion == null) {
            Log.error("RP Tubes could not tell which Minecraft version is running, so it was not loaded");
            return null;
        }
        try {
            List<Build> builds = index();
            for (Build build : builds) {
                if (build.loader.equals(loader) && build.supports(mcVersion)) {
                    Path jar = copy(build);
                    Log.info("RP Tubes: loading the " + build.file + " build for Minecraft " + mcVersion + " from " + jar);
                    return jar;
                }
            }
            Log.error("RP Tubes has no build for Minecraft " + mcVersion + " on " + loader + "; it was not loaded");
        } catch (IOException | RuntimeException e) {
            Log.error("RP Tubes could not unpack its build for Minecraft " + mcVersion + " on " + loader, e);
        }
        return null;
    }

    private static List<Build> index() throws IOException {
        List<Build> builds = new ArrayList<Build>();
        InputStream in = Universal.class.getResourceAsStream(INDEX);
        if (in == null) {
            throw new IOException(INDEX + " is missing from the RP Tubes jar");
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    builds.add(Build.parse(line));
                }
            }
        }
        return builds;
    }

    /**
     * Copies the build into its own folder under the system temp folder, named after its checksum,
     * so a folder only ever holds one jar and an unchanged build is not copied again. The copy is
     * written under a temporary name and moved into place, so a second instance starting at the same
     * time never reads a half-written jar.
     */
    private static Path copy(Build build) throws IOException {
        Path dir = Paths.get(System.getProperty("java.io.tmpdir"), "rptubes-universal", build.checksum);
        Path target = dir.resolve(build.file);
        if (Files.isRegularFile(target) && Files.size(target) == build.size) {
            return target;
        }
        Files.createDirectories(dir);
        Path partial = Files.createTempFile(dir, build.file, ".part");
        try (InputStream in = Universal.class.getResourceAsStream("/META-INF/rptubes/" + build.file);
             OutputStream out = Files.newOutputStream(partial)) {
            if (in == null) {
                throw new IOException(build.file + " is missing from the RP Tubes jar");
            }
            byte[] buffer = new byte[65536];
            int read;
            while ((read = in.read(buffer)) > 0) {
                out.write(buffer, 0, read);
            }
        }
        try {
            Files.move(partial, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            // Another instance got there first and has the jar open
            Files.deleteIfExists(partial);
            if (!Files.isRegularFile(target) || Files.size(target) != build.size) {
                throw e;
            }
        }
        return target;
    }

    /**
     * FML's Minecraft version: {@code FMLLoader.versionInfo()} from Forge 1.17 to NeoForge 21.8,
     * {@code FMLLoader.getCurrent().getVersionInfo()} from FML 10, a static field before 1.17, and
     * the {@code --fml.mcVersion} launch argument when none of those is set yet.
     */
    static String fmlMcVersion(String loadingPackage) {
        try {
            Class<?> loader = Class.forName(loadingPackage + ".FMLLoader");
            Object value = null;
            try {
                value = property(loader.getMethod("versionInfo").invoke(null));
            } catch (NoSuchMethodException e) {
                try {
                    Object current = loader.getMethod("getCurrentOrNull").invoke(null);
                    value = current == null ? null : property(current.getClass().getMethod("getVersionInfo").invoke(current));
                } catch (NoSuchMethodException old) {
                    Field field = loader.getDeclaredField("mcVersion");
                    field.setAccessible(true);
                    value = field.get(null);
                }
            }
            if (value instanceof String && !((String) value).isEmpty()) {
                return (String) value;
            }
        } catch (Throwable ignored) {
            // Fall back to the launch command
        }
        return launchArg("--fml.mcVersion");
    }

    private static Object property(Object versionInfo) throws ReflectiveOperationException {
        return versionInfo == null ? null : versionInfo.getClass().getMethod("mcVersion").invoke(versionInfo);
    }

    static String launchArg(String name) {
        String[] parts = System.getProperty("sun.java.command", "").split("\\s+");
        for (int i = 0; i + 1 < parts.length; i++) {
            if (parts[i].equals(name)) {
                return parts[i + 1];
            }
        }
        return null;
    }

    /** One line of the index: {@code loader minInclusive maxExclusive size checksum file}. */
    static final class Build {
        final String loader;
        final String min;
        final String max;
        final long size;
        final String checksum;
        final String file;

        private Build(String loader, String min, String max, long size, String checksum, String file) {
            this.loader = loader;
            this.min = min;
            this.max = max;
            this.size = size;
            this.checksum = checksum;
            this.file = file;
        }

        static Build parse(String line) {
            String[] f = line.split("\\s+");
            if (f.length != 6) {
                throw new IllegalArgumentException("Bad RP Tubes index line: " + line);
            }
            return new Build(f[0], f[1], f[2], Long.parseLong(f[3]), f[4], f[5]);
        }

        boolean supports(String mcVersion) {
            return compare(mcVersion, min) >= 0 && compare(mcVersion, max) < 0;
        }
    }

    /** Compares dotted release numbers such as 1.20.1 and 26.1.2; a missing part counts as 0. */
    static int compare(String a, String b) {
        String[] x = a.split("[.\\-+]");
        String[] y = b.split("[.\\-+]");
        for (int i = 0; i < Math.max(x.length, y.length); i++) {
            int p = i < x.length ? number(x[i]) : 0;
            int q = i < y.length ? number(y[i]) : 0;
            if (p != q) {
                return p < q ? -1 : 1;
            }
        }
        return 0;
    }

    private static int number(String part) {
        int end = 0;
        while (end < part.length() && Character.isDigit(part.charAt(end))) {
            end++;
        }
        return end == 0 ? 0 : Integer.parseInt(part.substring(0, end));
    }
}
