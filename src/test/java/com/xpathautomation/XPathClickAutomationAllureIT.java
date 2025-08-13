package com.xpathautomation;

import com.xpathautomation.support.AllureTestWatcher;
import com.xpathautomation.support.AllureUtils;
import io.qameta.allure.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

@Epic("XPath Click Automation")
@Feature("End-to-End Navigation and Clicking")
@ExtendWith(AllureTestWatcher.class)
class XPathClickAutomationAllureIT {

    private XPathClickAutomation automation;

    @BeforeEach
    void setup() {
        automation = new XPathClickAutomation()
                .setHeadless(true) // CI ortamı için uygun
                .setWaitTimeout(15)
                .setClickDelay(500);
    }

    @AfterEach
    void tearDown() {
        if (automation != null) {
            automation.closeDriver();
        }
    }

    @Test
    @Story("User can open a page and click key elements")
    @Description("Navigate to example page and perform a sequence of clicks using XPath")
    @Severity(SeverityLevel.CRITICAL)
    void userCanNavigateAndClick() {
        stepOpenHome("https://www.w3schools.com/html/html_forms.asp");

        Allure.step("Add XPaths to click in order", () -> {
            automation.setXPaths(List.of(
                    "//input[@type='text']",
                    "//input[@type='submit']"));
        });

        Allure.step("Execute clicking sequence", () -> {
            automation.run();
        });

        // Bilgilendirme amaçlı ekran görüntüsü
        Allure.step("Attach final screenshot", () -> AllureUtils.attachScreenshot(extractDriver(), "final"));
    }

    @Test
    @Story("Graceful handling of invalid XPath")
    @Description("Invalid XPath should not break the scenario; failures are recorded")
    @Severity(SeverityLevel.NORMAL)
    void invalidXPathIsHandledGracefully() {
        stepOpenHome("https://www.w3schools.com/html/html_forms.asp");

        Allure.step("Add one invalid XPath and one valid XPath", () -> {
            automation.setXPaths(List.of(
                    "//this-is-not-valid",
                    "//input[@type='text']"));
        });

        Allure.step("Run sequence and expect partial success", () -> {
            automation.run();
        });

        Allure.step("Attach screenshot after run", () -> AllureUtils.attachScreenshot(extractDriver(), "after-run"));
    }

    @Step("Open home page: {url}")
    void stepOpenHome(String url) {
        automation.setUrl(url);
        automation.navigateToUrl(url);
        AllureUtils.attachScreenshot(extractDriver(), "home-opened");
    }

    // Reflection ile WebDriver'ı çıkarmak (test watcher ile de uyumlu)
    private org.openqa.selenium.WebDriver extractDriver() {
        try {
            java.lang.reflect.Field f = XPathClickAutomation.class.getDeclaredField("driver");
            f.setAccessible(true);
            Object v = f.get(automation);
            if (v instanceof org.openqa.selenium.WebDriver)
                return (org.openqa.selenium.WebDriver) v;
        } catch (Throwable ignored) {
        }
        return null;
    }
}
