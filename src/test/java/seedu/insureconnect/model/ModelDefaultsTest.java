package seedu.insureconnect.model;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.insureconnect.testutil.TypicalPersons.ALICE;
import static seedu.insureconnect.testutil.TypicalPersons.BOB;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import seedu.insureconnect.model.person.Person;

/**
 * Verifies the interface contract for implementations that do not override the default filter.
 */
public class ModelDefaultsTest {
    @Test
    public void getFilteredPersonListPredicate_defaultImplementation_showsEveryCustomer() {
        InvocationHandler defaultMethods = (proxy, method, arguments) -> {
            if (method.isDefault()) {
                return InvocationHandler.invokeDefault(proxy, method, arguments);
            }
            throw new AssertionError("Unexpected abstract model call: " + method.getName());
        };
        Model model = (Model) Proxy.newProxyInstance(Model.class.getClassLoader(), new Class<?>[]{Model.class},
                defaultMethods);
        Predicate<Person> filter = model.getFilteredPersonListPredicate();
        assertSame(Model.PREDICATE_SHOW_ALL_PERSONS, filter);
        assertTrue(filter.test(ALICE));
        assertTrue(filter.test(BOB));
    }
}
