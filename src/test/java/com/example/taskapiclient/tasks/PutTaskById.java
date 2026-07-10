package com.example.taskapiclient.tasks;

import com.example.taskapiclient.TestBase;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class PutTaskById extends TestBase {

    @Test
    void shouldUpdateTaskWhenValidIdIsProvided() {
        var taskId = obterTaskId();
        var request = Map.of(
                "title", "titulo atualizado",
                "description", "descricao atualizada",
                "status", "EM_ANDAMENTO"
        );

        var response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .pathParams("id", taskId)
                .body(request)
                .when()
                .put("/tasks/{id}")
                .then()
                .statusCode(200)
                .extract()
                .response();

        assertThat(response.jsonPath().getLong("id")).isEqualTo(taskId);
        assertThat(response.jsonPath().getString("title")).isEqualTo(request.get("title"));
        assertThat(response.jsonPath().getString("description")).isEqualTo(request.get("description"));
        assertThat(response.jsonPath().getString("status")).isEqualTo(request.get("status"));
    }

    @Test
    void shouldUpdateTaskWithOnlyRequiredField() {
        var taskId = obterTaskId();
        var request = Map.of(
                "title", "apenas o titulo atualizado"
        );

        var oldTaskData = RestAssured
                .given()
                .accept(ContentType.JSON)
                .pathParam("id", taskId)
                .when()
                .get("/tasks/{id}")
                .then()
                .statusCode(200)
                .extract().response();

        var response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(request)
                .pathParam("id", taskId)
                .when()
                .put("/tasks/{id}")
                .then()
                .statusCode(200)
                .extract().response();

        assertThat(response.jsonPath().getLong("id")).isEqualTo(taskId);
        assertThat(response.jsonPath().getString("title")).isEqualTo(request.get("title"));
        assertThat(response.jsonPath().getString("description")).isNull();
        assertThat(response.jsonPath().getString("status")).isEqualTo(oldTaskData.jsonPath().getString("status"));
    }

    @Test
    void shouldNotUpdateTaskWhenTitleIsEmpty() {
        var taskId = obterTaskId();
        var request = Map.of(
                "title", "",
                "description", "descricao atualizada",
                "status", "EM_ANDAMENTO"
        );

        var response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .pathParams("id", taskId)
                .body(request)
                .when()
                .put("/tasks/{id}")
                .then()
                .statusCode(400)
                .extract()
                .response();

        String error = response.jsonPath().getString("error");
        assertThat(error)
                .contains("Título é obrigatório")
                .contains("mínimo 3 caracteres");
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistentTask() {
        var taskId = 0;
        var request = Map.of(
                "title", "titulo atualizado",
                "description", "descricao atualizada",
                "status", "EM_ANDAMENTO"
        );

        var response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .pathParams("id", taskId)
                .body(request)
                .when()
                .put("/tasks/{id}")
                .then()
                .statusCode(404)
                .extract()
                .response();

        assertThat(response.jsonPath().getString("error")).isEqualTo("Tarefa não encontrada com id: " + taskId);
    }

    @Test
    void shouldNotUpdateTaskWhenInvalidIdIsProvided() {
        var id = "ab";
        var request = Map.of(
                "title", "titulo atualizado",
                "description", "descricao atualizada",
                "status", "EM_ANDAMENTO"
        );

        var response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .pathParam("id", id)
                .body(request)
                .when()
                .put("/tasks/{id}")
                .then()
                .statusCode(400);
                /*.extract()
                .response();

        assertThat(response.jsonPath().getString("error")).isEqualTo("Tarefa não encontrada com id: " + id);*/
    }

    @Test
    void shouldNotUpdateTaskWhenInvalidStatusIsProvided() {
        var taskId = obterTaskId();
        var request = Map.of(
                "title", "titulo atualizado",
                "description", "descricao atualizada",
                "status", "TESTE"
        );

        var response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .pathParam("id", taskId)
                .body(request)
                .when()
                .put("/tasks/{id}")
                .then()
                .statusCode(400);
                /*.extract()
                .response();

        assertThat(response.jsonPath().getString("error")).isEqualTo("Tarefa não encontrada com id: " + id);*/
    }
}
