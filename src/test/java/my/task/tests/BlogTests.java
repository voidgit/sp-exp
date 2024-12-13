package my.task.tests;

import io.qameta.allure.Description;
import my.task.clients.BlogClient;
import my.task.models.Address;
import my.task.models.Comment;
import my.task.models.Company;
import my.task.models.Geo;
import my.task.models.User;
import org.assertj.core.api.AutoCloseableSoftAssertions;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.SoftAssertionsProvider;
import org.junit.jupiter.api.Test;

import static my.task.conditions.BlogChecks.isValidEmail;
import static org.assertj.core.api.Assertions.assertThat;

public class BlogTests {

    public static final String DELPHINE_TEST_USER = "Delphine";

    @Test
    @Description("Given existing user with posts, then comments should contain valid emails")
    void validateCommentsEmails() {
        var client = new BlogClient();

        var delphine = getUser(client, DELPHINE_TEST_USER);

        var delphinePostIds = client.getPostsIds(delphine.getId());
        assertThat(delphinePostIds)
            .as("Cannot find any posts for user with id '%s' and name '%s'", delphine.getId(), DELPHINE_TEST_USER)
            .isNotEmpty();

        var commentsToDelphinePosts = client.getComments()
            .stream()
            .filter(comment -> delphinePostIds.contains(comment.getPostId()))
            .toList();

        assertThat(commentsToDelphinePosts)
            .as("There should be comments to user '%s' posts", DELPHINE_TEST_USER)
            .isNotEmpty();
        assertThat(commentsToDelphinePosts)
            .as("All comments should have valid emails")
            .extracting(Comment::getEmail)
            .allMatch(isValidEmail);
    }

    /**
     * Additional test examples
     */
    @Test
    @Description("Given existing user, then his data should exactly match to stored one")
    void validateUser() {
        var expectedUser = User.builder()
            .id(2)
            .name("Ervin Howell")
            .username("Antonette")
            .email("Shanna@melissa.tv")
            .address(Address.builder()
                .street("Victor Plains")
                .suite("Suite 879")
                .city("Wisokyburgh")
                .zipcode("90566-7771")
                .geo(Geo.builder()
                    .lat("-43.9509")
                    .lng("-34.4618")
                    .build())
                .build())
            .phone("010-692-6593 x09125")
            .website("anastasia.net")
            .company(Company.builder()
                .name("Deckow-Crist")
                .catchPhrase("Proactive didactic contingency")
                .bs("synergize scalable supply-chains")
                .build())
            .build();

        var client = new BlogClient();
        var actualUser = getUser(client, expectedUser.getUsername());

        assertThat(actualUser)
            .as("Retrieved user should be exactly the same as expected one.")
            .usingRecursiveComparison()
            .isEqualTo(expectedUser);
    }

    @Test
    @Description("All posts should have title and body at least 5 characters long")
    void validatePosts() {
        var client = new BlogClient();
        var posts = client.getPosts();

        assertThat(posts)
            .allSatisfy(post -> {
                try (var softly = new AutoCloseableSoftAssertions()) {
                    softly.assertThat(post.getId())
                        .isNotNull();
                    softly.assertThat(post.getUserId())
                        .isNotNull();
                    softly.assertThat(post.getTitle())
                        .isNotBlank()
                        .hasSizeGreaterThanOrEqualTo(3);
                    softly.assertThat(post.getTitle())
                        .isNotBlank()
                        .hasSizeGreaterThanOrEqualTo(5);
                }
            });
    }

    private static User getUser(BlogClient client, String username) {
        return client.getUsers()
            .stream()
            .filter(user -> user.getUsername().equalsIgnoreCase(username))
            .findFirst()
            .orElseThrow(() -> new AssertionError(String.format("Test user '%s' not found", username)));
    }
}
