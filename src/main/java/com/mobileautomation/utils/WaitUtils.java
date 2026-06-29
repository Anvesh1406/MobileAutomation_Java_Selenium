package com.mobileautomation.utils;

import com.mobileautomation.config.ConfigReader;
import io.appium.java_client.AppiumDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class WaitUtils {

    private final AppiumDriver driver;
    private final WebDriverWait wait;

    public WaitUtils(AppiumDriver driver) {
        this.driver = driver;
        int timeout = ConfigReader.getInstance().getInt("explicit.wait");
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
    }

    public WebElement waitForVisibility(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public WebElement waitForClickability(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    public boolean waitForInvisibility(By locator) {
        return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public WebElement waitForPresence(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }
}
