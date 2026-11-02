package seedu.insureconnect.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.insureconnect.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.insureconnect.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.insureconnect.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.insureconnect.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.insureconnect.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.insureconnect.testutil.Assert.assertThrows;
import static seedu.insureconnect.testutil.TypicalPersons.ALICE;
import static seedu.insureconnect.testutil.TypicalPersons.BOB;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import seedu.insureconnect.testutil.PersonBuilder;

public class PersonTest {

    @Test
    public void asObservableList_modifyList_throwsUnsupportedOperationException() {
        Person person = new PersonBuilder().build();
        assertThrows(UnsupportedOperationException.class, () -> person.getTags().remove(0));
    }

    @Test
    public void constructor_noPolicyNumbers_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new PersonBuilder().withTags().build());
    }

    @Test
    public void isSamePerson() {
        // same object -> returns true
        assertTrue(ALICE.isSamePerson(ALICE));

        // null -> returns false
        assertFalse(ALICE.isSamePerson(null));

        // same name and phone, other attributes different -> returns true
        Person editedAlice = new PersonBuilder(ALICE).withEmail(VALID_EMAIL_BOB)
                .withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND).build();
        assertTrue(ALICE.isSamePerson(editedAlice));

        // same name, different phone -> returns false
        editedAlice = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB).build();
        assertFalse(ALICE.isSamePerson(editedAlice));

        // different name, same phone -> returns false
        editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.isSamePerson(editedAlice));
    }

    @Test
    public void isSamePerson_nameCaseAndExtraSpaces_returnsTrue() {
        Person editedBob = new PersonBuilder(BOB).withName("bOB   cHOO  ").build();
        assertTrue(BOB.isSamePerson(editedBob));
        assertTrue(editedBob.isSamePerson(BOB));
        assertFalse(BOB.equals(editedBob));
        assertEquals("bOB   cHOO  ", editedBob.getName().fullName);
    }

    @Test
    public void isSamePerson_canonicallyEquivalentNamesAndSamePhone_returnsTrue() {
        Person precomposedName = new PersonBuilder(BOB).withName("José").build();
        String decomposedSpelling = "Jose\u0301";
        Person decomposedName = new PersonBuilder(BOB).withName(decomposedSpelling).build();

        // EP: precomposed and decomposed accents represent the same name with the same phone number
        assertTrue(precomposedName.isSamePerson(decomposedName));
        assertTrue(decomposedName.isSamePerson(precomposedName));
        assertEquals(decomposedSpelling, decomposedName.getName().fullName);
    }

    @Test
    public void isSamePerson_canonicallyEquivalentNamesAndDifferentPhone_returnsFalse() {
        Person precomposedName = new PersonBuilder(BOB).withName("José").build();
        Person decomposedName = new PersonBuilder(BOB).withName("Jose\u0301")
                .withPhone(ALICE.getPhone().value).build();

        // EP: canonical name match does not override a different phone number
        assertFalse(precomposedName.isSamePerson(decomposedName));
    }

    @Test
    public void isSamePerson_normalizedNameButDifferentPhone_returnsFalse() {
        Person editedBob = new PersonBuilder(BOB).withName("bOB   cHOO  ")
                .withPhone(ALICE.getPhone().value).build();
        assertFalse(BOB.isSamePerson(editedBob));
    }

    @Test
    public void isSamePerson_differentWordBoundaries_returnsFalse() {
        Person editedBob = new PersonBuilder(BOB).withName("BobChoo").build();
        assertFalse(BOB.isSamePerson(editedBob));
    }


    @Test
    public void getCreatedAt_returnsCreationTimestamp() {
        Instant createdAt = Instant.parse("2026-02-01T00:00:00Z");
        Person person = new PersonBuilder()
                .withCreatedAt(createdAt)
                .build();

        assertEquals(createdAt, person.getCreatedAt());
    }

    @Test
    public void equals() {
        // same values -> returns true
        Person aliceCopy = new PersonBuilder(ALICE).build();
        assertTrue(ALICE.equals(aliceCopy));

        // same object -> returns true
        assertTrue(ALICE.equals(ALICE));

        // null -> returns false
        assertFalse(ALICE.equals(null));

        // different type -> returns false
        assertFalse(ALICE.equals(5));

        // different person -> returns false
        assertFalse(ALICE.equals(BOB));

        // different name -> returns false
        Person editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different phone -> returns false
        editedAlice = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different email -> returns false
        editedAlice = new PersonBuilder(ALICE).withEmail(VALID_EMAIL_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different address -> returns false
        editedAlice = new PersonBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different tags -> returns false
        editedAlice = new PersonBuilder(ALICE).withTags(VALID_TAG_HUSBAND).build();
        assertFalse(ALICE.equals(editedAlice));

    }

    @Test
    public void toStringMethod() {
        String expected = Person.class.getCanonicalName() + "{name=" + ALICE.getName() + ", phone=" + ALICE.getPhone()
                + ", email=" + ALICE.getEmail() + ", address=" + ALICE.getAddress() + ", tags=" + ALICE.getTags()
                + ", remark=" + ALICE.getRemark() + "}";
        assertEquals(expected, ALICE.toString());
    }
}
