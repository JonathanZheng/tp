package seedu.insureconnect.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.insureconnect.logic.Messages.MESSAGE_PERSONS_LISTED_OVERVIEW;
import static seedu.insureconnect.testutil.Assert.assertThrows;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.insureconnect.commons.core.index.Index;
import seedu.insureconnect.logic.commands.exceptions.CommandException;
import seedu.insureconnect.logic.parser.InsureConnectParser;
import seedu.insureconnect.logic.parser.exceptions.ParseException;
import seedu.insureconnect.model.InsureConnect;
import seedu.insureconnect.model.Model;
import seedu.insureconnect.model.ModelManager;
import seedu.insureconnect.model.UserPrefs;
import seedu.insureconnect.model.person.NameContainsKeywordsPredicate;
import seedu.insureconnect.model.person.Person;
import seedu.insureconnect.model.person.Phone;
import seedu.insureconnect.model.person.PhoneStartsWithPredicate;
import seedu.insureconnect.testutil.PersonBuilder;

/**
 * Tests phone search from command text through the model, including subsequent indexed operations.
 */
public class FindPhoneCommandTest {
    private final Person johnny = new PersonBuilder().withName("Johnny Lim").withPhone("98765432")
            .withTags("P1").build();
    private final Person john = new PersonBuilder().withName("John Tan").withPhone("98765432")
            .withTags("P2").build();
    private final Person alice = new PersonBuilder().withName("Alice Lee").withPhone("12349876")
            .withTags("P3").build();
    private final Person internationalCustomer = new PersonBuilder()
            .withName("Jane International")
            .withTags("P4")
            .withPhone("+6591234567")
            .build();
    private final List<Person> storedPersons = List.of(alice, johnny, john, internationalCustomer);
    private Model model;

    /**
     * Includes shared phone numbers, a suffix-only number, and reversed name-match quality.
     */
    @BeforeEach
    public void setUp() {
        InsureConnect data = new InsureConnect();
        data.setPersons(storedPersons);
        model = new ModelManager(data, new UserPrefs());
    }

    /**
     * Phone prefixes return every matching customer in stored order and preserve stored data.
     */
    @Test
    public void execute_phonePrefix_returnsAllMatchingCustomers()
            throws ParseException, CommandException {
        assertSearch("find p/9876", List.of(johnny, john));
        assertSearch("find p/98765432", List.of(johnny, john));
        assertSearch("find p/9", List.of(johnny, john));
        assertSearch("find p/+65", List.of(internationalCustomer));

        assertEquals(
                storedPersons,
                model.getInsureConnect().getPersonList());
    }

    /**
     * Missing prefixes and phone suffixes produce zero results.
     */
    @Test
    public void execute_nonMatchingPhone_returnsNoCustomers() throws ParseException, CommandException {
        assertSearch("find p/5432", List.of());
        assertSearch("find p/987654321", List.of());
    }

    /**
     * Switching between search modes replaces both the filter and relevance order.
     */
    @Test
    public void execute_switchSearchModes_replacesResultsAndRanking() throws ParseException, CommandException {
        assertSearch("find John", List.of(john, johnny));
        assertSearch("find p/9876", List.of(johnny, john));
        assertSearch("find John", List.of(john, johnny));
        assertSearch("find p/123", List.of(alice));
        assertSearch("find 9876", List.of());
    }

    /**
     * Phone searching resets explicit sorting, and listing subsequently restores the full data.
     */
    @Test
    public void execute_afterSortingAndBeforeListing_preservesExpectedOrder() throws ParseException, CommandException {
        new ListCommand(ListCommand.SortOrder.NAME).execute(model);
        assertSearch("find p/9876", List.of(johnny, john));
        new ListCommand().execute(model);
        assertEquals(storedPersons, model.getFilteredPersonList());
    }

    /**
     * Invalid input leaves the previously displayed search results and data intact.
     */
    @Test
    public void execute_invalidPhoneQuery_preservesModel() throws ParseException, CommandException {
        assertSearch("find John", List.of(john, johnny));
        assertThrows(ParseException.class, () -> new InsureConnectParser().parseCommand("find p/abc"));
        assertEquals(List.of(john, johnny), model.getFilteredPersonList());
        assertEquals(storedPersons, model.getInsureConnect().getPersonList());
    }

    /**
     * Delete uses the displayed phone-search index instead of the storage index.
     */
    @Test
    public void execute_deletePhoneResult_deletesDisplayedCustomer() throws ParseException, CommandException {
        assertSearch("find p/9876", List.of(johnny, john));
        new DeleteCommand(Index.fromOneBased(1)).confirmed().execute(model);
        assertFalse(model.hasPerson(johnny));
        assertTrue(model.hasPerson(alice));
        assertEquals(List.of(john), model.getFilteredPersonList());
    }

    /**
     * Edit uses the displayed phone-search index and keeps other customers unchanged.
     */
    @Test
    public void execute_editPhoneResult_editsDisplayedCustomer() throws ParseException, CommandException {
        assertSearch("find p/9876", List.of(johnny, john));
        EditCommand.EditPersonDescriptor descriptor = new EditCommand.EditPersonDescriptor();
        descriptor.setPhone(new Phone("91234567"));
        new EditCommand(Index.fromOneBased(1), descriptor).execute(model);
        assertTrue(model.hasPerson(new PersonBuilder(johnny).withPhone("91234567").build()));
        assertFalse(model.hasPerson(johnny));
        assertTrue(model.hasPerson(alice));
    }

    /**
     * Commands compare their search mode and value, and reject null matching rules.
     */
    @Test
    public void equals_phoneSearchVariants_distinguishesSearchModes() {
        FindCommand command = new FindCommand(new PhoneStartsWithPredicate("9876"));
        // EP: same phone prefix
        assertEquals(command, new FindCommand(new PhoneStartsWithPredicate("9876")));
        // EP: different phone prefix
        assertFalse(command.equals(new FindCommand(new PhoneStartsWithPredicate("1234"))));
        // EP: same text searched as a name
        assertFalse(command.equals(new FindCommand(new NameContainsKeywordsPredicate(List.of("9876")))));
    }

    @Test
    public void constructor_nullPredicate_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new FindCommand(null));
    }

    /**
     * Executes through the application parser and checks explicit results and their reported count.
     */
    private void assertSearch(String input, List<Person> expectedPersons) throws ParseException, CommandException {
        CommandResult result = new InsureConnectParser().parseCommand(input).execute(model);
        assertEquals(expectedPersons, model.getFilteredPersonList());
        assertEquals(String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, expectedPersons.size()),
                result.getFeedbackToUser());
    }
}
