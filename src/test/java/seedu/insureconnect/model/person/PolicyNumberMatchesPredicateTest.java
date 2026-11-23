package seedu.insureconnect.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.insureconnect.testutil.PersonBuilder;

/**
 * Tests matching customers against an exact policy number.
 */
public class PolicyNumberMatchesPredicateTest {

    /**
     * A customer with the requested policy number is matched.
     */
    @Test
    public void test_matchingPolicyNumber_returnsTrue() {
        PolicyNumberMatchesPredicate predicate =
                new PolicyNumberMatchesPredicate("LIFE20001");

        assertTrue(predicate.test(new PersonBuilder()
                .withTags("LIFE20001", "HEALTH20002")
                .build()));
    }

    /**
     * A customer without the requested policy number is not matched.
     */
    @Test
    public void test_differentPolicyNumber_returnsFalse() {
        PolicyNumberMatchesPredicate predicate =
                new PolicyNumberMatchesPredicate("LIFE20001");

        assertFalse(predicate.test(new PersonBuilder()
                .withTags("HEALTH20002")
                .build()));
    }

    /**
     * Policy-number matching ignores letter case.
     */
    @Test
    public void test_differentCase_returnsTrue() {
        PolicyNumberMatchesPredicate predicate =
                new PolicyNumberMatchesPredicate("life20001");

        assertTrue(predicate.test(new PersonBuilder()
                .withTags("LIFE20001")
                .build()));
    }
    @Test
    public void test_partialPolicy_returnsFalse() {
        Person customer = new PersonBuilder().withTags("LIFE20001").build();
        for (String query : new String[]{
            // EP: only part of the policy number
            "LIFE", "20001",
            // EP: policy number with extra characters
            "LIFE200012", // boundary value
        }) {
            assertFalse(new PolicyNumberMatchesPredicate(query).test(customer));
        }
    }

    @Test
    public void equals_variousObjects_ignoresCaseAndRejectsOtherValues() {
        PolicyNumberMatchesPredicate predicate = new PolicyNumberMatchesPredicate("LIFE20001");
        // EP: same policy number in any case
        assertEquals(predicate, predicate);
        assertEquals(predicate, new PolicyNumberMatchesPredicate("life20001"));
        // EP: different policy number
        assertFalse(predicate.equals(new PolicyNumberMatchesPredicate("AUTO1")));
        // EP: null or another type
        assertFalse(predicate.equals(null));
        assertFalse(predicate.equals("LIFE20001"));
    }

    @Test
    public void toString_validPolicy_returnsPolicyNumber() {
        assertEquals("LIFE20001", new PolicyNumberMatchesPredicate("LIFE20001").toString());
    }

    @Test
    public void constructor_nullPolicy_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new PolicyNumberMatchesPredicate(null));
    }
}
