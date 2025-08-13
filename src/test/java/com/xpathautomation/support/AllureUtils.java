package com.xpathautomation.support;

import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public final class AllureUtils {

    private AllureUtils() {
    }

    public static void attachScreenshot(WebDriver webDriver, String name) {
        if (webDriver == null) {
            return;
        }
        try {
            if (webDriver instanceof TakesScreenshot) {
                TakesScreenshot takesScreenshot = (TakesScreenshot) webDriver;
                byte[] bytes = takesScreenshot.getScreenshotAs(OutputType.BYTES);
                Allure.addAttachment(name == null ? "screenshot" : name, "image/png", new ByteArrayInputStream(bytes),
                        "png");
            }
        } catch (Throwable ignored) {
        }
    }

    public static void attachPageSource(WebDriver webDriver, String name) {
        if (webDriver == null) {
            return;
        }
        try {
            String source = webDriver.getPageSource();
            Allure.addAttachment(name == null ? "page-source" : name, "text/html",
                    new ByteArrayInputStream(source.getBytes(StandardCharsets.UTF_8)), "html");
        } catch (Throwable ignored) {
        }
    }
}
