package seedu.insureconnect.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.insureconnect.testutil.Assert.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.insureconnect.testutil.PersonBuilder;

/**
 * Tests phone-prefix validation and matching independently of names and other customer fields.
 */
public class PhoneStartsWithPredicateTest {
    /**
     * Direct construction rejects null, empty, and non-digit prefixes
     * (with the exception of + as first char being allowed).
     */
    @Test
    public void constructor_invalidPrefix_throwsException() {
        assertThrows(NullPointerException.class, () -> new PhoneStartsWithPredicate(null));

        for (String prefix : List.of(
                "", " ", "abc", "+", "++65", "65+1",
                "98 76", "98-76", "１２３")) {
            assertThrows(
                    IllegalArgumentException.class, () -> new PhoneStartsWithPredicate(prefix));
        }
    }

    /**
     * Accepts phone prefixes containing digits with an optional leading plus sign.
     */
    @Test
    public void isValidPrefix_optionalLeadingPlusAccepted() {
        assertTrue(PhoneStartsWithPredicate.isValidPrefix("65"));
        assertTrue(PhoneStartsWithPredicate.isValidPrefix("+65"));
    }

    /**
     * Partial and complete numbers match, including single digits and leading zeros.
     */
    @Test
    public void test_matchingPrefix_returnsTrue() {
        Person person = new PersonBuilder().withPhone("00123456").build();
        // EP: leading digits of the number, including leading zeros
        assertTrue(new PhoneStartsWithPredicate("0").test(person)); // boundary value
        assertTrue(new PhoneStartsWithPredicate("0012").test(person));
        // EP: the whole number
        assertTrue(new PhoneStartsWithPredicate("00123456").test(person)); // boundary value
    }

    /**
     * Infixes, suffixes, and queries longer than the number do not match.
     */
    @Test
    public void test_nonMatchingPrefix_returnsFalse() {
        Person person = new PersonBuilder().withPhone("98765432").build();
        // EP: digits from the middle or end of the number
        assertFalse(new PhoneStartsWithPredicate("7654").test(person));
        assertFalse(new PhoneStartsWithPredicate("5432").test(person));
        // EP: query longer than the number
        assertFalse(new PhoneStartsWithPredicate("987654321").test(person)); // boundary value
    }

    /**
     * Digits present only in other customer fields must not match.
     */
    @Test
    public void test_otherFieldsOnly_returnsFalse() {
        Person person = new PersonBuilder().withName("9876 Alice").withPhone("12345678")
                .withEmail("9876@example.com").withAddress("9876 Main Street").withTags("9876")
                .withRemark("9876").build();
        assertFalse(new PhoneStartsWithPredicate("9876").test(person));
    }

    /**
     * Equal predicates represent the same digit prefix and preserve leading zeros.
     */
    @Test
    public void equals_variousObjects_comparesPrefixes() {
        PhoneStartsWithPredicate predicate = new PhoneStartsWithPredicate("0012");
        // EP: same prefix
        assertTrue(predicate.equals(predicate));
        assertTrue(predicate.equals(new PhoneStartsWithPredicate("0012")));
        // EP: different prefix, including the same digits without leading zeros
        assertFalse(predicate.equals(new PhoneStartsWithPredicate("12")));
        // EP: null or another type
        assertFalse(predicate.equals(null));
        assertFalse(predicate.equals("0012"));
    }

    /**
     * Diagnostic output includes the exact phone prefix.
     */
    @Test
    public void toString_validPrefix_includesPrefix() {
        assertEquals(PhoneStartsWithPredicate.class.getCanonicalName() + "{prefix=0012}",
                new PhoneStartsWithPredicate("0012").toString());
    }
}
