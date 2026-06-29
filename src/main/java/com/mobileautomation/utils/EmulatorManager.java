package com.mobileautomation.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class EmulatorManager {

    private static final Logger log = LogManager.getLogger(EmulatorManager.class);

    private static final String ANDROID_SDK = System.getenv("LOCALAPPDATA") + "\\Android\\Sdk";
    private static final String EMULATOR_PATH = ANDROID_SDK + "\\emulator\\emulator.exe";
    private static final String ADB_PATH = ANDROID_SDK + "\\platform-tools\\adb.exe";
    private static final String AVD_NAME = "Pixel_9_Pro";

    private static Process emulatorProcess;

    private EmulatorManager() {}

    /**
     * Starts the Pixel 9 Pro emulator and waits until it is fully booted.
     */
    public static void start() {
        if (isRunning()) {
            log.info("Emulator '{}' is already running.", AVD_NAME);
            return;
        }

        try {
            log.info("Starting emulator: {}", AVD_NAME);
            ProcessBuilder pb = new ProcessBuilder(
                    EMULATOR_PATH,
                    "-avd", AVD_NAME,
                    "-no-snapshot-load"
            );
            // inheritIO() lets the emulator window appear on screen
            pb.inheritIO();
            emulatorProcess = pb.start();
            log.info("Emulator process launched. Waiting for boot...");
            waitForBoot();
        } catch (Exception e) {
            throw new RuntimeException("Failed to start emulator '" + AVD_NAME + "': " + e.getMessage(), e);
        }
    }

    /**
     * Stops the emulator by sending 'adb emu kill'.
     */
    public static void stop() {
        try {
            log.info("Stopping emulator: {}", AVD_NAME);
            new ProcessBuilder(ADB_PATH, "-s", getEmulatorUdid(), "emu", "kill")
                    .start()
                    .waitFor();
            if (emulatorProcess != null) {
                emulatorProcess.destroy();
            }
            log.info("Emulator stopped.");
        } catch (Exception e) {
            log.warn("Could not stop emulator cleanly: {}", e.getMessage());
        }
    }

    /**
     * Returns true if the Pixel 9 Pro AVD is detected by ADB as a running device.
     */
    public static boolean isRunning() {
        return !getEmulatorUdid().isEmpty();
    }

    /**
     * Returns the ADB serial (e.g. emulator-5554) of the running Pixel 9 Pro emulator.
     * Returns empty string if not found.
     */
    public static String getEmulatorUdid() {
        try {
            Process adb = new ProcessBuilder(ADB_PATH, "devices").start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(adb.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("emulator-") && line.contains("device")) {
                    return line.split("\\s+")[0];
                }
            }
        } catch (Exception e) {
            log.warn("ADB devices check failed: {}", e.getMessage());
        }
        return "";
    }

    /**
     * Polls 'adb shell getprop sys.boot_completed' every 5 seconds until the
     * emulator reports it has fully booted (up to 3 minutes).
     */
    private static void waitForBoot() {
        int maxWaitSeconds = 180;
        int pollingInterval = 5;
        int elapsed = 0;

        log.info("Polling for emulator boot (max {}s)...", maxWaitSeconds);

        while (elapsed < maxWaitSeconds) {
            try {
                Thread.sleep(pollingInterval * 1000L);
                elapsed += pollingInterval;

                String udid = getEmulatorUdid();
                if (udid.isEmpty()) {
                    log.info("Waiting for emulator to appear in ADB... ({}s)", elapsed);
                    continue;
                }

                Process check = new ProcessBuilder(
                        ADB_PATH, "-s", udid, "shell", "getprop", "sys.boot_completed")
                        .start();
                String result = new BufferedReader(new InputStreamReader(check.getInputStream()))
                        .readLine();

                if ("1".equals(result != null ? result.trim() : "")) {
                    log.info("Emulator '{}' fully booted in {}s. UDID: {}", AVD_NAME, elapsed, udid);
                    // Dismiss the lock screen
                    unlockScreen(udid);
                    return;
                }

                log.info("Still booting... ({}s elapsed)", elapsed);

            } catch (Exception e) {
                log.warn("Boot poll error: {}", e.getMessage());
            }
        }

        throw new RuntimeException("Emulator '" + AVD_NAME + "' did not boot within " + maxWaitSeconds + "s.");
    }

    private static void unlockScreen(String udid) {
        try {
            // Wake up the screen then swipe up to unlock
            new ProcessBuilder(ADB_PATH, "-s", udid, "shell", "input", "keyevent", "KEYCODE_WAKEUP").start().waitFor();
            Thread.sleep(1000);
            new ProcessBuilder(ADB_PATH, "-s", udid, "shell", "input", "keyevent", "KEYCODE_MENU").start().waitFor();
            log.info("Lock screen dismissed.");
        } catch (Exception e) {
            log.warn("Could not unlock screen: {}", e.getMessage());
        }
    }
}
