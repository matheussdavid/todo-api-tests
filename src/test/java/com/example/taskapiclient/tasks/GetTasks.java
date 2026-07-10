package com.example.taskapiclient.tasks;

import com.example.taskapiclient.TestBase;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.Map;

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

        var tasks = response.jsonPath().getList("$", Map.class);
        assertThat(tasks).isNotEmpty();
        assertThat(tasks.get(0)).containsKeys("id", "title", "description", "status",
                "createdAt", "updatedAt");

    }
}
