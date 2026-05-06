package com.app.utils;

import java.time.LocalDateTime;

public class LoggerUtil {

    public static void log(String level, String packageName, String message) {
        String timestamp = LocalDateTime.now().toString();
        System.out.printf("[%s] [%s] [%s]: %s%n", timestamp, level.toUpperCase(), packageName, message);
    }

    public static void info(String packageName, String message) {
        log("INFO", packageName, message);
    }

    public static void warn(String packageName, String message) {
        log("WARN", packageName, message);
    }

    public static void error(String packageName, String message) {
        log("ERROR", packageName, message);
    }
}
