package seedu.insureconnect.model.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.insureconnect.model.ReadOnlyInsureConnect;
import seedu.insureconnect.model.person.Person;
import seedu.insureconnect.model.tag.Tag;
import seedu.insureconnect.storage.JsonInsureConnectStorage;

/**
 * Checks that the bundled examples satisfy model constraints and survive storage.
 */
public class SampleDataUtilTest {
    @TempDir
    public Path temporaryFolder;

    @Test
    public void sampleData_hasUniquePoliciesAndRoundTripsThroughStorage() throws Exception {
        ReadOnlyInsureConnect sample = SampleDataUtil.getSampleInsureConnect();
        assertEquals(6, sample.getPersonList().size());
        Set<Tag> policies = new HashSet<>();
        for (Person customer : sample.getPersonList()) {
            assertFalse(customer.getTags().isEmpty());
            assertEquals("", customer.getRemark().value);
            for (Tag policy : customer.getTags()) {
                assertTrue(policies.add(policy), "Sample customers must not share policies");
            }
        }
        JsonInsureConnectStorage storage = new JsonInsureConnectStorage(temporaryFolder.resolve("sample.json"));
        storage.saveInsureConnect(sample);
        assertEquals(sample, storage.readInsureConnect().orElseThrow());
    }

    @Test
    public void getTagSet_deduplicatesAndValidatesValues() {
        assertEquals(Set.of(), SampleDataUtil.getTagSet());
        assertEquals(Set.of(new Tag("LIFE1")), SampleDataUtil.getTagSet("LIFE1", "LIFE1"));
        assertThrows(IllegalArgumentException.class, () -> SampleDataUtil.getTagSet("LIFE_1"));
        assertThrows(NullPointerException.class, () -> SampleDataUtil.getTagSet((String) null));
    }
}
