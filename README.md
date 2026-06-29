# Mobile Automation Framework

Java + Appium + TestNG | Page Object Model | Android and iOS

---

## Project Structure

```
MobileAutomation_Java_Selenium/
├── config/
│   └── config.properties
├── src/
│   ├── main/java/com/mobileautomation/
│   │   ├── base/BaseDriver.java
│   │   ├── config/ConfigReader.java
│   │   ├── pages/
│   │   │   ├── base/BasePage.java
│   │   │   └── youtube/
│   │   │       ├── YouTubeHomePage.java
│   │   │       ├── YouTubeSearchPage.java
│   │   │       ├── YouTubeSearchResultsPage.java
│   │   │       └── YouTubeVideoPlayerPage.java
│   │   └── utils/
│   │       ├── AppiumServerManager.java
│   │       ├── EmulatorManager.java
│   │       ├── ExtentReportManager.java
│   │       ├── ScreenshotUtils.java
│   │       └── WaitUtils.java
│   └── test/java/com/mobileautomation/
│       └── tests/
│           ├── base/BaseTest.java
│           └── youtube/YouTubeSearchTest.java
├── screenshots/
├── reports/
├── logs/
├── testng.xml
└── pom.xml
```

---

## Prerequisites

| Tool | Version |
|------|---------|
| Java JDK | 11+ |
| Maven | 3.8+ |
| Appium | 2.x |
| Android SDK | API 30+ |
| Node.js | 18+ |

---

## Setup

### 1. Install Appium

```bash
npm install -g appium
appium driver install uiautomator2
```

### 2. Configure device

Edit config/config.properties:

```properties
platform.name=Android
platform.version=14
device.name=Pixel_9_Pro
device.udid=emulator-5554
```

### 3. Run test

```bash
mvn test -Dtest=YouTubeSearchTest#testYouTubeSearch_GoogleLaunchEvent
```

The framework automatically starts the emulator, starts Appium, runs the test, captures screenshots, generates the Extent HTML report, then stops everything.

---

## Test Flow

1. Launch YouTube app
2. Verify Home page is loaded
3. Tap Search icon
4. Type Google Launch Event and submit
5. Assert results are displayed
6. Log all visible video titles
7. Tap first video and capture title, channel, view count
8. Screenshot at each step

---

## Reports

Open the HTML report after the run:

```
reports/MobileAutomationReport_timestamp.html
```

---

## Adding a New App

1. Add package/activity to config.properties
2. Create page objects under pages/appname/
3. Create test class under tests/appname/
4. Add entry to testng.xml
