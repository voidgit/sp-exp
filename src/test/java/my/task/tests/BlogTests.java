package my.task.tests;

import my.task.clients.BlogClient;
import my.task.models.Comment;
import org.junit.jupiter.api.Test;

import static my.task.conditions.BlogChecks.isValidEmail;
import static org.assertj.core.api.Assertions.assertThat;

public class BlogTests {

    public static final String DELPHINE_TEST_USER = "Delphine";

    @Test
    public void validateComments() {
        var client = new BlogClient();

        var delphine = client.getUsers()
            .stream()
            .filter(user -> user.getUsername().equalsIgnoreCase(DELPHINE_TEST_USER))
            .findFirst()
            .orElseThrow(() -> new AssertionError(String.format("Test user '%s' not found", DELPHINE_TEST_USER)));

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
}
