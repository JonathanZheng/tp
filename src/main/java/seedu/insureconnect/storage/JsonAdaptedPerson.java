package seedu.insureconnect.storage;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.insureconnect.commons.exceptions.IllegalValueException;
import seedu.insureconnect.model.person.Address;
import seedu.insureconnect.model.person.Email;
import seedu.insureconnect.model.person.Name;
import seedu.insureconnect.model.person.Person;
import seedu.insureconnect.model.person.Phone;
import seedu.insureconnect.model.person.Remark;
import seedu.insureconnect.model.tag.Tag;

/**
 * Jackson-friendly version of {@link Person}.
 */
class JsonAdaptedPerson {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Customer's %s field is missing!";

    private final String name;
    private final String phone;
    private final String email;
    private final String address;
    private final List<JsonAdaptedTag> tags;
    private final String remark;
    private final String createdAt;

    /**
     * Constructs a {@code JsonAdaptedPerson} with the given person details.
     */
    @JsonCreator
    public JsonAdaptedPerson(@JsonProperty("name") String name, @JsonProperty("phone") String phone,
            @JsonProperty("email") String email, @JsonProperty("address") String address,
            @JsonProperty("tags") List<JsonAdaptedTag> tags, @JsonProperty("remark") String remark,
            @JsonProperty("createdAt") String createdAt) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.tags = tags;
        this.remark = remark;
        this.createdAt = createdAt;
    }

    /**
     * Converts a given {@code Person} into this class for Jackson use.
     */
    public JsonAdaptedPerson(Person source) {
        name = source.getName().fullName;
        phone = source.getPhone().value;
        email = source.getEmail().value;
        address = source.getAddress().value;
        tags = source.getTags()
            .stream()
            .map(JsonAdaptedTag::new)
            .collect(Collectors.toList());
        remark = source.getRemark().value;
        createdAt = source.getCreatedAt().toString();
    }

    /**
     * Converts this Jackson-friendly adapted person object into the model's {@code Person} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted person.
     */
    public Person toModelType() throws IllegalValueException {
        return new Person(readName(), readPhone(), readEmail(), readAddress(),
                readTags(), readRemark(), readCreatedAt());
    }

    /**
     * Validates and restores the customer's name.
     */
    private Name readName() throws IllegalValueException {
        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        if (!Name.isValidName(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        return new Name(name);
    }

    /**
     * Validates and restores the customer's phone number.
     */
    private Phone readPhone() throws IllegalValueException {
        if (phone == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName()));
        }
        if (!Phone.isValidPhone(phone)) {
            throw new IllegalValueException(Phone.MESSAGE_CONSTRAINTS);
        }
        return new Phone(phone);
    }

    /**
     * Validates and restores the customer's email address.
     */
    private Email readEmail() throws IllegalValueException {
        if (email == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName()));
        }
        if (!Email.isValidEmail(email)) {
            throw new IllegalValueException(Email.MESSAGE_CONSTRAINTS);
        }
        return new Email(email);
    }

    /**
     * Validates and restores the customer's address.
     */
    private Address readAddress() throws IllegalValueException {
        if (address == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Address.class.getSimpleName()));
        }
        if (!Address.isValidAddress(address)) {
            throw new IllegalValueException(Address.MESSAGE_CONSTRAINTS);
        }
        return new Address(address);
    }

    /**
     * Restores policy numbers and requires at least one policy.
     */
    private Set<Tag> readTags() throws IllegalValueException {
        if (tags == null) {
            throw new IllegalValueException(Person.MESSAGE_MISSING_POLICY_NUMBER);
        }
        final Set<Tag> modelTags = new HashSet<>();
        for (JsonAdaptedTag tag : tags) {
            modelTags.add(tag.toModelType());
        }

        if (modelTags.isEmpty()) {
            throw new IllegalValueException(Person.MESSAGE_MISSING_POLICY_NUMBER);
        }

        return modelTags;
    }

    /**
     * Restores the creation time, using the epoch for legacy records with an unknown date.
     * A fixed fallback keeps repeated reads and creation-date sorting deterministic.
     * Explicit but malformed timestamps remain invalid.
     */
    private Instant readCreatedAt() throws IllegalValueException {
        if (createdAt == null) {
            return Instant.EPOCH;
        }
        try {
            return Instant.parse(createdAt);
        } catch (DateTimeParseException e) {
            throw new IllegalValueException("Invalid createdAt timestamp: " + createdAt);
        }
    }

    /**
     * Restores an optional remark, defaulting older records to an empty remark.
     */
    private Remark readRemark() throws IllegalValueException {
        String remarkValue = remark == null ? "" : remark;

        if (!Remark.isValidRemark(remarkValue)) {
            throw new IllegalValueException(Remark.MESSAGE_CONSTRAINTS);
        }

        return new Remark(remarkValue);
    }
}
