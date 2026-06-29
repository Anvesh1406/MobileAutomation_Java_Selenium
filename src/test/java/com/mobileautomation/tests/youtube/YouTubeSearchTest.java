package com.mobileautomation.tests.youtube;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.mobileautomation.base.BaseDriver;
import com.mobileautomation.config.ConfigReader;
import com.mobileautomation.pages.youtube.YouTubeHomePage;
import com.mobileautomation.pages.youtube.YouTubeSearchPage;
import com.mobileautomation.pages.youtube.YouTubeSearchResultsPage;
import com.mobileautomation.pages.youtube.YouTubeVideoPlayerPage;
import com.mobileautomation.tests.base.BaseTest;
import com.mobileautomation.utils.ExtentReportManager;
import com.mobileautomation.utils.ScreenshotUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Test suite for YouTube search functionality.
 *
 * Flow:
 *   1. Launch YouTube app on the device
 *   2. Verify the Home page is displayed
 *   3. Tap the Search icon
 *   4. Search for the configured keyword ("Google Launch Event")
 *   5. Capture a screenshot of the results
 *   6. Log all visible video titles
 *   7. Assert the first result title is not empty
 *   8. Tap the first video and capture its details
 */
public class YouTubeSearchTest extends BaseTest {

    private ConfigReader config;

    @BeforeMethod
    public void setUp() {
        config = ConfigReader.getInstance();
        BaseDriver.initDriver(
                config.get("youtube.package"),
                config.get("youtube.activity"));
        log.info("YouTube driver initialised");
    }

    @Test(description = "Search for Google Launch Event and verify search results")
    public void testYouTubeSearch_GoogleLaunchEvent() {
        ExtentTest test = ExtentReportManager.createTest(
                "YouTube Search – Google Launch Event",
                "Searches YouTube for 'Google Launch Event' and validates the results page");

        // ── Step 1: Verify Home Page ──────────────────────────────────────
        test.info("Launching YouTube and verifying Home page");
        YouTubeHomePage homePage = new YouTubeHomePage(getDriver());
        Assert.assertTrue(homePage.isLoaded(), "YouTube Home page should be displayed");
        test.pass("YouTube Home page loaded successfully");

        String homeScreenshot = ScreenshotUtils.capture(getDriver(), "YouTube_HomeScreen");
        test.info("Home screen captured",
                MediaEntityBuilder.createScreenCaptureFromPath(homeScreenshot).build());

        // ── Step 2: Tap Search ────────────────────────────────────────────
        test.info("Tapping Search button");
        YouTubeSearchPage searchPage = homePage.tapSearch();
        test.pass("Search screen opened");

        // ── Step 3: Enter search keyword ──────────────────────────────────
        String searchKeyword = config.get("youtube.search.keyword");
        test.info("Searching for: " + searchKeyword);
        YouTubeSearchResultsPage resultsPage = searchPage.search(searchKeyword);
        test.pass("Search submitted: " + searchKeyword);

        // ── Step 4: Screenshot – search results ───────────────────────────
        String resultsScreenshot = ScreenshotUtils.capture(getDriver(), "YouTube_SearchResults");
        test.info("Search results screenshot captured",
                MediaEntityBuilder.createScreenCaptureFromPath(resultsScreenshot).build());

        // ── Step 5: Validate results ──────────────────────────────────────
        Assert.assertTrue(resultsPage.hasResults(),
                "Search results should be displayed for: " + searchKeyword);

        String firstTitle = resultsPage.getFirstVideoTitle();
        Assert.assertFalse(firstTitle.isEmpty(),
                "First video title should not be empty");
        test.pass("First video title retrieved: " + firstTitle);

        // ── Step 6: Log all visible titles ────────────────────────────────
        List<String> allTitles = resultsPage.getAllVideoTitles();
        test.info("All visible video titles (" + allTitles.size() + "):");
        allTitles.forEach(title -> test.info("  • " + title));
        log.info("Video titles found: {}", allTitles);

        // ── Step 7: Tap first video ───────────────────────────────────────
        test.info("Tapping first video to open the player");
        YouTubeVideoPlayerPage playerPage = resultsPage.tapFirstVideo();

        String playerTitle   = playerPage.getVideoTitle();
        String channelName   = playerPage.getChannelName();
        String viewCount     = playerPage.getViewCount();

        log.info("Video player — Title: '{}', Channel: '{}', Views: '{}'",
                playerTitle, channelName, viewCount);
        test.info("Video title in player: " + playerTitle);
        test.info("Channel: " + channelName);
        test.info("View count: " + viewCount);

        String playerScreenshot = ScreenshotUtils.capture(getDriver(), "YouTube_VideoPlayer");
        test.pass("Video player screenshot captured",
                MediaEntityBuilder.createScreenCaptureFromPath(playerScreenshot).build());

        Assert.assertFalse(playerTitle.isEmpty(),
                "Video player title should not be empty");
        test.pass("Test completed successfully. Video title: " + playerTitle);
    }

    @Test(description = "Verify search returns results for multiple queries",
          dependsOnMethods = "testYouTubeSearch_GoogleLaunchEvent")
    public void testYouTubeSearch_AdditionalQuery() {
        ExtentTest test = ExtentReportManager.createTest(
                "YouTube Search – Additional Query Validation",
                "Validates that YouTube search returns results for a generic query");

        test.info("Navigating to search");
        YouTubeHomePage homePage = new YouTubeHomePage(getDriver());
        YouTubeSearchPage searchPage = homePage.tapSearch();

        test.info("Searching for: Android Development");
        YouTubeSearchResultsPage resultsPage = searchPage.search("Android Development");

        String screenshot = ScreenshotUtils.capture(getDriver(), "YouTube_AdditionalSearch");
        test.info("Results screenshot",
                MediaEntityBuilder.createScreenCaptureFromPath(screenshot).build());

        Assert.assertTrue(resultsPage.hasResults(), "Results should appear for 'Android Development'");
        test.pass("Additional search test passed. Results found: " + resultsPage.getFirstVideoTitle());
    }
}
