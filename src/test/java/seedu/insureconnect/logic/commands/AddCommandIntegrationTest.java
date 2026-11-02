package seedu.insureconnect.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.insureconnect.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.insureconnect.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.insureconnect.testutil.TypicalPersons.getTypicalInsureConnect;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.insureconnect.logic.Messages;
import seedu.insureconnect.logic.commands.exceptions.CommandException;
import seedu.insureconnect.model.Model;
import seedu.insureconnect.model.ModelManager;
import seedu.insureconnect.model.UserPrefs;
import seedu.insureconnect.model.person.Person;
import seedu.insureconnect.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) for {@code AddCommand}.
 */
public class AddCommandIntegrationTest {

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalInsureConnect(), new UserPrefs());
    }

    @Test
    public void execute_newPerson_success() {
        Person validPerson = new PersonBuilder().build();

        Model expectedModel = new ModelManager(model.getInsureConnect(), new UserPrefs());
        expectedModel.addPerson(validPerson);

        assertCommandSuccess(new AddCommand(validPerson), model,
                String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(validPerson)),
                expectedModel);
    }

    @Test
    public void execute_duplicatePerson_throwsCommandException() {
        Person personInList = model.getInsureConnect().getPersonList().get(0);
        int displayedIndex = model.getFilteredPersonList().indexOf(personInList) + 1;
        assertCommandFailure(new AddCommand(personInList), model,
                String.format(AddCommand.MESSAGE_DUPLICATE_PERSON, displayedIndex,
                        personInList.getName().fullName, personInList.getPhone().value));
    }

    @Test
    public void execute_sameNameDifferentPhone_success() {
        Person personInList = model.getInsureConnect().getPersonList().get(0);
        Person newPerson = new PersonBuilder(personInList).withPhone("88888888")
                .withTags("LIFE88888").build();
        Model expectedModel = new ModelManager(model.getInsureConnect(), new UserPrefs());
        expectedModel.addPerson(newPerson);

        assertCommandSuccess(new AddCommand(newPerson), model,
                String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(newPerson)), expectedModel);
    }

    @Test
    public void execute_policyNumberBelongsToAnotherCustomer_failure() {
        Person owner = model.getInsureConnect().getPersonList().get(0);
        String policyNumber = owner.getTags().iterator().next().tagName;
        Person newPerson = new PersonBuilder().withTags(policyNumber).build();

        assertCommandFailure(new AddCommand(newPerson), model,
                String.format(AddCommand.MESSAGE_DUPLICATE_POLICY_NUMBER,
                        policyNumber, owner.getName().fullName, owner.getPhone().value));
    }

    @Test
    public void execute_normalizedNameAndSamePhone_throwsCommandException() {
        Person personInList = model.getInsureConnect().getPersonList().get(0);
        Person duplicatePerson = new PersonBuilder(personInList).withName("aLICE   pAULINE  ")
                .withEmail("different@example.com").build();

        int displayedIndex = model.getFilteredPersonList().indexOf(personInList) + 1;
        assertCommandFailure(new AddCommand(duplicatePerson), model,
                String.format(AddCommand.MESSAGE_DUPLICATE_PERSON, displayedIndex,
                        personInList.getName().fullName, personInList.getPhone().value));
    }

    @Test
    public void execute_canonicallyEquivalentNameAndSamePhone_throwsCommandException() {
        Person original = new PersonBuilder().withName("José").withPhone("81234567")
                .withTags("NFC1").build();
        model.addPerson(original);
        Person decomposedName = new PersonBuilder(original).withName("Jose\u0301")
                .withEmail("different@example.com").withTags("NFC2").build();

        int displayedIndex = model.getFilteredPersonList().indexOf(original) + 1;
        // EP: canonically equivalent name and matching phone is rejected despite other fields differing
        assertCommandFailure(new AddCommand(decomposedName), model,
                String.format(AddCommand.MESSAGE_DUPLICATE_PERSON, displayedIndex,
                        original.getName().fullName, original.getPhone().value));
    }

    @Test
    public void execute_duplicatePersonHiddenByFilter_clearsFilterAndReportsDisplayedIndex() {
        Person duplicate = model.getInsureConnect().getPersonList().get(0);
        model.updatePersonListComparator((first, second) -> second.getName().fullName
                .compareToIgnoreCase(first.getName().fullName));
        List<Person> fullDisplayedList = List.copyOf(model.getFilteredPersonList());
        int expectedIndex = fullDisplayedList.indexOf(duplicate) + 1;
        model.updateFilteredPersonList(person -> !person.isSamePerson(duplicate));

        CommandException exception = assertThrows(CommandException.class, () -> new AddCommand(duplicate)
                .execute(model));

        assertEquals(String.format(AddCommand.MESSAGE_DUPLICATE_PERSON, expectedIndex,
                duplicate.getName().fullName, duplicate.getPhone().value), exception.getMessage());
        assertEquals(fullDisplayedList, model.getFilteredPersonList());
        assertEquals(getTypicalInsureConnect(), model.getInsureConnect());
    }

}
