package seedu.insureconnect.ui;

import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import seedu.insureconnect.commons.core.LogsCenter;
import seedu.insureconnect.logic.commands.CommandFormats;

/**
 * Controller for a help page
 */
public class HelpWindow extends UiPart<Stage> {

    public static final String USERGUIDE_URL = "https://AY2627S1-CS2103T-W09-4.github.io/tp/UserGuide.html";
    public static final String HELP_MESSAGE = "Offline guide: type userguide. Copy URL for the online guide.";

    private static final String COMMAND_FORMATS = String.join("\n\n",
            CommandFormats.ADD_COMMAND_FORMAT,
            CommandFormats.EDIT_COMMAND_FORMAT,
            CommandFormats.DELETE_COMMAND_FORMAT,
            CommandFormats.FIND_COMMAND_FORMAT,
            CommandFormats.LIST_COMMAND_FORMAT,
            CommandFormats.RESIZE_COMMAND_FORMAT,
            CommandFormats.CLEAR_COMMAND_FORMAT,
            CommandFormats.HELP_COMMAND_FORMAT,
            CommandFormats.USER_GUIDE_COMMAND_FORMAT,
            CommandFormats.EXIT_COMMAND_FORMAT);

    private static final Logger logger = LogsCenter.getLogger(HelpWindow.class);
    private static final String FXML = "HelpWindow.fxml";

    @FXML
    private Button copyButton;

    @FXML
    private Label helpMessage;

    @FXML
    private TextArea commandFormats;

    /**
     * Creates a new HelpWindow.
     *
     * @param root Stage to use as the root of the HelpWindow.
     */
    public HelpWindow(Stage root) {
        super(FXML, root);
        helpMessage.setText(HELP_MESSAGE);
        commandFormats.setText(COMMAND_FORMATS);
        getRoot().getScene().addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyPressed);
    }

    /**
     * Creates a new HelpWindow.
     */
    public HelpWindow() {
        this(new Stage());
    }

    /**
     * Shows the help window.
     * @throws IllegalStateException
     *     <ul>
     *         <li>
     *             if this method is called on a thread other than the JavaFX Application Thread.
     *         </li>
     *         <li>
     *             if this method is called during animation or layout processing.
     *         </li>
     *         <li>
     *             if this method is called on the primary stage.
     *         </li>
     *         <li>
     *             if {@code dialogStage} is already showing.
     *         </li>
     *     </ul>
     */
    public void show() {
        logger.fine("Showing help page about the application.");
        getRoot().show();
        getRoot().centerOnScreen();
    }

    /**
     * Returns true if the help window is currently being shown.
     */
    public boolean isShowing() {
        return getRoot().isShowing();
    }

    /**
     * Hides the help window.
     */
    public void hide() {
        getRoot().hide();
    }

    /**
     * Focuses on the help window.
     */
    public void focus() {
        getRoot().requestFocus();
    }

    /**
     * Closes the help window when Escape or F1 is pressed.
     */
    private void handleKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ESCAPE || event.getCode() == KeyCode.F1) {
            hide();
            event.consume();
        }
    }

    /**
     * Copies the URL to the user guide to the clipboard.
     */
    @FXML
    private void copyUrl() {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent url = new ClipboardContent();
        url.putString(USERGUIDE_URL);
        clipboard.setContent(url);
    }
}
