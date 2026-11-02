package seedu.insureconnect.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.insureconnect.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's name in InsureConnect.
 * Guarantees: immutable; is valid as declared in {@link #isValidName(String)}
 */
public class Name {

    public static final String MESSAGE_CONSTRAINTS =
            "Names should start with a Latin letter or digit, and can only contain Latin letters, combining marks, "
            + "digits, spaces, and the characters: - ' ’ . / !";

    /*
     * The first character of the name must be a Latin letter or ASCII digit.
     * This ensures names cannot be blank or consist solely of punctuation (e.g. "---").
     */
    public static final String VALIDATION_REGEX = "(?:\\p{IsLatin}\\p{M}*|[0-9])"
            + "(?:\\p{IsLatin}\\p{M}*|[0-9 '\\-\\./!’])*";

    public final String fullName;

    /**
     * Constructs a {@code Name}.
     *
     * @param name A valid name.
     */
    public Name(String name) {
        requireNonNull(name);
        checkArgument(isValidName(name), MESSAGE_CONSTRAINTS);
        fullName = name;
    }

    /**
     * Returns true if a given string is a valid name.
     */
    public static boolean isValidName(String test) {
        return test.matches(VALIDATION_REGEX);
    }


    @Override
    public String toString() {
        return fullName;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Name otherName)) {
            return false;
        }

        return fullName.equals(otherName.fullName);
    }

    @Override
    public int hashCode() {
        return fullName.hashCode();
    }

}
