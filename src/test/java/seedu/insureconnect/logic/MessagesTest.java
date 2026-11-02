package seedu.insureconnect.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.insureconnect.logic.Messages.MESSAGE_UNKNOWN_COMMAND;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.insureconnect.logic.parser.Prefix;
import seedu.insureconnect.model.person.Person;
import seedu.insureconnect.testutil.PersonBuilder;

public class MessagesTest {

    @Test
    public void format_personWithRemark_includesRemark() {
        Person person = new PersonBuilder()
                .withRemark("Prefers WhatsApp after 6pm")
                .build();

        assertTrue(Messages.format(person)
                .contains("; Remark: Prefers WhatsApp after 6pm"));
    }
    @Test
    public void missingFieldsAndPreamble_formatsEachCombination() {
        assertEquals("Invalid command format!\nMissing required field: name.\nusage",
                Messages.getErrorMessageForMissingFieldsAndPreamble(List.of("name"), "", "usage"));
        assertEquals("Invalid command format!\nMissing required fields: name, phone. "
                + "Unexpected text before the first field: \"extra\".\nusage",
                Messages.getErrorMessageForMissingFieldsAndPreamble(List.of("name", "phone"), "extra", "usage"));
        assertEquals("Invalid command format!\nUnexpected text before the first field: \"extra\".\nusage",
                Messages.getErrorMessageForMissingFieldsAndPreamble(List.of(), "extra", "usage"));
    }

    @Test
    public void duplicatePrefixes_deduplicatesAndRequiresAtLeastOneField() {
        assertEquals(Messages.MESSAGE_DUPLICATE_FIELDS + "p/",
                Messages.getErrorMessageForDuplicatePrefixes(new Prefix("p/"), new Prefix("p/")));
        assertThrows(AssertionError.class, Messages::getErrorMessageForDuplicatePrefixes);
    }

    @Test
    public void invalidPersonDisplayedIndex_nonEmptyList_includesValidRange() {
        assertEquals("The customer index must be a positive integer within "
                        + "the currently displayed range (1 to 5).",
                Messages.getInvalidPersonDisplayedIndexMessage(5));
    }

    @Test
    public void invalidPersonDisplayedIndex_emptyList_reportsNoDisplayedCustomers() {
        assertEquals("The customer index must be a positive integer, "
                        + "but no customers are currently displayed.",
                Messages.getInvalidPersonDisplayedIndexMessage(0));
    }

    @Test
    public void invalidPersonDisplayedIndex_negativeListSize_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> Messages.getInvalidPersonDisplayedIndexMessage(-1));
    }

    @Test
    public void unknownCommandMessage_includesHelpCommandAndKeyboardShortcut() {
        assertEquals(
                "Unknown command. Type help or press F1 to see the available commands.",
                MESSAGE_UNKNOWN_COMMAND);
    }
}
