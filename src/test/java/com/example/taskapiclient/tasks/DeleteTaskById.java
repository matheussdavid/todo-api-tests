package com.example.taskapiclient.tasks;

import com.example.taskapiclient.TestBase;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DeleteTaskById extends TestBase {

    @Test
    void shouldDeleteTaskWhenValidIdIsProvided() {
        var response = RestAssured
                .given()
                .accept(ContentType.JSON)
                .pathParams("id", obterTaskId())
                .when()
                .delete("/tasks/{id}")
                .then()
                .statusCode(204);
    }

    @Test
    void shouldReturn404WhenDeletingNonExistentTask() {
        var id = 0;
        var response = RestAssured
                .given()
                .accept(ContentType.JSON)
                .pathParam("id", id)
                .when()
                .delete("/tasks/{id}")
                .then()
                .statusCode(404)
                .extract()
                .response();

        assertThat(response.jsonPath().getString("error")).isEqualTo("Tarefa não encontrada com id: " + id);
    }

    @Test
    void shouldNotDeleteTaskWhenInvalidIdIsProvided() {
        var id = "ab";
        var response = RestAssured
                .given()
                .accept(ContentType.JSON)
                .pathParam("id", id)
                .when()
                .delete("/tasks/{id}")
                .then()
                .statusCode(400)
                .extract()
                .response();

        assertThat(response.jsonPath().getString("error")).isEqualTo("O ID informado não é válido");
    }

    @Test
    void shouldReturn404WhenTaskIsAlreadyDeleted() {
        var taskId = obterTaskId();
        RestAssured
                .given()
                .accept(ContentType.JSON)
                .pathParams("id", taskId)
                .when()
                .delete("/tasks/{id}")
                .then()
                .statusCode(204);

        var response = RestAssured
                .given()
                .accept(ContentType.JSON)
                .pathParams("id", taskId)
                .when()
                .delete("/tasks/{id}")
                .then()
                .statusCode(404)
                .extract()
                .response();

        assertThat(response.jsonPath().getString("error")).isEqualTo("Tarefa não encontrada com id: " + taskId);
    }
}
