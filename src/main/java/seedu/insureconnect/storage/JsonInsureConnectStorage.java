package seedu.insureconnect.storage;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

import seedu.insureconnect.commons.core.LogsCenter;
import seedu.insureconnect.commons.exceptions.DataLoadingException;
import seedu.insureconnect.commons.exceptions.IllegalValueException;
import seedu.insureconnect.commons.util.FileUtil;
import seedu.insureconnect.commons.util.JsonUtil;
import seedu.insureconnect.model.ReadOnlyInsureConnect;

/**
 * A class to access InsureConnect data stored as a JSON file on the hard disk.
 */
public class JsonInsureConnectStorage {

    private static final Logger logger = LogsCenter.getLogger(JsonInsureConnectStorage.class);

    private final Set<Path> unreadableFilePaths = new HashSet<>();
    private final Path filePath;

    /**
     * Creates a storage that reads and writes the customer data file at {@code filePath}.
     */
    public JsonInsureConnectStorage(Path filePath) {
        this.filePath = requireNonNull(filePath);
    }

    /**
     * Returns the path of the customer data file this storage uses by default.
     */
    public Path getInsureConnectFilePath() {
        return filePath;
    }

    /**
     * Returns true if the data file at {@code filePath} failed to load and is marked unreadable.
     */
    public boolean isUnreadableFile(Path filePath) {
        requireNonNull(filePath);
        return unreadableFilePaths.contains(filePath.toAbsolutePath().normalize());
    }

    /**
     * Returns InsureConnect data as a {@link ReadOnlyInsureConnect}.
     * Returns {@code Optional.empty()} if storage file is not found.
     *
     * @throws DataLoadingException if loading the data from storage failed.
     */
    public Optional<ReadOnlyInsureConnect> readInsureConnect() throws DataLoadingException {
        return readInsureConnect(filePath);
    }

    /**
     * Similar to {@link #readInsureConnect()}.
     *
     * @param filePath location of the data. Cannot be null.
     * @throws DataLoadingException if loading the data from storage failed.
     */
    public Optional<ReadOnlyInsureConnect> readInsureConnect(Path filePath) throws DataLoadingException {
        requireNonNull(filePath);
        Path normalizedPath = filePath.toAbsolutePath().normalize();

        try {
            Optional<JsonSerializableInsureConnect> jsonInsureConnect = JsonUtil.readJsonFile(
                    filePath, JsonSerializableInsureConnect.class);
            if (!jsonInsureConnect.isPresent()) {
                return Optional.empty();
            }

            JsonSerializableInsureConnect serializedData = jsonInsureConnect.get();
            ReadOnlyInsureConnect loaded = serializedData.toModelType();
            unreadableFilePaths.remove(normalizedPath);
            return Optional.of(loaded);
        } catch (DataLoadingException | IllegalValueException e) {
            unreadableFilePaths.add(normalizedPath);
            if (e instanceof DataLoadingException) {
                throw (DataLoadingException) e;
            } else {
                logger.info("Illegal values found in " + filePath + ": " + e.getMessage());
                throw new DataLoadingException(e);
            }
        }
    }

    /**
     * Saves the given {@link ReadOnlyInsureConnect} to the storage.
     * @param insureConnect cannot be null.
     * @throws IOException if there was any problem writing to the file.
     */
    public void saveInsureConnect(ReadOnlyInsureConnect insureConnect) throws IOException {
        saveInsureConnect(insureConnect, filePath);
    }

    /**
     * Similar to {@link #saveInsureConnect(ReadOnlyInsureConnect)}.
     *
     * @param filePath location of the data. Cannot be null.
     */
    public void saveInsureConnect(ReadOnlyInsureConnect insureConnect, Path filePath) throws IOException {
        requireNonNull(insureConnect);
        requireNonNull(filePath);
        Path normalizedPath = filePath.toAbsolutePath().normalize();

        if (unreadableFilePaths.contains(normalizedPath)) {
            throw new IOException("The existing data file at " + filePath
                    + " is unreadable and cannot be overwritten.");
        }

        FileUtil.createIfMissing(filePath);
        JsonUtil.saveJsonFile(new JsonSerializableInsureConnect(insureConnect), filePath);
    }

}
