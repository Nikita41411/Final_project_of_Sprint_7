package ru.yandex.praktikum;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Test;
import ru.yandex.praktikum.dto.Courier;
import ru.yandex.praktikum.dto.CourierCredentials;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@DisplayName("Тесты на создание курьера")
public class CreateCourierTest extends BaseTest {

    private Integer courierId;
    private String testLogin;

    @After
    public void tearDown() {
        deleteTestCourier(courierId);
    }

    @Test
    @DisplayName("Успешное создание курьера")
    public void createCourierSuccess() {
        testLogin = "courier" + System.currentTimeMillis();
        Courier courier = new Courier(testLogin, "password123", "Test Courier");

        // Создаем курьера и проверяем ответ
        createCourier(courier)
                .statusCode(201)
                .body("ok", is(true));

        // Логинимся, чтобы получить ID для удаления после теста
        courierId = loginCourier(new CourierCredentials(testLogin, "password123"))
                .extract()
                .path("id");
    }

    @Test
    @DisplayName("Нельзя создать двух одинаковых курьеров")
    public void createDuplicateCourierShouldFail() {
        testLogin = "duplicate" + System.currentTimeMillis();
        Courier courier = new Courier(testLogin, "password123", "Duplicate Courier");

        // Первое создание - успешно
        createCourier(courier)
                .statusCode(201);

        // Второе создание - должно вернуть ошибку
        createCourier(courier)
                .statusCode(409);

        // Получаем ID для удаления
        courierId = loginCourier(new CourierCredentials(testLogin, "password123"))
                .extract()
                .path("id");
    }

    @Test
    @DisplayName("Создание курьера без логина")
    public void createCourierWithoutLoginShouldFail() {
        Courier courier = new Courier(null, "password123", "Test Courier");

        createCourier(courier)
                .statusCode(400);
    }

    @Test
    @DisplayName("Создание курьера без пароля")
    public void createCourierWithoutPasswordShouldFail() {
        Courier courier = new Courier("nopassword" + System.currentTimeMillis(), null, "Test Courier");

        createCourier(courier)
                .statusCode(400);
    }

    @Step("Создание курьера")
    private io.restassured.response.ValidatableResponse createCourier(Courier courier) {
        return given()
                .contentType("application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier")
                .then();
    }

    @Step("Логин курьера")
    private io.restassured.response.ValidatableResponse loginCourier(CourierCredentials credentials) {
        return given()
                .contentType("application/json")
                .body(credentials)
                .when()
                .post("/api/v1/courier/login")
                .then();
    }
}