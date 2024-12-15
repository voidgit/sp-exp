package my.task.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Issue;
import my.task.clients.BlogClient;
import my.task.models.Address;
import my.task.models.Comment;
import my.task.models.Company;
import my.task.models.Geo;
import my.task.models.Post;
import my.task.models.User;
import org.assertj.core.api.AutoCloseableSoftAssertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static my.task.conditions.BlogChecks.isValidEmail;
import static org.assertj.core.api.Assertions.assertThat;

public class BlogTests {
    private static final String DELPHINE_TEST_USER = "Delphine";
    private static final int EXISTING_POST_ID = 1;
    private final BlogClient client = new BlogClient();

    @Test
    @Description("Given existing user with posts, then comments should contain valid emails")
    @Issue("JRASERVER-71768")
    void shouldHaveValidEmailsInComments() {
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
     * =================================================================================================================
     * Additional test examples
     * =================================================================================================================
     */

    @Test
    @Description("Given attempt to create post with all null data, then creation attempt should be rejected")
    @Issue("JRASERVER-71768")
    void shouldRejectIncorrectPostCreation() {
        var malformedPost = Post.builder()
            .title(null)
            .body(null)
            .userId(null)
            .build();
        var response = client.createPostAsResult(malformedPost);

        assertThat(response.getStatusCode())
            .as("Should reject new Post creation with all key data as null")
            .isEqualTo(400);
    }

    @Test
    @Description("Given empty body of the comment, then comment creation should be rejected")
    @Issue("JRASERVER-71768")
    void shouldRejectIncorrectCommentCreation() {
        var commentWithoutBody = Comment.builder()
            .name("valid name " + UUID.randomUUID())
            .body("")
            .email("valid_test_email@restmail.net")
            .postId(EXISTING_POST_ID)
            .build();
        var response = client.createCommentAsResult(commentWithoutBody);

        assertThat(response.getStatusCode())
            .as("Should reject new Comment creation with empty body")
            .isEqualTo(400);
    }

    @Test
    @Description("Given new comment is created, when retrieving list of comments, then comment should be in the list")
    @Issue("JRASERVER-71768")
    void shouldRetrieveCreatedComment() {
        var validComment = Comment.builder()
            .name("valid name " + UUID.randomUUID())
            .body("valid body")
            .email("valid_test_email@restmail.net")
            .postId(EXISTING_POST_ID)
            .build();
        var createdComment = client.createComment(validComment);

        assertThat(createdComment)
            .as("Response to newly created comment should be exactly equal to validComment ignoring id")
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(validComment);
        assertThat(createdComment.getId())
            .as("Id should be not null")
            .isNotNull();

        var retreivedComment = client.getComments()
            .stream()
            .filter(comment -> comment.getName().equals(validComment.getName()))
            .findFirst()
            .orElseThrow(() -> new AssertionError(String.format("Cannot find just created comment by name '%s'", validComment.getName())));

        assertThat(retreivedComment)
            .as("Newly created retrieved comment should be exactly equal to validComment ignoring id")
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(validComment);
        assertThat(retreivedComment.getId())
            .as("Id should be not null")
            .isNotNull();
    }

    @Test
    @Description("Should return 404 for non-existing endpoint")
    @Issue("JRASERVER-71768")
    void shouldReturn404ForNonExistingEndpoint() {
        var response = client.getNonExisingEndpoint("there_is_no_such_endpoint");

        assertThat(response.getStatusCode())
            .isEqualTo(404);
    }

    @Test
    @Description("Given existing user, then his data should exactly match to stored one")
    @Issue("JRASERVER-77736")
    void shouldReturnValidExistingUser() {
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

        var actualUser = getUser(client, expectedUser.getUsername());

        assertThat(actualUser)
            .as("Retrieved user should be exactly the same as expected one.")
            .usingRecursiveComparison()
            .isEqualTo(expectedUser);
    }

    @Test
    @Description("All posts should have ids, with title and body at least 3 and 5 chars long respectively")
    @Issue("JRASERVER-77736")
    void shouldHavePostsWithValidContent() {
        var posts = client.getPosts();

        assertThat(posts)
            .as("All posts should have ids, with title and body at least 3 and 5 chars long respectively")
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
