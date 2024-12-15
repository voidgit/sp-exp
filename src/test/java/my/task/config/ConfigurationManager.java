package my.task.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.aeonbits.owner.ConfigFactory;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConfigurationManager {
    private final static TestConfig config = ConfigFactory.create(TestConfig.class);

    public static TestConfig getConfiguration() {
        return config;
    }
}
