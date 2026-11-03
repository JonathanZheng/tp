package seedu.insureconnect.ui;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.skin.TextAreaSkin;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import seedu.insureconnect.logic.commands.AddCommand;
import seedu.insureconnect.logic.commands.ClearCommand;
import seedu.insureconnect.logic.commands.CommandResult;
import seedu.insureconnect.logic.commands.DeleteCommand;
import seedu.insureconnect.logic.commands.EditCommand;
import seedu.insureconnect.logic.commands.ExitCommand;
import seedu.insureconnect.logic.commands.FindCommand;
import seedu.insureconnect.logic.commands.HelpCommand;
import seedu.insureconnect.logic.commands.ListCommand;
import seedu.insureconnect.logic.commands.ResizeCommand;
import seedu.insureconnect.logic.commands.UserGuideCommand;
import seedu.insureconnect.logic.commands.exceptions.CommandException;
import seedu.insureconnect.logic.parser.exceptions.ParseException;

/**
 * The UI component that is responsible for receiving user command inputs.
 */
public class CommandBox extends UiPart<Region> {

    public static final String ERROR_STYLE_CLASS = "error";
    private static final String FXML = "CommandBox.fxml";

    private static final KeyCombination HISTORY_PREVIOUS = new KeyCodeCombination(KeyCode.UP);
    private static final KeyCombination HISTORY_NEXT = new KeyCodeCombination(KeyCode.DOWN);
    private static final KeyCombination ACCEPT_SUGGESTION = new KeyCodeCombination(KeyCode.TAB);
    private static final Map<KeyCode, String> ACCENTS = Map.of(
        KeyCode.A, "àáâäãåāæ",
        KeyCode.C, "çćč",
        KeyCode.E, "èéêëēėę",
        KeyCode.I, "ìíîïīį",
        KeyCode.N, "ñń",
        KeyCode.O, "òóôöõøōœ",
        KeyCode.U, "ùúûüū",
        KeyCode.Y, "ýÿ");

    // Sorted so that the alphabetically first match is suggested when several command words fit.
    private static final List<String> COMMAND_WORDS = Stream.of(AddCommand.COMMAND_WORD,
            ClearCommand.COMMAND_WORD, DeleteCommand.COMMAND_WORD,
            EditCommand.COMMAND_WORD, ExitCommand.COMMAND_WORD, FindCommand.COMMAND_WORD,
            HelpCommand.COMMAND_WORD, ListCommand.COMMAND_WORD, ResizeCommand.COMMAND_WORD,
            UserGuideCommand.COMMAND_ALIAS, UserGuideCommand.COMMAND_WORD).sorted().toList();

    private final CommandExecutor commandExecutor;
    private boolean awaitingConfirmation;

    private final CommandHistory history = new CommandHistory();
    private int draftAnchor;
    private int draftCaret;

    private KeyCode heldAccentKey;
    private String accentChoices;
    private String firstTypedCharacter;
    private int accentStart;
    private boolean consumeNumber;
    private boolean accentSelectionActive;

    @FXML
    private TextArea commandTextField;

    @FXML
    private Text commandSuggestionText;

    /**
     * Creates a {@code CommandBox} with the given {@code CommandExecutor}.
     */
    public CommandBox(CommandExecutor commandExecutor) {
        super(FXML);
        this.commandExecutor = commandExecutor;
        if (System.getProperty("os.name", "")
                .toLowerCase(Locale.ROOT).contains("mac")) {
            commandTextField.addEventFilter(
                    KeyEvent.KEY_PRESSED, this::handleAccentPressed);
            commandTextField.addEventFilter(
                    KeyEvent.KEY_TYPED, this::handleAccentTyped);
            commandTextField.addEventFilter(
                    KeyEvent.KEY_RELEASED, this::handleAccentReleased);
        }
        commandTextField.addEventFilter(KeyEvent.KEY_PRESSED, this::handleCommandKey);
        commandTextField.addEventFilter(KeyEvent.KEY_PRESSED, this::handleSuggestionKey);

        commandTextField.setTextFormatter(new TextFormatter<>(change -> {
            change.setText(change.getText().replaceAll("[\\r\\n\\t]+", " "));
            return change;
        }));
        // calls #setStyleToDefault() whenever there is a change to the text of the command box.
        commandTextField.textProperty().addListener((unused1, unused2, unused3) -> setStyleToDefault());
        commandTextField.textProperty().addListener((unused1, unused2, unused3) -> updateSuggestion());
        commandTextField.caretPositionProperty().addListener((unused1, unused2, unused3) -> updateSuggestion());
        commandTextField.selectionProperty().addListener((unused1, unused2, unused3) -> updateSuggestion());
    }

    /**
     * Submits on Enter and handles command-history navigation.
     */
    private void handleCommandKey(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            handleCommandEntered();
            event.consume();
            return;
        }

        handleHistoryKey(event);
    }

    /**
     * Handles the Enter button pressed event.
     */
    @FXML
    private void handleCommandEntered() {
        String commandText = commandTextField.getText();
        if (commandText.isEmpty() && !awaitingConfirmation) {
            return;
        }

        history.record(commandText);
        try {
            CommandResult result = commandExecutor.execute(commandText);
            awaitingConfirmation = result.isConfirmationRequired();
            commandTextField.setText("");
        } catch (CommandException | ParseException e) {
            awaitingConfirmation = false;
            setStyleToIndicateCommandFailure();
        }
    }

    /**
     * Handles unmodified history shortcuts only within the command input.
     */
    private void handleHistoryKey(KeyEvent event) {
        if (HISTORY_PREVIOUS.match(event)) {
            recallPreviousCommand();
        } else if (HISTORY_NEXT.match(event)) {
            recallNextCommand();
        } else {
            return;
        }
        event.consume();
    }

    /**
     * Saves the draft selection before recalling an older submission.
     */
    private void recallPreviousCommand() {
        if (history.isAtDraft()) {
            draftAnchor = commandTextField.getAnchor();
            draftCaret = commandTextField.getCaretPosition();
        }
        showHistoryEntry(history.previous(commandTextField.getText()));
    }

    /**
     * Advances toward the draft, leaving its editing state alone when already there.
     */
    private void recallNextCommand() {
        if (!history.isAtDraft()) {
            showHistoryEntry(history.next(commandTextField.getText()));
        }
    }

    /**
     * Places recalled commands at the end and restores the original draft selection.
     */
    private void showHistoryEntry(String text) {
        commandTextField.setText(text);
        if (history.isAtDraft()) {
            commandTextField.selectRange(draftAnchor, draftCaret);
        } else {
            commandTextField.positionCaret(text.length());
        }
    }

    /**
     * Fills in the suggested command word when Tab is pressed with a suggestion showing.
     */
    private void handleSuggestionKey(KeyEvent event) {
        String suggestion = commandSuggestionText.getText();
        if (!ACCEPT_SUGGESTION.match(event) || suggestion.isEmpty()) {
            return;
        }
        commandTextField.appendText(suggestion);
        commandTextField.end();
        event.consume();
    }

    /**
     * Shows the rest of the suggested command word as faded text after the typed text.
     */
    private void updateSuggestion() {
        String text = commandTextField.getText();
        String suggestion = isCaretAtEnd() ? findSuggestion(text) : "";
        commandSuggestionText.setText(suggestion);

        if (!suggestion.isEmpty()) {
            Platform.runLater(this::positionSuggestionAtCaret);
        }
    }

    /**
     * Returns whether the caret is at the end of the text with nothing selected.
     */
    private boolean isCaretAtEnd() {
        return commandTextField.getSelection().getLength() == 0
                && commandTextField.getCaretPosition() == commandTextField.getLength();
    }

    /**
     * Returns the untyped rest of the first command word that starts with {@code text},
     * or an empty string if no command word extends it.
     */
    static String findSuggestion(String text) {
        if (text.isEmpty()) {
            return "";
        }
        return COMMAND_WORDS.stream()
            .filter(word -> word.length() > text.length() && word.startsWith(text))
            .findFirst()
            .map(word -> word.substring(text.length()))
            .orElse("");
    }

    /**
     * Sets the command box style to use the default style.
     */
    private void setStyleToDefault() {
        commandTextField.getStyleClass().remove(ERROR_STYLE_CLASS);
    }

    /**
     * Sets the command box style to indicate a failed command.
     */
    private void setStyleToIndicateCommandFailure() {
        ObservableList<String> styleClass = commandTextField.getStyleClass();

        if (styleClass.contains(ERROR_STYLE_CLASS)) {
            return;
        }

        styleClass.add(ERROR_STYLE_CLASS);
    }

    /**
     * Returns the control that receives keyboard focus for this area.
     */
    Node getFocusTarget() {
        return commandTextField;
    }
    /**
     * Opens the accent selector on a repeated held-key event and handles number selection.
     */
    private void handleAccentPressed(KeyEvent event) {
        String keyText = event.getText();
        int option = keyText.length() == 1
                ? "123456789".indexOf(keyText)
                : -1;

        if (accentSelectionActive
                && option >= 0
                && option < accentChoices.length()) {
            int accentEnd = accentStart + firstTypedCharacter.length();
            String currentText = commandTextField.getText();

            if (accentEnd <= currentText.length()
                    && currentText.substring(accentStart, accentEnd)
                            .equals(firstTypedCharacter)) {
                String replacement =
                        String.valueOf(accentChoices.charAt(option));

                if (Character.isUpperCase(
                        firstTypedCharacter.codePointAt(0))) {
                    replacement = replacement.toUpperCase(Locale.ROOT);
                }

                commandTextField.replaceText(
                        accentStart, accentEnd, replacement);
                commandTextField.positionCaret(
                        accentStart + replacement.length());
            }

            clearAccentState();
            consumeNumber = true;
            event.consume();
            return;
        }

        String choices = ACCENTS.get(event.getCode());
        boolean modified = event.isControlDown()
                || event.isAltDown()
                || event.isMetaDown();

        if (modified || choices == null) {
            if (heldAccentKey != null
                    && event.getCode() != heldAccentKey) {
                clearAccentState();
            }
            return;
        }

        if (heldAccentKey == null) {
            heldAccentKey = event.getCode();
            accentChoices = choices;
            accentStart = commandTextField.getSelection().getStart();
            return;
        }

        if (event.getCode() == heldAccentKey) {
            accentSelectionActive = firstTypedCharacter != null;
            event.consume();
        }
    }

    /**
     * Keeps the first letter and suppresses subsequent auto-repeat characters.
     */
    private void handleAccentTyped(KeyEvent event) {
        if (consumeNumber) {
            consumeNumber = false;
            event.consume();
            return;
        }

        if (heldAccentKey == null || event.getCharacter().isEmpty()) {
            return;
        }

        if (firstTypedCharacter == null) {
            firstTypedCharacter = event.getCharacter();
        } else if (event.getCharacter()
                .equalsIgnoreCase(firstTypedCharacter)) {
            event.consume();
        }
    }

    /**
     * Ends accent handling when the original letter is released.
     */
    private void handleAccentReleased(KeyEvent event) {
        if (event.getCode() == heldAccentKey) {
            clearAccentState();
        }
    }

    /**
     * Hides the selector and clears the held-key state.
     */
    private void clearAccentState() {
        heldAccentKey = null;
        accentChoices = null;
        firstTypedCharacter = null;
        accentStart = 0;
        consumeNumber = false;
        accentSelectionActive = false;
    }
    /**
     * Represents a function that can execute commands.
     */
    @FunctionalInterface
    public interface CommandExecutor {
        /**
         * Executes the command and returns the result.
         *
         * @see seedu.insureconnect.logic.Logic#execute(String)
         */
        CommandResult execute(String commandText) throws CommandException, ParseException;
    }

    /**
     * Positions the suggestion beside the TextArea's rendered caret.
     */
    private void positionSuggestionAtCaret() {
        if (commandSuggestionText.getText().isEmpty()) {
            return;
        }

        commandTextField.applyCss();
        commandTextField.requestLayout();
        commandTextField.layout();
        commandSuggestionText.applyCss();

        if (!(commandTextField.getSkin() instanceof TextAreaSkin skin)) {
            return;
        }

        Bounds caretInScene =
                commandTextField.localToScene(skin.getCaretBounds());
        Bounds caretInParent =
                commandSuggestionText.getParent().sceneToLocal(caretInScene);
        Bounds suggestionBounds = commandSuggestionText.getLayoutBounds();

        double x = caretInParent.getMaxX();
        double y = caretInParent.getMinY()
                + (caretInParent.getHeight() - suggestionBounds.getHeight()) / 2;

        commandSuggestionText.relocate(x, y);
    }
}
