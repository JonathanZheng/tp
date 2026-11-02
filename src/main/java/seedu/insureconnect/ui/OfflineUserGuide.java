package seedu.insureconnect.ui;

import java.awt.Desktop;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Extracts and opens the bundled, self-contained User Guide.
 */
final class OfflineUserGuide {

    private OfflineUserGuide() {
    }

    /**
     * Opens a local copy of the guide in the default browser.
     */
    static void open() throws IOException {
        open(extract(), uri -> Desktop.getDesktop().browse(uri));
    }

    /**
     * Opens a local copy of the guide with a replacement browser integration.
     */
    static void open(BrowserOpener opener) throws IOException {
        open(extract(), opener);
    }

    /**
     * Opens the extracted file, reporting its location if browser integration fails.
     */
    static void open(Path guide, BrowserOpener opener) throws IOException {
        try {
            opener.open(guide.toUri());
        } catch (IOException | UnsupportedOperationException | SecurityException e) {
            throw new IOException("Unable to open the User Guide automatically. Open this file in your browser: "
                    + guide.toAbsolutePath(), e);
        }
    }

    /**
     * Extracts the guide without relying on the working directory or network.
     */
    static Path extract() throws IOException {
        return extract(OfflineUserGuide.class.getResourceAsStream("/docs/UserGuide.html"));
    }

    /**
     * Copies a bundled resource, reporting a missing guide as a recoverable error.
     */
    static Path extract(InputStream source) throws IOException {
        try (source) {
            if (source == null) {
                throw new IOException("The bundled User Guide is missing. Download a fresh copy of InsureConnect.");
            }
            Path guide = Files.createTempFile("insureconnect-guide-", ".html");
            guide.toFile().deleteOnExit();
            Files.copy(source, guide, StandardCopyOption.REPLACE_EXISTING);
            return guide;
        }
    }

    /**
     * Opens a URI in a browser.
     */
    @FunctionalInterface
    interface BrowserOpener {
        void open(URI uri) throws IOException;
    }
}
