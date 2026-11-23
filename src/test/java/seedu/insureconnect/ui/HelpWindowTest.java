package seedu.insureconnect.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class HelpWindowTest extends GuiTest {

    @Test
    public void show_afterFocusAndHide_reusesWindowWithCommandFormats() throws Exception {
        runOnFxThread(() -> {
            Stage stage = new Stage();
            HelpWindow help = new HelpWindow(stage);
            assertFalse(help.isShowing());
            Label message = (Label) stage.getScene().lookup("#helpMessage");
            assertEquals(HelpWindow.HELP_MESSAGE, message.getText());
            TextArea formats = (TextArea) stage.getScene().lookup("#commandFormats");
            String[] commands = {"add ", "edit ", "delete ", "find ", "list ", "clear", "help", "exit"};
            for (String command : commands) {
                assertTrue(formats.getText().contains(command));
            }
            help.show();
            assertTrue(help.isShowing());
            help.focus();
            assertTrue(help.isShowing());
            help.hide();
            assertFalse(help.isShowing());
            help.show();
            assertTrue(help.isShowing());
        });
    }

    @Test
    public void constructor_defaultStage_listsCombinedFindSyntax() throws Exception {
        runOnFxThread(() -> {
            HelpWindow help = new HelpWindow();
            TextArea formats = (TextArea) help.getRoot().getScene().lookup("#commandFormats");
            assertTrue(formats.getText().contains("find [n/KEYWORDS] [p/PHONE_PREFIX] [t/POLICY_NUMBER]"));
        });
    }

    @Test
    public void copyUrl_buttonFired_copiesUserGuideUrl() throws Exception {
        runOnFxThread(() -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            Map<DataFormat, Object> original = copyClipboardContents(clipboard);
            try {
                HelpWindow help = new HelpWindow();
                Button copy = (Button) help.getRoot().getScene().lookup("#copyButton");
                copy.fire();
                assertEquals(HelpWindow.USERGUIDE_URL, clipboard.getString());
            } finally {
                clipboard.setContent(original);
            }
        });
    }

    @Test
    public void escapeKey_closesHelpWindow() throws Exception {
        runOnFxThread(() -> {
            HelpWindow help = new HelpWindow();
            help.show();

            pressKey(help, KeyCode.ESCAPE);

            assertFalse(help.isShowing());
        });
    }

    @Test
    public void f1Key_closesHelpWindow() throws Exception {
        runOnFxThread(() -> {
            HelpWindow help = new HelpWindow();
            help.show();

            pressKey(help, KeyCode.F1);

            assertFalse(help.isShowing());
        });
    }

    @Test
    public void enterKey_doesNotCloseHelpWindow() throws Exception {
        runOnFxThread(() -> {
            HelpWindow help = new HelpWindow();
            help.show();

            pressKey(help, KeyCode.ENTER);

            assertTrue(help.isShowing());
        });
    }

    private void pressKey(HelpWindow help, KeyCode keyCode) {
        help.getRoot().getScene().getRoot().fireEvent(new KeyEvent(
                KeyEvent.KEY_PRESSED, "", "", keyCode,
                false, false, false, false));
    }

    /**
     * Captures every available format so the test can restore the user's clipboard.
     */
    private Map<DataFormat, Object> copyClipboardContents(Clipboard clipboard) {
        Map<DataFormat, Object> contents = new HashMap<>();
        for (DataFormat format : clipboard.getContentTypes()) {
            contents.put(format, clipboard.getContent(format));
        }
        return contents;
    }
}
