package seedu.insureconnect.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Tests area traversal and shortcut isolation without requiring a graphical display.
 */
public class FocusNavigationTest {

    private final FocusTarget commandInput = new FocusTarget();
    private final FocusTarget commandOutput = new FocusTarget();
    private final FocusTarget contacts = new FocusTarget();
    private final FocusNavigation navigation = new FocusNavigation(commandInput, commandOutput, contacts);

    @Test
    public void handleKeyPressed_forwardShortcut_cyclesAndWraps() {
        assertMovesFocus(commandInput, commandOutput, false);
        assertMovesFocus(commandOutput, contacts, false);
        assertMovesFocus(contacts, commandInput, false);
    }

    @Test
    public void handleKeyPressed_reverseShortcut_cyclesAndWraps() {
        assertMovesFocus(commandInput, contacts, true);
        assertMovesFocus(contacts, commandOutput, true);
        assertMovesFocus(commandOutput, commandInput, true);
    }

    @Test
    public void handleKeyPressed_childTarget_usesContainingArea() {
        Group listCell = new Group();
        Group cellContent = new Group();
        listCell.getChildren().add(cellContent);
        contacts.getChildren().add(listCell);
        assertMovesFocus(cellContent, commandInput, false);
        assertMovesFocus(cellContent, commandOutput, true);
    }

    @Test
    public void handleKeyPressed_outsideCycle_entersAtAppropriateEnd() {
        assertMovesFocus(new Group(), commandInput, false);
        assertMovesFocus(new Group(), contacts, true);
    }

    @Test
    public void handleKeyPressed_ordinaryKeys_doesNotIntercept() {
        KeyCode[] ordinaryKeys = {
            // EP: keys that edit or submit text
            KeyCode.A, KeyCode.ENTER,
            // EP: keys that move within a control
            KeyCode.TAB, KeyCode.UP, KeyCode.DOWN,
            // EP: function keys other than F6
            KeyCode.F1,
        };
        for (KeyCode code : ordinaryKeys) {
            assertIgnored(new KeyEvent(commandInput, commandInput, KeyEvent.KEY_PRESSED, "", "", code,
                    false, false, false, false));
        }
    }

    @Test
    public void handleKeyPressed_modifiedFunctionKey_doesNotIntercept() {
        assertIgnored(new KeyEvent(commandInput, commandInput, KeyEvent.KEY_PRESSED, "", "", KeyCode.F6,
                false, true, false, false));
        assertIgnored(new KeyEvent(commandInput, commandInput, KeyEvent.KEY_PRESSED, "", "", KeyCode.F6,
                false, false, true, false));
        assertIgnored(new KeyEvent(commandInput, commandInput, KeyEvent.KEY_PRESSED, "", "", KeyCode.F6,
                false, false, false, true));
    }

    @Test
    public void constructor_focusTargets_marksAllAreasForVisibleFocus() {
        assertTrue(commandInput.getStyleClass().contains("navigation-target"));
        assertTrue(commandOutput.getStyleClass().contains("navigation-target"));
        assertTrue(contacts.getStyleClass().contains("navigation-target"));
    }

    @Test
    public void handleKeyPressed_withoutNodeTarget_entersCycle() {
        KeyEvent event = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.F6,
                false, false, false, false);
        navigation.handleKeyPressed(event);
        assertEquals(1, commandInput.focusRequests);
        assertTrue(event.isConsumed());
    }

    /**
     * Verifies one traversal step and shortcut consumption.
     */
    private void assertMovesFocus(Node currentTarget, FocusTarget expectedTarget, boolean isReverse) {
        int previousRequests = expectedTarget.focusRequests;
        KeyEvent event = new KeyEvent(currentTarget, currentTarget, KeyEvent.KEY_PRESSED, "", "", KeyCode.F6,
                isReverse, false, false, false);
        navigation.handleKeyPressed(event);
        assertEquals(previousRequests + 1, expectedTarget.focusRequests);
        assertTrue(event.isConsumed());
    }

    /**
     * Verifies that unrelated shortcuts remain available to their controls.
     */
    private void assertIgnored(KeyEvent event) {
        navigation.handleKeyPressed(event);
        assertFalse(event.isConsumed());
        assertEquals(0, commandInput.focusRequests);
        assertEquals(0, commandOutput.focusRequests);
        assertEquals(0, contacts.focusRequests);
    }

    /**
     * Records focus requests without creating a native window.
     */
    private static class FocusTarget extends Group {
        private int focusRequests;

        @Override
        public void requestFocus() {
            focusRequests++;
        }
    }
}
