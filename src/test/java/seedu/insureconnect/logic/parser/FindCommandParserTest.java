package seedu.insureconnect.logic.parser;

import static seedu.insureconnect.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.insureconnect.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.insureconnect.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.insureconnect.logic.commands.FindCommand;
import seedu.insureconnect.model.person.NameContainsKeywordsPredicate;
import seedu.insureconnect.model.person.PhoneStartsWithPredicate;
import seedu.insureconnect.model.person.PolicyNumberMatchesPredicate;
import seedu.insureconnect.model.tag.Tag;

public class FindCommandParserTest {

    private FindCommandParser parser = new FindCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ", String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_validArgs_returnsFindCommand() {
        // no leading and trailing whitespaces
        FindCommand expectedFindCommand =
                new FindCommand(new NameContainsKeywordsPredicate(List.of("Alice", "Bob")));
        assertParseSuccess(parser, "Alice Bob", expectedFindCommand);

        // multiple whitespaces between keywords
        assertParseSuccess(parser, " \n Alice \n \t Bob  \t", expectedFindCommand);
    }

    /**
     * Phone mode accepts partial or complete numbers and surrounding whitespace.
     */
    @Test
    public void parse_phonePrefix_returnsPhoneSearch() {
        // EP: digits only, from one digit to a full number
        assertParseSuccess(parser, "p/9", // boundary value
                new FindCommand(new PhoneStartsWithPredicate("9")));
        assertParseSuccess(parser, "p/98765432",
                new FindCommand(new PhoneStartsWithPredicate("98765432")));
        // EP: whitespace around the query
        assertParseSuccess(parser, " \t p/ 0012 \n",
                new FindCommand(new PhoneStartsWithPredicate("0012")));
        // EP: country code with a leading plus
        assertParseSuccess(parser, "p/+65",
                new FindCommand(new PhoneStartsWithPredicate("+65")));
    }

    /**
     * Empty values and invalid phone formatting are rejected.
     */
    @Test
    public void parse_invalidPhoneQuery_throwsParseException() {
        for (String query : List.of(
                "p/", "p/  ", "p/abc", "p/+", "p/++65", "p/65+1234",
                "p/987-654", "p/98 76", "p/9876 Alice", "p/１２３")) {
            assertParseFailure(
                    parser,
                    query,
                    PhoneStartsWithPredicate.MESSAGE_CONSTRAINTS);
        }
    }

    /**
     * A phone marker cannot silently become a name keyword when preceded by a name.
     */
    @Test
    public void parse_mixedNameAndPhone_throwsParseException() {
        assertParseFailure(parser, "Alice p/9876",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    /**
     * Digits without the explicit phone marker retain the existing name-search interpretation.
     */
    @Test
    public void parse_numericNameKeyword_returnsNameSearch() {
        assertParseSuccess(parser, "9876", new FindCommand(new NameContainsKeywordsPredicate(List.of("9876"))));
    }

    @Test
    public void parse_policyNumber_returnsPolicyNumberSearch() {
        // EP: valid alphanumeric policy number
        assertParseSuccess(parser, "t/LIFE20001",
                new FindCommand(new PolicyNumberMatchesPredicate("LIFE20001")));

        // EP: valid lowercase policy number with surrounding whitespace
        assertParseSuccess(parser, " \t t/life20001 \n",
                new FindCommand(new PolicyNumberMatchesPredicate("life20001")));

        // EP: valid policy number with supported hyphen and slash separators
        assertParseSuccess(parser, "t/LIFE-2026/SG",
                new FindCommand(new PolicyNumberMatchesPredicate("LIFE-2026/SG")));
    }

    @Test
    public void parse_invalidPolicyNumber_throwsParseException() {
        for (String query : List.of("t/", "t/  ", "t/LIFE_20001", "t/LIFE 20001",
                "t/LIFE20001 Alice")) {
            assertParseFailure(parser, query, Tag.MESSAGE_CONSTRAINTS);
        }
    }

    @Test
    public void parse_nameFollowedByPolicy_rejectsMixedSearchModes() {
        assertParseFailure(parser, "Alice t/LIFE123",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }
}
