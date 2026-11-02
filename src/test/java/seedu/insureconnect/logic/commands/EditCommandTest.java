package seedu.insureconnect.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.insureconnect.logic.commands.CommandTestUtil.DESC_AMY;
import static seedu.insureconnect.logic.commands.CommandTestUtil.DESC_BOB;
import static seedu.insureconnect.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.insureconnect.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.insureconnect.logic.commands.CommandTestUtil.VALID_REMARK;
import static seedu.insureconnect.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.insureconnect.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.insureconnect.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.insureconnect.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.insureconnect.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.insureconnect.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.insureconnect.testutil.TypicalPersons.getTypicalInsureConnect;

import java.util.Locale;

import org.junit.jupiter.api.Test;

import seedu.insureconnect.commons.core.index.Index;
import seedu.insureconnect.logic.Messages;
import seedu.insureconnect.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.insureconnect.model.InsureConnect;
import seedu.insureconnect.model.Model;
import seedu.insureconnect.model.ModelManager;
import seedu.insureconnect.model.UserPrefs;
import seedu.insureconnect.model.person.Person;
import seedu.insureconnect.testutil.EditPersonDescriptorBuilder;
import seedu.insureconnect.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for EditCommand.
 */
public class EditCommandTest {

    private Model model = new ModelManager(getTypicalInsureConnect(), new UserPrefs());

    @Test
    public void execute_allFieldsSpecifiedUnfilteredList_success() {
        Person editedPerson = new PersonBuilder().build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(editedPerson).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new InsureConnect(model.getInsureConnect()), new UserPrefs());
        expectedModel.setPerson(model.getFilteredPersonList().get(0), editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_someFieldsSpecifiedUnfilteredList_success() {
        Index indexLastPerson = Index.fromOneBased(model.getFilteredPersonList().size());
        Person lastPerson = model.getFilteredPersonList().get(indexLastPerson.getZeroBased());

        PersonBuilder personInList = new PersonBuilder(lastPerson);
        Person editedPerson = personInList.withName(VALID_NAME_BOB).withPhone(VALID_PHONE_BOB)
                .withTags(VALID_TAG_HUSBAND).build();

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB)
                .withPhone(VALID_PHONE_BOB).withTags(VALID_TAG_HUSBAND).build();
        EditCommand editCommand = new EditCommand(indexLastPerson, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new InsureConnect(model.getInsureConnect()), new UserPrefs());
        expectedModel.setPerson(lastPerson, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_remarkOnly_success() {
        Person personToEdit = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(personToEdit).withRemark(VALID_REMARK).build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withRemark(VALID_REMARK).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, descriptor);

        Model expectedModel = new ModelManager(new InsureConnect(model.getInsureConnect()), new UserPrefs());
        expectedModel.setPerson(personToEdit, editedPerson);

        assertCommandSuccess(editCommand, model,
                String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson)), expectedModel);
    }

    @Test
    public void execute_emptyRemark_clearsExistingRemark() {
        Person originalPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person personWithRemark = new PersonBuilder(originalPerson).withRemark(VALID_REMARK).build();
        model.setPerson(originalPerson, personWithRemark);

        Person editedPerson = new PersonBuilder(personWithRemark).withRemark("").build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withRemark("").build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, descriptor);

        Model expectedModel = new ModelManager(new InsureConnect(model.getInsureConnect()), new UserPrefs());
        expectedModel.setPerson(personWithRemark, editedPerson);

        assertCommandSuccess(editCommand, model,
                String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson)), expectedModel);
    }

    @Test
    public void execute_remarkOmitted_preservesExistingRemark() {
        Person originalPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person personWithRemark = new PersonBuilder(originalPerson).withRemark(VALID_REMARK).build();
        model.setPerson(originalPerson, personWithRemark);

        Person editedPerson = new PersonBuilder(personWithRemark).withPhone(VALID_PHONE_BOB).build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withPhone(VALID_PHONE_BOB).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, descriptor);

        Model expectedModel = new ModelManager(new InsureConnect(model.getInsureConnect()), new UserPrefs());
        expectedModel.setPerson(personWithRemark, editedPerson);

        assertCommandSuccess(editCommand, model,
                String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson)), expectedModel);
    }

    @Test
    public void execute_noFieldSpecifiedUnfilteredList_success() {
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, new EditPersonDescriptor());
        Person editedPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new InsureConnect(model.getInsureConnect()), new UserPrefs());

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_filteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person personInFilteredList = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(personInFilteredList).withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new InsureConnect(model.getInsureConnect()), new UserPrefs());
        expectedModel.setPerson(model.getFilteredPersonList().get(0), editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_duplicatePersonUnfilteredList_failure() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(firstPerson).build();
        EditCommand editCommand = new EditCommand(INDEX_SECOND_PERSON, descriptor);

        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_duplicatePersonFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        // edit person in filtered list into a duplicate in InsureConnect
        Person personInList = model.getInsureConnect().getPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder(personInList).build());

        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_invalidPersonIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(outOfBoundIndex, descriptor);

        assertCommandFailure(editCommand, model,
            Messages.getInvalidPersonDisplayedIndexMessage(model.getFilteredPersonList().size()));
    }

    /**
     * Edit filtered list where index is larger than size of filtered list,
     * but smaller than size of InsureConnect
     */
    @Test
    public void execute_invalidPersonIndexFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of InsureConnect list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getInsureConnect().getPersonList().size());

        EditCommand editCommand = new EditCommand(outOfBoundIndex,
                new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build());

        assertCommandFailure(editCommand, model,
            Messages.getInvalidPersonDisplayedIndexMessage(model.getFilteredPersonList().size()));
    }

    @Test
    public void execute_sameNameDifferentPhone_success() {
        Person firstPerson = model.getFilteredPersonList().get(0);
        Person secondPerson = model.getFilteredPersonList().get(1);
        Person editedPerson = new PersonBuilder(secondPerson).withName(firstPerson.getName().fullName).build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withName(firstPerson.getName().fullName).build();
        Model expectedModel = new ModelManager(model.getInsureConnect(), new UserPrefs());
        expectedModel.setPerson(secondPerson, editedPerson);

        assertCommandSuccess(new EditCommand(INDEX_SECOND_PERSON, descriptor), model,
                String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson)), expectedModel);
    }

    @Test
    public void execute_policyNumberBelongsToAnotherCustomer_failure() {
        Person owner = model.getInsureConnect().getPersonList().get(0);
        String policyNumber = owner.getTags().iterator().next().tagName;
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withTags(policyNumber).build();

        assertCommandFailure(new EditCommand(INDEX_SECOND_PERSON, descriptor), model,
                String.format(AddCommand.MESSAGE_DUPLICATE_POLICY_NUMBER,
                        policyNumber, owner.getName().fullName, owner.getPhone().value));
    }

    @Test
    public void execute_policyNumberDiffersOnlyByCase_failure() {
        Person policyOwner = model.getInsureConnect().getPersonList().get(0);
        String existingPolicyNumber = policyOwner.getTags().iterator().next().tagName;
        String differentlyCasedPolicyNumber =
                existingPolicyNumber.toLowerCase(Locale.ROOT);

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withTags(differentlyCasedPolicyNumber)
                .build();

        assertCommandFailure(
                new EditCommand(INDEX_SECOND_PERSON, descriptor),
                model,
                String.format(
                        AddCommand.MESSAGE_DUPLICATE_POLICY_NUMBER,
                        existingPolicyNumber,
                        policyOwner.getName().fullName,
                        policyOwner.getPhone().value));
    }

    @Test
    public void execute_removeAllPolicyNumbers_failure() {
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withTags().build();

        assertCommandFailure(new EditCommand(INDEX_FIRST_PERSON, descriptor), model,
                Person.MESSAGE_MISSING_POLICY_NUMBER);
    }

    @Test
    public void execute_phoneChangeCreatesDuplicate_failure() {
        Person firstPerson = model.getFilteredPersonList().get(0);
        Person secondPerson = model.getFilteredPersonList().get(1);
        Person sameNamePerson = new PersonBuilder(secondPerson).withName("aLICE   pAULINE  ").build();
        model.setPerson(secondPerson, sameNamePerson);
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder()
                .withPhone(firstPerson.getPhone().value).build();

        assertCommandFailure(new EditCommand(INDEX_SECOND_PERSON, descriptor), model,
                EditCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_normalizedDuplicateOutsideFilteredList_failure() {
        showPersonAtIndex(model, INDEX_SECOND_PERSON);
        Person firstPerson = model.getInsureConnect().getPersonList().get(0);
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName("aLICE   pAULINE  ")
                .withPhone(firstPerson.getPhone().value).build();

        assertCommandFailure(new EditCommand(INDEX_FIRST_PERSON, descriptor), model,
                EditCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_normalizeOwnName_success() {
        Person firstPerson = model.getFilteredPersonList().get(0);
        Person editedPerson = new PersonBuilder(firstPerson).withName("aLICE   pAULINE  ").build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName("aLICE   pAULINE  ").build();
        Model expectedModel = new ModelManager(model.getInsureConnect(), new UserPrefs());
        expectedModel.setPerson(firstPerson, editedPerson);

        assertCommandSuccess(new EditCommand(INDEX_FIRST_PERSON, descriptor), model,
                String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson)), expectedModel);
    }

    @Test
    public void equals() {
        final EditCommand standardCommand = new EditCommand(INDEX_FIRST_PERSON, DESC_AMY);

        // same values -> returns true
        EditPersonDescriptor copyDescriptor = new EditPersonDescriptor(DESC_AMY);
        EditCommand commandWithSameValues = new EditCommand(INDEX_FIRST_PERSON, copyDescriptor);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different index -> returns false
        assertFalse(standardCommand.equals(new EditCommand(INDEX_SECOND_PERSON, DESC_AMY)));

        // different descriptor -> returns false
        assertFalse(standardCommand.equals(new EditCommand(INDEX_FIRST_PERSON, DESC_BOB)));

        // different remark -> returns false
        EditPersonDescriptor descriptorWithRemark = new EditPersonDescriptorBuilder(DESC_AMY)
                .withRemark(VALID_REMARK).build();
        assertFalse(standardCommand.equals(new EditCommand(INDEX_FIRST_PERSON, descriptorWithRemark)));
    }

    @Test
    public void toStringMethod() {
        Index index = Index.fromOneBased(1);
        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();
        EditCommand editCommand = new EditCommand(index, editPersonDescriptor);
        String expected = EditCommand.class.getCanonicalName() + "{index=" + index + ", editPersonDescriptor="
                + editPersonDescriptor + "}";
        assertEquals(expected, editCommand.toString());
    }

}
