package com.mobileautomation.base;

import com.mobileautomation.config.ConfigReader;
import com.mobileautomation.utils.AppiumServerManager;
import com.mobileautomation.utils.EmulatorManager;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.options.XCUITestOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

/**
 * BaseDriver manages AppiumDriver lifecycle.
 * Supports Android (UiAutomator2) and iOS (XCUITest).
 */
public class BaseDriver {

    private static final Logger log = LogManager.getLogger(BaseDriver.class);
    // ThreadLocal for parallel execution safety
    private static final ThreadLocal<AppiumDriver> driverThread = new ThreadLocal<>();

    private BaseDriver() {}

    public static AppiumDriver getDriver() {
        return driverThread.get();
    }

    /**
     * Initialises the driver for the given app package and activity.
     *
     * @param appPackage  Android package name (e.g. com.google.android.youtube)
     * @param appActivity Android launch activity
     */
    public static void initDriver(String appPackage, String appActivity) {
        ConfigReader config = ConfigReader.getInstance();
        String platform = config.get("platform.name");
        // Prefer the live service URL; fall back to config if server was started externally
        String serverUrl = AppiumServerManager.isRunning()
                ? AppiumServerManager.getServiceUrl()
                : config.get("appium.server.url");

        try {
            AppiumDriver driver;
            if ("iOS".equalsIgnoreCase(platform)) {
                driver = createIOSDriver(serverUrl, appPackage, appActivity, config);
            } else {
                driver = createAndroidDriver(serverUrl, appPackage, appActivity, config);
            }

            int implicitWait = config.getInt("implicit.wait");
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));
            driverThread.set(driver);
            log.info("Driver initialised — Platform: {}, Package: {}", platform, appPackage);

        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid Appium server URL: " + serverUrl, e);
        }
    }

    private static AndroidDriver createAndroidDriver(String serverUrl,
                                                      String appPackage,
                                                      String appActivity,
                                                      ConfigReader config) throws MalformedURLException {
        UiAutomator2Options options = new UiAutomator2Options()
                .setPlatformName(config.get("platform.name"))
                .setPlatformVersion(config.get("platform.version"))
                .setDeviceName(config.get("device.name"))
                // Use the live ADB serial when emulator was started by the framework
                .setUdid(EmulatorManager.isRunning()
                        ? EmulatorManager.getEmulatorUdid()
                        : config.get("device.udid"))
                .setAppPackage(appPackage)
                .setAppActivity(appActivity)
                .setAutomationName(config.get("automation.name"))
                .setAutoGrantPermissions(config.getBoolean("auto.grant.permissions"))
                .setNoReset(config.getBoolean("no.reset"))
                .setNewCommandTimeout(Duration.ofSeconds(config.getInt("new.command.timeout")));

        return new AndroidDriver(new URL(serverUrl), options);
    }

    private static IOSDriver createIOSDriver(String serverUrl,
                                              String appPackage,
                                              String appActivity,
                                              ConfigReader config) throws MalformedURLException {
        XCUITestOptions options = new XCUITestOptions()
                .setPlatformName(config.get("platform.name"))
                .setPlatformVersion(config.get("platform.version"))
                .setDeviceName(config.get("device.name"))
                .setBundleId(appPackage)
                .setAutomationName("XCUITest");

        return new IOSDriver(new URL(serverUrl), options);
    }

    public static void quitDriver() {
        AppiumDriver driver = driverThread.get();
        if (driver != null) {
            driver.quit();
            driverThread.remove();
            log.info("Driver session closed.");
        }
    }
}
