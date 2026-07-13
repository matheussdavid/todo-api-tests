package com.example.taskapiclient.tasks;

import com.example.taskapiclient.TestBase;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class GetTaskByIdTest extends TestBase {

    @Test
    void shouldReturnTaskWhenValidIdIsProvided() {
        var response = RestAssured
                .given()
                .accept(ContentType.JSON)
                .pathParam("id", obterTaskId())
                .when()
                .get("/tasks/{id}")
                .then()
                .statusCode(200)
                .extract()
                .response();

        assertThat(response.jsonPath().getMap("$")).containsKeys("id", "title", "description",
                "status", "createdAt", "updatedAt");
    }

    @Test
    void shouldReturn404WhenGettingNonExistentTask() {
        var taskId = 0;
        var response = RestAssured
                .given()
                .accept(ContentType.JSON)
                .pathParam("id", taskId)
                .when()
                .get("/tasks/{id}")
                .then()
                .statusCode(404)
                .extract()
                .response();

        assertThat(response.jsonPath().getString("error")).isEqualTo("Tarefa não encontrada com id: " + taskId);
    }

    @Test
    void shouldNotReturnTaskWhenInvalidIdIsProvided() {
        var taskId = "ab";
        var response = RestAssured
                .given()
                .accept(ContentType.JSON)
                .pathParam("id", taskId)
                .when()
                .get("/tasks/{id}")
                .then()
                .statusCode(400)
                .extract()
                .response();

        assertThat(response.jsonPath().getString("error")).isEqualTo("O ID informado não é válido");
    }
}
