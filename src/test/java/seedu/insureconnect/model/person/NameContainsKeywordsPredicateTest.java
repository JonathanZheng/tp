package seedu.insureconnect.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.insureconnect.testutil.PersonBuilder;

/**
 * Tests name-only matching and keyword combinations for customer searches.
 */
public class NameContainsKeywordsPredicateTest {

    @Test
    public void equals() {
        List<String> firstPredicateKeywordList = List.of("first");
        List<String> secondPredicateKeywordList = List.of("first", "second");

        NameContainsKeywordsPredicate firstPredicate = new NameContainsKeywordsPredicate(firstPredicateKeywordList);
        NameContainsKeywordsPredicate secondPredicate = new NameContainsKeywordsPredicate(secondPredicateKeywordList);

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        NameContainsKeywordsPredicate firstPredicateCopy = new NameContainsKeywordsPredicate(firstPredicateKeywordList);
        assertTrue(firstPredicate.equals(firstPredicateCopy));

        // different types -> returns false
        assertFalse(firstPredicate.equals(1));

        // null -> returns false
        assertFalse(firstPredicate.equals(null));

        // different person -> returns false
        assertFalse(firstPredicate.equals(secondPredicate));
    }

    @Test
    public void test_nameContainsKeywords_returnsTrue() {
        // One keyword
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(List.of("Alice"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Bob").build()));

        // Multiple keywords
        predicate = new NameContainsKeywordsPredicate(List.of("Alice", "Bob"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Bob").build()));

        // Only one matching keyword
        predicate = new NameContainsKeywordsPredicate(List.of("Bob", "Carol"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Carol").build()));

        // Mixed-case keywords
        predicate = new NameContainsKeywordsPredicate(List.of("aLIce", "bOB"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Bob").build()));
    }

    @Test
    public void test_nameDoesNotContainKeywords_returnsFalse() {
        // Zero keywords
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(List.of());
        assertFalse(predicate.test(new PersonBuilder().withName("Alice").build()));

        // Non-matching keyword
        predicate = new NameContainsKeywordsPredicate(List.of("Carol"));
        assertFalse(predicate.test(new PersonBuilder().withName("Alice Bob").build()));

        // Keywords match phone, email and address, but do not match name
        predicate = new NameContainsKeywordsPredicate(List.of("12345", "alice@email.com", "Main", "Street"));
        assertFalse(predicate.test(new PersonBuilder().withName("Alice").withPhone("12345")
                .withEmail("alice@email.com").withAddress("Main Street").build()));
    }

    /**
     * Partial keywords match the first, middle, or last name word regardless of case.
     */
    @Test
    public void test_nameWordPrefixes_returnsTrue() {
        Person person = new PersonBuilder().withName("Alice   Mary Bob").build();
        // EP: prefix of any name word, in a different case
        assertTrue(new NameContainsKeywordsPredicate(List.of("aL")).test(person));
        assertTrue(new NameContainsKeywordsPredicate(List.of("mA")).test(person));
        assertTrue(new NameContainsKeywordsPredicate(List.of("bO")).test(person));
        // EP: single-letter prefix
        assertTrue(new NameContainsKeywordsPredicate(List.of("a")).test(person)); // boundary value
    }

    /**
     * One matching prefix is sufficient, regardless of keyword order or repetition.
     */
    @Test
    public void test_multiplePrefixesWithOneMatch_returnsTrue() {
        Person person = new PersonBuilder().withName("Alice Bob").build();
        // EP: one matching keyword among non-matching ones, in any position
        assertTrue(new NameContainsKeywordsPredicate(List.of("Car", "Al")).test(person));
        assertTrue(new NameContainsKeywordsPredicate(List.of("Al", "Car")).test(person));
        // EP: repeated matching keyword
        assertTrue(new NameContainsKeywordsPredicate(List.of("Al", "Al")).test(person));
    }

    /**
     * Matching inside a word or extending beyond a complete word is insufficient.
     */
    @Test
    public void test_infixSuffixAndLongerKeywords_returnsFalse() {
        Person person = new PersonBuilder().withName("Alice Bob").build();
        assertFalse(new NameContainsKeywordsPredicate(List.of("lic", "ice", "ob")).test(person));
        assertFalse(new NameContainsKeywordsPredicate(List.of("Alicee", "Bobby")).test(person));
    }

    /**
     * Prefixes of contact details, policy numbers, and remarks must not match the name.
     */
    @Test
    public void test_prefixesOnlyMatchOtherFields_returnsFalse() {
        Person person = new PersonBuilder().withName("Alice Bob").withPhone("98765432")
                .withEmail("carol@example.com").withAddress("Main Street").withTags("LIFE123")
                .withRemark("Follow up").build();
        NameContainsKeywordsPredicate predicate =
                new NameContainsKeywordsPredicate(List.of("987", "car", "Mai", "LIF", "Fol"));
        assertFalse(predicate.test(person));
    }

    @Test
    public void toStringMethod() {
        List<String> keywords = List.of("keyword1", "keyword2");
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(keywords);

        String expected = NameContainsKeywordsPredicate.class.getCanonicalName() + "{keywords=" + keywords + "}";
        assertEquals(expected, predicate.toString());
    }

    /**
     * Exact matches consider any name word and any keyword without case sensitivity.
     */
    @Test
    public void hasExactMatch_completeWord_returnsTrue() {
        Person person = new PersonBuilder().withName("Alice   Mary Bob").build();
        // EP: one keyword equal to a name word, in a different case
        assertTrue(new NameContainsKeywordsPredicate(List.of("aLICE")).hasExactMatch(person));
        // EP: exact keyword among non-matching or repeated keywords
        assertTrue(new NameContainsKeywordsPredicate(List.of("car", "MARY")).hasExactMatch(person));
        assertTrue(new NameContainsKeywordsPredicate(List.of("bob", "bob")).hasExactMatch(person));
    }

    /**
     * Partial words and absent keywords do not qualify for the higher match rank.
     */
    @Test
    public void hasExactMatch_partialOrMissingWord_returnsFalse() {
        Person person = new PersonBuilder().withName("Alice Bob").build();
        // EP: keywords that are only part of a name word
        assertFalse(new NameContainsKeywordsPredicate(List.of("Al", "Bo")).hasExactMatch(person));
        assertFalse(new NameContainsKeywordsPredicate(List.of("lice", "Bobby")).hasExactMatch(person));
        // EP: no keywords
        assertFalse(new NameContainsKeywordsPredicate(List.of()).hasExactMatch(person)); // boundary value
    }

    /**
     * A name keyword can match the later component of a hyphenated customer name.
     */
    @Test
    public void test_hyphenatedNameLaterComponent_returnsTrue() {
        Person person = new PersonBuilder().withName("Mary-Jane Tan").build();

        // EP: later component of a hyphenated name
        assertTrue(new NameContainsKeywordsPredicate(List.of("Jane")).test(person));
        // EP: complete hyphenated name remains searchable
        assertTrue(new NameContainsKeywordsPredicate(List.of("Mary-Jane")).test(person));
        assertTrue(new NameContainsKeywordsPredicate(List.of("Mary-J")).test(person));
    }

    @Test
    public void canonicallyEquivalentAccentedNames_matchForPrefixAndExactSearch() {
        String precomposedName = "José";
        String decomposedName = "Jose\u0301";

        Person precomposedPerson = new PersonBuilder().withName(precomposedName).build();
        Person decomposedPerson = new PersonBuilder().withName(decomposedName).build();

        NameContainsKeywordsPredicate precomposedPredicate =
                new NameContainsKeywordsPredicate(List.of(precomposedName));
        NameContainsKeywordsPredicate decomposedPredicate =
                new NameContainsKeywordsPredicate(List.of(decomposedName));
        NameContainsKeywordsPredicate unaccentedPredicate =
                new NameContainsKeywordsPredicate(List.of("Jose"));

        // EP: precomposed keyword matches a canonically equivalent decomposed name
        assertTrue(precomposedPredicate.test(decomposedPerson));
        assertTrue(precomposedPredicate.hasExactMatch(decomposedPerson));

        // EP: decomposed keyword matches a canonically equivalent precomposed name
        assertTrue(decomposedPredicate.test(precomposedPerson));
        assertTrue(decomposedPredicate.hasExactMatch(precomposedPerson));

        // EP: an unaccented keyword does not match an accented name
        assertFalse(unaccentedPredicate.test(precomposedPerson)); // boundary value
        assertFalse(unaccentedPredicate.hasExactMatch(precomposedPerson)); // boundary value
    }
}
