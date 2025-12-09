package ru.yandex.praktikum;

import io.qameta.allure.junit4.DisplayName;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@DisplayName("Тесты на получение списка заказов")
public class GetOrdersListTest extends BaseTest {

    @Test
    @DisplayName("Получение списка заказов")
    public void getOrdersListTest() {
        given()
                .when()
                .get("/api/v1/orders")
                .then()
                .statusCode(200)
                .body("orders", not(empty()));
    }
}