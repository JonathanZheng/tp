package seedu.insureconnect.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.insureconnect.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.insureconnect.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.insureconnect.testutil.Assert.assertThrows;
import static seedu.insureconnect.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.insureconnect.logic.commands.AddCommand;
import seedu.insureconnect.logic.commands.ClearCommand;
import seedu.insureconnect.logic.commands.DeleteCommand;
import seedu.insureconnect.logic.commands.EditCommand;
import seedu.insureconnect.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.insureconnect.logic.commands.ExitCommand;
import seedu.insureconnect.logic.commands.FindCommand;
import seedu.insureconnect.logic.commands.HelpCommand;
import seedu.insureconnect.logic.commands.ListCommand;
import seedu.insureconnect.logic.commands.ListCommand.SortOrder;
import seedu.insureconnect.logic.commands.UserGuideCommand;
import seedu.insureconnect.logic.parser.exceptions.ParseException;
import seedu.insureconnect.model.person.NameContainsKeywordsPredicate;
import seedu.insureconnect.model.person.Person;
import seedu.insureconnect.testutil.EditPersonDescriptorBuilder;
import seedu.insureconnect.testutil.PersonBuilder;
import seedu.insureconnect.testutil.PersonUtil;

public class InsureConnectParserTest {

    private final InsureConnectParser parser = new InsureConnectParser();

    @Test
    public void parseCommand_add() throws Exception {
        Person person = new PersonBuilder().build();
        AddCommand command = (AddCommand) parser.parseCommand(PersonUtil.getAddCommand(person));
        assertEquals(new AddCommand(person), command);
    }

    @Test
    public void parseCommand_clear() throws Exception {
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD) instanceof ClearCommand);
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD + " 3") instanceof ClearCommand);
    }

    @Test
    public void parseCommand_confirm_unknownCommand() throws Exception {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("confirm"));
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("confirm now"));
    }

    @Test
    public void parseCommand_delete() throws Exception {
        DeleteCommand command = (DeleteCommand) parser.parseCommand(
                DeleteCommand.COMMAND_WORD + " " + INDEX_FIRST_PERSON.getOneBased());
        assertEquals(new DeleteCommand(INDEX_FIRST_PERSON), command);
    }

    @Test
    public void parseCommand_edit() throws Exception {
        Person person = new PersonBuilder().build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(person).build();
        EditCommand command = (EditCommand) parser.parseCommand(EditCommand.COMMAND_WORD + " "
                + INDEX_FIRST_PERSON.getOneBased() + " " + PersonUtil.getEditPersonDescriptorDetails(descriptor));
        assertEquals(new EditCommand(INDEX_FIRST_PERSON, descriptor), command);
    }

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + " 3") instanceof ExitCommand);
    }

    @Test
    public void parseCommand_find() throws Exception {
        List<String> keywords = List.of("foo", "bar", "baz");
        FindCommand command = (FindCommand) parser.parseCommand(
                FindCommand.COMMAND_WORD + " " + keywords.stream().collect(Collectors.joining(" ")));
        assertEquals(new FindCommand(new NameContainsKeywordsPredicate(keywords)), command);
    }

    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD) instanceof HelpCommand);
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD + " 3") instanceof HelpCommand);
    }

    @Test
    public void parseCommand_list() throws Exception {
        assertEquals(new ListCommand(), parser.parseCommand(ListCommand.COMMAND_WORD));
        assertEquals(new ListCommand(SortOrder.NAME), parser.parseCommand(ListCommand.COMMAND_WORD + " n/"));
        assertEquals(new ListCommand(SortOrder.CREATED_AT), parser.parseCommand(ListCommand.COMMAND_WORD + " c/"));
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE), ()
            -> parser.parseCommand(""));
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("unknownCommand"));
    }

    @Test
    public void parseCommand_userGuide() throws Exception {
        // EP: full command word without arguments
        assertTrue(parser.parseCommand(UserGuideCommand.COMMAND_WORD)
                instanceof UserGuideCommand);

        // EP: short-form command word without arguments
        assertTrue(parser.parseCommand(UserGuideCommand.COMMAND_ALIAS)
                instanceof UserGuideCommand);

        // EP: extraneous arguments after the full command word are ignored
        assertTrue(parser.parseCommand(UserGuideCommand.COMMAND_WORD + " extra")
                instanceof UserGuideCommand);

        // EP: extraneous arguments after the short-form command word are ignored
        assertTrue(parser.parseCommand(UserGuideCommand.COMMAND_ALIAS + " extra")
                instanceof UserGuideCommand);
    }
}
