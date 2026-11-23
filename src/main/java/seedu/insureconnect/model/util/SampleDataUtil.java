package seedu.insureconnect.model.util;

import java.time.Instant;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import seedu.insureconnect.model.InsureConnect;
import seedu.insureconnect.model.ReadOnlyInsureConnect;
import seedu.insureconnect.model.person.Address;
import seedu.insureconnect.model.person.Email;
import seedu.insureconnect.model.person.Name;
import seedu.insureconnect.model.person.Person;
import seedu.insureconnect.model.person.Phone;
import seedu.insureconnect.model.person.Remark;
import seedu.insureconnect.model.tag.Tag;

/**
 * Contains utility methods for populating {@code InsureConnect} with sample data.
 */
public final class SampleDataUtil {
    private static final Remark EMPTY_REMARK = new Remark("");

    private SampleDataUtil() {
    }

    public static Person[] getSamplePersons() {
        return new Person[] {
            new Person(new Name("Alex Yeoh"), new Phone("87438807"), new Email("alexyeoh@example.com"),
                new Address("Blk 30 Geylang Street 29, #06-40"),
                getTagSet("LIFE10001"), EMPTY_REMARK, Instant.now()),
            new Person(new Name("Bernice Yu"), new Phone("99272758"), new Email("berniceyu@example.com"),
                new Address("Blk 30 Lorong 3 Serangoon Gardens, #07-18"),
                getTagSet("HEALTH10002", "LIFE10002"), EMPTY_REMARK, Instant.now()),
            new Person(new Name("Charlotte Oliveiro"), new Phone("93210283"), new Email("charlotte@example.com"),
                new Address("Blk 11 Ang Mo Kio Street 74, #11-04"),
                getTagSet("LIFE10003"), EMPTY_REMARK, Instant.now()),
            new Person(new Name("David Li"), new Phone("91031282"), new Email("lidavid@example.com"),
                new Address("Blk 436 Serangoon Gardens Street 26, #16-43"),
                getTagSet("MOTOR10004"), EMPTY_REMARK, Instant.now()),
            new Person(new Name("Irfan Ibrahim"), new Phone("92492021"), new Email("irfan@example.com"),
                new Address("Blk 47 Tampines Street 20, #17-35"),
                getTagSet("LIFE10005"), EMPTY_REMARK, Instant.now()),
            new Person(new Name("Roy Balakrishnan"), new Phone("92624417"), new Email("royb@example.com"),
                new Address("Blk 45 Aljunied Street 85, #11-31"),
                getTagSet("HEALTH10006"), EMPTY_REMARK, Instant.now())
        };
    }

    /**
     * Returns an InsureConnect holding the sample customers from {@link #getSamplePersons()}.
     */
    public static ReadOnlyInsureConnect getSampleInsureConnect() {
        InsureConnect sampleInsureConnect = new InsureConnect();
        for (Person samplePerson : getSamplePersons()) {
            sampleInsureConnect.addPerson(samplePerson);
        }
        return sampleInsureConnect;
    }

    /**
     * Returns a tag set containing the list of strings given.
     */
    public static Set<Tag> getTagSet(String... strings) {
        return Arrays.stream(strings)
                .map(Tag::new)
                .collect(Collectors.toSet());
    }

}
