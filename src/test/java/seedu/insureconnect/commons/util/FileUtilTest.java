package seedu.insureconnect.commons.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Checks file creation, preservation and UTF-8 I/O with isolated test files.
 */
public class FileUtilTest {
    @TempDir
    public Path temporaryFolder;

    @Test
    public void createIfMissing_nestedFile_createsParentsAndPreservesExistingContent() throws IOException {
        Path file = temporaryFolder.resolve("nested/directory/contacts.txt");
        FileUtil.createIfMissing(file);
        assertTrue(Files.isRegularFile(file));
        String content = "José 李\nSecond line";
        FileUtil.writeToFile(file, content);
        FileUtil.createIfMissing(file);
        assertEquals(content, FileUtil.readFromFile(file));
    }

    @Test
    public void createIfMissing_filenameWithoutParent_createsFile() throws IOException {
        Path file = Path.of("file-util-" + UUID.randomUUID() + ".tmp");
        try {
            FileUtil.createIfMissing(file);
            assertTrue(Files.isRegularFile(file));
            assertEquals("", FileUtil.readFromFile(file));
        } finally {
            Files.deleteIfExists(file);
        }
    }

    @Test
    public void readFromFile_missingFile_throwsIoException() {
        Path file = temporaryFolder.resolve("missing.txt");
        assertThrows(IOException.class, () -> FileUtil.readFromFile(file));
    }

    @Test
    public void createIfMissing_parentIsRegularFile_throwsIoExceptionAndPreservesFile() throws IOException {
        Path file = temporaryFolder.resolve("file.txt");
        Files.writeString(file, "preserve me");
        assertThrows(IOException.class, () -> FileUtil.createIfMissing(file.resolve("child.txt")));
        assertEquals("preserve me", Files.readString(file));
    }
}
