package com.mobileautomation.utils;

import com.mobileautomation.config.ConfigReader;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.time.Duration;

/**
 * Manages the Appium server process lifecycle.
 * Starts before the suite and stops after the suite.
 */
public class AppiumServerManager {

    private static final Logger log = LogManager.getLogger(AppiumServerManager.class);
    private static AppiumDriverLocalService service;

    private AppiumServerManager() {}

    /**
     * Starts the Appium server on the port defined in config.properties.
     * No-op if the server is already running.
     */
    public static void start() {
        if (service != null && service.isRunning()) {
            log.info("Appium server already running at {}", service.getUrl());
            return;
        }

        ConfigReader config = ConfigReader.getInstance();
        int port = config.getInt("appium.server.port");

        AppiumServiceBuilder builder = new AppiumServiceBuilder()
                .withIPAddress("127.0.0.1")
                .usingPort(port)
                // Redirect Appium logs to a file so test console stays clean
                .withLogFile(new File("logs/appium-server.log"))
                .withTimeout(Duration.ofSeconds(60))
                .withArgument(GeneralServerFlag.SESSION_OVERRIDE)
                .withArgument(GeneralServerFlag.BASEPATH, "/")
                .withArgument(GeneralServerFlag.LOG_LEVEL, "info");

        // Specify the Node.js executable path if Appium is not on the system PATH.
        // Uncomment and set the correct path if needed:
        // builder.usingDriverExecutable(new File("C:/Program Files/nodejs/node.exe"));
        // builder.withAppiumJS(new File("C:/Users/<user>/AppData/Roaming/npm/node_modules/appium/build/lib/main.js"));

        service = AppiumDriverLocalService.buildService(builder);
        service.start();

        if (service.isRunning()) {
            log.info("Appium server started at: {}", service.getUrl());
        } else {
            throw new RuntimeException("Appium server failed to start. Check logs/appium-server.log for details.");
        }
    }

    /**
     * Stops the Appium server if it is currently running.
     */
    public static void stop() {
        if (service != null && service.isRunning()) {
            service.stop();
            log.info("Appium server stopped.");
        } else {
            log.warn("Appium server was not running — nothing to stop.");
        }
    }

    /**
     * Returns true if the Appium server process is active.
     */
    public static boolean isRunning() {
        return service != null && service.isRunning();
    }

    /**
     * Returns the URL the server is listening on (e.g. http://127.0.0.1:4723).
     */
    public static String getServiceUrl() {
        if (service == null) {
            throw new IllegalStateException("Appium server has not been started.");
        }
        return service.getUrl().toString();
    }
}
