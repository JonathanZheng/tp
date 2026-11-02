package seedu.insureconnect;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.stage.Stage;
import seedu.insureconnect.commons.core.LogsCenter;
import seedu.insureconnect.commons.exceptions.DataLoadingException;
import seedu.insureconnect.commons.util.StringUtil;
import seedu.insureconnect.logic.Logic;
import seedu.insureconnect.logic.LogicManager;
import seedu.insureconnect.model.InsureConnect;
import seedu.insureconnect.model.Model;
import seedu.insureconnect.model.ModelManager;
import seedu.insureconnect.model.ReadOnlyInsureConnect;
import seedu.insureconnect.model.ReadOnlyUserPrefs;
import seedu.insureconnect.model.UserPrefs;
import seedu.insureconnect.storage.JsonInsureConnectStorage;
import seedu.insureconnect.storage.JsonUserPrefsStorage;
import seedu.insureconnect.storage.Storage;
import seedu.insureconnect.storage.StorageManager;
import seedu.insureconnect.ui.Ui;
import seedu.insureconnect.ui.UiManager;

/**
 * Runs the application.
 */
public class MainApp extends Application {

    public static final String VERSION = "V1.6";

    public static final String MESSAGE_DATA_FILE_ERROR =
        "Warning: Data file at %s could not be loaded. Starting with an empty customer list. "
                + "Customer records cannot be modified until the unreadable file is repaired or replaced "
                + "and InsureConnect is restarted.";
    private static final Logger logger = LogsCenter.getLogger(MainApp.class);
    private static final Path USER_PREFS_FILE_PATH = Paths.get("preferences.json");
    // Keep the legacy filename so existing installations continue to load their
    // records.
    private static final Path INSURE_CONNECT_FILE_PATH = Paths.get("data", "addressbook.json");

    protected Ui ui;
    protected Logic logic;
    protected Storage storage;
    protected Model model;
    protected String initialFeedback;

    private final Path userPrefsFilePath;
    private final Path insureConnectFilePath;

    /**
     * Creates the application using the existing installation's file locations.
     */
    public MainApp() {
        this(USER_PREFS_FILE_PATH, INSURE_CONNECT_FILE_PATH);
    }

    /**
     * Creates the application with isolated storage locations for lifecycle tests.
     */
    MainApp(Path userPrefsFilePath, Path insureConnectFilePath) {
        this.userPrefsFilePath = requireNonNull(userPrefsFilePath);
        this.insureConnectFilePath = requireNonNull(insureConnectFilePath);
    }

    @Override
    public void init() throws Exception {
        logger.info("=============================[ Initializing InsureConnect ]===========================");
        super.init();

        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(userPrefsFilePath);
        UserPrefs userPrefs = initPrefs(userPrefsStorage);
        JsonInsureConnectStorage insureConnectStorage = new JsonInsureConnectStorage(insureConnectFilePath);
        storage = new StorageManager(insureConnectStorage, userPrefsStorage);

        model = initModelManager(storage, userPrefs);

        logic = new LogicManager(model, storage);

        ui = new UiManager(logic, storage.getInsureConnectFilePath(), initialFeedback);
    }

    /**
     * Returns a {@code ModelManager} with the data from {@code storage}'s
     * InsureConnect and {@code userPrefs}. <br>
     * Uses an empty InsureConnect when the data file is missing or cannot be read.
     */
    private Model initModelManager(Storage storage, ReadOnlyUserPrefs userPrefs) {
        logger.info("Using data file : " + storage.getInsureConnectFilePath());

        Optional<ReadOnlyInsureConnect> insureConnectOptional;
        ReadOnlyInsureConnect initialData;
        try {
            insureConnectOptional = storage.readInsureConnect();
            if (insureConnectOptional.isEmpty()) {
                logger.info("No data file found. " + storage.getInsureConnectFilePath()
                        + " populated with an empty InsureConnect.");
            }
            initialData = insureConnectOptional.orElseGet(InsureConnect::new);
        } catch (DataLoadingException e) {
            logger.warning("Data file at " + storage.getInsureConnectFilePath() + " could not be loaded."
                    + " Will be starting with an empty InsureConnect.");
            initialFeedback = String.format(MESSAGE_DATA_FILE_ERROR, storage.getInsureConnectFilePath());
            initialData = new InsureConnect();
        }

        return new ModelManager(initialData, userPrefs);
    }

    /**
     * Returns a {@code UserPrefs} using the file at {@code storage}'s user prefs
     * file path,
     * or a new {@code UserPrefs} with default configuration if errors occur when
     * reading from the file.
     */
    protected UserPrefs initPrefs(JsonUserPrefsStorage storage) {
        Path prefsFilePath = storage.getUserPrefsFilePath();
        logger.info("Using preference file : " + prefsFilePath);

        UserPrefs initializedPrefs;
        try {
            Optional<UserPrefs> prefsOptional = storage.readUserPrefs();
            if (prefsOptional.isEmpty()) {
                logger.info("Creating new preference file " + prefsFilePath);
            }
            initializedPrefs = prefsOptional.orElse(new UserPrefs());
        } catch (DataLoadingException e) {
            logger.warning("Preference file at " + prefsFilePath + " could not be loaded."
                    + " Using default preferences.");
            initializedPrefs = new UserPrefs();
        }

        // Update prefs file in case it was missing to begin with or there are
        // new/unused fields
        try {
            storage.saveUserPrefs(initializedPrefs);
        } catch (IOException e) {
            logger.warning("Failed to save preference file : " + StringUtil.getDetails(e));
        }

        return initializedPrefs;
    }

    @Override
    public void start(Stage primaryStage) {
        logger.info("Starting InsureConnect " + MainApp.VERSION);
        var loadedGuiSettings = model.getGuiSettings();
        ui.start(primaryStage);
        if (!loadedGuiSettings.equals(model.getGuiSettings())) {
            try {
                storage.saveUserPrefs(model.getUserPrefs());
            } catch (IOException e) {
                logger.warning("Failed to save corrected window preferences " + StringUtil.getDetails(e));
            }
        }
    }

    @Override
    public void stop() {
        logger.info("============================ [ Stopping InsureConnect ] =============================");
        try {
            storage.saveUserPrefs(model.getUserPrefs());
        } catch (IOException e) {
            logger.severe("Failed to save preferences " + StringUtil.getDetails(e));
        }
    }
}
