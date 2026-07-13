package com.example.taskapiclient.report;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ExtentReportManager {

    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> testNode = new ThreadLocal<>();
    private static final Map<String, ExtentTest> parentTests = new HashMap<>();

    private ExtentReportManager() {
    }

    public static void init() {
        if(extent != null) return;
        new File("target/extent-report").mkdirs();
        ExtentSparkReporter spark = new ExtentSparkReporter("target/extent-report/index.html");
        spark.config().setDocumentTitle("API Tests Report");
        spark.config().setReportName("Todo API Tests");
        extent = new ExtentReports();
        extent.attachReporter(spark);
        extent.setSystemInfo("Java", System.getProperty("java.version"));
        extent.setSystemInfo("OS", System.getProperty("os.name"));
    }

    public static void flush() {
        if (extent != null) {
            extent.flush();
        }
        parentTests.clear();
    }

    public static ExtentTest getOrCreateParentTest(String className) {
        return parentTests.computeIfAbsent(className, name -> extent.createTest(name));
    }

    public static ExtentTest createChildTest(String parentClassName, String childName) {
        ExtentTest parent = getOrCreateParentTest(parentClassName);
        ExtentTest child = parent.createNode(childName);
        testNode.set(child);
        return child;
    }

    public static ExtentTest getCurrentTest() {
        return testNode.get();
    }

    public static void removeCurrentTest() {
        testNode.remove();
    }
}
