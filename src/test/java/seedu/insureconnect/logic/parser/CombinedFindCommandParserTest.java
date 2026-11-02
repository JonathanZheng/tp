package seedu.insureconnect.logic.parser;

import static seedu.insureconnect.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.insureconnect.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.insureconnect.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.insureconnect.logic.Messages;
import seedu.insureconnect.logic.commands.FindCommand;
import seedu.insureconnect.model.person.AllCriteriaPredicate;
import seedu.insureconnect.model.person.NameContainsKeywordsPredicate;
import seedu.insureconnect.model.person.PhoneStartsWithPredicate;
import seedu.insureconnect.model.person.PolicyNumberMatchesPredicate;
import seedu.insureconnect.model.tag.Tag;

/**
 * Tests explicit find criteria, ordering, validation and compatibility with legacy searches.
 */
public class CombinedFindCommandParserTest {
    private final FindCommandParser parser = new FindCommandParser();

    @Test
    public void parse_combinedCriteria_acceptsAnyOrderAndWhitespace() {
        FindCommand expected = new FindCommand(new AllCriteriaPredicate(List.of(
                new NameContainsKeywordsPredicate(List.of("Alice", "Bob")),
                new PhoneStartsWithPredicate("912"), new PolicyNumberMatchesPredicate("LIFE"))));
        for (String query : List.of("n/Alice Bob p/912 t/LIFE", "t/LIFE p/912 n/Alice Bob",
                "p/912 n/Alice Bob t/LIFE", " \n n/ Alice\tBob\tp/912\nt/LIFE  ")) {
            assertParseSuccess(parser, query, expected);
        }
    }

    @Test
    public void parse_singleCriteria_reusesExistingMatchingRules() {
        // EP: one plain field
        assertParseSuccess(parser, "n/Alice Bob",
                new FindCommand(new NameContainsKeywordsPredicate(List.of("Alice", "Bob"))));
        assertParseSuccess(parser, "t/LIFE", new FindCommand(new PolicyNumberMatchesPredicate("LIFE")));
        // EP: name containing an escaped prefix
        assertParseSuccess(parser, "n/Mohd !a/l",
                new FindCommand(new NameContainsKeywordsPredicate(List.of("Mohd", "a/l"))));
        assertParseSuccess(parser, "n/!p/123",
                new FindCommand(new NameContainsKeywordsPredicate(List.of("p/123"))));
    }

    @Test
    public void parse_patronymicInName_acceptsWithoutEscape() {
        // EP: patronymic slash words that are valid name text, in any case
        assertParseSuccess(parser, "n/Ravi s/o",
                new FindCommand(new NameContainsKeywordsPredicate(List.of("Ravi", "s/o"))));
        assertParseSuccess(parser, "n/Priya D/O",
                new FindCommand(new NameContainsKeywordsPredicate(List.of("Priya", "D/O"))));
        // EP: patronymic followed by another supported criterion
        assertParseSuccess(parser, "n/Ravi s/o p/912", new FindCommand(new AllCriteriaPredicate(List.of(
                new NameContainsKeywordsPredicate(List.of("Ravi", "s/o")), new PhoneStartsWithPredicate("912")))));
    }

    @Test
    public void parse_slashWordThatIsNotAField_treatsAsNameText() {
        // EP: slash words that are not recognised command prefixes
        assertParseSuccess(parser, "n/Tan w/o Lim",
                new FindCommand(new NameContainsKeywordsPredicate(List.of("Tan", "w/o", "Lim"))));
        assertParseSuccess(parser, "n/Alice x/value",
                new FindCommand(new NameContainsKeywordsPredicate(List.of("Alice", "x/value"))));
    }

    @Test
    public void parse_legacyLiteralKeywords_preservesPunctuation() {
        assertParseSuccess(parser, "!Alice a/l",
                new FindCommand(new NameContainsKeywordsPredicate(List.of("!Alice", "a/l"))));
    }

    @Test
    public void parse_missingValues_reportsInvalidValueOrUsage() {
        String usage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE);
        // EP: empty name value, alone or next to another field
        for (String query : List.of("n/", "n/ \t", "n/ p/912", "p/912 n/")) {
            assertParseFailure(parser, query, usage);
        }
        // EP: empty or invalid phone and policy values
        assertParseFailure(parser, "n/Alice p/", PhoneStartsWithPredicate.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "n/Alice t/", Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "n/Alice t/LIFE_1", Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "t/" + "A".repeat(31), Tag.MESSAGE_CONSTRAINTS); // boundary value
    }

    @Test
    public void parse_duplicateCriteria_rejectsRatherThanIgnoringValues() {
        for (Prefix prefix : List.of(CliSyntax.PREFIX_NAME, CliSyntax.PREFIX_PHONE, CliSyntax.PREFIX_TAG)) {
            String marker = prefix.getPrefix();
            String message = Messages.getErrorMessageForDuplicatePrefixes(prefix);
            assertParseFailure(parser, marker + "123 " + marker + "456", message);
            assertParseFailure(parser, marker + " " + marker + "123", message);
            assertParseFailure(parser, marker + "123 " + marker, message);
        }
    }

    @Test
    public void parse_mixedPreambleOrUnsupportedCriteria_rejectsAmbiguity() {
        List<String> queries = List.of(
                // EP: preamble before a prefixed criterion
                "Alice p/912", "Alice n/Bob",
                // EP: customer-field prefixes that find cannot search
                "n/Alice e/a@example.com", "n/Alice a/address", "n/Alice r/remark", "t/LIFE e/a@example.com",
                // EP: unescaped field prefix inside a name
                "n/Mohd a/l");
        for (String query : queries) {
            assertParseFailure(parser, query, String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }
    }
}
