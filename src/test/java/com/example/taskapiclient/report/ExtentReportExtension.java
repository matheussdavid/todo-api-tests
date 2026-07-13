package com.example.taskapiclient.report;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.Optional;

public class ExtentReportExtension implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback {

    @Override
    public void beforeAll(ExtensionContext ctx) {
        ExtentReportManager.init();
    }

    @Override
    public void afterAll(ExtensionContext ctx) {
        ExtentReportManager.flush();
    }

    @Override
    public void beforeEach(ExtensionContext ctx) {
        String className = ctx.getRequiredTestClass().getSimpleName();
        String methodName = ctx.getDisplayName();
        ExtentTest test = ExtentReportManager.createTest(methodName);
        test.assignCategory(className);
    }

    @Override
    public void afterEach(ExtensionContext ctx) {
        ExtentTest test = ExtentReportManager.getCurrentTest();
        if (test != null) {
            Optional<Throwable> exception = ctx.getExecutionException();
            if (exception.isPresent()) {
                test.log(Status.FAIL, exception.get());
            } else if (test.getStatus() == null) {
                test.log(Status.PASS, "Test passed");
            }
        }
        ExtentReportManager.removeCurrentTest();
    }
}
