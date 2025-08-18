package com.xpathautomation;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * XPath Click Automation - A Selenium-based utility to find and click elements
 * using XPath.
 *
 * This class navigates to a given URL and clicks elements matching provided
 * XPaths.
 * It waits 1 second after each click and handles error scenarios.
 *
 * @author XPath Automation Team
 * @version 1.0.0
 */
public class XPathClickAutomation {

    private WebDriver driver;
    private WebDriverWait wait;
    private static final int CLICK_DELAY_MS = 1000; // 1 saniye
    private static final int WAIT_TIMEOUT_SECONDS = 10;

    // Configuration parameters
    private String targetUrl;
    private List<String> xpaths;
    private boolean headless = false;
    private int clickDelayMs = CLICK_DELAY_MS;
    private int waitTimeoutSeconds = WAIT_TIMEOUT_SECONDS;

    /**
     * Constructor - Initializes and configures WebDriver
     */
    public XPathClickAutomation() {
        initializeDriver();
    }

    /**
     * Sets the target URL
     *
     * @param url The target URL
     * @return this (for method chaining)
     */
    public XPathClickAutomation setUrl(String url) {
        this.targetUrl = url;
        return this;
    }

    /**
     * Sets the XPath list
     *
     * @param xpaths List of XPaths
     * @return this (for method chaining)
     */
    public XPathClickAutomation setXPaths(List<String> xpaths) {
        this.xpaths = new ArrayList<>(xpaths);
        return this;
    }

    /**
     * Adds a single XPath
     *
     * @param xpath XPath to add
     * @return this (for method chaining)
     */
    public XPathClickAutomation addXPath(String xpath) {
        if (this.xpaths == null) {
            this.xpaths = new ArrayList<>();
        }
        this.xpaths.add(xpath);
        return this;
    }

    /**
     * Sets headless mode
     *
     * @param headless if true, runs in headless mode
     * @return this (for method chaining)
     */
    public XPathClickAutomation setHeadless(boolean headless) {
        this.headless = headless;
        return this;
    }

    /**
     * Sets click delay
     *
     * @param clickDelayMs delay in milliseconds
     * @return this (for method chaining)
     */
    public XPathClickAutomation setClickDelay(int clickDelayMs) {
        this.clickDelayMs = clickDelayMs;
        return this;
    }

    /**
     * Sets wait timeout
     *
     * @param waitTimeoutSeconds timeout in seconds
     * @return this (for method chaining)
     */
    public XPathClickAutomation setWaitTimeout(int waitTimeoutSeconds) {
        this.waitTimeoutSeconds = waitTimeoutSeconds;
        return this;
    }

    /**
     * Initializes and configures Chrome WebDriver
     */
    private void initializeDriver() {
        try {
            // Automatically download and configure ChromeDriver with WebDriverManager
            WebDriverManager.chromedriver().setup();

            // Configure Chrome options
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--start-maximized");
            options.addArguments("--disable-blink-features=AutomationControlled");
            options.addArguments("--disable-extensions");

            // Headless mode
            if (headless) {
                options.addArguments("--headless");
            }

            // Start WebDriver
            driver = new ChromeDriver(options);

            // Configure WebDriverWait
            wait = new WebDriverWait(driver, Duration.ofSeconds(waitTimeoutSeconds));

            System.out.println("Chrome WebDriver started successfully.");

        } catch (Exception e) {
            System.err.println("Error while starting WebDriver: " + e.getMessage());
            throw new RuntimeException("WebDriver could not be started", e);
        }
    }

    /**
     * Navigates to the given URL
     *
     * @param url URL to navigate
     */
    public void navigateToUrl(String url) {
        try {
            System.out.println("Navigating to URL: " + url);
            driver.get(url);

            // Wait for page load
            wait.until(ExpectedConditions.jsReturnsValue("return document.readyState === 'complete'"));
            System.out.println("Page loaded successfully.");

        } catch (Exception e) {
            System.err.println("Error while navigating to URL: " + e.getMessage());
            throw new RuntimeException("Could not navigate to URL: " + url, e);
        }
    }

    /**
     * Finds and clicks an element by the given XPath
     *
     * @param xpath XPath of the target element
     * @return true if element was clicked, false otherwise
     */
    public boolean clickElementByXPath(String xpath) {
        try {
            System.out.println("Searching element by XPath: " + xpath);

            // Wait for element to be clickable
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));

            // Click element
            element.click();
            System.out.println("Element clicked successfully: " + xpath);

            // Wait after click
            Thread.sleep(clickDelayMs);

            return true;

        } catch (NoSuchElementException e) {
            System.err.println("Element not found by XPath: " + xpath);
            return false;
        } catch (Exception e) {
            System.err.println("Error while clicking element (" + xpath + "): " + e.getMessage());
            return false;
        }
    }

    /**
     * Finds and clicks elements by a list of XPaths
     *
     * @param xpaths List of XPaths to click
     */
    public void clickElementsByXPaths(List<String> xpaths) {
        if (xpaths == null || xpaths.isEmpty()) {
            System.err.println("XPath list is empty or null!");
            return;
        }

        System.out.println("Total " + xpaths.size() + " XPath(s) to process.");

        int successCount = 0;
        int failureCount = 0;

        for (int i = 0; i < xpaths.size(); i++) {
            String xpath = xpaths.get(i);
            System.out.println("\n--- Processing XPath " + (i + 1) + "/" + xpaths.size() + " ---");

            if (clickElementByXPath(xpath)) {
                successCount++;
            } else {
                failureCount++;
            }
        }

        System.out.println("\n=== Process Completed ===");
        System.out.println("Successful clicks: " + successCount);
        System.out.println("Failed clicks: " + failureCount);
        System.out.println("Total XPaths: " + xpaths.size());
    }

    /**
     * Validates configuration and runs the automation
     */
    public void run() {
        if (targetUrl == null || targetUrl.isEmpty()) {
            throw new IllegalArgumentException("URL is not set! Use setUrl().");
        }

        if (xpaths == null || xpaths.isEmpty()) {
            throw new IllegalArgumentException("XPath list is empty! Use setXPaths() or addXPath().");
        }

        try {
            // Navigate to URL
            navigateToUrl(targetUrl);

            // Fix XPaths and process
            List<String> fixedXpaths = xpaths.stream()
                    .map(XPathClickAutomation::fixXPathQuotes)
                    .toList();

            clickElementsByXPaths(fixedXpaths);

        } catch (Exception e) {
            System.err.println("An error occurred during automation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Closes WebDriver and releases resources
     */
    public void closeDriver() {
        if (driver != null) {
            try {
                driver.quit();
                System.out.println("WebDriver closed successfully.");
            } catch (Exception e) {
                System.err.println("Error while closing WebDriver: " + e.getMessage());
            }
        }
    }

    /**
     * Replaces double quotes with single quotes in XPath.
     * This method automatically fixes strings that contain double quotes in XPath.
     *
     * @param xpath Original XPath string
     * @return Fixed XPath string
     */
    private static String fixXPathQuotes(String xpath) {
        if (xpath == null || xpath.isEmpty()) {
            return xpath;
        }

        // Replace double quotes with single quotes
        return xpath.replace("\"", "'");
    }

    /**
     * Ana metod - Komut satırı kullanımı
     * 
     * @param args Komut satırı argümanları
     */
    public static void main(String[] args) {
        System.out.println("=== XPath Click Automation (CLI) ===");
        if (args == null || args.length == 0) {
            printUsage();
            return;
        }

        String url = null;
        List<String> xpaths = new ArrayList<>();
        boolean headless = false;
        int delayMs = CLICK_DELAY_MS;
        int waitSeconds = WAIT_TIMEOUT_SECONDS;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            switch (arg) {
                case "--help":
                case "-h":
                    printUsage();
                    return;
                case "--url":
                case "-u":
                    url = requireNext(args, ++i, "value required for --url");
                    break;
                case "--xpath":
                case "-x":
                    xpaths.add(requireNext(args, ++i, "value required for --xpath"));
                    break;
                case "--headless":
                case "-H":
                    headless = true;
                    break;
                case "--delay":
                case "-d":
                    delayMs = parsePositiveInt(requireNext(args, ++i, "value required for --delay"), "delay(ms)");
                    break;
                case "--timeout":
                case "-t":
                    waitSeconds = parsePositiveInt(requireNext(args, ++i, "value required for --timeout"),
                            "timeout(s)");
                    break;
                default:
                    System.err.println("Unknown argument: " + arg);
                    printUsage();
                    return;
            }
        }

        if (url == null || url.isEmpty()) {
            System.err.println("Error: --url is required");
            printUsage();
            return;
        }
        if (xpaths.isEmpty()) {
            System.err.println("Error: at least one --xpath must be provided");
            printUsage();
            return;
        }

        XPathClickAutomation automation = new XPathClickAutomation()
                .setHeadless(headless)
                .setClickDelay(delayMs)
                .setWaitTimeout(waitSeconds)
                .setUrl(url)
                .setXPaths(xpaths);

        try {
            automation.run();
        } finally {
            automation.closeDriver();
        }
    }

    private static void printUsage() {
        System.out.println(
                "Usage:\n" +
                        "  java -jar xpath-click-automation-1.0.0.jar --url <URL> --xpath <XPATH> [--xpath <XPATH> ...] [--headless] [--delay <ms>] [--timeout <s>]\n\n"
                        +
                        "Options:\n" +
                        "  -u, --url       Target URL (required)\n" +
                        "  -x, --xpath     XPath to click (can be repeated multiple times)\n" +
                        "  -H, --headless  Run in headless mode\n" +
                        "  -d, --delay     Delay between clicks (ms), default: " + CLICK_DELAY_MS + "\n" +
                        "  -t, --timeout   Wait timeout (s), default: " + WAIT_TIMEOUT_SECONDS + "\n" +
                        "  -h, --help      This help\n");
    }

    private static String requireNext(String[] args, int index, String errorMessage) {
        if (index >= args.length) {
            throw new IllegalArgumentException(errorMessage);
        }
        return args[index];
    }

    private static int parsePositiveInt(String value, String name) {
        try {
            int v = Integer.parseInt(value.trim());
            if (v < 0)
                throw new NumberFormatException("negative");
            return v;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid " + name + ": " + value);
        }
    }
}