package com.rptubes.universal;

import java.lang.reflect.Method;

/** Logs through Log4j, which every Forge and NeoForge version ships, or to the console without it. */
final class Log {
    private static final Object LOGGER;
    private static final Method INFO;
    private static final Method ERROR;

    static {
        Object logger = null;
        Method info = null;
        Method error = null;
        try {
            Class<?> manager = Class.forName("org.apache.logging.log4j.LogManager");
            logger = manager.getMethod("getLogger", String.class).invoke(null, "RP Tubes");
            Class<?> type = Class.forName("org.apache.logging.log4j.Logger");
            info = type.getMethod("info", String.class);
            error = type.getMethod("error", String.class, Throwable.class);
        } catch (Throwable t) {
            logger = null;
        }
        LOGGER = logger;
        INFO = info;
        ERROR = error;
    }

    private Log() {
    }

    static void info(String message) {
        if (!call(INFO, message)) {
            System.out.println("[RP Tubes] " + message);
        }
    }

    static void error(String message) {
        error(message, null);
    }

    static void error(String message, Throwable t) {
        if (!call(ERROR, message, t)) {
            System.err.println("[RP Tubes] " + message);
            if (t != null) {
                t.printStackTrace();
            }
        }
    }

    private static boolean call(Method method, Object... args) {
        if (LOGGER == null) {
            return false;
        }
        try {
            method.invoke(LOGGER, args);
            return true;
        } catch (Throwable t) {
            return false;
        }
    }
}
