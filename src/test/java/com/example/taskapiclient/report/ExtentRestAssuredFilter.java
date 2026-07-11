package com.example.taskapiclient.report;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

import java.util.Map;

public class ExtentRestAssuredFilter implements Filter {

    private static final int MAX_BODY_LENGTH = 2000;

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext ctx) {

        Response response = ctx.next(requestSpec, responseSpec);

        ExtentTest test = ExtentReportManager.getCurrentTest();
        if (test == null) {
            return response;
        }

        StringBuilder reqLog = new StringBuilder();
        reqLog.append("<strong>Method:</strong> ").append(requestSpec.getMethod()).append("<br/>");
        reqLog.append("<strong>URI:</strong> ").append(requestSpec.getURI()).append("<br/>");

        Map<String, String> queryParams = requestSpec.getQueryParams();
        if (queryParams != null && !queryParams.isEmpty()) {
            reqLog.append("<strong>Query Params:</strong> ").append(queryParams).append("<br/>");
        }

        Headers reqHeaders = requestSpec.getHeaders();
        if (reqHeaders != null && !reqHeaders.asList().isEmpty()) {
            reqLog.append("<strong>Request Headers:</strong><pre>");
            reqHeaders.forEach(h -> reqLog.append(h.getName()).append(": ").append(h.getValue()).append("\n"));
            reqLog.append("</pre>");
        }

        String reqBody = requestSpec.getBody();
        if (reqBody != null) {
            reqLog.append("<strong>Request Body:</strong><pre>")
                    .append(truncate(reqBody))
                    .append("</pre>");
        }
        test.log(Status.INFO, reqLog.toString());

        StringBuilder resLog = new StringBuilder();
        resLog.append("<strong>Status:</strong> ").append(response.getStatusCode()).append("<br/>");
        resLog.append("<strong>Response Time:</strong> ").append(response.getTime()).append(" ms<br/>");

        Headers resHeaders = response.getHeaders();
        if (resHeaders != null && !resHeaders.asList().isEmpty()) {
            resLog.append("<strong>Response Headers:</strong><pre>");
            resHeaders.forEach(h -> resLog.append(h.getName()).append(": ").append(h.getValue()).append("\n"));
            resLog.append("</pre>");
        }

        String resBody = response.getBody().asString();
        if (resBody != null && !resBody.isEmpty()) {
            resLog.append("<strong>Response Body:</strong><pre>")
                    .append(truncate(resBody))
                    .append("</pre>");
        }

        if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
            test.log(Status.PASS, resLog.toString());
        } else {
            test.log(Status.FAIL, resLog.toString());
        }

        return response;
    }

    private String truncate(String value) {
        if (value == null) {
            return "";
        }
        return value.length() > MAX_BODY_LENGTH
                ? value.substring(0, MAX_BODY_LENGTH) + "..."
                : value;
    }
}
