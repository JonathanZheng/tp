package seedu.insureconnect.logic.commands;

import static seedu.insureconnect.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.insureconnect.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.insureconnect.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.insureconnect.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.insureconnect.logic.parser.CliSyntax.PREFIX_REMARK;
import static seedu.insureconnect.logic.parser.CliSyntax.PREFIX_TAG;

/**
 * Command formats shown in the help window and usage messages.
 */
public class CommandFormats {

    public static final String ADD_COMMAND_FORMAT = AddCommand.COMMAND_WORD + " "
            + PREFIX_NAME + "NAME "
            + PREFIX_PHONE + "PHONE "
            + PREFIX_EMAIL + "EMAIL "
            + PREFIX_ADDRESS + "ADDRESS "
            + PREFIX_TAG + "POLICY_NUMBER [" + PREFIX_TAG + "POLICY_NUMBER]... "
            + "[" + PREFIX_REMARK + "REMARK]";

    public static final String EDIT_COMMAND_FORMAT = EditCommand.COMMAND_WORD + " INDEX "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_PHONE + "PHONE] "
            + "[" + PREFIX_EMAIL + "EMAIL] "
            + "[" + PREFIX_ADDRESS + "ADDRESS] "
            + "[" + PREFIX_TAG + "POLICY_NUMBER]... "
            + "[" + PREFIX_REMARK + "REMARK]";

    public static final String DELETE_COMMAND_FORMAT = DeleteCommand.COMMAND_WORD + " INDEX";
    public static final String FIND_COMMAND_FORMAT = FindCommand.COMMAND_WORD
            + " KEYWORD [MORE_KEYWORDS]... or " + FindCommand.COMMAND_WORD
            + " [" + PREFIX_NAME + "KEYWORDS] [" + PREFIX_PHONE + "PHONE_PREFIX] [" + PREFIX_TAG + "POLICY_NUMBER]";
    public static final String LIST_COMMAND_FORMAT = ListCommand.COMMAND_WORD + " [n/|c/]";
    public static final String RESIZE_COMMAND_FORMAT = ResizeCommand.COMMAND_WORD
            + " (p/min|p/med|p/max|s/WIDTH HEIGHT)";
    public static final String CLEAR_COMMAND_FORMAT = ClearCommand.COMMAND_WORD;
    public static final String HELP_COMMAND_FORMAT = HelpCommand.COMMAND_WORD;
    public static final String USER_GUIDE_COMMAND_FORMAT = UserGuideCommand.COMMAND_WORD + " or "
        + UserGuideCommand.COMMAND_ALIAS;
    public static final String EXIT_COMMAND_FORMAT = ExitCommand.COMMAND_WORD;

    private CommandFormats() {}
}
