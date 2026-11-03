package seedu.insureconnect.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import seedu.insureconnect.logic.commands.CommandResult;
import seedu.insureconnect.logic.commands.exceptions.CommandException;
import seedu.insureconnect.logic.parser.exceptions.ParseException;

public class CommandBoxTest extends GuiTest {

    @Test
    public void submit_validCommand_executesExactInputAndClearsDraft() throws Exception {
        runOnFxThread(() -> {
            List<String> commands = new ArrayList<>();
            CommandBox box = new CommandBox(command -> {
                commands.add(command);
                return new CommandResult("Done");
            });
            TextArea input = inputOf(box);
            submit(input, "  list n/  ");
            assertEquals(List.of("  list n/  "), commands);
            assertEquals("", input.getText());
            assertFalse(input.getStyleClass().contains(CommandBox.ERROR_STYLE_CLASS));
        });
    }

    @Test
    public void submit_emptyInput_doesNotExecute() throws Exception {
        runOnFxThread(() -> {
            CommandBox box = new CommandBox(command -> {
                throw new AssertionError("Empty input must not reach the executor");
            });
            submit(inputOf(box), "");
            assertEquals("", inputOf(box).getText());
        });
    }

    @Test
    public void submit_emptyInputWhileAwaitingConfirmation_executesDefaultResponse() throws Exception {
        runOnFxThread(() -> {
            List<String> commands = new ArrayList<>();
            CommandBox box = new CommandBox(command -> {
                commands.add(command);
                return commands.size() == 1
                        ? new CommandResult("Confirm?", false, false, true)
                        : new CommandResult("Cancelled");
            });
            TextArea input = inputOf(box);
            submit(input, "clear");
            // EP: empty input is submitted so the logic can apply default n
            submit(input, "");
            assertEquals(List.of("clear", ""), commands);
            assertEquals("", input.getText());
        });
    }

    @Test
    public void submit_yesWhileAwaitingConfirmation_executesResponse() throws Exception {
        runOnFxThread(() -> {
            List<String> commands = new ArrayList<>();
            CommandBox box = new CommandBox(command -> {
                commands.add(command);
                return commands.size() == 1
                        ? new CommandResult("Confirm?", false, false, true)
                        : new CommandResult("Done");
            });
            TextArea input = inputOf(box);
            submit(input, "delete 1");
            // EP: affirmative confirmation input is forwarded unchanged
            submit(input, "Y");
            assertEquals(List.of("delete 1", "Y"), commands);
            assertEquals("", input.getText());
        });
    }

    @Test
    public void submit_parseFailure_preservesDraftUntilUserEdits() throws Exception {
        runOnFxThread(() -> assertFailurePreservesDraft(command -> {
            throw new ParseException("Invalid command");
        }));
    }

    @Test
    public void submit_commandFailure_preservesDraftUntilUserEdits() throws Exception {
        runOnFxThread(() -> assertFailurePreservesDraft(command -> {
            throw new CommandException("Duplicate customer");
        }));
    }

    @Test
    public void commandBox_defaultConfiguration_enablesWrappingAcrossThreeRows() throws Exception {
        runOnFxThread(() -> {
            TextArea input = inputOf(new CommandBox(command -> new CommandResult("Done")));

            assertTrue(input.isWrapText());
            assertEquals(3, input.getPrefRowCount());
        });
    }

    @Test
    public void paste_lineBreaksAndTabs_replacesWithSpacesBeforeSubmission() throws Exception {
        runOnFxThread(() -> {
            List<String> commands = new ArrayList<>();
            CommandBox box = new CommandBox(command -> {
                commands.add(command);
                return new CommandResult("Done");
            });
            TextArea input = inputOf(box);

            // replaceText simulates inserting pasted clipboard content.
            input.replaceText(0, 0,
                    "add\nn/John\r\np/91234567\te/john@example.com");

            String expected =
                    "add n/John p/91234567 e/john@example.com";
            assertEquals(expected, input.getText());

            pressKey(input, KeyCode.ENTER);

            assertEquals(List.of(expected), commands);
            assertEquals("", input.getText());
        });
    }

    @Test
    public void findSuggestion_userGuideAliasPrefix_returnsRemainingAlias() {
        // EP: prefix shared by the alias and full command word
        assertEquals("g", CommandBox.findSuggestion("u"));
    }

    @Test
    public void findSuggestion_userGuideCommandPrefix_returnsRemainingCommandWord() {
        // EP: prefix matching only the full command word
        assertEquals("erguide", CommandBox.findSuggestion("us"));
    }

    @Test
    public void findSuggestion_confirmPrefix_returnsNoSuggestion() {
        // EP: prefix for a confirmation-only command that is excluded from suggestions
        assertEquals("", CommandBox.findSuggestion("con"));
    }

    @Test
    public void accentInput_holdOAndSelectFour_insertsUmlaut() throws Exception {
        runOnFxThread(() -> {
            CommandBox box = createCommandBoxForOperatingSystem("Mac OS X");
            show(box.getRoot());

            TextArea input = inputOf(box);
            input.requestFocus();

            press(input, KeyCode.O, "o", false, false);
            type(input, "o", false);

            // Simulates the repeated events generated while O remains held.
            press(input, KeyCode.O, "o", false, false);
            type(input, "o", false);
            press(input, KeyCode.O, "o", false, false);
            type(input, "o", false);

            assertEquals("o", input.getText());

            press(input, KeyCode.DIGIT4, "4", false, false);
            type(input, "4", false);

            assertEquals("ö", input.getText());
            assertEquals(1, input.getCaretPosition());
        });
    }

    @Test
    public void accentInput_shortPressAndUppercaseSelection_behavesCorrectly() throws Exception {
        runOnFxThread(() -> {
            CommandBox box = createCommandBoxForOperatingSystem("Mac OS X");
            show(box.getRoot());

            TextArea input = inputOf(box);
            input.requestFocus();

            press(input, KeyCode.O, "o", false, false);
            type(input, "o", false);
            release(input, KeyCode.O, false);

            assertEquals("o", input.getText());

            input.clear();

            press(input, KeyCode.O, "O", true, false);
            type(input, "O", true);
            press(input, KeyCode.O, "O", true, false);
            type(input, "O", true);

            press(input, KeyCode.DIGIT4, "4", false, false);
            type(input, "4", false);

            assertEquals("Ö", input.getText());
        });
    }

    @Test
    public void accentInput_modifiedAndInterruptedInput_doesNotActivate() throws Exception {
        runOnFxThread(() -> {
            CommandBox box = createCommandBoxForOperatingSystem("Mac OS X");
            show(box.getRoot());

            TextArea input = inputOf(box);
            input.requestFocus();

            // Option-modified keys must remain under JavaFX/macOS control.
            press(input, KeyCode.O, "o", false, true);
            press(input, KeyCode.O, "o", false, true);
            assertEquals("", input.getText());

            // Repetition before KEY_TYPED must not activate selection.
            press(input, KeyCode.O, "o", false, false);
            press(input, KeyCode.O, "o", false, false);
            release(input, KeyCode.O, false);
            assertEquals("", input.getText());

            // Another key cancels the held-letter state.
            press(input, KeyCode.O, "o", false, false);
            type(input, "o", false);
            press(input, KeyCode.B, "b", false, false);

            // With no held accent key, ordinary typed input continues normally.
            type(input, "x", false);
            assertEquals("ox", input.getText());
        });
    }

    @Test
    public void accentInput_optionOutsideAvailableRange_typesNumberNormally() throws Exception {
        runOnFxThread(() -> {
            CommandBox box = createCommandBoxForOperatingSystem("Mac OS X");
            show(box.getRoot());

            TextArea input = inputOf(box);
            input.requestFocus();

            // Y only has two configured choices: ý and ÿ.
            press(input, KeyCode.Y, "y", false, false);
            type(input, "y", false);
            press(input, KeyCode.Y, "y", false, false);
            type(input, "y", false);

            press(input, KeyCode.DIGIT4, "4", false, false);
            type(input, "4", false);

            assertEquals("y4", input.getText());
        });
    }

    @Test
    public void accentInput_nonMac_doesNotInterceptRepeatedInput() throws Exception {
        runOnFxThread(() -> {
            CommandBox box = createCommandBoxForOperatingSystem("Linux");
            show(box.getRoot());

            TextArea input = inputOf(box);
            input.requestFocus();

            press(input, KeyCode.O, "o", false, false);
            type(input, "o", false);
            press(input, KeyCode.O, "o", false, false);
            type(input, "o", false);

            assertEquals("oo", input.getText());
        });
    }

    /**
     * Verifies retry styling is not duplicated and that editing clears only the error state.
     */
    private void assertFailurePreservesDraft(CommandBox.CommandExecutor executor) {
        TextArea input = inputOf(new CommandBox(executor));
        input.getStyleClass().add("custom-style");
        submit(input, "invalid draft");
        pressKey(input, KeyCode.ENTER);
        assertEquals("invalid draft", input.getText());
        assertEquals(1, input.getStyleClass().stream().filter(CommandBox.ERROR_STYLE_CLASS::equals).count());
        input.appendText(" corrected");
        assertFalse(input.getStyleClass().contains(CommandBox.ERROR_STYLE_CLASS));
        assertTrue(input.getStyleClass().contains("custom-style"));
    }

    /**
     * Creates a command box while reporting the requested operating system.
     */
    private CommandBox createCommandBoxForOperatingSystem(String operatingSystem) {
        String originalOperatingSystem = System.getProperty("os.name");

        try {
            System.setProperty("os.name", operatingSystem);
            return new CommandBox(command -> new CommandResult("Done"));
        } finally {
            if (originalOperatingSystem == null) {
                System.clearProperty("os.name");
            } else {
                System.setProperty("os.name", originalOperatingSystem);
            }
        }
    }

    /**
     * Sends a key-pressed event with printable key text.
     */
    private void press(TextArea input, KeyCode code, String text,
                       boolean shiftDown, boolean altDown) {
        input.fireEvent(new KeyEvent(
                KeyEvent.KEY_PRESSED,
                "",
                text,
                code,
                shiftDown,
                false,
                altDown,
                false));
    }

    /**
     * Sends a typed-character event.
     */
    private void type(TextArea input, String character,
                      boolean shiftDown) {
        input.fireEvent(new KeyEvent(
                KeyEvent.KEY_TYPED,
                character,
                "",
                KeyCode.UNDEFINED,
                shiftDown,
                false,
                false,
                false));
    }

    /**
     * Sends a key-release event.
     */
    private void release(TextArea input, KeyCode code,
                         boolean shiftDown) {
        input.fireEvent(new KeyEvent(
                KeyEvent.KEY_RELEASED,
                "",
                "",
                code,
                shiftDown,
                false,
                false,
                false));
    }

    /**
     * Retrieves the real text area loaded from FXML.
     */
    private TextArea inputOf(CommandBox box) {
        return (TextArea) box.getRoot().lookup("#commandTextField");
    }

    /**
     * Submits a command through the control's Enter-key handler.
     */
    private void submit(TextArea input, String command) {
        input.setText(command);
        pressKey(input, KeyCode.ENTER);
    }
}
