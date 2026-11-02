package seedu.insureconnect.model.person;

import static seedu.insureconnect.commons.util.CollectionUtil.requireAllNonNull;

import java.text.Normalizer;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import seedu.insureconnect.commons.util.ToStringBuilder;
import seedu.insureconnect.model.tag.Tag;

/**
 * Represents a Person in InsureConnect.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Person {

    public static final String MESSAGE_MISSING_POLICY_NUMBER = "A customer must have at least one policy number.";

    // Identity fields
    private final Name name;
    private final Phone phone;
    private final Instant createdAt;

    // Data fields
    private final Email email;
    private final Address address;
    private final Set<Tag> tags = new HashSet<>();
    private final Remark remark;

    /**
     * Every field must be present and not null.
     */
    public Person(Name name, Phone phone, Email email, Address address, Set<Tag> tags,
                  Remark remark, Instant createdAt) {
        requireAllNonNull(name, phone, email, address, tags, remark, createdAt);
        if (tags.isEmpty()) {
            throw new IllegalArgumentException(MESSAGE_MISSING_POLICY_NUMBER);
        }
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.tags.addAll(tags);
        this.remark = remark;
        this.createdAt = createdAt;
    }

    public Name getName() {
        return name;
    }

    public Phone getPhone() {
        return phone;
    }

    public Email getEmail() {
        return email;
    }

    public Address getAddress() {
        return address;
    }

    public Remark getRemark() {
        return remark;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Returns true if both persons have the same name (ignoring case, extra spaces, and canonical Unicode
     * differences) and phone number.
     * This defines a weaker notion of equality between two persons.
     */
    public boolean isSamePerson(Person otherPerson) {
        if (otherPerson == this) {
            return true;
        }

        return otherPerson != null
                && otherPerson.getPhone().hasSameDigits(getPhone())
                && getNormalizedName().equalsIgnoreCase(otherPerson.getNormalizedName());
    }

    /**
     * Returns the name with surrounding spaces removed, repeated spaces collapsed, and canonically equivalent
     * Unicode sequences normalized to NFC.
     */
    private String getNormalizedName() {
        String nameWithNormalizedSpaces = name.fullName.trim().replaceAll(" +", " ");
        return Normalizer.normalize(nameWithNormalizedSpaces, Normalizer.Form.NFC);
    }

    /**
     * Returns true if both persons have the same identity and data fields.
     * This defines a stronger notion of equality between two persons.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Person otherPerson)) {
            return false;
        }

        return name.equals(otherPerson.name)
                && phone.equals(otherPerson.phone)
                && email.equals(otherPerson.email)
                && address.equals(otherPerson.address)
                && tags.equals(otherPerson.tags)
                && remark.equals(otherPerson.remark);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, phone, email, address, tags, remark);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("address", address)
                .add("tags", tags)
                .add("remark", remark)
                .toString();
    }

}
