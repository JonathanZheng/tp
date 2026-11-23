package seedu.insureconnect.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * Verifies prefix value semantics used by argument maps.
 */
public class PrefixTest {
    @Test
    public void hashCode_equalPrefixes_findSameMapEntry() {
        Map<Prefix, String> values = new HashMap<>();
        values.put(new Prefix("n/"), "Alice");
        // EP: equal prefix
        assertEquals("Alice", values.get(new Prefix("n/")));
        // EP: different prefix
        assertNull(values.get(new Prefix("p/")));
    }

    @Test
    public void equals_variousObjects_returnsCorrectResult() {
        Prefix name = new Prefix("n/");
        // EP: same object
        assertEquals(name, name);
        // EP: null or another type
        assertFalse(name.equals(null));
        assertFalse(name.equals("n/"));
    }

    @Test
    public void toString_namePrefix_returnsPrefixText() {
        Prefix name = new Prefix("n/");
        assertEquals("n/", name.getPrefix());
        assertEquals("n/", name.toString());
    }

    @Test
    public void constructor_nullPrefix_rejectedImmediately() {
        assertThrows(NullPointerException.class, () -> new Prefix(null));
    }
}
