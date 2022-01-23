package com.polly.utils.logging;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Logging {
    private static Logger logger;

    static {
        setLogger(Logger.GLOBAL_LOGGER_NAME, Level.ALL);
        addHandler(Level.ALL);
    }

    private static void setLogger(String name, Level loggerLevel) {
        Logging.logger = Logger.getLogger(name);
        Logging.logger.setLevel(loggerLevel);
        Logging.logger.setUseParentHandlers(false);
    }

    private static void addHandler(Level level) {
        Handler handler = new ConsoleHandler();
        handler.setLevel(level);
        Logging.logger.addHandler(handler);
    }

    public static void log(Level level, String sourceClass, String sourceMethod, String msg) {
        Logging.logger.logp(level, sourceClass, sourceMethod, msg);
    }

    public static void log(Level level, String sourceClass, String sourceMethod, String msg, Object param1) {
        Logging.logger.logp(level, sourceClass, sourceMethod, msg, param1);
    }

    public static void log(Level level, String sourceClass, String sourceMethod, String msg, Object[] params) {
        Logging.logger.logp(level, sourceClass, sourceMethod, msg, params);
    }

    public static void log(Level level, String sourceClass, String sourceMethod, String msg, Throwable thrown) {
        Logging.logger.logp(level, sourceClass, sourceMethod, msg, thrown);
    }
}
