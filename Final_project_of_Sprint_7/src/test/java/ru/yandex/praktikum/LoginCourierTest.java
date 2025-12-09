package ru.yandex.praktikum;

import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.dto.Courier;
import ru.yandex.praktikum.dto.CourierCredentials;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@DisplayName("Тесты на логин курьера")
public class LoginCourierTest extends BaseTest {

    private Integer courierId;
    private String testLogin;
    private String testPassword = "password123";

    @Before
    public void setUp() {
        super.setUp();
        // Создаем курьера перед каждым тестом
        testLogin = "logincourier" + System.currentTimeMillis();
        Courier courier = new Courier(testLogin, testPassword, "Login Test Courier");

        given()
                .contentType("application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(201);

        // Получаем ID созданного курьера
        courierId = given()
                .contentType("application/json")
                .body(new CourierCredentials(testLogin, testPassword))
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(200)
                .extract()
                .path("id");
    }

    @After
    public void tearDown() {
        deleteTestCourier(courierId);
    }

    @Test
    @DisplayName("Успешный логин курьера")
    public void loginCourierSuccess() {
        given()
                .contentType("application/json")
                .body(new CourierCredentials(testLogin, testPassword))
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(200)
                .body("id", notNullValue());
    }

    @Test
    @DisplayName("Логин с неверным паролем")
    public void loginCourierWithWrongPasswordShouldFail() {
        given()
                .contentType("application/json")
                .body(new CourierCredentials(testLogin, "wrongpassword"))
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Логин с неверным логином")
    public void loginCourierWithWrongLoginShouldFail() {
        given()
                .contentType("application/json")
                .body(new CourierCredentials("wronglogin", testPassword))
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Логин без логина")
    public void loginCourierWithoutLoginShouldFail() {
        // Создаем JSON без поля login - важно передавать null как строку
        String jsonWithoutLogin = "{\"password\": \"" + testPassword + "\"}";

        given()
                .contentType("application/json")
                .body(jsonWithoutLogin)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .log().ifValidationFails()  // Логируем при ошибке для отладки
                .statusCode(400);
    }

    @Test
    @DisplayName("Логин без пароля")
    public void loginCourierWithoutPasswordShouldFail() {
        // Создаем JSON без поля password
        String jsonWithoutPassword = "{\"login\": \"" + testLogin + "\"}";

        given()
                .contentType("application/json")
                .body(jsonWithoutPassword)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .log().ifValidationFails()  // Логируем при ошибке
                .statusCode(400);
    }

    @Test
    @DisplayName("Логин несуществующего курьера")
    public void loginNonExistentCourierShouldFail() {
        // Используем уникальный логин для гарантии, что пользователя не существует
        String nonExistentLogin = "nonexistent" + System.currentTimeMillis();

        given()
                .contentType("application/json")
                .body(new CourierCredentials(nonExistentLogin, "password"))
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(404);
    }
}