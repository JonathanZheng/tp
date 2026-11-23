package seedu.insureconnect.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.insureconnect.testutil.TypicalPersons.ALICE;

import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.insureconnect.model.tag.Tag;
import seedu.insureconnect.testutil.PersonBuilder;

/**
 * Checks value equality and hashing when model objects are used as collection keys.
 */
public class ValueEqualityTest {
    @Test
    public void hashCode_equalValues_workAsHashSetKeys() {
        assertEquivalentKeys(new Name("Alice"), new Name("Alice"));
        assertEquivalentKeys(new Phone("91234567"), new Phone("91234567"));
        assertEquivalentKeys(new Email("alice@example.com"), new Email("alice@example.com"));
        assertEquivalentKeys(new Address("123 Main Street"), new Address("123 Main Street"));
        assertEquivalentKeys(new Remark("Call tomorrow"), new Remark("Call tomorrow"));
        assertEquivalentKeys(new Tag("LIFE123"), new Tag("LIFE123"));
        assertEquivalentKeys(ALICE, new PersonBuilder(ALICE).build());
    }

    @Test
    public void person_differentRemark_changesEqualityButNotIdentity() {
        Person changed = new PersonBuilder(ALICE).withRemark("Call tomorrow").build();
        assertNotEquals(ALICE, changed);
        assertTrue(ALICE.isSamePerson(changed));
    }

    @Test
    public void equals_tagVariants_ignoresCaseAndRequiresSameType() {
        Tag policy = new Tag("LIFE123");

        // EP: same policy in any case
        assertEquals(policy, policy);
        assertEquals(policy, new Tag("life123"));
        // EP: null or another type
        assertFalse(policy.equals(null));
        assertFalse(policy.equals("LIFE123"));
    }

    @Test
    public void equals_policyPredicateVariants_ignoresCaseAndRejectsOtherValues() {
        PolicyNumberMatchesPredicate predicate = new PolicyNumberMatchesPredicate("LIFE123");
        // EP: same policy number in any case
        assertEquals(predicate, predicate);
        assertEquals(predicate, new PolicyNumberMatchesPredicate("life123"));
        // EP: null, another type or a different policy number
        assertFalse(predicate.equals(null));
        assertFalse(predicate.equals("LIFE123"));
        assertNotEquals(predicate, new PolicyNumberMatchesPredicate("AUTO1"));
    }

    @Test
    public void test_partialOrDifferentCasePolicy_matchesOnlyWholeNumber() {
        Person customer = new PersonBuilder().withTags("LIFE123", "AUTO456").build();
        for (String query : List.of(
                // EP: only part of a policy number
                "LIFE", "123",
                // EP: policy number with extra characters
                "LIFE1234")) { // boundary value
            assertFalse(new PolicyNumberMatchesPredicate(query).test(customer));
        }
        // EP: whole policy number in a different case
        assertTrue(new PolicyNumberMatchesPredicate("auto456").test(customer));
    }

    /**
     * Verifies that independently constructed equal values are interchangeable as keys.
     */
    private void assertEquivalentKeys(Object first, Object second) {
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        HashSet<Object> keys = new HashSet<>();
        keys.add(first);
        assertTrue(keys.contains(second));
        assertFalse(keys.add(second));
        assertEquals(1, keys.size());
    }
}
