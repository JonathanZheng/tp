package seedu.insureconnect.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.insureconnect.logic.parser.Prefix;
import seedu.insureconnect.model.person.Person;

/**
 * Container for user visible messages.
 */
public final class Messages {

    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command. Type help or press "
        + "F1 to see the available commands.";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format!\n%1$s";
    public static final String MESSAGE_PERSONS_LISTED_OVERVIEW = "%1$d customer(s) listed!";
    public static final String MESSAGE_DUPLICATE_FIELDS =
                "Multiple values specified for the following single-valued field(s): ";

    private Messages() {
    }

    /**
     * Returns an error message describing the valid customer index range.
     */
    public static String getInvalidPersonDisplayedIndexMessage(int displayedListSize) {
        assert displayedListSize >= 0;

        if (displayedListSize == 0) {
            return "The customer index must be a positive integer, "
                    + "but no customers are currently displayed.";
        }

        return String.format("The customer index must be a positive integer within "
                + "the currently displayed range (1 to %d).", displayedListSize);
    }

    /**
     * Returns an invalid command format message describing missing fields and/or unexpected preamble text.
     */
    public static String getErrorMessageForMissingFieldsAndPreamble(List<String> missingFields, String preamble,
                                                                    String commandUsage) {
        List<String> errors = new ArrayList<>();
        if (!missingFields.isEmpty()) {
            errors.add("Missing required field" + (missingFields.size() == 1 ? "" : "s") + ": "
                    + String.join(", ", missingFields) + ".");
        }
        if (!preamble.isEmpty()) {
            errors.add("Unexpected text before the first field: \"" + preamble + "\".");
        }
        return String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                String.join(" ", errors) + "\n" + commandUsage);
    }

    /**
     * Returns an error message indicating the duplicate prefixes.
     */
    public static String getErrorMessageForDuplicatePrefixes(Prefix... duplicatePrefixes) {
        assert duplicatePrefixes.length > 0;

        Set<String> duplicateFields =
                Stream.of(duplicatePrefixes).map(Prefix::toString).collect(Collectors.toSet());

        return MESSAGE_DUPLICATE_FIELDS + String.join(" ", duplicateFields);
    }

    /**
     * Formats the {@code person} for display to the user.
     */
    public static String format(Person person) {
        final StringBuilder builder = new StringBuilder();
        builder.append(person.getName())
                .append("; Phone: ")
                .append(person.getPhone())
                .append("; Email: ")
                .append(person.getEmail())
                .append("; Address: ")
                .append(person.getAddress())
                .append("; Policy numbers: ");
        person.getTags().forEach(builder::append);
        builder.append("; Remark: ")
                .append(person.getRemark());
        return builder.toString();
    }

}
