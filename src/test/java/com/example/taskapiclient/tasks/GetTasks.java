package com.example.taskapiclient.tasks;

import com.example.taskapiclient.TestBase;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GetTasks extends TestBase {

    @Test
    void shouldListAllTasks() {
        obterTaskId();
        var response = RestAssured
                .given()
                    .accept(ContentType.JSON)
                .when()
                    .get("/tasks")
                .then()
                    .statusCode(200)
                    .extract()
                    .response();

        assertThat(response.jsonPath().getList("$")).isNotEmpty();
        assertThat(response.jsonPath().getMap("$[0]")).containsKeys("id", "title", "description", "status",
                "createdAt", "updatedAt");

    }
}
