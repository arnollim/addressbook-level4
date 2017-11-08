# Juxarius
###### \java\seedu\address\logic\commands\EditCommand.java
``` java
    /**
     * Creates and returns a {@code Person} with the details of {@code personToEdit}
     * edited with {@code editPersonDescriptor}.
     */
    private static Person createEditedPerson(ReadOnlyPerson personToEdit,
                                             EditPersonDescriptor editPersonDescriptor) {
        assert personToEdit != null;

        Name updatedName = editPersonDescriptor.getName().orElse(personToEdit.getName());
        Phone updatedPhone = editPersonDescriptor.getPhone().orElse(personToEdit.getPhone());
        Email updatedEmail = editPersonDescriptor.getEmail().orElse(personToEdit.getEmail());
        Address updatedAddress = editPersonDescriptor.getAddress().orElse(personToEdit.getAddress());
        DateOfBirth updatedDateOfBirth = editPersonDescriptor.getDateOfBirth().orElse(personToEdit.getDateOfBirth());
        Gender updatedGender = editPersonDescriptor.getGender().orElse(personToEdit.getGender());

        Set<Tag> updatedTags = personToEdit.getTags();

        if (editPersonDescriptor.getTagsToDel().isPresent()) {
            for (Tag tag : editPersonDescriptor.getTagsToDel().get()) {
                if (tag.getTagName().equals("all")) {
                    updatedTags.clear();
                }
            }
            updatedTags.removeAll(editPersonDescriptor.getTagsToDel().get());
        }

        if (editPersonDescriptor.getTags().isPresent()) {
            updatedTags.addAll(editPersonDescriptor.getTags().get());
        }
        return new Person(updatedName, updatedPhone, updatedEmail, updatedAddress,
                updatedDateOfBirth, updatedGender, updatedTags);
    }
```
###### \java\seedu\address\logic\commands\EditCommand.java
``` java
        public void setTagsToDel(Set<Tag> tagsToDel) {
            this.tagsToDel = tagsToDel;
        }

        public Optional<Set<Tag>> getTagsToDel() {
            return Optional.ofNullable(tagsToDel);
        }
```
###### \java\seedu\address\logic\LogicManager.java
``` java
    @Override
    public CommandResult execute(String commandText) throws CommandException, ParseException {
        logger.info("----------------[USER COMMAND][" + commandText + "]");
        try {
            Command command = addressBookParser.parseCommand(commandText);
            command.setData(model, history, undoRedoStack);
            CommandResult result = command.execute();
            undoRedoStack.push(command);
            return result;

        } catch (EmptyFieldException efe) {
            // index check was bypassed, this checks the index before filling empty prefix
            if (efe.getIndex().getOneBased() > model.getFilteredPersonList().size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            }
            commandText = getAutoFilledCommand(commandText, efe.getIndex());
            throw efe;
        } finally {
            history.add(commandText);
        }
    }
```
###### \java\seedu\address\logic\LogicManager.java
``` java
    /**
     * Replaces the given command text with filled command text
     * @param commandText original input command text
     * @param index index of person to edit
     * @return filled command
     */
    private String getAutoFilledCommand(String commandText, Index index) {
        ReadOnlyPerson person = model.getAddressBook().getPersonList().get(index.getZeroBased());
        for (Prefix prefix : PREFIXES_PERSON) {
            String prefixInConcern = prefix.getPrefix();
            if (commandText.contains(prefixInConcern)) {
                String replacementText = prefixInConcern + person.getDetailByPrefix(prefix) + " ";
                commandText = commandText.replaceFirst(prefixInConcern, replacementText);
            }
        }
        if (commandText.contains(PREFIX_TAG.getPrefix())) {
            String formattedTags = PREFIX_TAG.getPrefix()
                    + person.getDetailByPrefix(PREFIX_TAG).replaceAll(" ", " t/") + " ";
            commandText = commandText.replaceFirst(PREFIX_TAG.getPrefix(), formattedTags);
        }
        if (commandText.contains(PREFIX_DELTAG.getPrefix())) {
            String formattedTags = PREFIX_DELTAG.getPrefix()
                    + person.getDetailByPrefix(PREFIX_DELTAG).replaceAll(" ", " t/") + " ";
            commandText = commandText.replaceFirst(PREFIX_DELTAG.getPrefix(), formattedTags);
        }
        return commandText.trim();
    }
```
###### \java\seedu\address\logic\parser\AddressBookParser.java
``` java
    /**
     * Enumerator list to define the types of commands.
     */
    private enum CommandType {
        ADD, ADDLI, CLEAR, DEL, EDIT, EXIT, FIND, PFIND, HELP, HISTORY, LIST, PRINT, REDO, UNDO, SELECT, WHY, NONE
    }

    /**
     * Used for initial separation of command word and args.
     */
    private static final Pattern BASIC_COMMAND_FORMAT = Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)");

    /**
     * Parses user input into command for execution.
     *
     * @param userInput full user input string
     * @return the command based on the user input
     * @throws ParseException if the user input does not conform the expected format
     */
    public Command parseCommand(String userInput) throws ParseException {
        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(userInput.trim());
        if (!matcher.matches()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }

        final String commandWord = matcher.group("commandWord");
        final String arguments = matcher.group("arguments");

        CommandType commandType = getCommandType(commandWord.toLowerCase());

        switch (commandType) {

        case ADD:
            return new AddCommandParser().parse(arguments);

        case ADDLI:
            return new AddLifeInsuranceCommandParser().parse(arguments);

        case EDIT:
            return new EditCommandParser().parse(arguments);

        case SELECT:
            return new SelectCommandParser().parse(arguments);

        case DEL:
            return new DeleteCommandParser().parse(arguments);

        case CLEAR:
            return new ClearCommand();

        case FIND:
            return new FindCommandParser().parse(arguments);

        case PFIND:
            return new PartialFindCommandParser().parse(arguments);

        case LIST:
            return new ListCommand();

        case HISTORY:
            return new HistoryCommand();

        case EXIT:
            return new ExitCommand();

        case HELP:
            return new HelpCommand();

        case UNDO:
            return new UndoCommand();

        case REDO:
            return new RedoCommand();

        case PRINT:
            return new PrintCommand(arguments);

        case WHY:
            return new WhyCommandParser().parse(arguments);

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

        return CommandType.NONE;
    }
```
###### \java\seedu\address\logic\parser\DateParser.java
``` java
/**
 * Parses a string into a LocalDate
 */
public class DateParser {

    public static final String MESSAGE_DATE_CONSTRAINTS = "Date input must have at least 2 arguments.";
    public static final String MESSAGE_INVALID_MONTH = "Month input is invalid.";
    public static final String MESSAGE_INVALID_DAY = "Day input is invalid.";
    public static final String MESSAGE_INVALID_YEAR = "Year input is invalid.";

    public static final String[] MONTH_NAME_SHORT = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    public static final String[] MONTH_NAME_LONG = {"january", "february", "march",
        "april", "may", "june", "july", "august", "september", "october", "november", "december"};
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    /**
     * Parses the given {@code String} of arguments to produce a LocalDate object. Input string
     * must be in the Day-Month-Year format where the month can be a number or the name
     * and the year can be input in 2-digit or 4-digit format.
     * @throws IllegalValueException if the format is not correct.
     */
    /**
     * Parses input dob string
     */
    public LocalDate parse(String dob) throws IllegalValueException {
        List<String> arguments = Arrays.asList(dob.split("[\\s-/.,]"));
        if (arguments.size() < 2) {
            throw new IllegalValueException(MESSAGE_DATE_CONSTRAINTS);
        }
        String day = arguments.get(0);
        String month = arguments.get(1);
        String year = arguments.size() > 2 ? arguments.get(2) : String.valueOf(LocalDate.now().getYear());
        return LocalDate.parse(getValidDay(day) + " " + getValidMonth(month) + " " + getValidYear(year),
                DATE_FORMAT);
    }

    /**
     *
     * @param year 2 or 4 digit string
     * @return
     * @throws IllegalValueException
     */
    public String getValidYear(String year) throws IllegalValueException {
        int currYear = LocalDate.now().getYear();
        if (year.length() > 4) {
            year = year.substring(0, 4);
        }
        if (!year.matches("\\d+") || (year.length() != 2 && year.length() != 4)) {
            throw new IllegalValueException(MESSAGE_INVALID_YEAR);
        } else if (year.length() == 2) {
            int iYear = Integer.parseInt(year);
            // Change this if condition to edit your auto-correcting range for 2-digit year inputs
            if (iYear > currYear % 100) {
                return Integer.toString(iYear + (currYear / 100 - 1) * 100);
            } else {
                return Integer.toString(iYear + currYear / 100 * 100);
            }
        } else {
            return year;
        }
    }

    public String getValidDay(String day) throws IllegalValueException {
        if (Integer.parseInt(day) > 31) {
            throw new IllegalValueException(MESSAGE_INVALID_DAY);
        }
        if (day.length() == 1) {
            return "0" + day;
        } else if (day.length() == 2) {
            return day;
        } else {
            throw new IllegalValueException(MESSAGE_INVALID_DAY);
        }
    }

    public String getValidMonth(String month) throws IllegalValueException {
        int iMonth;
        if (month.matches("\\p{Alpha}+")) {
            iMonth = getMonth(month);
        } else {
            iMonth = Integer.parseInt(month);
        }
        if (iMonth > 12 || iMonth < 1) {
            throw new IllegalValueException(MESSAGE_INVALID_MONTH);
        } else {
            return MONTH_NAME_SHORT[iMonth - 1];
        }
    }

    /**
     * finds int month from string month name
     */
    public int getMonth(String monthName) throws IllegalValueException {
        for (int i = 0; i < MONTH_NAME_LONG.length; i++) {
            if (monthName.toLowerCase().equals(MONTH_NAME_LONG[i].toLowerCase())
                    || monthName.toLowerCase().equals(MONTH_NAME_SHORT[i].toLowerCase())) {
                return i + 1;
            }
        }
        throw new IllegalValueException(MESSAGE_INVALID_MONTH);
    }

    /**
     * Takes a LocalDate and produces it in a nice format
     * @param date
     * @return
     */
    public static String dateString(LocalDate date) {
        return date.format(DATE_FORMAT);
    }
}
```
###### \java\seedu\address\logic\parser\EditCommandParser.java
``` java
    /**
     * Parses the given {@code String} of arguments in the context of the EditCommand
     * and returns an EditCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public EditCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(
                        args, PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS,
                        PREFIX_DOB, PREFIX_GENDER, PREFIX_TAG, PREFIX_DELTAG);

        Index index;

        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (IllegalValueException ive) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));
        }

        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();
        try {
            ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME)).ifPresent(editPersonDescriptor::setName);
            ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PHONE)).ifPresent(editPersonDescriptor::setPhone);
            ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL)).ifPresent(editPersonDescriptor::setEmail);
            ParserUtil.parseAddress(argMultimap.getValue(PREFIX_ADDRESS)).ifPresent(editPersonDescriptor::setAddress);
            ParserUtil.parseDateOfBirth(argMultimap.getValue(PREFIX_DOB))
                    .ifPresent(editPersonDescriptor::setDateOfBirth);
            ParserUtil.parseGender(argMultimap.getValue(PREFIX_GENDER))
                    .ifPresent(editPersonDescriptor::setGender);
            parseTagsForEdit(argMultimap.getAllValues(PREFIX_TAG)).ifPresent(editPersonDescriptor::setTags);
            parseDetagsForEdit(argMultimap.getAllValues(PREFIX_DELTAG)).ifPresent(editPersonDescriptor::setTagsToDel);
        } catch (EmptyFieldException efe) {
            throw new EmptyFieldException(efe, index);
        } catch (IllegalValueException ive) {
            throw new ParseException(ive.getMessage(), ive);
        }

        if (!editPersonDescriptor.isAnyFieldEdited()) {
            throw new ParseException(EditCommand.MESSAGE_NOT_EDITED);
        }

        return new EditCommand(index, editPersonDescriptor);
    }
```
###### \java\seedu\address\logic\parser\EditCommandParser.java
``` java
    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>} if {@code tags} is non-empty.
     * If {@code tags} contain only one element which is an empty string, it will be parsed into a
     * return an EmptyFieldException which will trigger an autofill
     */
    private Optional<Set<Tag>> parseDetagsForEdit(Collection<String> tags) throws IllegalValueException {
        assert tags != null;

        if (tags.isEmpty()) {
            return Optional.empty();
        }
        if (tags.size() == 1 && tags.contains("")) {
            throw new EmptyFieldException(PREFIX_DELTAG);
        }
        return Optional.of(ParserUtil.parseTags(tags));
    }

}
```
###### \java\seedu\address\logic\parser\exceptions\EmptyFieldException.java
``` java
/**
 * Signifies that a certain field is empty but the prefix is specified.
 */

public class EmptyFieldException extends ParseException {

    private Prefix emptyFieldPrefix;
    private Index index;
    /**
     * @param message should contain information on the empty field.
     */
    public EmptyFieldException(String message) {
        super(message);
    }
    /**
     * @param emptyFieldPrefix contains the prefix of the field that is empty.
     */
    public EmptyFieldException(Prefix emptyFieldPrefix) {
        super(emptyFieldPrefix.getPrefix() + " field is empty");
        this.emptyFieldPrefix = emptyFieldPrefix;
    }

    /**
     * @param index is the oneBasedIndex of the person in concern
     */
    public EmptyFieldException(EmptyFieldException efe, Index index) {
        super(efe.getMessage());
        this.emptyFieldPrefix = efe.getEmptyFieldPrefix();
        this.index = index;
    }

    public Prefix getEmptyFieldPrefix() {
        return emptyFieldPrefix;
    }

    public Index getIndex() {
        return index;
    }
}
```
###### \java\seedu\address\model\insurance\LifeInsurance.java
``` java
    private StringProperty insuranceName;
    private LocalDate signingDate;
    private LocalDate expiryDate;
```
###### \java\seedu\address\model\person\Address.java
``` java
        if (address.isEmpty()) {
            throw new EmptyFieldException(PREFIX_ADDRESS);
        }
```
###### \java\seedu\address\model\person\DateOfBirth.java
``` java
    public static final String MESSAGE_DOB_CONSTRAINTS =
            "Please enter in Day Month Year format where the month can be a number or the name"
                    + " and the year can be input in 2-digit or 4-digit format.";

    /*
     * The first character of the address must not be a whitespace,
     * otherwise " " (a blank string) becomes a valid input.
     */
    public static final String DOB_VALIDATION_REGEX = "\\d+[\\s-./,]\\p{Alnum}+[\\s-./,]\\d+.*";

    public final LocalDate dateOfBirth;
    private boolean dateSet;

    /**
     * Initialise a DateOfBirth object with value of empty String. This can ONLY be used in the default field of
     * {@code AddPersonOptionalFieldDescriptor}
     */
    public DateOfBirth() {
        this.dateOfBirth = LocalDate.now();
        this.dateSet = false;
    }

    /**
     * Validates given Date of Birth.
     *
     * @throws IllegalValueException if given date of birth string is invalid.
     */
    public DateOfBirth(String dob) throws IllegalValueException {
        requireNonNull(dob);
        if (dob.isEmpty()) {
            throw new EmptyFieldException(PREFIX_DOB);
        }
        if (!isValidDateOfBirth(dob)) {
            throw new IllegalValueException(MESSAGE_DOB_CONSTRAINTS);
        }
        this.dateOfBirth = new DateParser().parse(dob);
        this.dateSet = true;
    }

    /**
     * Returns true if a given string is a valid person date of birth.
     */
    public static boolean isValidDateOfBirth(String test) {
        return test.matches(DOB_VALIDATION_REGEX);
    }
    @Override
    public String toString() {
        return dateSet ? dateOfBirth.format(DateParser.DATE_FORMAT) : "";
    }
```
###### \java\seedu\address\ui\CommandBox.java
``` java
    /**
     * Handles the Enter button pressed event.
     */
    @FXML
    private void handleCommandInputChanged() {
        try {
            CommandResult commandResult = logic.execute(commandTextField.getText());
            initHistory();
            historySnapshot.next();
            // process result of the command
            commandTextField.setText("");
            logger.info("Result: " + commandResult.feedbackToUser);
            raise(new NewResultAvailableEvent(commandResult.feedbackToUser, false));
        } catch (EmptyFieldException efe) {
            initHistory();
            // autofill function triggered
            logger.info("Autofill triggered: " + commandTextField.getText());
            historySnapshot.next();
            commandTextField.setText(historySnapshot.previous());
            commandTextField.positionCaret(commandTextField.getText().length());
            raise(new NewResultAvailableEvent("Autofilled!", false));

        } catch (CommandException | ParseException e) {
            initHistory();
            // handle command failure
            setStyleToIndicateCommandFailure();
            logger.info("Invalid command: " + commandTextField.getText());
            raise(new NewResultAvailableEvent(e.getMessage(), true));
        }
    }
```
###### \java\seedu\address\ui\InsuranceIdLabel.java
``` java
    private void setPremiumLevel(Double premium) {
        if (premium > 500.0) {
            insuranceId.getStyleClass().add("gold-insurance-header");
        } else if (premium > 100.0) {
            insuranceId.getStyleClass().add("silver-insurance-header");
        } else {
            insuranceId.getStyleClass().add("normal-insurance-header");
        }
    }
```
###### \java\seedu\address\ui\InsuranceListPanel.java
``` java
    @Subscribe
    private void handleSwitchToProfilePanelRequestEvent(SwitchToProfilePanelRequestEvent event) {
        insuranceListView.getSelectionModel().clearSelection();
    }

    /*@Subscribe
    private void handleInsurancePanelSelectionChangedEvent(InsurancePanelSelectionChangedEvent event) {
        InsuranceProfile selected = insuranceListView.getItems().filtered(insuranceProfile -> {
            return insuranceProfile.getInsurance().equals(event.getInsurance());
        }).get(0);
        insuranceListView.getSelectionModel().select(selected);
    }*/
```
###### \java\seedu\address\ui\InsuranceProfile.java
``` java
    private void setPremiumLevel(Double premium) {
        if (premium > 500.0) {
            insuranceName.getStyleClass().add("gold-insurance-header");
            index.getStyleClass().add("gold-insurance-header");
        } else if (premium > 100.0) {
            insuranceName.getStyleClass().add("silver-insurance-header");
            index.getStyleClass().add("silver-insurance-header");
        } else {
            insuranceName.getStyleClass().add("normal-insurance-header");
            index.getStyleClass().add("normal-insurance-header");
        }
    }
```
###### \java\seedu\address\ui\InsuranceProfilePanel.java
``` java

/**
 * Profile panel for insurance when the respective insurance is selected
 */
public class InsuranceProfilePanel extends UiPart<Region> {
    private static final String FXML = "InsuranceProfilePanel.fxml";
    private static final String PDFFOLDERPATH = "data/";
    private final Logger logger = LogsCenter.getLogger(this.getClass());

    private File insuranceFile;
    private ReadOnlyInsurance insurance;

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private AnchorPane insuranceProfilePanel;
    @FXML
    private Label insuranceName;
    @FXML
    private Label owner;
    @FXML
    private Label insured;
    @FXML
    private Label beneficiary;
    @FXML
    private Label premium;
    @FXML
    private Label signingDate;
    @FXML
    private Label expiryDate;
    @FXML
    private Label contractPath;

    public InsuranceProfilePanel() {
        super(FXML);
        scrollPane.setFitToWidth(true);
        insuranceProfilePanel.prefWidthProperty().bind(scrollPane.widthProperty());
        enableNameToProfileLink(insurance);
        registerAsAnEventHandler(this);
    }

```
###### \java\seedu\address\ui\InsuranceProfilePanel.java
``` java
    private void setPremiumLevel(Double premium) {
        if (premium > 500.0) {
            insuranceName.getStyleClass().add("gold-insurance-header");
        } else if (premium > 100.0) {
            insuranceName.getStyleClass().add("silver-insurance-header");
        } else {
            insuranceName.getStyleClass().add("normal-insurance-header");
        }
    }

    @Subscribe
    private void handleSwitchToInsurancePanelRequestEvent(InsurancePanelSelectionChangedEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        initializeContractFile(event.getInsurance());
        bindListeners(event.getInsurance());
        raise(new SwitchToInsurancePanelRequestEvent());
    }
```
###### \java\seedu\address\ui\PersonListPanel.java
``` java
    @Subscribe
    private void handleInsurancePanelSelectionChangedEvent(InsurancePanelSelectionChangedEvent event) {
        personListView.getSelectionModel().clearSelection();
    }

    @Subscribe
    private void handlePersonNameClickedEvent(PersonNameClickedEvent event) {
        PersonCard selected = personListView.getItems().filtered((p) -> {
            return p.person.getName().toString().equals(event.getPerson().get().getName().toString());
        }).get(0);
        personListView.getSelectionModel().select(selected);
    }
```
###### \resources\view\DarkTheme.css
``` css
.profile-header {
    -fx-font-size: 35pt;
    -fx-font-weight: bolder;
    -fx-text-fill: white;
    -fx-opacity: 1;
}

#profilePanel .profile-field {
    -fx-font-size: 15pt;
    -fx-font-family: "Segoe UI SemiLight";
    -fx-text-fill: white;
    -fx-opacity: 1;
}

#insuranceProfilePanel .static-labels {
    -fx-font-size: 15pt;
    -fx-font-family: "Segoe UI SemiBold";
    -fx-text-fill: white;
    -fx-opacity: 1;
}

.static-insurance-labels {
    -fx-font-size: 13pt;
    -fx-font-family: "Segoe UI SemiBold";
    -fx-text-fill: white;
    -fx-opacity: 1;
}

#insuranceListView #owner:hover, #insured:hover, #beneficiary:hover {
    -fx-font-size: 13pt;
    -fx-font-family: "Segoe UI Light";
    -fx-text-fill: #ff4500;
    -fx-opacity: 1;
}

#insuranceListView .insurance-header {
    -fx-font-size: 17pt;
    -fx-font-family: "Impact";
    -fx-opacity: 1;
}

#insuranceListView .gold-insurance-header {
    -fx-text-fill: #daa520;
}

#insuranceListView .silver-insurance-header {
    -fx-text-fill: #a9a9a9;
}

#insuranceListView .normal-insurance-header {
    -fx-text-fill: white;
}
```
###### \resources\view\DarkTheme.css
``` css
#insuranceListView {
    -fx-background-color: derive(#1d1d1d, 20%);
}
```
###### \resources\view\InsuranceProfile.fxml
``` fxml
         <VBox alignment="CENTER_LEFT" GridPane.columnIndex="1">
            <children>
               <HBox>
                  <children>
                     <Label fx:id="owner" styleClass="particular-link" text="\$owner" />
                  </children>
               </HBox>
               <HBox>
                  <children>
                     <Label fx:id="insured" styleClass="particular-link" text="\$insured" />
                  </children>
               </HBox>
               <HBox>
                  <children>
                     <Label fx:id="beneficiary" styleClass="particular-link" text="\$beneficiary" />
                  </children>
               </HBox>
               <HBox>
                  <children>
                     <Label fx:id="premium" styleClass="particular-link" text="\$premium" />
                  </children>
               </HBox>
            </children>
         </VBox>
```
