package seedu.insureconnect.logic.parser;

import static seedu.insureconnect.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.insureconnect.logic.commands.CommandTestUtil.ADDRESS_DESC_AMY;
import static seedu.insureconnect.logic.commands.CommandTestUtil.ADDRESS_DESC_BOB;
import static seedu.insureconnect.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.insureconnect.logic.commands.CommandTestUtil.EMAIL_DESC_BOB;
import static seedu.insureconnect.logic.commands.CommandTestUtil.INVALID_ADDRESS_DESC;
import static seedu.insureconnect.logic.commands.CommandTestUtil.INVALID_EMAIL_DESC;
import static seedu.insureconnect.logic.commands.CommandTestUtil.INVALID_NAME_DESC;
import static seedu.insureconnect.logic.commands.CommandTestUtil.INVALID_PHONE_DESC;
import static seedu.insureconnect.logic.commands.CommandTestUtil.INVALID_TAG_DESC;
import static seedu.insureconnect.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.insureconnect.logic.commands.CommandTestUtil.NAME_DESC_BOB;
import static seedu.insureconnect.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.insureconnect.logic.commands.CommandTestUtil.PHONE_DESC_BOB;
import static seedu.insureconnect.logic.commands.CommandTestUtil.PREAMBLE_NON_EMPTY;
import static seedu.insureconnect.logic.commands.CommandTestUtil.PREAMBLE_WHITESPACE;
import static seedu.insureconnect.logic.commands.CommandTestUtil.REMARK_DESC;
import static seedu.insureconnect.logic.commands.CommandTestUtil.TAG_DESC_FRIEND;
import static seedu.insureconnect.logic.commands.CommandTestUtil.TAG_DESC_HUSBAND;
import static seedu.insureconnect.logic.commands.CommandTestUtil.VALID_REMARK;
import static seedu.insureconnect.logic.commands.CommandTestUtil.VALID_TAG_FRIEND;
import static seedu.insureconnect.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.insureconnect.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.insureconnect.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.insureconnect.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.insureconnect.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.insureconnect.logic.parser.CliSyntax.PREFIX_REMARK;
import static seedu.insureconnect.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.insureconnect.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.insureconnect.testutil.TypicalPersons.AMY;
import static seedu.insureconnect.testutil.TypicalPersons.BOB;

import org.junit.jupiter.api.Test;

import seedu.insureconnect.logic.Messages;
import seedu.insureconnect.logic.commands.AddCommand;
import seedu.insureconnect.model.person.Address;
import seedu.insureconnect.model.person.Email;
import seedu.insureconnect.model.person.Name;
import seedu.insureconnect.model.person.Person;
import seedu.insureconnect.model.person.Phone;
import seedu.insureconnect.model.person.Remark;
import seedu.insureconnect.model.tag.Tag;
import seedu.insureconnect.testutil.PersonBuilder;

public class AddCommandParserTest {
    private AddCommandParser parser = new AddCommandParser();

    @Test
    public void parse_allFieldsPresent_success() {
        Person expectedPerson = new PersonBuilder(BOB).withTags(VALID_TAG_FRIEND).build();

        // whitespace only preamble
        assertParseSuccess(parser, PREAMBLE_WHITESPACE + NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB
                + ADDRESS_DESC_BOB + TAG_DESC_FRIEND, new AddCommand(expectedPerson));


        // multiple tags - all accepted
        Person expectedPersonMultipleTags = new PersonBuilder(BOB).withTags(VALID_TAG_FRIEND, VALID_TAG_HUSBAND)
                .build();
        assertParseSuccess(parser,
                NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB + TAG_DESC_HUSBAND + TAG_DESC_FRIEND,
                new AddCommand(expectedPersonMultipleTags));
    }
    @Test
    public void parse_policyNumberWithSeparators_success() {
        // EP: policy number with supported hyphen and slash separators
        String policyNumber = "LIFE-2026/SG";
        Person expectedPerson = new PersonBuilder(BOB)
                .withTags(policyNumber)
                .build();

        assertParseSuccess(parser,
                NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB
                        + ADDRESS_DESC_BOB + " t/" + policyNumber,
                new AddCommand(expectedPerson));
    }

    @Test
    public void parse_nameWithEscapedPrefix_success() {
        Person expectedPerson = new PersonBuilder(BOB).withName("Mohd a/l Kassim")
                .withTags(VALID_TAG_FRIEND).build();
        assertParseSuccess(parser, " n/Mohd !a/l Kassim " + PHONE_DESC_BOB + EMAIL_DESC_BOB
                + ADDRESS_DESC_BOB + TAG_DESC_FRIEND, new AddCommand(expectedPerson));
    }

    @Test
    public void parse_nameWithAccents_success() {
        String unicodeName = "José O\u0308zdemir D’Angelo";
        Person expectedPerson = new PersonBuilder(BOB).withName(unicodeName)
                .withTags(VALID_TAG_FRIEND).build();

        // EP: name uses precomposed and decomposed Latin accents with a curly apostrophe
        assertParseSuccess(parser, " n/" + unicodeName + " " + PHONE_DESC_BOB + EMAIL_DESC_BOB
                + ADDRESS_DESC_BOB + TAG_DESC_FRIEND, new AddCommand(expectedPerson));
    }

    @Test
    public void parse_addressWithEscapedPrefixAndExclamation_success() {
        Person expectedPerson = new PersonBuilder(BOB).withAddress("Blk 123 t/4!")
                .withTags(VALID_TAG_FRIEND).build();
        assertParseSuccess(parser, NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB
                + " " + PREFIX_ADDRESS + "Blk 123 !t/4!!" + TAG_DESC_FRIEND, new AddCommand(expectedPerson));
    }

    @Test
    public void parse_repeatedNonTagValue_failure() {
        String validExpectedPersonString = NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB
                + ADDRESS_DESC_BOB + TAG_DESC_FRIEND;

        // multiple names
        assertParseFailure(parser, NAME_DESC_AMY + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));

        // multiple phones
        assertParseFailure(parser, PHONE_DESC_AMY + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // multiple emails
        assertParseFailure(parser, EMAIL_DESC_AMY + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_EMAIL));

        // multiple addresses
        assertParseFailure(parser, ADDRESS_DESC_AMY + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ADDRESS));

        // multiple fields repeated
        assertParseFailure(parser,
                validExpectedPersonString + PHONE_DESC_AMY + EMAIL_DESC_AMY + NAME_DESC_AMY + ADDRESS_DESC_AMY
                        + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME, PREFIX_ADDRESS, PREFIX_EMAIL, PREFIX_PHONE));

        // invalid value followed by valid value

        // invalid name
        assertParseFailure(parser, INVALID_NAME_DESC + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));

        // invalid email
        assertParseFailure(parser, INVALID_EMAIL_DESC + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_EMAIL));

        // invalid phone
        assertParseFailure(parser, INVALID_PHONE_DESC + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // invalid address
        assertParseFailure(parser, INVALID_ADDRESS_DESC + validExpectedPersonString,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ADDRESS));

        // valid value followed by invalid value

        // invalid name
        assertParseFailure(parser, validExpectedPersonString + INVALID_NAME_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));

        // invalid email
        assertParseFailure(parser, validExpectedPersonString + INVALID_EMAIL_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_EMAIL));

        // invalid phone
        assertParseFailure(parser, validExpectedPersonString + INVALID_PHONE_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // invalid address
        assertParseFailure(parser, validExpectedPersonString + INVALID_ADDRESS_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ADDRESS));
    }

    @Test
    public void parse_policyNumberMissing_failure() {
        assertParseFailure(parser, NAME_DESC_AMY + PHONE_DESC_AMY + EMAIL_DESC_AMY + ADDRESS_DESC_AMY,
                expectedFormatError("Missing required field: policy number (t/)."));
    }

    @Test
    public void parse_compulsoryFieldMissing_failure() {
        // missing name prefix
        assertParseFailure(parser, PHONE_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB + TAG_DESC_FRIEND,
                expectedFormatError("Missing required field: name (n/)."));

        // missing phone prefix
        assertParseFailure(parser, NAME_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB + TAG_DESC_FRIEND,
                expectedFormatError("Missing required field: phone (p/)."));

        // missing email prefix
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + ADDRESS_DESC_BOB + TAG_DESC_FRIEND,
                expectedFormatError("Missing required field: email (e/)."));

        // missing address prefix
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + TAG_DESC_FRIEND,
                expectedFormatError("Missing required field: address (a/)."));

        // multiple fields missing
        assertParseFailure(parser, EMAIL_DESC_BOB + ADDRESS_DESC_BOB + TAG_DESC_FRIEND,
                expectedFormatError("Missing required fields: name (n/), phone (p/)."));

        // all required prefixes missing
        assertParseFailure(parser, "", expectedFormatError(
                "Missing required fields: name (n/), phone (p/), email (e/), address (a/), policy number (t/)."));

        // missing fields and unprefixed text are both reported
        assertParseFailure(parser, PREAMBLE_NON_EMPTY + EMAIL_DESC_BOB + ADDRESS_DESC_BOB + TAG_DESC_FRIEND,
                expectedFormatError("Missing required fields: name (n/), phone (p/)."
                        + " Unexpected text before the first field: \""
                        + PREAMBLE_NON_EMPTY.trim() + "\"."));
    }

    @Test
    public void parse_invalidValue_failure() {
        // invalid name
        assertParseFailure(parser, INVALID_NAME_DESC + PHONE_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB
                + TAG_DESC_HUSBAND + TAG_DESC_FRIEND, "Invalid name: " + Name.MESSAGE_CONSTRAINTS);

        // invalid phone
        assertParseFailure(parser, NAME_DESC_BOB + INVALID_PHONE_DESC + EMAIL_DESC_BOB + ADDRESS_DESC_BOB
                + TAG_DESC_HUSBAND + TAG_DESC_FRIEND, "Invalid phone: " + Phone.MESSAGE_CONSTRAINTS);

        // invalid email
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + INVALID_EMAIL_DESC + ADDRESS_DESC_BOB
                + TAG_DESC_HUSBAND + TAG_DESC_FRIEND, "Invalid email: " + Email.MESSAGE_CONSTRAINTS);

        // invalid address
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + INVALID_ADDRESS_DESC
                + TAG_DESC_HUSBAND + TAG_DESC_FRIEND, "Invalid address: " + Address.MESSAGE_CONSTRAINTS);

        // invalid tag
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB
                + INVALID_TAG_DESC + VALID_TAG_FRIEND, "Invalid policy number: " + Tag.MESSAGE_CONSTRAINTS);

        // two invalid values, only first invalid value reported
        assertParseFailure(parser, INVALID_NAME_DESC + PHONE_DESC_BOB + EMAIL_DESC_BOB + INVALID_ADDRESS_DESC
                        + TAG_DESC_FRIEND,
                "Invalid name: " + Name.MESSAGE_CONSTRAINTS);

        // non-empty preamble
        assertParseFailure(parser, PREAMBLE_NON_EMPTY + NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB
                + ADDRESS_DESC_BOB + TAG_DESC_HUSBAND + TAG_DESC_FRIEND,
                expectedFormatError("Unexpected text before the first field: \"" + PREAMBLE_NON_EMPTY.trim()
                        + "\"."));

        // EP: policy number containing prohibited whitespace
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB
                        + " t/POLICY 123", "Invalid policy number: " + Tag.MESSAGE_CONSTRAINTS);
    }


    @Test
    public void parse_emptyRequiredField_failure() {
        assertParseFailure(parser, " n/" + PHONE_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB + TAG_DESC_FRIEND,
                "Invalid name: " + Name.MESSAGE_CONSTRAINTS);

        // Empty and whitespace-only single-valued prefixes are present, but their values are invalid.
        assertParseFailure(parser, NAME_DESC_BOB + " p/ " + EMAIL_DESC_BOB + ADDRESS_DESC_BOB + TAG_DESC_FRIEND,
                "Invalid phone: " + Phone.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, NAME_DESC_BOB + " p/   " + EMAIL_DESC_BOB + ADDRESS_DESC_BOB + TAG_DESC_FRIEND,
                "Invalid phone: " + Phone.MESSAGE_CONSTRAINTS);

        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + " e/ " + ADDRESS_DESC_BOB + TAG_DESC_FRIEND,
                "Invalid email: " + Email.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + " e/   " + ADDRESS_DESC_BOB + TAG_DESC_FRIEND,
                "Invalid email: " + Email.MESSAGE_CONSTRAINTS);

        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + " a/ " + TAG_DESC_FRIEND,
                "Invalid address: " + Address.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + " a/   " + TAG_DESC_FRIEND,
                "Invalid address: " + Address.MESSAGE_CONSTRAINTS);

        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB + " t/",
                "Invalid policy number: " + Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB + " t/   ",
                "Invalid policy number: " + Tag.MESSAGE_CONSTRAINTS);

        // An empty repeated tag is invalid whether it comes before or after a valid tag.
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB
                        + " t/" + TAG_DESC_FRIEND,
                "Invalid policy number: " + Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB
                        + TAG_DESC_FRIEND + " t/",
                "Invalid policy number: " + Tag.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_remarkPresent_success() {
        Person expectedPerson = new PersonBuilder(BOB)
                .withTags(VALID_TAG_FRIEND)
                .withRemark(VALID_REMARK)
                .build();

        assertParseSuccess(parser,
                NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB
                        + ADDRESS_DESC_BOB + TAG_DESC_FRIEND + REMARK_DESC,
                new AddCommand(expectedPerson));
    }

    @Test
    public void parse_remarkMissing_success() {
        Person expectedPerson = new PersonBuilder(AMY)
                .withRemark("")
                .build();

        assertParseSuccess(parser,
                NAME_DESC_AMY + PHONE_DESC_AMY + EMAIL_DESC_AMY + ADDRESS_DESC_AMY + TAG_DESC_FRIEND,
                new AddCommand(expectedPerson));
    }

    @Test
    public void parse_emptyRemark_success() {
        Person expectedPerson = new PersonBuilder(AMY)
                .withRemark("")
                .build();

        assertParseSuccess(parser,
                NAME_DESC_AMY + PHONE_DESC_AMY + EMAIL_DESC_AMY + ADDRESS_DESC_AMY + TAG_DESC_FRIEND
                        + " " + PREFIX_REMARK,
                new AddCommand(expectedPerson));
    }

    @Test
    public void parse_duplicateRemark_failure() {
        String validCommand = NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB
                + ADDRESS_DESC_BOB + TAG_DESC_FRIEND;

        assertParseFailure(parser,
                validCommand + REMARK_DESC + REMARK_DESC,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_REMARK));
    }

    @Test
    public void parse_remarkTooLong_failure() {
        String invalidRemarkDescription = " " + PREFIX_REMARK + "a".repeat(501);

        assertParseFailure(parser,
                NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB
                        + ADDRESS_DESC_BOB + TAG_DESC_FRIEND + invalidRemarkDescription,
                "Invalid remark: " + Remark.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_addressLengthBoundary() {
        String validAddressDescription = " " + PREFIX_ADDRESS + "a".repeat(Address.MAX_LENGTH);
        String validCommand = NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB
                + validAddressDescription + TAG_DESC_FRIEND;
        Person expectedPerson = new PersonBuilder(BOB)
                .withAddress("a".repeat(Address.MAX_LENGTH))
                .withTags(VALID_TAG_FRIEND)
                .build();
        assertParseSuccess(parser, validCommand, new AddCommand(expectedPerson));

        String invalidAddressDescription = " " + PREFIX_ADDRESS + "a".repeat(Address.MAX_LENGTH + 1);
        String invalidCommand = NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB
                + invalidAddressDescription + TAG_DESC_FRIEND;
        assertParseFailure(parser, invalidCommand, "Invalid address: " + Address.MESSAGE_LENGTH_CONSTRAINTS);
    }

    @Test
    public void parse_remarkWhitespaceNormalized_success() {
        String remarkWithExtraWhitespace = " " + PREFIX_REMARK
                + "  Prefers   WhatsApp after 6pm  ";

        Person expectedPerson = new PersonBuilder(AMY)
                .withRemark(VALID_REMARK)
                .build();

        assertParseSuccess(parser,
                NAME_DESC_AMY + PHONE_DESC_AMY + EMAIL_DESC_AMY
                        + ADDRESS_DESC_AMY + TAG_DESC_FRIEND + remarkWithExtraWhitespace,
                new AddCommand(expectedPerson));
    }

    @Test
    public void parse_remarkEscapedPrefixAndExclamation_success() {
        // EP: escaped prefix-like text and a doubled exclamation mark are unescaped in an added remark
        Person expectedPerson = new PersonBuilder(AMY)
                .withRemark("dietary restrictions: n/a Follow up!")
                .build();

        assertParseSuccess(parser,
                NAME_DESC_AMY + PHONE_DESC_AMY + EMAIL_DESC_AMY + ADDRESS_DESC_AMY + TAG_DESC_FRIEND
                        + " " + PREFIX_REMARK + "dietary restrictions: !n/a Follow up!!",
                new AddCommand(expectedPerson));
    }


    private static String expectedFormatError(String details) {
        return String.format(MESSAGE_INVALID_COMMAND_FORMAT, details + "\n" + AddCommand.MESSAGE_USAGE);
    }

}
