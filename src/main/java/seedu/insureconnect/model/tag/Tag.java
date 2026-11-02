package seedu.insureconnect.model.tag;

import static java.util.Objects.requireNonNull;
import static seedu.insureconnect.commons.util.AppUtil.checkArgument;

import java.util.Locale;

/**
 * Represents a Tag in InsureConnect.
 * Guarantees: immutable; name is valid as declared in {@link #isValidTagName(String)}
 */
public class Tag {

    public static final String MESSAGE_CONSTRAINTS = "Policy numbers must be 1 to 30 characters long, "
            + "contain at least one letter or digit, and may include hyphens or slashes.";

    public static final String VALIDATION_REGEX =
            "(?=.*[A-Za-z0-9])[A-Za-z0-9/-]{1,30}";

    public final String tagName;

    /**
     * Constructs a {@code Tag}.
     *
     * @param tagName A valid tag name.
     */
    public Tag(String tagName) {
        requireNonNull(tagName);
        checkArgument(isValidTagName(tagName), MESSAGE_CONSTRAINTS);
        this.tagName = tagName.toUpperCase(Locale.ROOT);
    }

    /**
     * Returns true if a given string is a valid tag name.
     */
    public static boolean isValidTagName(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Tag otherTag)) {
            return false;
        }

        return tagName.equals(otherTag.tagName);
    }

    @Override
    public int hashCode() {
        return tagName.hashCode();
    }

    /**
     * Formats state as text for viewing.
     */
    public String toString() {
        return '[' + tagName + ']';
    }

}
