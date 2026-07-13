package com.example.taskapiclient;

import com.example.taskapiclient.report.ExtentReportExtension;
import com.example.taskapiclient.report.ExtentRestAssuredFilter;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

@ExtendWith(ExtentReportExtension.class)
public abstract class TestBase {

    private static boolean filterAdded = false;

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "http://localhost:8080";
        RestAssured.basePath = "/api";
        if (!filterAdded) {
            RestAssured.filters(new ExtentRestAssuredFilter());
            filterAdded = true;
        }
    }

    protected int obterTaskId() {
        var response = RestAssured
                .given()
                .accept(ContentType.JSON)
                .when()
                .get("/tasks");
        if (response.statusCode() == 200
            && !response.jsonPath().getList("$").isEmpty()) {
            return response.jsonPath().getInt("[0].id");
        }

        return RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(Map.of(
                        "title", "task padrão",
                        "description", "Criada automatic    amente",
                        "status", "PENDENTE"
                ))
                .when()
                .post("/tasks")
                .jsonPath()
                .getInt("id");
    }
}
