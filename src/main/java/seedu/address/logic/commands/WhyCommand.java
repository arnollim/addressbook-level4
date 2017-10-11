package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.core.EventsCenter;
import seedu.address.commons.events.ui.ShowHelpRequestEvent;
import seedu.address.commons.core.index.Index;


/**
 * Format full help instructions for every command for display.
 */
public class WhyCommand extends Command {

    public static final String[] COMMAND_WORDS = {"why"};
    public static final String COMMAND_WORD = "why";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Tells you why.\n"
            + "Example: " + COMMAND_WORD;

    public static final String MESSAGE_WHY_REMARK_SUCCESS = "Added remark to Person: %1$s";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book.";

    public static final String SHOWING_WHY_MESSAGE = "Because %1$s is cool";

    private final Index targetindex;

    public WhyCommand(Index targetIndex){
        requireNonNull(targetIndex);

        this.targetindex = targetIndex;
    }

    @Override
    public CommandResult execute() {
        //EventsCenter.getInstance().post(new ShowHelpRequestEvent());
        return new CommandResult(SHOWING_WHY_MESSAGE);
    }
}
