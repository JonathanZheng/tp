package seedu.insureconnect.commons.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.Point;

import org.junit.jupiter.api.Test;

import seedu.insureconnect.commons.util.JsonUtil;

public class GuiSettingsTest {
    @Test
    public void withColourScheme_customWindow_preservesWindowSettings() {
        GuiSettings original = new GuiSettings(800, 700, 40, 50);
        GuiSettings updated = original.withColourScheme(ColourScheme.LIGHT);
        assertEquals(original.getWindowWidth(), updated.getWindowWidth());
        assertEquals(original.getWindowHeight(), updated.getWindowHeight());
        assertEquals(original.getWindowCoordinates(), updated.getWindowCoordinates());
        assertEquals(ColourScheme.LIGHT, updated.getColourScheme());
        assertEquals(ColourScheme.DARK, original.getColourScheme());
        assertNotEquals(original, updated);
        assertEquals(original, updated.withColourScheme(ColourScheme.DARK));
        assertEquals(original.hashCode(), updated.withColourScheme(ColourScheme.DARK).hashCode());
    }

    @Test
    public void withColourScheme_defaultSettings_preservesAutomaticPosition() {
        GuiSettings updated = new GuiSettings().withColourScheme(ColourScheme.LIGHT);
        assertNull(updated.getWindowCoordinates());
    }

    @Test
    public void withColourScheme_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new GuiSettings().withColourScheme(null));
    }

    @Test
    public void toStringMethod() {
        GuiSettings guiSettings = new GuiSettings();
        String expected = GuiSettings.class.getCanonicalName() + "{windowWidth=" + guiSettings.getWindowWidth()
                + ", windowHeight=" + guiSettings.getWindowHeight() + ", windowCoordinates="
                + guiSettings.getWindowCoordinates() + ", colourScheme=" + guiSettings.getColourScheme() + "}";
        assertEquals(expected, guiSettings.toString());
    }
    @Test
    public void equals_variousObjects_comparesEveryWindowProperty() {
        GuiSettings settings = new GuiSettings(800, 700, 40, 50);
        // EP: same object
        assertEquals(settings, settings);
        // EP: null or another type
        assertFalse(settings.equals(null));
        assertFalse(settings.equals("settings"));
        // EP: one window property differs
        assertNotEquals(settings, new GuiSettings(900, 700, 40, 50));
        assertNotEquals(settings, new GuiSettings(800, 800, 40, 50));
        assertNotEquals(settings, new GuiSettings(800, 700, 41, 50));
    }

    @Test
    public void getWindowCoordinates_resultMutated_settingsUnchanged() {
        GuiSettings settings = new GuiSettings(800, 700, 40, 50);
        Point position = settings.getWindowCoordinates();
        position.translate(100, 100);
        assertEquals(new Point(40, 50), settings.getWindowCoordinates());
    }

    @Test
    public void deserialize_nullColourScheme_defaultsToDark() throws Exception {
        GuiSettings settings = JsonUtil.fromJsonString("{\"colourScheme\":null}", GuiSettings.class);
        assertEquals(ColourScheme.DARK, settings.getColourScheme());
        assertEquals(new GuiSettings(), settings);
        assertEquals(new GuiSettings().hashCode(), settings.hashCode());
    }
}
