package my.task.config;

import org.aeonbits.owner.Config;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({
    "system:env",
    "classpath:environment.properties",
    "classpath:blog.properties"
})
public interface TestConfig extends Config {
    @Key("base.url")
    String getBaseUrl();

    @Key("get.users")
    String getUsers();

    @Key("get.posts")
    String getPosts();

    @Key("get.comments")
    String getComments();
}