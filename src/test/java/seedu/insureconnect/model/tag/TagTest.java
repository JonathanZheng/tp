package seedu.insureconnect.model.tag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.insureconnect.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class TagTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Tag(null));
    }

    @Test
    public void constructor_invalidTagName_throwsIllegalArgumentException() {
        String invalidTagName = "";
        assertThrows(IllegalArgumentException.class, () -> new Tag(invalidTagName));
    }

    @Test
    public void isValidTagName() {
        // null tag name
        assertThrows(NullPointerException.class, () -> Tag.isValidTagName(null));

        // EP: valid alphanumeric policy numbers
        assertTrue(Tag.isValidTagName("A"));
        assertTrue(Tag.isValidTagName("LIFE20481"));

        // EP: valid policy numbers containing supported separators
        assertTrue(Tag.isValidTagName("POLICY-123"));
        assertTrue(Tag.isValidTagName("POLICY/123"));
        assertTrue(Tag.isValidTagName("LIFE-2026/SG"));

        // Boundary value: maximum permitted length (30 characters)
        assertTrue(Tag.isValidTagName("A".repeat(28) + "-/"));

        // EP: empty or separator-only policy numbers
        assertFalse(Tag.isValidTagName(""));
        assertFalse(Tag.isValidTagName("-"));
        assertFalse(Tag.isValidTagName("/"));
        assertFalse(Tag.isValidTagName("-/"));

        // Boundary value: above maximum permitted length
        assertFalse(Tag.isValidTagName("A".repeat(31)));

        // EP: policy numbers containing prohibited characters
        assertFalse(Tag.isValidTagName("POLICY 123"));
        assertFalse(Tag.isValidTagName("POLICY_123"));
        assertFalse(Tag.isValidTagName("POLICY.123"));
        assertFalse(Tag.isValidTagName("POLICY@123"));
    }

    @Test
    public void constructor_mixedCasePolicyNumber_normalizesToUpperCase() {
        Tag lowercaseTag = new Tag("life20481");
        Tag uppercaseTag = new Tag("LIFE20481");

        assertEquals("LIFE20481", lowercaseTag.tagName);
        assertEquals(uppercaseTag, lowercaseTag);
        assertEquals(uppercaseTag.hashCode(), lowercaseTag.hashCode());
    }

}
