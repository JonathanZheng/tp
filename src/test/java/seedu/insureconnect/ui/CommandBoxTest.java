package seedu.insureconnect.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
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
