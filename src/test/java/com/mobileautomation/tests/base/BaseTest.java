package com.mobileautomation.tests.base;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.mobileautomation.base.BaseDriver;
import com.mobileautomation.utils.AppiumServerManager;
import com.mobileautomation.utils.EmulatorManager;
import com.mobileautomation.utils.ExtentReportManager;
import com.mobileautomation.utils.ScreenshotUtils;
import io.appium.java_client.AppiumDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

/**
 * Base class for all test classes.
 * Handles Appium server lifecycle, driver teardown, Extent Report lifecycle,
 * and automatic screenshot capture on test failure.
 */
public abstract class BaseTest {

    protected static final Logger log = LogManager.getLogger(BaseTest.class);

    @BeforeSuite(alwaysRun = true)
    public void startSuite() {
        log.info("Starting Pixel 9 Pro emulator...");
        EmulatorManager.start();
        log.info("Starting Appium server...");
        AppiumServerManager.start();
    }

    protected AppiumDriver getDriver() {
        return BaseDriver.getDriver();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        AppiumDriver driver = getDriver();
        ExtentTest test = ExtentReportManager.getTest();

        if (result.getStatus() == ITestResult.FAILURE) {
            log.error("Test FAILED: {}", result.getName());
            if (driver != null && test != null) {
                String screenshotPath = ScreenshotUtils.capture(driver, result.getName());
                if (!screenshotPath.isEmpty()) {
                    test.fail("Test failed — screenshot attached",
                            MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
                } else {
                    test.fail(result.getThrowable());
                }
            }
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            if (test != null) test.pass("Test PASSED");
            log.info("Test PASSED: {}", result.getName());
        } else {
            if (test != null) test.log(Status.SKIP, "Test SKIPPED");
            log.warn("Test SKIPPED: {}", result.getName());
        }

        BaseDriver.quitDriver();
        ExtentReportManager.removeTest();
    }

    @AfterSuite(alwaysRun = true)
    public void tearDownSuite() {
        ExtentReportManager.flush();
        log.info("Stopping Appium server...");
        AppiumServerManager.stop();
        log.info("Stopping emulator...");
        EmulatorManager.stop();
    }
}
