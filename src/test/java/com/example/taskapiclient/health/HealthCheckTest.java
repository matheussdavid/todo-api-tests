package com.example.taskapiclient.health;

import com.example.taskapiclient.TestBase;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HealthCheckTest extends TestBase {

    @Test
    void shouldReturn200WithStatusUP() {
        var response = RestAssured
                .when()
                .get("/health")
                .then()
                .statusCode(200)
                .extract()
                .response();

        assertThat(response.jsonPath().getString("status")).isEqualTo("UP");
        assertThat(response.jsonPath().getString("service")).isEqualTo("task-api");
    }
}
