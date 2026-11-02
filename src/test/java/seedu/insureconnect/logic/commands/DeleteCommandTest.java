package seedu.insureconnect.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.insureconnect.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.insureconnect.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.insureconnect.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.insureconnect.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.insureconnect.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.insureconnect.testutil.TypicalPersons.getTypicalInsureConnect;

import org.junit.jupiter.api.Test;

import seedu.insureconnect.commons.core.index.Index;
import seedu.insureconnect.logic.Messages;
import seedu.insureconnect.model.Model;
import seedu.insureconnect.model.ModelManager;
import seedu.insureconnect.model.UserPrefs;
import seedu.insureconnect.model.person.Person;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code DeleteCommand}.
 */
public class DeleteCommandTest {

    private Model model = new ModelManager(getTypicalInsureConnect(), new UserPrefs());

    @Test
    public void execute_validIndexWithoutConfirmation_showsConfirmationAndDoesNotDelete() throws Exception {
        Person personToDelete = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        DeleteCommand deleteCommand = new DeleteCommand(INDEX_FIRST_PERSON);

        String expectedMessage = String.format(DeleteCommand.MESSAGE_CONFIRM_DELETE, Messages.format(personToDelete));

        Model expectedModel = new ModelManager(model.getInsureConnect(), new UserPrefs());

        CommandResult result = deleteCommand.execute(model);

        assertEquals(expectedMessage, result.getFeedbackToUser());
        assertTrue(result.isConfirmationRequired());
        assertEquals(expectedModel, model);
    }

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Person personToDelete = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        DeleteCommand deleteCommand = new DeleteCommand(INDEX_FIRST_PERSON).confirmed();

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                Messages.format(personToDelete));

        ModelManager expectedModel = new ModelManager(model.getInsureConnect(), new UserPrefs());
        expectedModel.deletePerson(personToDelete);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        DeleteCommand deleteCommand = new DeleteCommand(outOfBoundIndex);

        assertCommandFailure(deleteCommand, model,
            Messages.getInvalidPersonDisplayedIndexMessage(model.getFilteredPersonList().size()));
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person personToDelete = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        DeleteCommand deleteCommand = new DeleteCommand(INDEX_FIRST_PERSON).confirmed();

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                Messages.format(personToDelete));

        Model expectedModel = new ModelManager(model.getInsureConnect(), new UserPrefs());
        expectedModel.deletePerson(personToDelete);
        showNoPerson(expectedModel);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of InsureConnect list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getInsureConnect().getPersonList().size());

        DeleteCommand deleteCommand = new DeleteCommand(outOfBoundIndex);

        assertCommandFailure(deleteCommand, model,
            Messages.getInvalidPersonDisplayedIndexMessage(model.getFilteredPersonList().size()));
    }

    @Test
    public void equals() {
        DeleteCommand deleteFirstCommand = new DeleteCommand(INDEX_FIRST_PERSON);
        DeleteCommand deleteSecondCommand = new DeleteCommand(INDEX_SECOND_PERSON);
        DeleteCommand confirmedDeleteFirstCommand = new DeleteCommand(INDEX_FIRST_PERSON).confirmed();

        // same object -> returns true
        assertTrue(deleteFirstCommand.equals(deleteFirstCommand));

        // same values -> returns true
        DeleteCommand deleteFirstCommandCopy = new DeleteCommand(INDEX_FIRST_PERSON);
        assertTrue(deleteFirstCommand.equals(deleteFirstCommandCopy));

        // different types -> returns false
        assertFalse(deleteFirstCommand.equals(1));

        // null -> returns false
        assertFalse(deleteFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(deleteFirstCommand.equals(deleteSecondCommand));

        // same index but different confirmation state -> returns false
        assertFalse(deleteFirstCommand.equals(confirmedDeleteFirstCommand));
    }

    @Test
    public void toStringMethod() {
        Index targetIndex = Index.fromOneBased(1);
        DeleteCommand deleteCommand = new DeleteCommand(targetIndex);
        String expected = DeleteCommand.class.getCanonicalName()
                + "{targetIndex=" + targetIndex + ", isConfirmed=false}";
        assertEquals(expected, deleteCommand.toString());
    }

    /**
     * Updates {@code model}'s filtered list to show no one.
     */
    private void showNoPerson(Model model) {
        model.updateFilteredPersonList(p -> false);

        assertTrue(model.getFilteredPersonList().isEmpty());
    }
}
