package seedu.insureconnect.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.insureconnect.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class NameTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Name(null));
    }

    @Test
    public void constructor_invalidName_throwsIllegalArgumentException() {
        String invalidName = "";
        assertThrows(IllegalArgumentException.class, () -> new Name(invalidName));
    }

    @Test
    public void isValidName() {
        // null name
        assertThrows(NullPointerException.class, () -> Name.isValidName(null));

        // invalid name
        assertFalse(Name.isValidName("")); // empty string
        assertFalse(Name.isValidName(" ")); // spaces only
        assertFalse(Name.isValidName("^")); // only non-alphanumeric characters
        assertFalse(Name.isValidName("peter*")); // contains non-alphanumeric characters
        assertFalse(Name.isValidName("-")); // only hyphen
        assertFalse(Name.isValidName("---")); // multiple hyphens only
        assertFalse(Name.isValidName(".")); // only period
        assertFalse(Name.isValidName("...")); // multiple periods only
        assertFalse(Name.isValidName("'")); // only apostrophe
        assertFalse(Name.isValidName("'''")); // multiple apostrophes only
        assertFalse(Name.isValidName("-'.")); // combination of punctuation only
        assertFalse(Name.isValidName("/")); // only slash
        assertFalse(Name.isValidName("///")); // multiple slashes only
        assertFalse(Name.isValidName("-Mary")); // starts with hyphen
        assertFalse(Name.isValidName(".Tan")); // starts with period
        assertFalse(Name.isValidName("/Mary")); // starts with slash
        assertFalse(Name.isValidName("!Zoe")); // starts with exclamation mark
        // EP: names with non-Latin letters are unsupported
        assertFalse(Name.isValidName("张三"));
        // EP: names containing emoji are unsupported
        assertFalse(Name.isValidName("José😀"));
        // EP: names containing unrelated symbols are unsupported
        assertFalse(Name.isValidName("Jos@e"));
        // EP: a combining mark cannot start a name
        assertFalse(Name.isValidName("\u0301Jose"));

        // valid name
        assertTrue(Name.isValidName("peter jack")); // alphabets only
        assertTrue(Name.isValidName("12345")); // numbers only
        assertTrue(Name.isValidName("peter the 2nd")); // alphanumeric characters
        assertTrue(Name.isValidName("Capital Tan")); // with capital letters
        assertTrue(Name.isValidName("David Roger Jackson Ray Jr 2nd")); // long names
        assertTrue(Name.isValidName("Mary-Jane")); // with hyphen
        assertTrue(Name.isValidName("Anne-Marie Smith-Jones")); // with multiple hyphens
        assertTrue(Name.isValidName("O'Connor")); // with apostrophe
        assertTrue(Name.isValidName("D'Souza")); // with apostrophe
        assertTrue(Name.isValidName("Dr. Tan")); // with period
        assertTrue(Name.isValidName("John Jr.")); // with trailing period
        assertTrue(Name.isValidName("J.K. Rowling")); // with multiple periods
        assertTrue(Name.isValidName("Dr. John O'Connor-Smith Jr.")); // with hyphen, apostrophe, and period
        assertTrue(Name.isValidName("Mary/Jane")); // with slash
        assertTrue(Name.isValidName("Ravi s/o Muthu")); // Indian name with s/o
        assertTrue(Name.isValidName("Priya d/o Muthu")); // Indian name with d/o
        assertTrue(Name.isValidName("Mohd a/l Kassim")); // Malaysian name with a/l
        assertTrue(Name.isValidName("Siti a/p Ahmad")); // Malaysian name with a/p
        assertTrue(Name.isValidName("Tan a/k/a Teck")); // with a/k/a
        assertTrue(Name.isValidName("Zoe !")); // with exclamation mark
        // EP: precomposed Latin letters with accents
        assertTrue(Name.isValidName("José Özdemir"));
        // EP: decomposed Latin letters followed by combining accent marks
        assertTrue(Name.isValidName("Jose\u0301 O\u0308zdemir"));
        // EP: curly apostrophe used in a legal name
        assertTrue(Name.isValidName("D’Angelo"));
    }

    @Test
    public void equals() {
        Name name = new Name("Valid Name");

        // same values -> returns true
        assertTrue(name.equals(new Name("Valid Name")));

        // same object -> returns true
        assertTrue(name.equals(name));

        // null -> returns false
        assertFalse(name.equals(null));

        // different types -> returns false
        assertFalse(name.equals(5.0f));

        // different values -> returns false
        assertFalse(name.equals(new Name("Other Valid Name")));
    }
}
