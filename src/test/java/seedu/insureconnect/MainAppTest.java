package seedu.insureconnect;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.insureconnect.testutil.TypicalPersons.getTypicalInsureConnect;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import seedu.insureconnect.commons.core.ColourScheme;
import seedu.insureconnect.commons.core.GuiSettings;
import seedu.insureconnect.model.InsureConnect;
import seedu.insureconnect.model.ReadOnlyUserPrefs;
import seedu.insureconnect.model.UserPrefs;
import seedu.insureconnect.model.person.Address;
import seedu.insureconnect.storage.JsonInsureConnectStorage;
import seedu.insureconnect.storage.JsonUserPrefsStorage;
import seedu.insureconnect.storage.StorageManager;
import seedu.insureconnect.ui.GuiTest;

/**
 * Exercises application startup and shutdown using isolated, real storage
 * files.
 */
public class MainAppTest extends GuiTest {
    @TempDir
    public Path temporaryFolder;

    @Test
    public void init_missingFiles_startsEmptyWithoutWarningAndCreatesPreferences() throws Exception {
        MainApp app = newApplication();
        app.init();
        assertEquals(new InsureConnect(), app.model.getInsureConnect());
        assertNull(app.initialFeedback);
        assertEquals(new UserPrefs(), app.storage.readUserPrefs().orElseThrow());
        assertFalse(Files.exists(dataPath()));
        assertEquals(preferencesPath(), app.storage.getUserPrefsFilePath());
        assertEquals(dataPath(), app.storage.getInsureConnectFilePath());
        assertNotNull(app.logic);
        assertNotNull(app.ui);
    }

    @Test
    public void init_existingFiles_restoresCustomersAndPreferences() throws Exception {
        InsureConnect data = getTypicalInsureConnect();
        new JsonInsureConnectStorage(dataPath()).saveInsureConnect(data);
        UserPrefs prefs = new UserPrefs();
        prefs.setGuiSettings(new GuiSettings(800, 700, 20, 30).withColourScheme(ColourScheme.LIGHT));
        new JsonUserPrefsStorage(preferencesPath()).saveUserPrefs(prefs);
        MainApp app = newApplication();
        app.init();
        assertEquals(data, app.model.getInsureConnect());
        assertEquals(prefs, app.model.getUserPrefs());
        assertEquals(data.getPersonList(), app.logic.getFilteredPersonList());
        assertEquals(prefs.getGuiSettings(), app.logic.getGuiSettings());
        assertNull(app.initialFeedback);
    }

    @Test
    public void init_longAddress_preservesAddressAndShowsNoWarning() throws Exception {
        new JsonInsureConnectStorage(dataPath()).saveInsureConnect(getTypicalInsureConnect());
        String longAddress = "x".repeat(Address.MAX_LENGTH + 1);
        String originalData = Files.readString(dataPath()).replace("123, Jurong West Ave 6, #08-111", longAddress);
        Files.writeString(dataPath(), originalData);

        MainApp app = newApplication();
        app.init();

        assertEquals(longAddress, app.model.getInsureConnect().getPersonList().get(0).getAddress().value);
        assertNull(app.initialFeedback);
        assertEquals(originalData, Files.readString(dataPath()));
    }

    @Test
    public void init_corruptFiles_preservesCustomerFileAndShowsWarning() throws Exception {
        Files.writeString(dataPath(), "invalid customers");
        Files.writeString(preferencesPath(), "invalid preferences");
        MainApp app = newApplication();
        app.init();
        String warning = String.format(MainApp.MESSAGE_DATA_FILE_ERROR, dataPath());
        assertEquals(warning, app.initialFeedback);
        assertEquals(new InsureConnect(), app.model.getInsureConnect());
        assertEquals(new UserPrefs(), app.model.getUserPrefs());
        assertEquals("invalid customers", Files.readString(dataPath()));
        runOnFxThread(() -> {
            Stage stage = new Stage();
            app.start(stage);
            assertTrue(stage.isShowing());
            TextArea feedback = (TextArea) stage.getScene().lookup("#resultDisplay");
            assertEquals(warning, feedback.getText());
        });
    }

    @Test
    public void init_nullTags_preservesCustomerFileAndShowsWarning() throws Exception {
        new JsonInsureConnectStorage(dataPath()).saveInsureConnect(getTypicalInsureConnect());
        String invalidData = Files.readString(dataPath()).replaceFirst(
                "\"tags\"\\s*:\\s*\\[[^\\]]*\\]",
                "\"tags\" : null");
        Files.writeString(dataPath(), invalidData);

        MainApp app = newApplication();
        app.init();

        String warning = String.format(MainApp.MESSAGE_DATA_FILE_ERROR, dataPath());
        assertEquals(warning, app.initialFeedback);
        assertEquals(new InsureConnect(), app.model.getInsureConnect());
        assertEquals(invalidData, Files.readString(dataPath()));

        runOnFxThread(() -> {
            Stage stage = new Stage();
            assertDoesNotThrow(() -> app.start(stage));
            assertTrue(stage.isShowing());

            TextArea feedback = (TextArea) stage.getScene().lookup("#resultDisplay");
            assertEquals(warning, feedback.getText());
        });
    }

    @Test
    public void stop_savesLatestPreferencesForNextLaunch() throws Exception {
        MainApp app = newApplication();
        app.init();
        GuiSettings settings = new GuiSettings(900, 650, 60, 70).withColourScheme(ColourScheme.LIGHT);
        app.logic.setGuiSettings(settings);
        app.stop();
        MainApp relaunched = newApplication();
        relaunched.init();
        assertEquals(settings, relaunched.logic.getGuiSettings());
    }

    @Test
    public void initPrefs_saveFailure_keepsLoadedPreferences() throws Exception {
        UserPrefs expected = new UserPrefs();
        expected.setGuiSettings(new GuiSettings().withColourScheme(ColourScheme.LIGHT));
        new JsonUserPrefsStorage(preferencesPath()).saveUserPrefs(expected);
        MainApp app = new MainApp();
        assertEquals(expected, app.initPrefs(failingPreferencesStorage()));
    }

    @Test
    public void start_offScreenPreferences_savesCorrectedGeometry() throws Exception {
        UserPrefs prefs = offScreenPreferences();
        new JsonUserPrefsStorage(preferencesPath()).saveUserPrefs(prefs);
        MainApp app = newApplication();
        app.init();

        runOnFxThread(() -> app.start(new Stage()));

        GuiSettings corrected = app.model.getGuiSettings();
        assertFalse(prefs.getGuiSettings().equals(corrected));
        assertEquals(corrected, app.storage.readUserPrefs().orElseThrow().getGuiSettings());
    }

    @Test
    public void start_correctedPreferencesSaveFailure_stillShowsWindow() throws Exception {
        UserPrefs prefs = offScreenPreferences();
        new JsonUserPrefsStorage(preferencesPath()).saveUserPrefs(prefs);
        MainApp app = newApplication();
        app.init();
        app.storage = new StorageManager(new JsonInsureConnectStorage(dataPath()), failingPreferencesStorage());

        runOnFxThread(() -> {
            Stage stage = new Stage();
            assertDoesNotThrow(() -> app.start(stage));
            assertTrue(stage.isShowing());
        });

        assertFalse(prefs.getGuiSettings().equals(app.model.getGuiSettings()));
        assertEquals(prefs.getGuiSettings(), app.storage.readUserPrefs().orElseThrow().getGuiSettings());
    }

    @Test
    public void stop_saveFailure_doesNotPreventShutdown() throws Exception {
        MainApp app = newApplication();
        app.init();
        app.storage = new StorageManager(new JsonInsureConnectStorage(dataPath()), failingPreferencesStorage());
        assertDoesNotThrow(app::stop);
        assertEquals(new UserPrefs(), app.model.getUserPrefs());
    }

    /**
     * Creates an application whose writes are confined to this test's temporary
     * directory.
     */
    private MainApp newApplication() {
        return new MainApp(preferencesPath(), dataPath());
    }

    private UserPrefs offScreenPreferences() {
        UserPrefs prefs = new UserPrefs();
        prefs.setGuiSettings(new GuiSettings(100000, 100000, -100000, -100000));
        return prefs;
    }

    private Path preferencesPath() {
        return temporaryFolder.resolve("preferences.json");
    }

    private Path dataPath() {
        return temporaryFolder.resolve("addressbook.json");
    }

    /**
     * Simulates an unwritable preferences file without platform-specific permission
     * changes.
     */
    private JsonUserPrefsStorage failingPreferencesStorage() {
        return new JsonUserPrefsStorage(preferencesPath()) {
            @Override
            public void saveUserPrefs(ReadOnlyUserPrefs prefs) throws IOException {
                throw new IOException("Simulated preferences write failure");
            }
        };
    }
}
