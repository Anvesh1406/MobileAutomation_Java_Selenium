package com.mobileautomation.utils;

import com.mobileautomation.config.ConfigReader;
import io.appium.java_client.AppiumDriver;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenshotUtils {

    private static final Logger log = LogManager.getLogger(ScreenshotUtils.class);

    private ScreenshotUtils() {}

    /**
     * Captures a screenshot and saves it under the configured screenshots directory.
     *
     * @param driver   Active AppiumDriver instance
     * @param testName Name used as prefix in the screenshot filename
     * @return Absolute path of the saved screenshot, or empty string on failure
     */
    public static String capture(AppiumDriver driver, String testName) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String screenshotDir = ConfigReader.getInstance().get("screenshot.path");
        String fileName = screenshotDir + testName + "_" + timestamp + ".png";

        try {
            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File destFile = new File(fileName);
            FileUtils.copyFile(srcFile, destFile);
            log.info("Screenshot saved: {}", destFile.getAbsolutePath());
            return destFile.getAbsolutePath();
        } catch (IOException e) {
            log.error("Failed to capture screenshot for '{}': {}", testName, e.getMessage());
            return "";
        }
    }
}
