package seedu.insureconnect.logic.commands;

import static seedu.insureconnect.logic.commands.CommandFormats.USER_GUIDE_COMMAND_FORMAT;

import seedu.insureconnect.model.Model;

/**
 * Opens the offline User Guide in the user's default web browser.
 */
public class UserGuideCommand extends Command {
    public static final String COMMAND_WORD = "userguide";
    public static final String COMMAND_ALIAS = "ug";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Opens the offline User Guide in the default web browser.\n"
            + "Format: " + USER_GUIDE_COMMAND_FORMAT;
    public static final String MESSAGE_SUCCESS = "Opening the User Guide in your browser.";

    @Override
    public CommandResult execute(Model model) {
        return new CommandResult(MESSAGE_SUCCESS, false, false, false, true);
    }
}
