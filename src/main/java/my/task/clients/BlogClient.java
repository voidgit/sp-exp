package my.task.clients;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.common.mapper.TypeRef;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import my.task.config.ConfigurationManager;
import my.task.config.TestConfig;
import my.task.models.User;

import java.util.List;

import static io.restassured.RestAssured.config;
import static io.restassured.RestAssured.given;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;

public class BlogClient {
    private static final TestConfig config = ConfigurationManager.getConfiguration();
    private final RequestSpecification requestSpecification;

    public BlogClient() {
        var objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        var restAssuredConfig = config().objectMapperConfig(
            objectMapperConfig().jackson2ObjectMapperFactory((cls, charset) -> objectMapper));

        requestSpecification = given()
            .spec(new RequestSpecBuilder()
                .setConfig(restAssuredConfig)
                .addFilter(new AllureRestAssured())
                .addFilter(new ResponseLoggingFilter())
                .addFilter(new RequestLoggingFilter())
                .setContentType(ContentType.JSON)
                .setBaseUri(config.getBaseUrl())
                .build());
    }

    public List<User> getUsers() {
        return givenBlogService()
            .get(config.getUsers())
            .then()
            .statusCode(200)
            .extract().response().as(new TypeRef<List<User>>() {
            });
    }

    private RequestSpecification givenBlogService() {
        return requestSpecification;
    }
}
