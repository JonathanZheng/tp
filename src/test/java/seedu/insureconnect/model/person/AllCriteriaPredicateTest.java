package seedu.insureconnect.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.insureconnect.testutil.Assert.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import seedu.insureconnect.testutil.PersonBuilder;

/**
 * Tests conjunction, immutable criteria and name-ranking discovery.
 */
public class AllCriteriaPredicateTest {
    private final NameContainsKeywordsPredicate name = new NameContainsKeywordsPredicate(List.of("Alice", "Bob"));
    private final PhoneStartsWithPredicate phone = new PhoneStartsWithPredicate("912");

    @Test
    public void test_allCriteriaRequired_nameKeywordsRemainAlternatives() {
        AllCriteriaPredicate predicate = new AllCriteriaPredicate(List.of(name, phone));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice").withPhone("91234567").build()));
        assertTrue(predicate.test(new PersonBuilder().withName("Bob").withPhone("91234567").build()));
        assertFalse(predicate.test(new PersonBuilder().withName("Alice").withPhone("81234567").build()));
        assertFalse(predicate.test(new PersonBuilder().withName("Carol").withPhone("91234567").build()));
        assertEquals(name, predicate.getNamePredicate().orElseThrow());
        assertTrue(new AllCriteriaPredicate(List.of(phone)).getNamePredicate().isEmpty());
    }

    @Test
    public void constructor_nullCriteria_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AllCriteriaPredicate(null));
    }

    @Test
    public void constructor_emptyCriteria_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, AllCriteriaPredicate.MESSAGE_NO_CRITERIA, () ->
                new AllCriteriaPredicate(List.of()));
    }

    @Test
    public void constructor_mutatedSource_doesNotChangeCriteria() {
        List<Predicate<Person>> source = new ArrayList<>(List.of(name, phone));
        AllCriteriaPredicate predicate = new AllCriteriaPredicate(source);
        source.clear();
        assertFalse(predicate.test(new PersonBuilder().withName("Carol").build()));
    }

    @Test
    public void equals_variousObjects_comparesCriteria() {
        AllCriteriaPredicate predicate = new AllCriteriaPredicate(List.of(name, phone));
        // EP: same criteria
        assertTrue(predicate.equals(predicate));
        assertEquals(predicate, new AllCriteriaPredicate(List.of(name, phone)));
        // EP: different criteria
        assertFalse(predicate.equals(new AllCriteriaPredicate(List.of(phone))));
        // EP: null or another type
        assertFalse(predicate.equals(null));
        assertFalse(predicate.equals(name));
    }

    @Test
    public void toString_twoCriteria_listsCriteria() {
        AllCriteriaPredicate predicate = new AllCriteriaPredicate(List.of(name, phone));
        assertEquals(AllCriteriaPredicate.class.getCanonicalName() + "{criteria=" + List.of(name, phone) + "}",
                predicate.toString());
    }
}
