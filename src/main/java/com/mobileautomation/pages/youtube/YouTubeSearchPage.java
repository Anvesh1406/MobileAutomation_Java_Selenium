package com.mobileautomation.pages.youtube;

import com.mobileautomation.pages.base.BasePage;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.WebElement;

/**
 * Page Object for the YouTube Search input screen.
 */
public class YouTubeSearchPage extends BasePage {

    @AndroidFindBy(xpath = "//android.widget.EditText[@resource-id='com.google.android.youtube:id/search_edit_text']")
    @iOSXCUITFindBy(accessibility = "Search YouTube")
    private WebElement searchInput;

    public YouTubeSearchPage(AppiumDriver driver) {
        super(driver);
    }

    /**
     * Types a search query and submits it.
     *
     * @param query The search term
     * @return YouTubeSearchResultsPage instance
     */
    public YouTubeSearchResultsPage search(String query) {
        log.info("Searching for: '{}'", query);
        wait.waitForVisibility(
                org.openqa.selenium.By.xpath(
                        "//android.widget.EditText[@resource-id='com.google.android.youtube:id/search_edit_text']"));
        sendKeys(searchInput, query);
        // Press the Search/Enter key on the Android soft keyboard
        ((AndroidDriver) driver).pressKey(
                new io.appium.java_client.android.nativekey.KeyEvent(
                        io.appium.java_client.android.nativekey.AndroidKey.ENTER));
        return new YouTubeSearchResultsPage(driver);
    }
}
