package seedu.insureconnect.storage;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.insureconnect.commons.core.LogsCenter;
import seedu.insureconnect.commons.exceptions.DataLoadingException;
import seedu.insureconnect.model.ReadOnlyInsureConnect;
import seedu.insureconnect.model.ReadOnlyUserPrefs;
import seedu.insureconnect.model.UserPrefs;

/**
 * Manages storage of InsureConnect data in local storage.
 */
public class StorageManager implements Storage {

    private static final Logger logger = LogsCenter.getLogger(StorageManager.class);
    private final JsonInsureConnectStorage insureConnectStorage;
    private final JsonUserPrefsStorage userPrefsStorage;

    /**
     * Creates a {@code StorageManager} with the given InsureConnect and user prefs storage.
     */
    public StorageManager(JsonInsureConnectStorage insureConnectStorage, JsonUserPrefsStorage userPrefsStorage) {
        this.insureConnectStorage = requireNonNull(insureConnectStorage);
        this.userPrefsStorage = requireNonNull(userPrefsStorage);
    }

    // ================ UserPrefs methods ==============================

    @Override
    public Path getUserPrefsFilePath() {
        return userPrefsStorage.getUserPrefsFilePath();
    }

    @Override
    public Optional<UserPrefs> readUserPrefs() throws DataLoadingException {
        return userPrefsStorage.readUserPrefs();
    }

    @Override
    public void saveUserPrefs(ReadOnlyUserPrefs userPrefs) throws IOException {
        userPrefsStorage.saveUserPrefs(userPrefs);
    }

    // ================ InsureConnect methods ==============================

    @Override
    public Path getInsureConnectFilePath() {
        return insureConnectStorage.getInsureConnectFilePath();
    }

    @Override
    public Optional<ReadOnlyInsureConnect> readInsureConnect() throws DataLoadingException {
        logger.fine("Attempting to read data from file: " + insureConnectStorage.getInsureConnectFilePath());
        return insureConnectStorage.readInsureConnect();
    }

    @Override
    public void saveInsureConnect(ReadOnlyInsureConnect insureConnect) throws IOException {
        logger.fine("Attempting to write to data file: " + insureConnectStorage.getInsureConnectFilePath());
        insureConnectStorage.saveInsureConnect(insureConnect);
    }

}
