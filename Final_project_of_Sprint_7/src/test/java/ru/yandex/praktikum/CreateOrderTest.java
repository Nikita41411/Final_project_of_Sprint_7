package ru.yandex.praktikum;

import io.qameta.allure.junit4.DisplayName;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.yandex.praktikum.dto.Order;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
@DisplayName("Тесты на создание заказа")
public class CreateOrderTest extends BaseTest {

    private final List<String> color;
    private final String description;

    public CreateOrderTest(List<String> color, String description) {
        this.color = color;
        this.description = description;
    }

    @Parameterized.Parameters(name = "Тест с цветом: {1}")
    public static Object[][] getColorData() {
        return new Object[][] {
                {Arrays.asList("BLACK"), "Черный"},
                {Arrays.asList("GREY"), "Серый"},
                {Arrays.asList("BLACK", "GREY"), "Оба цвета"},
                {null, "Без цвета"}
        };
    }

    @Test
    @DisplayName("Создание заказа")
    public void createOrderTest() {
        Order order = new Order(
                "Иван",
                "Иванов",
                "ул. Ленина, 1",
                "Черкизовская",
                "+79999999999",
                3,
                "2024-12-31",
                "Не звонить",
                color
        );

        given()
                .contentType("application/json")
                .body(order)
                .when()
                .post("/api/v1/orders")
                .then()
                .statusCode(201)
                .body("track", notNullValue());
    }
}