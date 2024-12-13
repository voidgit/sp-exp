package my.task.conditions;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;

import java.util.function.Predicate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BlogChecks {
    public static final Predicate<String> isValidEmail = possiblyAnEmail ->
        EmailValidator.getInstance().isValid(possiblyAnEmail);
}
