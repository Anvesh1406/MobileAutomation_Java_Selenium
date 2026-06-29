package com.mobileautomation.pages.youtube;

import com.mobileautomation.pages.base.BasePage;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Page Object for the YouTube Home screen.
 * Locators target the native YouTube Android/iOS app.
 */
public class YouTubeHomePage extends BasePage {

    // ── Search icon on the home toolbar ──────────────────────────────────
    @AndroidFindBy(accessibility = "Search")
    @iOSXCUITFindBy(accessibility = "Search")
    private WebElement searchButton;

    // Multiple fallback locators for the search button across YouTube versions
    private static final By[] SEARCH_LOCATORS = {
        By.xpath("//android.widget.ImageView[@content-desc='Search']"),
        By.xpath("//android.widget.ImageButton[@content-desc='Search']"),
        By.xpath("//*[@content-desc='Search']"),
        By.id("com.google.android.youtube:id/menu_item_1"),
        By.xpath("//*[@resource-id='com.google.android.youtube:id/menu_item_1']")
    };

    public YouTubeHomePage(AppiumDriver driver) {
        super(driver);
    }

    /**
     * Verifies YouTube is loaded by checking any known search button locator.
     */
    public boolean isLoaded() {
        for (By locator : SEARCH_LOCATORS) {
            try {
                if (driver.findElement(locator).isDisplayed()) {
                    log.info("YouTube home verified via locator: {}", locator);
                    return true;
                }
            } catch (Exception ignored) {}
        }
        log.warn("YouTube home page not detected with any known locator");
        return false;
    }

    /**
     * Taps the Search icon to open the search screen.
     * Tries multiple locators in order until one works.
     *
     * @return YouTubeSearchPage instance
     */
    public YouTubeSearchPage tapSearch() {
        log.info("Tapping Search button on YouTube Home");
        for (By locator : SEARCH_LOCATORS) {
            try {
                WebElement el = driver.findElement(locator);
                if (el.isDisplayed()) {
                    el.click();
                    log.info("Search tapped via locator: {}", locator);
                    return new YouTubeSearchPage(driver);
                }
            } catch (Exception ignored) {}
        }
        throw new RuntimeException("Could not find the YouTube Search button with any known locator");
    }
}
