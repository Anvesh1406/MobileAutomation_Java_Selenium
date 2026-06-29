package com.mobileautomation.pages.youtube;

import com.mobileautomation.pages.base.BasePage;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Page Object for the YouTube Video Player / Watch screen.
 */
public class YouTubeVideoPlayerPage extends BasePage {

    @AndroidFindBy(id = "com.google.android.youtube:id/title")
    @iOSXCUITFindBy(accessibility = "title")
    private WebElement videoTitle;

    @AndroidFindBy(id = "com.google.android.youtube:id/channel_name")
    @iOSXCUITFindBy(accessibility = "channel")
    private WebElement channelName;

    @AndroidFindBy(id = "com.google.android.youtube:id/view_count")
    @iOSXCUITFindBy(accessibility = "viewCount")
    private WebElement viewCount;

    private static final By TITLE_LOCATOR =
            By.id("com.google.android.youtube:id/title");

    public YouTubeVideoPlayerPage(AppiumDriver driver) {
        super(driver);
    }

    public String getVideoTitle() {
        wait.waitForVisibility(TITLE_LOCATOR);
        String title = getText(videoTitle);
        log.info("Video player title: '{}'", title);
        return title;
    }

    public String getChannelName() {
        try {
            return getText(channelName);
        } catch (Exception e) {
            log.warn("Channel name not found: {}", e.getMessage());
            return "N/A";
        }
    }

    public String getViewCount() {
        try {
            return getText(viewCount);
        } catch (Exception e) {
            log.warn("View count not found: {}", e.getMessage());
            return "N/A";
        }
    }
}
