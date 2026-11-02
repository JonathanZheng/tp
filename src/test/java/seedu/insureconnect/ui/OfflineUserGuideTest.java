package seedu.insureconnect.ui;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class OfflineUserGuideTest {

    @TempDir
    public Path tempDir;

    @Test
    public void extract_bundledGuide_copiesCompleteResource() throws Exception {
        Path guide = OfflineUserGuide.extract();
        try (InputStream source = OfflineUserGuide.class.getResourceAsStream("/docs/UserGuide.html")) {
            assertNotNull(source);
            assertArrayEquals(source.readAllBytes(), Files.readAllBytes(guide));
            String html = Files.readString(guide);
            assertTrue(html.contains("data:image/png;base64,"));
            assertTrue(html.contains("id=\"features\""));
            assertTrue(html.contains("id=\"command-summary\""));
        } finally {
            Files.deleteIfExists(guide);
        }
    }

    @Test
    public void extract_missingResource_reportsRecovery() {
        IOException failure = assertThrows(IOException.class, () -> OfflineUserGuide.extract(null));
        assertTrue(failure.getMessage().contains("Download a fresh copy"));
    }

    @Test
    public void open_localGuide_passesFileUriWithEscapedSpaces() throws Exception {
        Path guide = Files.writeString(tempDir.resolve("user guide.html"), "guide");
        List<URI> opened = new ArrayList<>();
        OfflineUserGuide.open(guide, opened::add);
        assertEquals(List.of(guide.toUri()), opened);
        assertEquals("file", opened.getFirst().getScheme());
        assertTrue(opened.getFirst().toString().contains("%20"));
    }

    @Test
    public void open_withBrowserOpener_extractsBundledGuide() throws Exception {
        List<URI> opened = new ArrayList<>();
        OfflineUserGuide.open(opened::add);
        assertEquals(1, opened.size());
    }

    @Test
    public void open_browserUnavailable_reportsReadableFileLocation() throws Exception {
        Path guide = Files.writeString(tempDir.resolve("guide.html"), "guide");
        // EP: operating-system integration fails or is unavailable/restricted
        for (Exception failure : List.of(new IOException("No browser"),
                new UnsupportedOperationException("Unsupported"), new SecurityException("Denied"))) {
            IOException actual = assertThrows(IOException.class, () -> OfflineUserGuide.open(guide, uri -> {
                if (failure instanceof IOException ioException) {
                    throw ioException;
                }
                throw (RuntimeException) failure;
            }));
            assertTrue(actual.getMessage().contains(guide.toAbsolutePath().toString()));
            assertSame(failure, actual.getCause());
            assertEquals("guide", Files.readString(guide));
        }
    }
}
