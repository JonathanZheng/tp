package seedu.insureconnect.logic.parser;

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
import static seedu.insureconnect.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.insureconnect.logic.commands.CommandTestUtil.PHONE_DESC_BOB;
import static seedu.insureconnect.logic.commands.CommandTestUtil.REMARK_DESC;
import static seedu.insureconnect.logic.commands.CommandTestUtil.TAG_DESC_FRIEND;
import static seedu.insureconnect.logic.commands.CommandTestUtil.TAG_DESC_HUSBAND;
import static seedu.insureconnect.logic.commands.CommandTestUtil.VALID_ADDRESS_AMY;
import static seedu.insureconnect.logic.commands.CommandTestUtil.VALID_EMAIL_AMY;
import static seedu.insureconnect.logic.commands.CommandTestUtil.VALID_NAME_AMY;
import static seedu.insureconnect.logic.commands.CommandTestUtil.VALID_PHONE_AMY;
import static seedu.insureconnect.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.insureconnect.logic.commands.CommandTestUtil.VALID_REMARK;
import static seedu.insureconnect.logic.commands.CommandTestUtil.VALID_TAG_FRIEND;
import static seedu.insureconnect.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.insureconnect.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.insureconnect.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.insureconnect.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.insureconnect.logic.parser.CliSyntax.PREFIX_REMARK;
import static seedu.insureconnect.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.insureconnect.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.insureconnect.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.insureconnect.logic.parser.ParserUtil.MESSAGE_INVALID_INDEX;
import static seedu.insureconnect.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.insureconnect.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.insureconnect.testutil.TypicalIndexes.INDEX_THIRD_PERSON;

import org.junit.jupiter.api.Test;

import seedu.insureconnect.commons.core.index.Index;
import seedu.insureconnect.logic.Messages;
import seedu.insureconnect.logic.commands.EditCommand;
import seedu.insureconnect.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.insureconnect.model.person.Address;
import seedu.insureconnect.model.person.Email;
import seedu.insureconnect.model.person.Name;
import seedu.insureconnect.model.person.Phone;
import seedu.insureconnect.model.person.Remark;
import seedu.insureconnect.model.tag.Tag;
import seedu.insureconnect.testutil.EditPersonDescriptorBuilder;

public class EditCommandParserTest {

    private static final String TAG_EMPTY = " " + PREFIX_TAG;

    private EditCommandParser parser = new EditCommandParser();

    @Test
    public void parse_missingParts_failure() {
        // no index specified
        assertParseFailure(parser, VALID_NAME_AMY, MESSAGE_INVALID_INDEX);
        // no field specified
        assertParseFailure(parser, "1", EditCommand.MESSAGE_NOT_EDITED);

        // no index and no field specified
        assertParseFailure(parser, "", MESSAGE_INVALID_INDEX);
    }

    @Test
    public void parse_invalidPreamble_failure() {
        // negative index
        assertParseFailure(parser, "-5" + NAME_DESC_AMY, MESSAGE_INVALID_INDEX);

        // zero index
        assertParseFailure(parser, "0" + NAME_DESC_AMY, MESSAGE_INVALID_INDEX);

        // invalid arguments being parsed as preamble
        assertParseFailure(parser, "1 some random string", MESSAGE_INVALID_INDEX);

        // invalid prefix being parsed as preamble
        assertParseFailure(parser, "1 i/ string", MESSAGE_INVALID_INDEX);
    }

    @Test
    public void parse_invalidValue_failure() {
        assertParseFailure(parser, "1" + INVALID_NAME_DESC, Name.MESSAGE_CONSTRAINTS); // invalid name
        assertParseFailure(parser, "1" + INVALID_PHONE_DESC, Phone.MESSAGE_CONSTRAINTS); // invalid phone
        assertParseFailure(parser, "1" + INVALID_EMAIL_DESC, Email.MESSAGE_CONSTRAINTS); // invalid email
        assertParseFailure(parser, "1" + INVALID_ADDRESS_DESC, Address.MESSAGE_CONSTRAINTS); // invalid address
        assertParseFailure(parser, "1 " + PREFIX_REMARK + "a".repeat(501), Remark.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "1" + INVALID_TAG_DESC, Tag.MESSAGE_CONSTRAINTS); // invalid tag

        // invalid phone followed by valid email
        assertParseFailure(parser, "1" + INVALID_PHONE_DESC + EMAIL_DESC_AMY, Phone.MESSAGE_CONSTRAINTS);

        // while parsing {@code PREFIX_TAG} alone will reset the tags of the {@code Person} being edited,
        // parsing it together with a valid tag results in error
        assertParseFailure(parser, "1" + TAG_DESC_FRIEND + TAG_DESC_HUSBAND + TAG_EMPTY, Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "1" + TAG_DESC_FRIEND + TAG_EMPTY + TAG_DESC_HUSBAND, Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "1" + TAG_EMPTY + TAG_DESC_FRIEND + TAG_DESC_HUSBAND, Tag.MESSAGE_CONSTRAINTS);

        // multiple invalid values, but only the first invalid value is captured
        assertParseFailure(parser, "1" + INVALID_NAME_DESC + INVALID_EMAIL_DESC + VALID_ADDRESS_AMY + VALID_PHONE_AMY,
                Name.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_allFieldsSpecified_success() {
        Index targetIndex = INDEX_SECOND_PERSON;
        String userInput = targetIndex.getOneBased() + PHONE_DESC_BOB + TAG_DESC_HUSBAND
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY + NAME_DESC_AMY + TAG_DESC_FRIEND + REMARK_DESC;

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_AMY)
                .withPhone(VALID_PHONE_BOB).withEmail(VALID_EMAIL_AMY).withAddress(VALID_ADDRESS_AMY)
                .withTags(VALID_TAG_HUSBAND, VALID_TAG_FRIEND).withRemark(VALID_REMARK).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_someFieldsSpecified_success() {
        Index targetIndex = INDEX_FIRST_PERSON;
        String userInput = targetIndex.getOneBased() + PHONE_DESC_BOB + EMAIL_DESC_AMY;

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withPhone(VALID_PHONE_BOB)
                .withEmail(VALID_EMAIL_AMY).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_oneFieldSpecified_success() {
        // name
        Index targetIndex = INDEX_THIRD_PERSON;
        String userInput = targetIndex.getOneBased() + NAME_DESC_AMY;
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_AMY).build();
        EditCommand expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // name with escape character
        userInput = targetIndex.getOneBased() + " n/Mohd !a/l Kassim";
        descriptor = new EditPersonDescriptorBuilder().withName("Mohd a/l Kassim").build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // EP: name uses precomposed and decomposed Latin accents with a curly apostrophe
        String unicodeName = "José O\u0308zdemir D’Angelo";
        userInput = targetIndex.getOneBased() + " n/" + unicodeName;
        descriptor = new EditPersonDescriptorBuilder().withName(unicodeName).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // phone
        userInput = targetIndex.getOneBased() + PHONE_DESC_AMY;
        descriptor = new EditPersonDescriptorBuilder().withPhone(VALID_PHONE_AMY).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // email
        userInput = targetIndex.getOneBased() + EMAIL_DESC_AMY;
        descriptor = new EditPersonDescriptorBuilder().withEmail(VALID_EMAIL_AMY).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // address
        userInput = targetIndex.getOneBased() + ADDRESS_DESC_AMY;
        descriptor = new EditPersonDescriptorBuilder().withAddress(VALID_ADDRESS_AMY).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // tags
        userInput = targetIndex.getOneBased() + TAG_DESC_FRIEND;
        descriptor = new EditPersonDescriptorBuilder().withTags(VALID_TAG_FRIEND).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // remark
        userInput = targetIndex.getOneBased() + REMARK_DESC;
        descriptor = new EditPersonDescriptorBuilder().withRemark(VALID_REMARK).build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // empty remark clears the existing remark
        userInput = targetIndex.getOneBased() + " " + PREFIX_REMARK;
        descriptor = new EditPersonDescriptorBuilder().withRemark("").build();
        expectedCommand = new EditCommand(targetIndex, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_remarkWhitespaceNormalized_success() {
        String userInput = INDEX_FIRST_PERSON.getOneBased() + " " + PREFIX_REMARK + "  Follow  up  tomorrow  ";
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withRemark("Follow up tomorrow").build();

        assertParseSuccess(parser, userInput, new EditCommand(INDEX_FIRST_PERSON, descriptor));
    }

    @Test
    public void parse_remarkEscapedPrefixAndExclamation_success() {
        // EP: escaped prefix-like text and a doubled exclamation mark are unescaped in an edited remark
        String userInput = INDEX_FIRST_PERSON.getOneBased() + " " + PREFIX_REMARK
                + "dietary restrictions: !n/a Follow up!!";
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withRemark("dietary restrictions: n/a Follow up!").build();

        assertParseSuccess(parser, userInput, new EditCommand(INDEX_FIRST_PERSON, descriptor));
    }

    @Test
    public void parse_addressWithEscapedPrefixAndExclamation_success() {
        String userInput = INDEX_FIRST_PERSON.getOneBased() + " " + PREFIX_ADDRESS + "Blk 123 !t/4!!";
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withAddress("Blk 123 t/4!").build();
        assertParseSuccess(parser, userInput, new EditCommand(INDEX_FIRST_PERSON, descriptor));
    }

    @Test
    public void parse_addressLengthBoundary() {
        String maxAddress = "a".repeat(Address.MAX_LENGTH);
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withAddress(maxAddress).build();
        assertParseSuccess(parser, INDEX_FIRST_PERSON.getOneBased() + " " + PREFIX_ADDRESS + maxAddress,
                new EditCommand(INDEX_FIRST_PERSON, descriptor));

        String tooLongAddress = "a".repeat(Address.MAX_LENGTH + 1);
        assertParseFailure(parser, INDEX_FIRST_PERSON.getOneBased() + " " + PREFIX_ADDRESS + tooLongAddress,
                Address.MESSAGE_LENGTH_CONSTRAINTS);
    }

    @Test
    public void parse_multipleRepeatedFields_failure() {
        // More extensive testing of duplicate parameter detections is done in
        // AddCommandParserTest#parse_repeatedNonTagValue_failure()

        // valid followed by invalid
        Index targetIndex = INDEX_FIRST_PERSON;
        String userInput = targetIndex.getOneBased() + INVALID_PHONE_DESC + PHONE_DESC_BOB;

        assertParseFailure(parser, userInput, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // repeated remark
        userInput = targetIndex.getOneBased() + REMARK_DESC + REMARK_DESC;
        assertParseFailure(parser, userInput, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_REMARK));

        // invalid followed by valid
        userInput = targetIndex.getOneBased() + PHONE_DESC_BOB + INVALID_PHONE_DESC;

        assertParseFailure(parser, userInput, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // multiple valid fields repeated
        userInput = targetIndex.getOneBased() + PHONE_DESC_AMY + ADDRESS_DESC_AMY + EMAIL_DESC_AMY
                + TAG_DESC_FRIEND + PHONE_DESC_AMY + ADDRESS_DESC_AMY + EMAIL_DESC_AMY + TAG_DESC_FRIEND
                + PHONE_DESC_BOB + ADDRESS_DESC_BOB + EMAIL_DESC_BOB + TAG_DESC_HUSBAND;

        assertParseFailure(parser, userInput,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS));

        // multiple invalid values
        userInput = targetIndex.getOneBased() + INVALID_PHONE_DESC + INVALID_ADDRESS_DESC + INVALID_EMAIL_DESC
                + INVALID_PHONE_DESC + INVALID_ADDRESS_DESC + INVALID_EMAIL_DESC;

        assertParseFailure(parser, userInput,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS));
    }

    @Test
    public void parse_resetTags_failure() {
        Index targetIndex = INDEX_THIRD_PERSON;
        String userInput = targetIndex.getOneBased() + TAG_EMPTY;

        assertParseFailure(parser, userInput, Tag.MESSAGE_CONSTRAINTS);
    }
}
