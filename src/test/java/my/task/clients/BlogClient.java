package my.task.clients;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.common.mapper.TypeRef;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import my.task.config.ConfigurationManager;
import my.task.config.TestConfig;
import my.task.models.Comment;
import my.task.models.Post;
import my.task.models.User;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.config;
import static io.restassured.RestAssured.given;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;

public class BlogClient {
    private static final TestConfig config = ConfigurationManager.getConfiguration();
    private final RestAssuredConfig restAssuredConfig;

    public BlogClient() {
        var objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        restAssuredConfig = config().objectMapperConfig(
            objectMapperConfig().jackson2ObjectMapperFactory((cls, charset) -> objectMapper));
    }

    @Step
    public List<User> getUsers() {
        return givenBlogService()
            .get(config.getUsers())
            .then()
            .statusCode(200)
            .extract().response().as(new TypeRef<>() {
            });
    }

    @Step
    public List<Post> getPosts() {
        return givenBlogService()
            .get(config.getPosts())
            .then()
            .statusCode(200)
            .extract().response().as(new TypeRef<>() {
            });
    }

    @Step
    public Set<Integer> getPostsIds(Integer userId) {
        return getPosts()
            .stream()
            .filter(post -> post.getUserId().equals(userId))
            .map(Post::getId)
            .collect(Collectors.toUnmodifiableSet());
    }

    @Step
    public List<Comment> getComments() {
        return givenBlogService()
            .get(config.getComments())
            .then()
            .statusCode(200)
            .extract().response().as(new TypeRef<>() {
            });
    }

    private RequestSpecification givenBlogService() {
        return given()
            .spec(new RequestSpecBuilder()
                .setConfig(restAssuredConfig)
                .addFilter(new AllureRestAssured())
                .addFilter(new ResponseLoggingFilter())
                .addFilter(new RequestLoggingFilter())
                .setContentType(ContentType.JSON)
                .setBaseUri(config.getBaseUrl())
                .build());
    }
}
