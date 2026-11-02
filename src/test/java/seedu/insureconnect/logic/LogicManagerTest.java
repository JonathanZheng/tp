package seedu.insureconnect.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.insureconnect.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.insureconnect.logic.commands.CommandTestUtil.ADDRESS_DESC_AMY;
import static seedu.insureconnect.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.insureconnect.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.insureconnect.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.insureconnect.testutil.Assert.assertThrows;
import static seedu.insureconnect.testutil.TypicalPersons.AMY;
import static seedu.insureconnect.testutil.TypicalPersons.getTypicalInsureConnect;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.insureconnect.commons.util.FileUtil;
import seedu.insureconnect.logic.commands.AddCommand;
import seedu.insureconnect.logic.commands.ClearCommand;
import seedu.insureconnect.logic.commands.CommandResult;
import seedu.insureconnect.logic.commands.DeleteCommand;
import seedu.insureconnect.logic.commands.ListCommand;
import seedu.insureconnect.logic.commands.exceptions.CommandException;
import seedu.insureconnect.logic.parser.exceptions.ParseException;
import seedu.insureconnect.model.InsureConnect;
import seedu.insureconnect.model.Model;
import seedu.insureconnect.model.ModelManager;
import seedu.insureconnect.model.ReadOnlyInsureConnect;
import seedu.insureconnect.model.UserPrefs;
import seedu.insureconnect.model.person.Person;
import seedu.insureconnect.storage.JsonInsureConnectStorage;
import seedu.insureconnect.storage.JsonUserPrefsStorage;
import seedu.insureconnect.storage.StorageManager;
import seedu.insureconnect.testutil.PersonBuilder;

public class LogicManagerTest {
    private static final IOException DUMMY_IO_EXCEPTION = new IOException("dummy IO exception");
    private static final IOException DUMMY_AD_EXCEPTION = new AccessDeniedException("dummy access denied exception");

    @TempDir
    public Path temporaryFolder;

    private Model model = new ModelManager();
    private Logic logic;
    private Path insureConnectFile;

    @BeforeEach
    public void setUp() {
        insureConnectFile = temporaryFolder.resolve("insureConnect.json");
        JsonInsureConnectStorage insureConnectStorage = new JsonInsureConnectStorage(insureConnectFile);
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        StorageManager storage = new StorageManager(insureConnectStorage, userPrefsStorage);
        logic = new LogicManager(model, storage);
    }

    @Test
    public void constructor_nullDependencies_rejectedImmediately() {
        StorageManager storage = new StorageManager(new JsonInsureConnectStorage(insureConnectFile),
                new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json")));
        assertThrows(NullPointerException.class, () -> new LogicManager(null, storage));
        assertThrows(NullPointerException.class, () -> new LogicManager(model, null));
    }

    @Test
    public void execute_invalidCommandFormat_throwsParseException() {
        String invalidCommand = "uicfhmowqewca";
        assertParseException(invalidCommand, MESSAGE_UNKNOWN_COMMAND);
    }

    @Test
    public void execute_commandExecutionError_throwsCommandException() {
        String deleteCommand = "delete 9";
        assertCommandException(deleteCommand,
                Messages.getInvalidPersonDisplayedIndexMessage(
                        model.getFilteredPersonList().size()));
    }

    @Test
    public void execute_validCommand_success() throws Exception {
        String listCommand = ListCommand.COMMAND_WORD;
        assertCommandSuccess(listCommand,
                String.format(ListCommand.MESSAGE_SUCCESS, model.getFilteredPersonList().size()),
                model);
    }

    @Test
    public void execute_clearWithNoCustomers_reportsNothingToClearWithoutConfirmation() throws Exception {
        CommandResult result = logic.execute(ClearCommand.COMMAND_WORD);

        // EP: empty customer list
        assertEquals(ClearCommand.MESSAGE_NO_CUSTOMERS_TO_CLEAR, result.getFeedbackToUser());
        assertFalse(result.isConfirmationRequired());
        assertTrue(model.getInsureConnect().getPersonList().isEmpty());
        assertFalse(Files.exists(insureConnectFile));
    }

    @Test
    public void execute_clearWithCustomersHiddenByFilter_promptsAndClearsAll() throws Exception {
        model.setInsureConnect(getTypicalInsureConnect());
        model.updateFilteredPersonList(person -> false);

        CommandResult prompt = logic.execute(ClearCommand.COMMAND_WORD);

        // EP: customers exist but the displayed list is empty due to a filter
        assertEquals(ClearCommand.MESSAGE_CONFIRMATION, prompt.getFeedbackToUser());
        assertTrue(prompt.isConfirmationRequired());

        CommandResult result = logic.execute("y");

        // EP: lowercase affirmative confirmation response
        assertEquals(ClearCommand.MESSAGE_SUCCESS, result.getFeedbackToUser());
        assertEquals(new InsureConnect(), model.getInsureConnect());
    }

    @Test
    public void execute_clearThenConfirm_clearsAndPersistsRecords() throws Exception {
        model.setInsureConnect(getTypicalInsureConnect());
        ReadOnlyInsureConnect recordsBeforePrompt = new InsureConnect(model.getInsureConnect());

        CommandResult prompt = logic.execute(ClearCommand.COMMAND_WORD);
        assertEquals(ClearCommand.MESSAGE_CONFIRMATION, prompt.getFeedbackToUser());
        assertTrue(prompt.isConfirmationRequired());
        assertEquals(recordsBeforePrompt, model.getInsureConnect());
        assertFalse(Files.exists(insureConnectFile));

        // EP: affirmative confirmation input; uppercase Y is accepted case-insensitively
        CommandResult result = logic.execute("Y");
        assertEquals(ClearCommand.MESSAGE_SUCCESS, result.getFeedbackToUser());
        assertEquals(new InsureConnect(), model.getInsureConnect());
        assertTrue(Files.exists(insureConnectFile));
    }

    @Test
    public void execute_clearThenAnyOtherInput_cancelsWithoutSaving() throws Exception {
        model.setInsureConnect(getTypicalInsureConnect());
        ReadOnlyInsureConnect recordsBeforePrompt = new InsureConnect(model.getInsureConnect());

        logic.execute(ClearCommand.COMMAND_WORD);
        // EP: explicit negative confirmation input
        CommandResult result = logic.execute("n");

        assertEquals(ClearCommand.MESSAGE_CANCELLED, result.getFeedbackToUser());
        assertEquals(recordsBeforePrompt, model.getInsureConnect());
        assertFalse(Files.exists(insureConnectFile));
    }

    @Test
    public void execute_clearThenBlankInput_usesDefaultNo() throws Exception {
        model.setInsureConnect(getTypicalInsureConnect());
        ReadOnlyInsureConnect recordsBeforePrompt = new InsureConnect(model.getInsureConnect());

        logic.execute(ClearCommand.COMMAND_WORD);
        // EP: empty input uses the default negative response
        CommandResult result = logic.execute("");

        assertEquals(ClearCommand.MESSAGE_CANCELLED, result.getFeedbackToUser());
        assertEquals(recordsBeforePrompt, model.getInsureConnect());
        assertFalse(Files.exists(insureConnectFile));
    }

    @Test
    public void execute_clearThenAnotherCommand_discardsThatInputAndCancels() throws Exception {
        model.setInsureConnect(getTypicalInsureConnect());
        ReadOnlyInsureConnect recordsBeforePrompt = new InsureConnect(model.getInsureConnect());

        logic.execute(ClearCommand.COMMAND_WORD);
        // EP: another command is consumed as a non-y response and cancels clearing
        CommandResult result = logic.execute(ListCommand.COMMAND_WORD);

        assertEquals(ClearCommand.MESSAGE_CANCELLED, result.getFeedbackToUser());
        assertEquals(recordsBeforePrompt, model.getInsureConnect());
        assertFalse(Files.exists(insureConnectFile));
    }

    @Test
    public void execute_duplicateHiddenByFilter_showsAllAndReportsDisplayedIndex() throws Exception {
        model.setInsureConnect(getTypicalInsureConnect());
        Person duplicate = model.getInsureConnect().getPersonList().get(0);
        Person other = model.getInsureConnect().getPersonList().get(1);
        logic.execute("find p/" + other.getPhone().value);
        String addDuplicate = "add n/" + duplicate.getName().fullName + " p/" + duplicate.getPhone().value
                + " e/new@example.com a/Somewhere t/NEWPOLICY1";
        // A phone search keeps stored order, so the duplicate is first once the filter is cleared.
        int expectedIndex = 1;

        assertThrows(CommandException.class, String.format(AddCommand.MESSAGE_DUPLICATE_PERSON, expectedIndex,
                duplicate.getName().fullName, duplicate.getPhone().value), () -> logic.execute(addDuplicate));
        assertEquals(duplicate, model.getFilteredPersonList().get(expectedIndex - 1));
        assertEquals(model.getInsureConnect().getPersonList().size(), model.getFilteredPersonList().size());
        assertEquals(getTypicalInsureConnect(), model.getInsureConnect());
    }

    @Test
    public void execute_duplicateHiddenByNameSearch_reportsIndexInRankedList() throws Exception {
        model.setInsureConnect(getTypicalInsureConnect());
        Person duplicate = model.getInsureConnect().getPersonList().get(0);
        Person searched = model.getInsureConnect().getPersonList().get(2);
        logic.execute("find " + searched.getName().fullName.split(" ")[0]);
        String addDuplicate = "add n/" + duplicate.getName().fullName + " p/" + duplicate.getPhone().value
                + " e/new@example.com a/Somewhere t/NEWPOLICY1";
        // The name search ranks its match first, which moves the duplicate from 1 to 2.
        int expectedIndex = 2;

        assertThrows(CommandException.class, String.format(AddCommand.MESSAGE_DUPLICATE_PERSON, expectedIndex,
                duplicate.getName().fullName, duplicate.getPhone().value), () -> logic.execute(addDuplicate));
        assertEquals(searched, model.getFilteredPersonList().get(0));
        assertEquals(duplicate, model.getFilteredPersonList().get(expectedIndex - 1));
        assertEquals(model.getInsureConnect().getPersonList().size(), model.getFilteredPersonList().size());
    }

    @Test
    public void execute_storageThrowsIoException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_IO_EXCEPTION, String.format(
                LogicManager.FILE_OPS_ERROR_FORMAT, DUMMY_IO_EXCEPTION.getMessage()));
    }

    @Test
    public void execute_storageThrowsAdException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_AD_EXCEPTION, String.format(
                LogicManager.FILE_OPS_PERMISSION_ERROR_FORMAT, DUMMY_AD_EXCEPTION.getMessage()));
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> logic.getFilteredPersonList().remove(0));
    }

    /**
     * Executes the command and confirms that
     * - no exceptions are thrown <br>
     * - the feedback message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandSuccess(String inputCommand, String expectedMessage,
            Model expectedModel) throws CommandException, ParseException {
        CommandResult result = logic.execute(inputCommand);
        assertEquals(expectedMessage, result.getFeedbackToUser());
        assertEquals(expectedModel, model);
    }

    /**
     * Executes the command, confirms that a ParseException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertParseException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, ParseException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that a CommandException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, CommandException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that the exception is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage) {
        Model expectedModel = new ModelManager(model.getInsureConnect(), new UserPrefs());
        assertCommandFailure(inputCommand, expectedException, expectedMessage, expectedModel);
    }

    /**
     * Executes the command and confirms that
     * - the {@code expectedException} is thrown <br>
     * - the resulting error message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandSuccess(String, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage, Model expectedModel) {
        assertThrows(expectedException, expectedMessage, () -> logic.execute(inputCommand));
        assertEquals(expectedModel, model);
    }

    /**
     * Tests the Logic component's handling of an {@code IOException} thrown by the Storage component.
     *
     * @param e the exception to be thrown by the Storage component
     * @param expectedMessage the message expected inside exception thrown by the Logic component
     */
    private void assertCommandFailureForExceptionFromStorage(IOException e, String expectedMessage) {
        Path prefPath = temporaryFolder.resolve("ExceptionUserPrefs.json");

        // Inject LogicManager with a JsonInsureConnectStorage that throws the IOException e when saving
        JsonInsureConnectStorage insureConnectStorage = new JsonInsureConnectStorage(prefPath) {
            @Override
            public void saveInsureConnect(ReadOnlyInsureConnect insureConnect) throws IOException {
                throw e;
            }
        };

        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("ExceptionUserPrefs.json"));
        StorageManager storage = new StorageManager(insureConnectStorage, userPrefsStorage);

        logic = new LogicManager(model, storage);

        // Triggers the saveInsureConnect method by executing an add command
        String addCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY + " t/POLICY90001";
        Person expectedPerson = new PersonBuilder(AMY).withTags("POLICY90001").build();
        ModelManager expectedModel = new ModelManager();
        assertCommandFailure(addCommand, CommandException.class, expectedMessage, expectedModel);
        assertFalse(model.hasPerson(expectedPerson));
    }

    @Test
    public void execute_unreadableStorageFile_rollsBackAndNonMutatingSucceeds() throws Exception {
        Path filePath = temporaryFolder.resolve("unreadableAddressBook.json");
        FileUtil.writeToFile(filePath, "invalid json");
        JsonInsureConnectStorage insureConnectStorage = new JsonInsureConnectStorage(filePath);
        try {
            insureConnectStorage.readInsureConnect();
        } catch (Exception ignored) {
            // expected DataLoadingException marking file as unreadable
        }
        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("unreadableUserPrefs.json"));
        StorageManager storage = new StorageManager(insureConnectStorage, userPrefsStorage);
        logic = new LogicManager(model, storage);

        // Non-mutating command succeeds
        assertCommandSuccess(ListCommand.COMMAND_WORD,
                String.format(ListCommand.MESSAGE_SUCCESS, model.getFilteredPersonList().size()),
                model);

        // Mutating command fails and rolls back in-memory changes
        String addCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY + " t/POLICY90001";
        Person expectedPerson = new PersonBuilder(AMY).withTags("POLICY90001").build();
        ModelManager expectedModel = new ModelManager();
        assertCommandFailure(addCommand, CommandException.class, String.format(
                LogicManager.FILE_OPS_ERROR_FORMAT,
                "The existing data file at " + filePath + " is unreadable and cannot be overwritten."),
                expectedModel);
        assertFalse(model.hasPerson(expectedPerson));
    }

    @Test
    public void execute_deleteRequestDoesNotSave_yesSavesDeletion() throws Exception {
        model.addPerson(AMY);
        Path dataFile = temporaryFolder.resolve("insureConnect.json");

        logic.execute("delete 1");

        // A request only prepares deletion; it must not write data to disk.
        assertFalse(Files.exists(dataFile));
        assertTrue(model.hasPerson(AMY));

        // EP: affirmative confirmation input
        logic.execute("y");

        // Confirmation deletes the customer and persists the changed book.
        assertTrue(Files.exists(dataFile));
        assertFalse(model.hasPerson(AMY));

        JsonInsureConnectStorage storage = new JsonInsureConnectStorage(dataFile);
        ReadOnlyInsureConnect savedData = storage.readInsureConnect().orElseThrow();
        assertFalse(savedData.getPersonList().contains(AMY));
    }

    @Test
    public void execute_deleteRequiresConfirmation() throws Exception {
        model.addPerson(AMY);

        ModelManager unchangedModel = new ModelManager(model.getInsureConnect(), new UserPrefs());
        String confirmationMessage = String.format(
                DeleteCommand.MESSAGE_CONFIRM_DELETE, Messages.format(AMY));

        CommandResult prompt = logic.execute("delete 1");
        assertEquals(confirmationMessage, prompt.getFeedbackToUser());
        assertTrue(prompt.isConfirmationRequired());
        assertEquals(unchangedModel, model);

        ModelManager deletedModel = new ModelManager(model.getInsureConnect(), new UserPrefs());
        deletedModel.deletePerson(AMY);

        // EP: affirmative confirmation input with uppercase Y
        assertCommandSuccess("Y",
                String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS, Messages.format(AMY)),
                deletedModel);
    }

    @Test
    public void execute_deleteThenAnyNonYesInput_cancelsDeletion() throws Exception {
        model.addPerson(AMY);

        logic.execute("delete 1");
        // EP: explicit negative confirmation input
        CommandResult result = logic.execute("n");

        assertEquals(DeleteCommand.MESSAGE_CANCELLED, result.getFeedbackToUser());
        assertTrue(model.hasPerson(AMY));
        assertFalse(Files.exists(insureConnectFile));
    }

    @Test
    public void execute_deleteThenBlankInput_usesDefaultNo() throws Exception {
        model.addPerson(AMY);

        logic.execute("delete 1");
        // EP: empty input uses the default negative response
        CommandResult result = logic.execute("");

        assertEquals(DeleteCommand.MESSAGE_CANCELLED, result.getFeedbackToUser());
        assertTrue(model.hasPerson(AMY));
        assertFalse(Files.exists(insureConnectFile));
    }

    @Test
    public void execute_confirmWithoutPendingDeletion_isUnknownCommand() {
        assertParseException("confirm", MESSAGE_UNKNOWN_COMMAND);
    }

    @Test
    public void execute_otherCommandAfterDeleteRequest_isConsumedAndCancelsDeletion() throws Exception {
        model.addPerson(AMY);
        ModelManager unchangedModel = new ModelManager(model.getInsureConnect(), new UserPrefs());
        String confirmationMessage = String.format(
                DeleteCommand.MESSAGE_CONFIRM_DELETE, Messages.format(AMY));

        assertCommandSuccess("delete 1", confirmationMessage, unchangedModel);
        // EP: another command is consumed as a non-y response and cancels deletion
        CommandResult result = logic.execute(ListCommand.COMMAND_WORD);
        assertEquals(DeleteCommand.MESSAGE_CANCELLED, result.getFeedbackToUser());
        assertEquals(unchangedModel, model);
        assertParseException("confirm", MESSAGE_UNKNOWN_COMMAND);
    }

    @Test
    public void execute_pendingDeletionAnyOtherInput_cancelsWithoutParsing() throws Exception {
        model.addPerson(AMY);

        String confirmationMessage = String.format(
                DeleteCommand.MESSAGE_CONFIRM_DELETE, Messages.format(AMY));
        assertCommandSuccess("delete 1", confirmationMessage, model);

        // EP: invalid non-y input cancels the pending deletion
        CommandResult result = logic.execute("confirm now");
        assertEquals(DeleteCommand.MESSAGE_CANCELLED, result.getFeedbackToUser());
        assertTrue(model.hasPerson(AMY));
    }

    @Test
    public void execute_pendingDeletionUnrelatedInvalidCommand_cancelsDeletion() throws Exception {
        model.addPerson(AMY);

        logic.execute("delete 1");

        // EP: unrecognized non-y input cancels the pending deletion
        CommandResult result = logic.execute("unknown");
        assertEquals(DeleteCommand.MESSAGE_CANCELLED, result.getFeedbackToUser());

        assertParseException("confirm", MESSAGE_UNKNOWN_COMMAND);

        assertTrue(model.hasPerson(AMY));
    }

    @Test
    public void execute_yesSaveFailure_restoresDataAndConsumesConfirmation() throws Exception {
        Person bob = new PersonBuilder().withName("Bob Tan").withTags("POLICY90002").build();
        model.addPerson(AMY);
        model.addPerson(bob);

        List<Person> originalPersons = List.copyOf(model.getInsureConnect().getPersonList());
        model.updateFilteredPersonList(person -> person.equals(AMY));
        List<Person> originalDisplayedPersons = List.copyOf(model.getFilteredPersonList());

        useStorageThatFailsOnSave(DUMMY_IO_EXCEPTION);

        logic.execute("delete 1");

        String expectedSaveFailureMessage = String.format(
                LogicManager.FILE_OPS_ERROR_FORMAT, DUMMY_IO_EXCEPTION.getMessage());
        // EP: affirmative confirmation input reaches the save path
        assertThrows(CommandException.class, expectedSaveFailureMessage, () -> logic.execute("y"));

        // Both stored data and the displayed filtered list are restored.
        assertEquals(originalPersons, model.getInsureConnect().getPersonList());
        assertEquals(originalDisplayedPersons, model.getFilteredPersonList());

        // The failed confirmation must not remain reusable.
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> logic.execute("confirm"));

        // A second confirmation must not change the restored data or displayed list.
        assertEquals(originalPersons, model.getInsureConnect().getPersonList());
        assertEquals(originalDisplayedPersons, model.getFilteredPersonList());
    }

    /**
     * Replaces storage with one that fails whenever customer data is saved.
     */
    private void useStorageThatFailsOnSave(IOException exception) {
        Path dataFile = temporaryFolder.resolve("failingInsureConnect.json");

        JsonInsureConnectStorage insureConnectStorage = new JsonInsureConnectStorage(dataFile) {
            @Override
            public void saveInsureConnect(ReadOnlyInsureConnect insureConnect) throws IOException {
                throw exception;
            }
        };

        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("failingUserPrefs.json"));

        logic = new LogicManager(model, new StorageManager(insureConnectStorage, userPrefsStorage));
    }
}
