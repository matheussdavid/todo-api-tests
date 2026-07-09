package com.example.taskapiclient;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;

import java.util.Map;

public abstract class TestBase {

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "http://localhost:8080";
        RestAssured.basePath = "/api";
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
                        "description", "Criada automatiamente",
                        "status", "PENDENTE"
                ))
                .when()
                .post("/tasks")
                .jsonPath()
                .getInt("id");
    }
}
