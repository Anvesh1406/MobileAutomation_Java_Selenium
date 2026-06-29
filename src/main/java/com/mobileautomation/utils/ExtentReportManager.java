package com.mobileautomation.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.mobileautomation.config.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ExtentReportManager {

    private static final Logger log = LogManager.getLogger(ExtentReportManager.class);

    private static ExtentReports extentReports;
    // ThreadLocal supports parallel test execution
    private static final ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();

    private ExtentReportManager() {}

    public static ExtentReports getInstance() {
        if (extentReports == null) {
            synchronized (ExtentReportManager.class) {
                if (extentReports == null) {
                    initReports();
                }
            }
        }
        return extentReports;
    }

    private static void initReports() {
        ConfigReader config = ConfigReader.getInstance();
        String reportPath = config.get("report.path");
        String reportName = config.get("report.name");
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String reportFile = reportPath + reportName + "_" + timestamp + ".html";

        ExtentSparkReporter spark = new ExtentSparkReporter(reportFile);
        spark.config().setTheme(Theme.DARK);
        spark.config().setDocumentTitle("Mobile Automation Report");
        spark.config().setReportName(reportName);
        spark.config().setTimeStampFormat("MMM dd, yyyy HH:mm:ss");

        extentReports = new ExtentReports();
        extentReports.attachReporter(spark);
        extentReports.setSystemInfo("Framework", "Appium + Java + TestNG");
        extentReports.setSystemInfo("Author", "QA Team");
        extentReports.setSystemInfo("Platform", config.get("platform.name"));
        extentReports.setSystemInfo("Device", config.get("device.name"));

        log.info("Extent Report initialised at: {}", reportFile);
    }

    public static ExtentTest createTest(String testName, String description) {
        ExtentTest test = getInstance().createTest(testName, description);
        extentTest.set(test);
        return test;
    }

    public static ExtentTest getTest() {
        return extentTest.get();
    }

    public static void flush() {
        if (extentReports != null) {
            extentReports.flush();
            log.info("Extent Report flushed.");
        }
    }

    public static void removeTest() {
        extentTest.remove();
    }
}
