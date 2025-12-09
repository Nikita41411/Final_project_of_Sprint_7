package ru.yandex.praktikum;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import org.junit.Before;
import ru.yandex.praktikum.dto.Courier;
import ru.yandex.praktikum.dto.CourierCredentials;

import static io.restassured.RestAssured.given;

public class BaseTest {

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";

        // Настройка таймаутов для предотвращения 504 ошибок
        RestAssured.config = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", 30000)
                        .setParam("http.socket.timeout", 30000)
                        .setParam("http.connection-manager.timeout", 30000));

        RestAssured.filters(new AllureRestAssured());
    }

    protected Integer createTestCourierAndGetId() {
        // Создаем уникального курьера для теста
        String uniqueLogin = "testcourier" + System.currentTimeMillis();
        Courier courier = new Courier(uniqueLogin, "password123", "Test Courier");

        // Создаем курьера
        given()
                .contentType(ContentType.JSON)
                .body(courier)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(201);

        // Получаем ID курьера
        return given()
                .contentType(ContentType.JSON)
                .body(new CourierCredentials(uniqueLogin, "password123"))
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(200)
                .extract()
                .path("id");
    }

    protected void deleteTestCourier(Integer courierId) {
        if (courierId != null) {
            given()
                    .delete("/api/v1/courier/" + courierId)
                    .then()
                    .statusCode(200);
        }
    }
}