package my.task;

import my.task.clients.BlogClient;
import my.task.config.ConfigurationManager;
import my.task.models.User;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class BlogTests {

    @Test
    public void validateComments() {
        var response = given()
            .baseUri(ConfigurationManager.getConfiguration().getBaseUrl())
            .when()
            .get(ConfigurationManager.getConfiguration().getUsers())
            .then()
            .statusCode(200)
            .extract().response();

        assertThat(response.getBody().asString())
            .as("debug check")
            .contains("Delphine");

        var client = new BlogClient();
        var users = client.getUsers();

        assertThat(users)
            .extracting(User::getUsername)
            .contains("Delphine");
    }
}
