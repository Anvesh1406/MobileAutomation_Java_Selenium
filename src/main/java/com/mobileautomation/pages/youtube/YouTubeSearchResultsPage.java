package com.mobileautomation.pages.youtube;

import com.mobileautomation.pages.base.BasePage;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Page Object for the YouTube Search Results screen.
 */
public class YouTubeSearchResultsPage extends BasePage {

    // First video title in the results list
    @AndroidFindBy(xpath = "(//android.widget.TextView[@resource-id='com.google.android.youtube:id/title'])[1]")
    @iOSXCUITFindBy(xpath = "(//XCUIElementTypeStaticText[@name='title'])[1]")
    private WebElement firstVideoTitle;

    // All video title elements in the results list
    @AndroidFindBy(id = "com.google.android.youtube:id/title")
    @iOSXCUITFindBy(xpath = "//XCUIElementTypeStaticText[@name='title']")
    private List<WebElement> videoTitles;

    // Filter chip row (Videos, Channels, Playlists …)
    @AndroidFindBy(xpath = "//android.widget.HorizontalScrollView")
    private WebElement filterChipRow;

    private static final By FIRST_TITLE_LOCATOR = By.xpath(
            "(//android.widget.TextView[@resource-id='com.google.android.youtube:id/title'])[1]");

    public YouTubeSearchResultsPage(AppiumDriver driver) {
        super(driver);
    }

    /**
     * Waits for the results list and returns the title of the first video.
     */
    public String getFirstVideoTitle() {
        wait.waitForVisibility(FIRST_TITLE_LOCATOR);
        String title = getText(firstVideoTitle);
        log.info("First video title: '{}'", title);
        return title;
    }

    /**
     * Returns all visible video titles on the current results page.
     */
    public List<String> getAllVideoTitles() {
        wait.waitForVisibility(FIRST_TITLE_LOCATOR);
        List<String> titles = new ArrayList<>();
        for (WebElement el : videoTitles) {
            String text = el.getText();
            if (text != null && !text.isBlank()) {
                titles.add(text);
            }
        }
        log.info("Found {} video titles on results page", titles.size());
        return titles;
    }

    /**
     * Checks whether any results are shown.
     */
    public boolean hasResults() {
        return isDisplayed(FIRST_TITLE_LOCATOR);
    }

    /**
     * Taps on the first video in the results.
     *
     * @return YouTubeVideoPlayerPage instance
     */
    public YouTubeVideoPlayerPage tapFirstVideo() {
        wait.waitForClickability(FIRST_TITLE_LOCATOR).click();
        log.info("Tapped first video in search results");
        return new YouTubeVideoPlayerPage(driver);
    }
}
