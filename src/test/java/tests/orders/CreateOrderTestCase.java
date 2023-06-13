package tests.orders;

import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.junit4.Tag;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import services.Ingredients;
import services.Order;
import services.User;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class CreateOrderTestCase {
    User user;
    Order order;
    Ingredients ingredients;
    private String accessToken;


    private String login() {
        String email = RandomStringUtils.randomAlphabetic(5) + "@gmail.com";
        String password = RandomStringUtils.randomAlphabetic(10);
        String username = RandomStringUtils.randomAlphabetic(10);
        Response createResponse = user.createUser(email, password, username);
        return createResponse.path("accessToken");
    }

    @Before
    public void setUp() {
        user = new User();
        order = new Order();
        ingredients = new Ingredients();
        accessToken = login();
    }


    @Tag("CreateOrder")
    @Test
    @DisplayName("Проверка создания заказа с ингредиентами авторизованным пользователем")
    public void checkOrderCreationWithIngredientsWithAuthorization() {

        Response responseIngredients = ingredients.getIngredients();
        List<String> ingredients = responseIngredients.path("data._id");
        Response response = order.createOrder(ingredients, accessToken);

        assertEquals("Неверный код ответа", 200, response.statusCode());
        assertEquals("Невалидные данные в ответе: success", true, response.path("success"));
    }

    @Tag("CreateOrder")
    @Test
    @DisplayName("Проверка создания заказа без авторизации")
    public void checkingOrderCreationWithoutAuthorization() {

        Response responseIngredients = ingredients.getIngredients();
        List<String> ingredients = responseIngredients.path("data._id");
        Response response = order.createOrder(ingredients, "");

        assertEquals("Неверный код ответа", 200, response.statusCode());
        assertEquals("Невалидные данные в ответе: success", true, response.path("success"));
    }

    @Tag("CreateOrder")
    @Test
    @DisplayName("Проверка создания заказа без ингредиентов")
    public void checkCreationOrderWithoutIngredients() {

        Response response = order.createOrder(null, accessToken);

        assertEquals("Неверный код ответа", 400, response.statusCode());
        assertEquals("Невалидные данные в ответе: success", false, response.path("success"));
        assertEquals("Невалидные данные в ответе: message", "Ingredient ids must be provided", response.path("message"));
    }

    @Tag("CreateOrder")
    @Test
    @DisplayName("Проверка создания заказа с неверным хэшем ингредиентов")
    public void checkOrderWasCreatedWithInvalidIngredientHash() {

        Response response = order.createOrder(List.of("InvalidIngredientHash"), accessToken);

        assertEquals("Неверный код ответа", 500, response.statusCode());


    }


    @After
    public void tearDown() {
        User.deleteUser(accessToken);
    }
}