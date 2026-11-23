package seedu.insureconnect.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static seedu.insureconnect.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.insureconnect.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.insureconnect.logic.commands.ListCommand.SortOrder.CREATED_AT;
import static seedu.insureconnect.logic.commands.ListCommand.SortOrder.NAME;
import static seedu.insureconnect.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.insureconnect.testutil.TypicalPersons.getTypicalInsureConnect;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.insureconnect.model.InsureConnect;
import seedu.insureconnect.model.Model;
import seedu.insureconnect.model.ModelManager;
import seedu.insureconnect.model.UserPrefs;
import seedu.insureconnect.model.person.NameContainsKeywordsPredicate;
import seedu.insureconnect.model.person.Person;
import seedu.insureconnect.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for ListCommand.
 */
public class ListCommandTest {

    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalInsureConnect(), new UserPrefs());
        expectedModel = new ModelManager(model.getInsureConnect(), new UserPrefs());
    }

    @Test
    public void execute_listIsNotFiltered_showsSameListAndCustomerCount() {
        String expectedMessage = "Listed all "
                + expectedModel.getFilteredPersonList().size()
                + " customer(s).";
        assertCommandSuccess(new ListCommand(), model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_listIsFiltered_showsEverythingAndCustomerCount() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        String expectedMessage = "Listed all "
                + expectedModel.getFilteredPersonList().size()
                + " customer(s).";
        assertCommandSuccess(new ListCommand(), model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_emptyList_displaysZeroCustomerCount() {
        model = new ModelManager(new InsureConnect(), new UserPrefs());
        expectedModel = new ModelManager(new InsureConnect(), new UserPrefs());

        String expectedMessage = "Listed all 0 customer(s).";

        assertCommandSuccess(new ListCommand(), model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_sortVariants_changeDisplayOrderWithoutChangingStoredOrder() {
        Person zoe = new PersonBuilder().withName("Zoe").withPhone("11111111")
                .withCreatedAt(Instant.parse("2026-03-01T00:00:00Z")).build();
        Person alice = new PersonBuilder().withName("alice").withPhone("22222222")
                .withCreatedAt(Instant.parse("2026-02-01T00:00:00Z")).build();
        Person bob = new PersonBuilder().withName("Bob").withPhone("33333333")
                .withCreatedAt(Instant.parse("2026-01-01T00:00:00Z")).build();
        InsureConnect data = new InsureConnect();
        data.setPersons(List.of(zoe, alice, bob));
        Model sortedModel = new ModelManager(data, new UserPrefs());

        new ListCommand(NAME).execute(sortedModel);
        assertEquals(List.of(alice, bob, zoe), sortedModel.getFilteredPersonList());

        new ListCommand(CREATED_AT).execute(sortedModel);
        assertEquals(List.of(bob, alice, zoe), sortedModel.getFilteredPersonList());
        assertEquals(List.of(zoe, alice, bob), sortedModel.getInsureConnect().getPersonList());

        new FindCommand(new NameContainsKeywordsPredicate(List.of("Zoe", "alice", "Bob")))
                .execute(sortedModel);
        assertEquals(List.of(zoe, alice, bob), sortedModel.getFilteredPersonList());

        new ListCommand(NAME).execute(sortedModel);
        new ListCommand().execute(sortedModel);
        assertEquals(List.of(zoe, alice, bob), sortedModel.getFilteredPersonList());
    }
    @Test
    public void equals_variousObjects_distinguishesSortOrders() {
        ListCommand command = new ListCommand();
        // EP: same sort order
        assertEquals(command, command);
        assertEquals(command, new ListCommand());
        // EP: different sort order
        assertNotEquals(command, new ListCommand(NAME));
        // EP: null or another type
        assertFalse(command.equals(null));
        assertFalse(command.equals("list"));
    }
}
