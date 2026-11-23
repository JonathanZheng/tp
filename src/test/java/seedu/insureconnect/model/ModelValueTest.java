package seedu.insureconnect.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.insureconnect.testutil.TypicalPersons.ALICE;
import static seedu.insureconnect.testutil.TypicalPersons.BOB;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.insureconnect.commons.core.ColourScheme;
import seedu.insureconnect.model.person.UniquePersonList;

/**
 * Checks equality, copying and iteration of model containers.
 */
public class ModelValueTest {
    @Test
    public void constructor_copiedPreferences_independentAndEqual() {
        UserPrefs original = new UserPrefs();
        UserPrefs copy = new UserPrefs(original);
        assertEquals(original, original);
        assertEquals(original, copy);
        assertEquals(original.hashCode(), copy.hashCode());
        assertFalse(original.equals(null));
        assertFalse(original.equals("preferences"));
        copy.setGuiSettings(copy.getGuiSettings().withColourScheme(ColourScheme.LIGHT));
        assertNotEquals(original, copy);
        assertEquals(ColourScheme.DARK, original.getGuiSettings().getColourScheme());
        assertThrows(NullPointerException.class, () -> new UserPrefs(null));
    }

    @Test
    public void constructor_copiedInsureConnect_independentAndOrderSensitive() {
        InsureConnect original = new InsureConnect();
        original.setPersons(List.of(ALICE, BOB));
        InsureConnect copy = new InsureConnect(original);
        assertEquals(original, original);
        assertEquals(original, copy);
        assertEquals(original.hashCode(), copy.hashCode());
        assertFalse(original.equals(null));
        assertFalse(original.equals(List.of(ALICE, BOB)));
        copy.setPersons(List.of(BOB, ALICE));
        assertNotEquals(original, copy);
        assertEquals(List.of(ALICE, BOB), original.getPersonList());
    }

    @Test
    public void setPersons_copiedList_equalAndIteratesInOrder() {
        UniquePersonList original = new UniquePersonList();
        original.setPersons(List.of(ALICE, BOB));
        UniquePersonList copy = new UniquePersonList();
        copy.setPersons(original);
        assertEquals(original, original);
        assertEquals(original, copy);
        assertEquals(original.hashCode(), copy.hashCode());
        assertFalse(original.equals(null));
        assertFalse(original.equals(List.of(ALICE, BOB)));
        var iterator = original.iterator();
        assertEquals(ALICE, iterator.next());
        assertEquals(BOB, iterator.next());
        assertFalse(iterator.hasNext());
        copy.setPersons(List.of(BOB, ALICE));
        assertNotEquals(original, copy);
    }
}
