package com.mobileautomation.pages.base;

import com.mobileautomation.utils.WaitUtils;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import java.time.Duration;

/**
 * Base class for all Page Objects.
 * Provides common element interactions and explicit-wait utilities.
 */
public abstract class BasePage {

    protected final AppiumDriver driver;
    protected final WaitUtils wait;
    protected final Logger log;

    protected BasePage(AppiumDriver driver) {
        this.driver = driver;
        this.wait = new WaitUtils(driver);
        this.log = LogManager.getLogger(this.getClass());
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(10)), this);
    }

    protected void click(WebElement element) {
        element.click();
        log.debug("Clicked element: {}", element);
    }

    protected void click(By locator) {
        wait.waitForClickability(locator).click();
    }

    protected void sendKeys(WebElement element, String text) {
        element.clear();
        element.sendKeys(text);
        log.debug("Typed '{}' into element", text);
    }

    protected String getText(WebElement element) {
        return element.getText();
    }

    protected boolean isDisplayed(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
