package io.github.glorrian.cmakegradleplugin.util;

import org.gradle.api.GradleException;

import java.io.File;
/**
 * Represents a native platform for building with operating system.
 *
 * @version 1.0
 */
public abstract class NativePlatform {
    public final static String OS_NAME = System.getProperty("os.name").toLowerCase();
    public final static boolean IS_LINUX = OS_NAME.contains("linux");
    public final static boolean IS_MACOS = OS_NAME.contains("mac") || OS_NAME.contains("darwin");
    public final static boolean IS_WINDOWS = OS_NAME.contains("windows");

    private static File cMakeExecutable;

    static {
        if (!IS_LINUX && !IS_MACOS && !IS_WINDOWS)
            throw new GradleException("Unsupported operation system: " + OS_NAME);
    }

    /**
     * A method for searching for the CMake executable file in system variables.
     * If the file is found once in the following method calls, it will be taken from the buffer and will not be searched again
     * @return The CMake executable file
     */
    public static File getCMakeExecutable() {
        if (cMakeExecutable != null)
            return cMakeExecutable;
        final String cmakeName = IS_WINDOWS ? "cmake.exe" : "cmake";
        String[] PATH = System.getenv("PATH").split(File.pathSeparator);
        for (String path : PATH) {
            File file = new File(path, cmakeName);
            if (file.exists() && file.canExecute()) {
                cMakeExecutable = file;
                return cMakeExecutable;
            }
        }
        throw new UnsupportedOperationException("CMake executable does not found. " +
                "Please check if CMake is installed on your machine.");
    }
}