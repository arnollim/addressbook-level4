# arnollim
###### /java/seedu/address/logic/commands/PrintCommand.java
``` java
package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import seedu.address.model.insurance.ReadOnlyInsurance;
import seedu.address.model.insurance.UniqueLifeInsuranceList;
import seedu.address.model.person.ReadOnlyPerson;

/**
 * Prints the list of contacts, along with any associated
 * insurance policies where the contact is involved in,
 * into a printable, readable .txt file.
 */
public class PrintCommand extends Command {

    public static final String[] COMMAND_WORDS = {"print"};
    public static final String COMMAND_WORD = "print";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Saves the addressbook into a .txt file named by you for your viewing.\n"
            + "Example: " + COMMAND_WORD + " filename\n"
            + "file can then be found in the in doc/books folder as filename.txt";

    public static final String MESSAGE_SUCCESS = "Addressbook has been saved! "
            + "Find your addressbook in the .txt file named by you in the doc/books folder.";

    private final String fileName;

    public PrintCommand(String filename) {
        requireNonNull(filename);

        this.fileName = filename;
    }


    @Override
    public CommandResult execute() {

        List<ReadOnlyPerson> lastShownList = model.getFilteredPersonList();

        List<String> lines = new ArrayList<>();
        String timeStamp = new SimpleDateFormat("dd/MM/YYYY" + " " + "HH:mm:ss").format(new Date());
        lines.add("Addressbook was last updated on: " + timeStamp + "\n");

        int personIndex = 1;
        for (ReadOnlyPerson person: lastShownList) {
            String entry = personIndex + ". " + person.getAsText();
            lines.add(entry);

            UniqueLifeInsuranceList insurances = person.getLifeInsurances();
            for (ReadOnlyInsurance insurance: insurances) {
                lines.add("Insurance Policy: =========");
                String owner = insurance.getOwner().getName();
                String insured = insurance.getInsured().getName();
                String beneficiary = insurance.getBeneficiary().getName();
                String premium = insurance.getPremium().toString();
                String signingDate = insurance.getSigningDate();
                String expiryDate = insurance.getExpiryDate();
                lines.add("Owner: " + owner + "\n"
                        + "Insured: " + insured + "\n"
                        + "Beneficiary: " + beneficiary + "\n"
                        + "Premium: " + premium + "\n"
                        + "Signing Date: " + signingDate + "\n"
                        + "Expiry Date: " + expiryDate + "\n"
                );
                lines.add("============");
            }
            personIndex++;
        }

        Path file = Paths.get("docs/books/" + fileName + ".txt");
        try {
            Files.write(file, lines, Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //System.out.println("test");
        return new CommandResult(MESSAGE_SUCCESS);
    }

}
```
###### /java/seedu/address/logic/commands/WhyCommand.java
``` java
package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.model.person.Address;
import seedu.address.model.person.Name;
import seedu.address.model.person.ReadOnlyPerson;


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

    public static final String SHOWING_WHY_MESSAGE = "Because %1$s lives in \n%2$s";

    private final Index targetIndex;

    public WhyCommand(Index targetIndex) {
        requireNonNull(targetIndex);

        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute() {
        //EventsCenter.getInstance().post(new ShowHelpRequestEvent());

        List<ReadOnlyPerson> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            //throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        ReadOnlyPerson personToDelete = lastShownList.get(targetIndex.getZeroBased());
        Name name = personToDelete.getName();
        Address address = personToDelete.getAddress();
        String reason = personToDelete.getReason();
        //return new CommandResult(String.format(SHOWING_WHY_MESSAGE, name, address));
        return new CommandResult(reason);
    }
}
```
###### /java/seedu/address/logic/parser/AddressBookParser.java
``` java
        case PRINT:
            return new PrintCommandParser().parse(arguments);

        case WHY:
            return new WhyCommandParser().parse(arguments);
```
###### /java/seedu/address/logic/parser/AddressBookParser.java
``` java

        default:
            throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
        }
    }

    /**
     * Searches the entire list of acceptable command words in each command and returns the enumerated value type.
     * @param commandWord
     * @return enumerated value for the switch statement to process
     */

    private CommandType getCommandType(String commandWord) {
        for (String word : AddCommand.COMMAND_WORDS) {
            if (commandWord.contentEquals(word)) {
                return CommandType.ADD;
            }
        }
        for (String word : AddLifeInsuranceCommand.COMMAND_WORDS) {
            if (commandWord.contentEquals(word)) {
                return CommandType.ADDLI;
            }
        }
        for (String word : ClearCommand.COMMAND_WORDS) {
            if (commandWord.contentEquals(word)) {
                return CommandType.CLEAR;
            }
        }
        for (String word : DeleteCommand.COMMAND_WORDS) {
            if (commandWord.contentEquals(word)) {
                return CommandType.DEL;
            }
        }
        for (String word : EditCommand.COMMAND_WORDS) {
            if (commandWord.contentEquals(word)) {
                return CommandType.EDIT;
            }
        }
        for (String word : ExitCommand.COMMAND_WORDS) {
            if (commandWord.contentEquals(word)) {
                return CommandType.EXIT;
            }
        }
        for (String word : FindCommand.COMMAND_WORDS) {
            if (commandWord.contentEquals(word)) {
                return CommandType.FIND;
            }
        }
        for (String word : PartialFindCommand.COMMAND_WORDS) {
            if (commandWord.contentEquals(word)) {
                return CommandType.PFIND;
            }
        }
        for (String word : HelpCommand.COMMAND_WORDS) {
            if (commandWord.contentEquals(word)) {
                return CommandType.HELP;
            }
        }
        for (String word : HistoryCommand.COMMAND_WORDS) {
            if (commandWord.contentEquals(word)) {
                return CommandType.HISTORY;
            }
        }
        for (String word : ListCommand.COMMAND_WORDS) {
            if (commandWord.contentEquals(word)) {
                return CommandType.LIST;
            }
        }
        for (String word : RedoCommand.COMMAND_WORDS) {
            if (commandWord.contentEquals(word)) {
                return CommandType.REDO;
            }
        }
        for (String word : SelectCommand.COMMAND_WORDS) {
            if (commandWord.contentEquals(word)) {
                return CommandType.SELECT;
            }
        }
        for (String word : UndoCommand.COMMAND_WORDS) {
            if (commandWord.contentEquals(word)) {
                return CommandType.UNDO;
            }
        }

```
###### /java/seedu/address/logic/parser/AddressBookParser.java
``` java
        for (String word : WhyCommand.COMMAND_WORDS) {
            if (commandWord.contentEquals(word)) {
                return CommandType.WHY;
            }
        }
        for (String word : PrintCommand.COMMAND_WORDS) {
            if (commandWord.contentEquals(word)) {
                return CommandType.PRINT;
            }
        }
```
###### /java/seedu/address/logic/parser/AddressBookParser.java
``` java

        return CommandType.NONE;
    }


}
```
###### /java/seedu/address/logic/parser/PrintCommandParser.java
``` java
/**
 * Parses input arguments and identifies the desired filename to return a new PrintCommand
 */
public class PrintCommandParser implements Parser<PrintCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the PrintCommand
     * and returns a PrintCommand Object with the specified file name
     * @throws ParseException if the user input does not conform the expected format
     * which requires at a valid string
     */
    public PrintCommand parse(String args) throws ParseException {
        try {
            String filename = ParserUtil.parseFilePath(args);
            return new PrintCommand(filename);
        } catch (IllegalValueException ive) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, PrintCommand.MESSAGE_USAGE));
        }
    }

}
```
###### /java/seedu/address/logic/parser/WhyCommandParser.java
``` java
package seedu.address.logic.parser;

//import static java.util.Objects.requireNonNull;
//import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.exceptions.IllegalValueException;
//import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.WhyCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * WhyCommandParser: Adapted from DeleteCommandParser due to similarities
 */
public class WhyCommandParser implements Parser<WhyCommand> {
    /**
     * Parses the given {@code String} of arguments in the context of the ReasonCommand
     * and returns an RemarkCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public WhyCommand parse(String args) throws ParseException {
        /**
         Parsing
         */
        try {
            Index index = ParserUtil.parseIndex(args);
            return new WhyCommand(index);
        } catch (IllegalValueException ive) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, WhyCommand.MESSAGE_USAGE));
        }
    }

}
```
###### /java/seedu/address/model/person/Person.java
``` java
    @Override
    public String getReason() {
        Address a = this.getAddress();
        Name n = this.getName();
        this.reason = String.format(SHOWING_WHY_MESSAGE, n, a);
        return reason;
    }
```
###### /java/seedu/address/model/person/Reason.java
``` java
package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.exceptions.IllegalValueException;

/**
 * Represents a Person's reason for "why" in the address book.
 */
public class Reason {

    public static final String SHOWING_WHY_MESSAGE = "Because %1$s lives in %2$s";
    public static final String MESSAGE_ADDRESS_CONSTRAINTS =
            "Person reason can take any values, and it should not be blank";

    /*
     * The first character of the address must not be a whitespace,
     * otherwise " " (a blank string) becomes a valid input.
     */
    public static final String ADDRESS_VALIDATION_REGEX = "[^\\s].*";

    public final String value;

    /**
     * Validates given address.
     *
     * @throws IllegalValueException if given address string is invalid.
     */
    public Reason(String reason) throws IllegalValueException {
        requireNonNull(reason);
        if (!isValidReason(reason)) {
            throw new IllegalValueException(MESSAGE_ADDRESS_CONSTRAINTS);
        }
        this.value = reason;
    }

    /**
     * Returns true if a given string is a valid person email.
     */
    public static boolean isValidReason(String test) {
        return test.matches(ADDRESS_VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Address // instanceof handles nulls
                && this.value.equals(((Address) other).value)); // state check
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
```
###### /java/seedu/address/model/person/UniquePersonList.java
``` java
            int x = String.CASE_INSENSITIVE_ORDER.compare(first.getName().fullName, second.getName().fullName);
            if (x == 0) {
                x = (first.getName().fullName).compareTo(second.getName().fullName);
            }
            return x;
        });
```