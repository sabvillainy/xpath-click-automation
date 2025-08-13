package com.xpathautomation.support;

import io.qameta.allure.Allure;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.WebDriver;

import java.lang.reflect.Field;

/**
 * JUnit5 extension to automatically attach a screenshot and page source on test
 * failure,
 * and mark step boundaries.
 */
public class AllureTestWatcher implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        // Mark the start of a test with a step for readability (optional)
        Allure.step("Start test: " + context.getDisplayName());
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        boolean failed = context.getExecutionException().isPresent();
        if (!failed) {
            return;
        }

        WebDriver driver = extractWebDriverFromTestInstance(context.getRequiredTestInstance());
        AllureUtils.attachScreenshot(driver, "failure-screenshot");
        AllureUtils.attachPageSource(driver, "failure-page-source");
        Allure.step("Test failed: " + context.getDisplayName());
    }

    private WebDriver extractWebDriverFromTestInstance(Object testInstance) {
        if (testInstance == null)
            return null;
        try {
            // Direct fields on the test instance
            for (Field f : testInstance.getClass().getDeclaredFields()) {
                f.setAccessible(true);
                Object value = f.get(testInstance);
                if (value instanceof WebDriver) {
                    return (WebDriver) value;
                }
                // If this is a holder of WebDriver (e.g., automation object), try to find a
                // field named 'driver'
                if (value != null) {
                    WebDriver nested = tryExtractDriverFromObject(value);
                    if (nested != null)
                        return nested;
                }
            }
        } catch (Throwable ignored) {
        }
        return null;
    }

    private WebDriver tryExtractDriverFromObject(Object holder) {
        try {
            Class<?> c = holder.getClass();
            while (c != null) {
                for (Field f : c.getDeclaredFields()) {
                    if (WebDriver.class.isAssignableFrom(f.getType()) || f.getName().equalsIgnoreCase("driver")) {
                        f.setAccessible(true);
                        Object v = f.get(holder);
                        if (v instanceof WebDriver) {
                            return (WebDriver) v;
                        }
                    }
                }
                c = c.getSuperclass();
            }
        } catch (Throwable ignored) {
        }
        return null;
    }
}
