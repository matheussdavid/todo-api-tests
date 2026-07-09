package com.example.taskapiclient.tasks;

import com.example.taskapiclient.TestBase;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class PostTask extends TestBase {

    @Test
    void shouldCreateTaskWithValidData() {
        var request = Map.of(
                "title","Estudar REST Assured",
                "description", "Praticar testes de API",
                "status", "PENDENTE"
        );

        var response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(request)
                .post("/tasks")
                .then()
                .statusCode(201)
                .extract()
                .response();

        assertThat(response.jsonPath().getLong("id")).isPositive();
        assertThat(response.jsonPath().getString("title")).isEqualTo("Estudar REST Assured");
        assertThat(response.jsonPath().getString("description")).isEqualTo("Praticar testes de API");
        assertThat(response.jsonPath().getString("status")).isEqualTo("PENDENTE");
    }

    @Test
    void shouldReturn400WhenTitleIsEmpty() {
        var request = Map.of(
                "title", "",
                "description", "Teste inválido",
                "status", "PENDENTE"
        );

        var response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(request)
                .post("/tasks")
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
    void shouldNotCreateTaskWhenTitleHasLessThan3Chars() {
        var request = Map.of(
                "title", "ab",
                "description", "Teste inválido",
                "status", "PENDENTE"
        );

        var response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(request)
                .post("/tasks")
                .then()
                .statusCode(400)
                .extract()
                .response();

        assertThat(response.jsonPath().getString("error")).isEqualTo("title: Título deve ter no mínimo 3 caracteres");
    }

    @Test
    void shouldNotCreateTaskWithInvalidStatus() {
        var request = Map.of(
                "title", "Tarefa teste",
                "description", "Teste inválido",
                "status", "TESTE"
        );

        var response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(request)
                .post("/tasks")
                .then()
                .statusCode(400);
    }

    @Test
    void shouldNotCreateTaskWithEmptyBody() {
        var request = Map.of();

        var response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(request)
                .post("/tasks")
                .then()
                .statusCode(400)
                .extract()
                .response();

        assertThat(response.jsonPath().getString("error")).isEqualTo("title: Título é obrigatório");
    }

    @Test
    void shouldCreateTaskWithExtraFields() {
        var request = Map.of(
                "title","Titulo teste",
                "description", "Descrição",
                "status", "PENDENTE",
                "campo1", "valor 1",
                "campo2", "valor 2"
        );

        var response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(request)
                .post("/tasks")
                .then()
                .statusCode(201)
                .extract()
                .response();
        assertThat(response.jsonPath().getMap("$")).doesNotContainKeys("campo1", "campo2");
    }

    @Test
    void shouldCreateTaskWithOnlyRequiredFields() {
        var request = Map.of(
                "title", "teste"
        );

        var response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(request)
                .post("/tasks")
                .then()
                .statusCode(201)
                .extract()
                .response();

        assertThat(response.jsonPath().getLong("id")).isPositive();
        assertThat(response.jsonPath().getString("title")).isEqualTo("teste");
        assertThat(response.jsonPath().getString("description")).isNull();
        assertThat(response.jsonPath().getString("status")).isEqualTo("PENDENTE");
    }
}