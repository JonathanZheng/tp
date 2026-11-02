package seedu.insureconnect.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static seedu.insureconnect.testutil.TypicalPersons.ALICE;

import java.awt.Point;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.event.Event;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import seedu.insureconnect.commons.core.ColourScheme;
import seedu.insureconnect.commons.core.GuiSettings;
import seedu.insureconnect.logic.commands.CommandResult;
import seedu.insureconnect.logic.commands.WindowResize;
import seedu.insureconnect.logic.commands.WindowResize.Preset;
import seedu.insureconnect.logic.commands.exceptions.CommandException;
import seedu.insureconnect.logic.parser.exceptions.ParseException;

public class MainWindowTest extends GuiTest {

    private static final String READY_MESSAGE =
            "Ready. Type a command to search or manage customers.";

    @Test
    public void fitToScreen_maximizedGeometry_keepsResizeEdgesVisible() {
        GuiSettings saved = new GuiSettings(1936, 1048, -8, -8).withColourScheme(ColourScheme.LIGHT);
        GuiSettings fitted = MainWindow.fitToScreen(saved, new Rectangle2D(0, 0, 1920, 1040));

        assertEquals(1896, fitted.getWindowWidth());
        assertEquals(1016, fitted.getWindowHeight());
        assertEquals(new Point(0, 0), fitted.getWindowCoordinates());
        assertEquals(ColourScheme.LIGHT, fitted.getColourScheme());
    }

    @Test
    public void fitToScreen_windowAlreadyFits_keepsGeometry() {
        GuiSettings saved = new GuiSettings(900, 700, 30, 40);
        GuiSettings fitted = MainWindow.fitToScreen(saved, new Rectangle2D(0, 34, 1710, 1018));

        assertEquals(saved, fitted);
    }

    @Test
    public void overlapArea_partialWindow_usesIntersectingArea() {
        Rectangle2D window = new Rectangle2D(1800, 100, 500, 600);
        assertEquals(72000, MainWindow.overlapArea(window, new Rectangle2D(0, 0, 1920, 1080)));
        assertEquals(228000, MainWindow.overlapArea(window, new Rectangle2D(1920, 0, 1920, 1080)));
        assertEquals(0, MainWindow.overlapArea(window, new Rectangle2D(4000, 0, 1920, 1080)));
    }

    @Test
    public void resizePresetAndCustom_applyBoundsAndSaveGeometry() throws Exception {
        runOnFxThread(() -> {
            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            assumeTrue(bounds.getWidth() >= 904 && bounds.getHeight() >= 624);
            UiLogicStub logic = new UiLogicStub();
            MainWindow window = createWindow(logic);
            logic.setExecutor(command -> new CommandResult("Window resized.", WindowResize.preset(Preset.MIN)));
            submit(window, "resize p/min");
            assertEquals(880, window.getRoot().getWidth());
            assertEquals(600, window.getRoot().getHeight());

            logic.setExecutor(command -> new CommandResult("Window resized.", WindowResize.preset(Preset.MED)));
            submit(window, "resize p/med");
            assertEquals(Math.max(880, Math.floor(bounds.getWidth() * 2 / 3)), window.getRoot().getWidth());
            assertEquals(Math.max(600, Math.floor(bounds.getHeight() * 2 / 3)), window.getRoot().getHeight());

            window.getRoot().setX(bounds.getMinX());
            window.getRoot().setY(bounds.getMinY());
            logic.setExecutor(command -> new CommandResult("Window resized.", WindowResize.preset(Preset.MAX)));
            submit(window, "resize p/max");
            assertEquals(bounds.getWidth() - 24, window.getRoot().getWidth());
            assertEquals(bounds.getHeight() - 24, window.getRoot().getHeight());
            assertEquals(window.getRoot().getWidth(), logic.getGuiSettings().getWindowWidth());
            assertEquals(12, window.getRoot().getX() - bounds.getMinX());
            assertEquals(12, window.getRoot().getY() - bounds.getMinY());
            assertEquals(12, bounds.getMaxX() - window.getRoot().getX() - window.getRoot().getWidth());
            assertEquals(12, bounds.getMaxY() - window.getRoot().getY() - window.getRoot().getHeight());

            logic.setExecutor(command -> new CommandResult("Window resized.", WindowResize.custom(880, 600)));
            submit(window, "resize s/880 600");
            assertEquals(880, window.getRoot().getWidth());
            assertEquals(600, window.getRoot().getHeight());
        });
    }

    @Test
    public void resizeTooLarge_displaysErrorAndRetainsInput() throws Exception {
        runOnFxThread(() -> {
            UiLogicStub logic = new UiLogicStub();
            MainWindow window = createWindow(logic);
            double originalWidth = window.getRoot().getWidth();
            logic.setExecutor(command -> new CommandResult("Window resized.",
                    WindowResize.custom(Integer.MAX_VALUE, Integer.MAX_VALUE)));
            submit(window, "resize s/2147483647 2147483647");
            assertEquals(originalWidth, window.getRoot().getWidth());
            assertEquals("resize s/2147483647 2147483647", inputOf(window).getText());
            assertTrue(outputOf(window).getText().contains("exceeds"));
        });
    }

    @Test
    public void resizeScreenTooSmall_rejectsEitherInsufficientDimension() throws Exception {
        runOnFxThread(() -> {
            MainWindow window = createWindow(new UiLogicStub());
            WindowResize request = WindowResize.preset(Preset.MIN);

            CommandException narrow = assertThrows(CommandException.class, ()
                    -> window.resizeWindow(request, new Rectangle2D(0, 0, 900, 700)));
            assertTrue(narrow.getMessage().contains("screen is too small"));

            CommandException shortScreen = assertThrows(CommandException.class, ()
                    -> window.resizeWindow(request, new Rectangle2D(0, 0, 1000, 620)));
            assertTrue(shortScreen.getMessage().contains("screen is too small"));
        });
    }

    @Test
    public void resizeCustomHeightTooLarge_rejectsRequest() throws Exception {
        runOnFxThread(() -> {
            MainWindow window = createWindow(new UiLogicStub());
            Rectangle2D bounds = new Rectangle2D(0, 0, 1200, 800);

            CommandException error = assertThrows(CommandException.class, ()
                    -> window.resizeWindow(WindowResize.custom(880, 800), bounds));
            assertTrue(error.getMessage().contains("exceeds"));
        });
    }

    @Test
    public void fitToScreen_savedPosition_neverPushesWindowPastMonitorEdge() {
        GuiSettings saved = new GuiSettings(900, 700, 3000, 900);
        GuiSettings fitted = MainWindow.fitToScreen(saved, new Rectangle2D(1920, 0, 1920, 1040));

        assertEquals(900, fitted.getWindowWidth());
        assertEquals(700, fitted.getWindowHeight());
        assertEquals(new Point(2928, 328), fitted.getWindowCoordinates());
    }

    @Test
    public void initialize_savedGeometryAndFeedback_fitsWindowAndRestoresCustomerWorkspace() throws Exception {
        runOnFxThread(() -> {
            UiLogicStub logic = new UiLogicStub();
            GuiSettings saved = new GuiSettings(900, 700, 30, 40);
            logic.setGuiSettings(saved);
            logic.getFilteredPersonList().add(ALICE);
            MainWindow window = new MainWindow(new Stage(), logic, Path.of("data", "test.json"), "Load warning");
            GuiSettings fitted = MainWindow.fitToScreen(saved, Screen.getPrimary().getVisualBounds());
            assertEquals(fitted, logic.getGuiSettings());
            assertEquals(fitted.getWindowWidth(), window.getRoot().getWidth());
            assertEquals(fitted.getWindowHeight(), window.getRoot().getHeight());
            assertEquals(fitted.getWindowCoordinates().getX(), window.getRoot().getX());
            assertEquals(fitted.getWindowCoordinates().getY(), window.getRoot().getY());
            window.show();
            window.fillInnerParts();
            window.getRoot().getScene().getRoot().applyCss();
            window.getRoot().getScene().getRoot().layout();
            assertEquals("Load warning", outputOf(window).getText());
            ListView<?> customers = customerListOf(window);
            assertEquals(List.of(ALICE), customers.getItems());
            assertSame(ALICE, customers.getSelectionModel().getSelectedItem());
            TextInputControl displayedName =
                    (TextInputControl) window.getRoot().getScene().lookup("#detailName");
            assertEquals(ALICE.getName().fullName, displayedName.getText());
        });
    }

    @Test
    public void submit_success_displaysFeedbackAndClearsInput() throws Exception {
        runOnFxThread(() -> {
            UiLogicStub logic = new UiLogicStub();
            MainWindow window = createWindow(logic);
            assertEquals(READY_MESSAGE, outputOf(window).getText());
            submit(window, "list");
            assertEquals(List.of("list"), logic.getCommands());
            assertEquals("Executed: list", outputOf(window).getText());
            assertEquals("", inputOf(window).getText());
        });
    }

    @Test
    public void submit_parseFailure_displaysErrorAndRetainsInput() throws Exception {
        runOnFxThread(() -> assertCommandFailure(command -> {
            throw new ParseException("Invalid input");
        }));
    }

    @Test
    public void submit_commandFailure_displaysErrorAndRetainsInput() throws Exception {
        runOnFxThread(() -> assertCommandFailure(command -> {
            throw new CommandException("Invalid input");
        }));
    }

    @Test
    public void submit_userGuideCommand_opensUserGuide() throws Exception {
        runOnFxThread(() -> {
            UiLogicStub logic = new UiLogicStub();
            List<String> openedUrls = new ArrayList<>();
            logic.setExecutor(command -> new CommandResult("Opening User Guide",
                    false, false, false, true, null));
            MainWindow.UserGuideOpener userGuideOpener = () ->
                    openedUrls.add(HelpWindow.USERGUIDE_URL);
            MainWindow window = createWindow(logic, userGuideOpener);

            // EP: User Guide opens successfully
            submit(window, "userguide");

            assertEquals(List.of(HelpWindow.USERGUIDE_URL), openedUrls);
            assertEquals("Opening User Guide", outputOf(window).getText());
        });
    }

    @Test
    public void submit_userGuideCommand_browserFailure() throws Exception {
        runOnFxThread(() -> {
            UiLogicStub logic = new UiLogicStub();
            logic.setExecutor(command -> new CommandResult("Opening User Guide",
                    false, false, false, true, null));
            MainWindow window = createWindow(logic, () -> {
                throw new IOException("Open the local guide at /tmp/guide.html");
            });

            // EP: browser cannot open the User Guide
            submit(window, "userguide");

            assertEquals("Open the local guide at /tmp/guide.html", outputOf(window).getText());
            assertEquals("userguide", inputOf(window).getText());
        });
    }

    @Test
    public void submit_helpThenExit_showsHelpAndClosesBothWindows() throws Exception {
        runOnFxThread(() -> {
            UiLogicStub logic = new UiLogicStub();
            MainWindow window = createWindow(logic);
            logic.setExecutor(command -> new CommandResult("Help", true, false));
            submit(window, "help");
            Stage help = helpStage();
            assertTrue(help.isShowing());
            window.handleHelp();
            assertSame(help, helpStage());
            logic.setExecutor(command -> new CommandResult("Bye", false, true));
            submit(window, "exit");
            assertFalse(window.getRoot().isShowing());
            assertFalse(help.isShowing());
            assertEquals(window.getRoot().getWidth(), logic.getGuiSettings().getWindowWidth());
            assertEquals(window.getRoot().getHeight(), logic.getGuiSettings().getWindowHeight());
        });
    }

    @Test
    public void closeRequest_savesWindowPositionAndHidesStage() throws Exception {
        runOnFxThread(() -> {
            UiLogicStub logic = new UiLogicStub();
            MainWindow window = createWindow(logic);
            Event.fireEvent(window.getRoot(), new WindowEvent(window.getRoot(), WindowEvent.WINDOW_CLOSE_REQUEST));
            assertFalse(window.getRoot().isShowing());
            assertEquals((int) window.getRoot().getX(), logic.getGuiSettings().getWindowCoordinates().x);
            assertEquals((int) window.getRoot().getY(), logic.getGuiSettings().getWindowCoordinates().y);
        });
    }

    @Test
    public void closeRequest_oversizedWindow_savesGeometryWithinMonitor() throws Exception {
        runOnFxThread(() -> {
            UiLogicStub logic = new UiLogicStub();
            MainWindow window = createWindow(logic);
            var bounds = Screen.getPrimary().getVisualBounds();
            window.getRoot().setX(bounds.getMinX());
            window.getRoot().setY(bounds.getMinY());
            window.getRoot().setWidth(bounds.getWidth() + 16);
            window.getRoot().setHeight(bounds.getHeight() + 16);

            Event.fireEvent(window.getRoot(), new WindowEvent(window.getRoot(), WindowEvent.WINDOW_CLOSE_REQUEST));

            GuiSettings saved = logic.getGuiSettings();
            assertTrue(saved.getWindowWidth() < bounds.getWidth());
            assertTrue(saved.getWindowHeight() < bounds.getHeight());
            assertTrue(saved.getWindowCoordinates().x >= bounds.getMinX());
            assertTrue(saved.getWindowCoordinates().y >= bounds.getMinY());
        });
    }

    @Test
    public void helpMenuAndFunctionKey_openSameHelpWindow() throws Exception {
        runOnFxThread(() -> {
            MainWindow window = createWindow(new UiLogicStub());
            MenuBar menus = (MenuBar) window.getRoot().getScene().lookup("#menuBar");
            menus.getMenus().stream().flatMap(menu -> menu.getItems().stream())
                    .filter(item -> "helpMenuItem".equals(item.getId())).findFirst().orElseThrow().fire();
            Stage help = helpStage();
            help.hide();
            inputOf(window).fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.F1,
                    false, false, false, false));
            assertSame(help, helpStage());
            help.hide();
            outputOf(window).fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.F1,
                    false, false, false, false));
            assertSame(help, helpStage());
        });
    }

    /**
     * Verifies that both parser and command errors reach the user through the same UI path.
     */
    private void assertCommandFailure(CommandBox.CommandExecutor executor) {
        UiLogicStub logic = new UiLogicStub();
        logic.setExecutor(executor);
        MainWindow window = createWindow(logic);
        submit(window, "invalid draft");
        assertEquals("Invalid input", outputOf(window).getText());
        assertEquals("invalid draft", inputOf(window).getText());
        assertTrue(inputOf(window).getStyleClass().contains(CommandBox.ERROR_STYLE_CLASS));
    }

    /**
     * Creates a populated window using the same initialization order as UiManager.
     */
    private MainWindow createWindow(UiLogicStub logic) {
        MainWindow window = new MainWindow(new Stage(), logic, Path.of("test.json"));
        window.show();
        window.fillInnerParts();
        return window;
    }

    /**
     * Creates a populated window with a deterministic User Guide opener.
     */
    private MainWindow createWindow(UiLogicStub logic, MainWindow.UserGuideOpener userGuideOpener) {
        MainWindow window = new MainWindow(new Stage(), logic, Path.of("test.json"), null, userGuideOpener);
        window.show();
        window.fillInnerParts();
        return window;
    }

    /**
     * Submits text through the command input's Enter-key handler.
     */
    private void submit(MainWindow window, String command) {
        TextArea input = inputOf(window);
        input.setText(command);
        pressKey(input, KeyCode.ENTER);
    }

    /**
     * Retrieves the command input from the live scene.
     */
    private TextArea inputOf(MainWindow window) {
        return (TextArea) window.getRoot().getScene().lookup("#commandTextField");
    }

    /**
     * Retrieves the feedback control from the live scene.
     */
    private TextArea outputOf(MainWindow window) {
        return (TextArea) window.getRoot().getScene().lookup("#resultDisplay");
    }

    /**
     * Retrieves the customer list from the live scene.
     */
    private ListView<?> customerListOf(MainWindow window) {
        return (ListView<?>) window.getRoot().getScene().lookup("#personListView");
    }

    /**
     * Finds the visible help stage without exposing private controller fields.
     */
    private Stage helpStage() {
        return Window.getWindows().stream().filter(window -> window instanceof Stage)
                .map(window -> (Stage) window).filter(stage -> "Help".equals(stage.getTitle()))
                .findFirst().orElseThrow();
    }
}
