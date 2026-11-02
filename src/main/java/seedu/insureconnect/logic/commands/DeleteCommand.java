package seedu.insureconnect.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.insureconnect.logic.commands.CommandFormats.DELETE_COMMAND_FORMAT;

import java.util.List;

import seedu.insureconnect.commons.core.index.Index;
import seedu.insureconnect.commons.util.ToStringBuilder;
import seedu.insureconnect.logic.Messages;
import seedu.insureconnect.logic.commands.exceptions.CommandException;
import seedu.insureconnect.model.Model;
import seedu.insureconnect.model.person.Person;

/**
 * Deletes a person identified using its displayed index from InsureConnect.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Requests deletion of the person identified by the index number used in the displayed person list.\n"
            + "Format: " + DELETE_COMMAND_FORMAT + " (INDEX must be a positive integer)\n"
            + "Enter y when prompted to confirm deletion. Any other response cancels the deletion.\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Deleted person: %1$s";

    public static final String MESSAGE_CONFIRM_DELETE =
            "Delete %s? (y/n, case-insensitive, default n)";
    public static final String MESSAGE_CANCELLED = "Delete cancelled. No records were deleted.";
    private final Index targetIndex;
    private final boolean isConfirmed;

    public DeleteCommand(Index targetIndex) {
        this(targetIndex, false);
    }

    private DeleteCommand(Index targetIndex, boolean isConfirmed) {
        this.targetIndex = requireNonNull(targetIndex);
        this.isConfirmed = isConfirmed;
    }

    public DeleteCommand confirmed() {
        return new DeleteCommand(targetIndex, true);
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    public int getTargetIndexOneBased() {
        return targetIndex.getOneBased();
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.getInvalidPersonDisplayedIndexMessage(lastShownList.size()));
        }

        Person personToDelete = lastShownList.get(targetIndex.getZeroBased());

        if (!isConfirmed) {
            return new CommandResult(String.format(MESSAGE_CONFIRM_DELETE, Messages.format(personToDelete)),
                    false, false, true);
        }

        model.deletePerson(personToDelete);
        return new CommandResult(String.format(MESSAGE_DELETE_PERSON_SUCCESS, Messages.format(personToDelete)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteCommand otherDeleteCommand)) {
            return false;
        }

        return targetIndex.equals(otherDeleteCommand.targetIndex)
                && isConfirmed == otherDeleteCommand.isConfirmed;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("isConfirmed", isConfirmed)
                .toString();
    }
}
