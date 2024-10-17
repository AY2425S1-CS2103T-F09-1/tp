import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ImportCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

public class ImportCommandTest {

    private static final String VALID_CSV_HEADERS = "Name,Phone Number,Email Address,Address,Tags";
    private static final String VALID_PERSON_ENTRY = "\"John Doe\",\"98765432\",\"johnd@example.com\","
            + "\"311, Clementi Ave 2, #02-25\",\"friends\"";
    private static final String INVALID_PERSON_ENTRY = "\"John Doe\",\"98765432\""; // Incomplete person entry
    private static final String EMPTY_CSV_FILE = ""; // Empty file content
    private static final String MULTIPLE_PERSON_ENTRIES = "\"Jane Doe\",\"91234567\",\"jane@example.com\","
            + "\"123, Main St\",\"family\"\n"
            + "\"John Smith\",\"87654321\",\"john@example.com\",\"456, Secondary St\",\"coworkers\"";
    private static final String INVALID_PARSE_LINE_FILE = "./data/InvalidParseLineEntry.csv"; // Invalid parse line file
    private static final String INVALID_NAME_FILE = "./data/InvalidNameEntry.csv"; // New invalid name entry file
    private static final String TEST_DIRECTORY = "./data";

    private Model model;
    private Model expectedModel;
    private Path validFilePath;
    private Path invalidFilePath;
    private Path emptyFilePath;
    private Path multipleFilePath;
    private Path invalidParseLineFilePath; // Invalid parse line file
    private Path invalidNameFilePath; // New path for invalid name entry file
    private Path nonExistentFilePath;

    @BeforeEach
    public void setUp() throws IOException {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());

        // Create paths for valid, invalid, empty, multiple, parse line error, and invalid name files
        validFilePath = Paths.get(TEST_DIRECTORY, "ValidImportContacts.csv");
        invalidFilePath = Paths.get(TEST_DIRECTORY, "InvalidImportContacts.csv");
        emptyFilePath = Paths.get(TEST_DIRECTORY, "EmptyImportContacts.csv");
        multipleFilePath = Paths.get(TEST_DIRECTORY, "MultipleImportContacts.csv");
        invalidParseLineFilePath = Paths.get(TEST_DIRECTORY, "InvalidParseLineEntry.csv"); // Invalid parse line file
        invalidNameFilePath = Paths.get(TEST_DIRECTORY, "InvalidNameEntry.csv"); // New invalid name entry file
        nonExistentFilePath = Paths.get(TEST_DIRECTORY, "NonExistentFile.csv");

        // Ensure the directory exists
        Files.createDirectories(validFilePath.getParent());

        // Write valid CSV content
        try (BufferedWriter writer = Files.newBufferedWriter(validFilePath)) {
            writer.write(VALID_CSV_HEADERS);
            writer.newLine();
            writer.write(VALID_PERSON_ENTRY);
        }

        // Write invalid CSV content
        try (BufferedWriter writer = Files.newBufferedWriter(invalidFilePath)) {
            writer.write(VALID_CSV_HEADERS);
            writer.newLine();
            writer.write(INVALID_PERSON_ENTRY);
        }

        // Create an empty CSV file
        try (BufferedWriter writer = Files.newBufferedWriter(emptyFilePath)) {
            writer.write(VALID_CSV_HEADERS); // Only headers, no entries
        }

        // Create a CSV file with multiple valid entries
        try (BufferedWriter writer = Files.newBufferedWriter(multipleFilePath)) {
            writer.write(VALID_CSV_HEADERS);
            writer.newLine();
            writer.write(MULTIPLE_PERSON_ENTRIES);
        }

        // Create an invalid CSV file entry that will cause parseLine to throw an error
        try (BufferedWriter writer = Files.newBufferedWriter(invalidParseLineFilePath)) {
            writer.write(VALID_CSV_HEADERS);
            writer.newLine();
            writer.write("\"John Doe\",\"98765432\",\"johnd@example.com\","
                    + "\"311, Clementi Ave 2, #02-25\",\"friends\",\"ExtraField\"");
        }

        // Create a CSV file with an invalid name entry (e.g., empty name or special characters)
        try (BufferedWriter writer = Files.newBufferedWriter(invalidNameFilePath)) {
            writer.write(VALID_CSV_HEADERS);
            writer.newLine();
            writer.write("\".\",\"98765432\",\"johnd@example.com\",\"311, Clementi Ave 2, #02-25\",\"friends\"");
        }
    }

    @AfterEach
    public void tearDown() throws IOException {
        // Clean up the files after each test
        if (Files.exists(validFilePath)) {
            Files.delete(validFilePath);
        }
        if (Files.exists(invalidFilePath)) {
            Files.delete(invalidFilePath);
        }
        if (Files.exists(emptyFilePath)) {
            Files.delete(emptyFilePath);
        }
        if (Files.exists(multipleFilePath)) {
            Files.delete(multipleFilePath);
        }
        if (Files.exists(invalidParseLineFilePath)) {
            Files.delete(invalidParseLineFilePath); // Clean up invalid parse line file
        }
        if (Files.exists(invalidNameFilePath)) {
            Files.delete(invalidNameFilePath); // Clean up invalid name file
        }
    }

    @Test
    public void execute_fileDoesNotExist_throwsCommandException() {
        ImportCommand importCommand = new ImportCommand("NonExistentFile.csv");
        try {
            importCommand.execute(model);
        } catch (CommandException e) {
            assertTrue(e.getMessage().contains(ImportCommand.MESSAGE_FILE_DOES_NOT_EXIST));
        }
    }

    @Test
    public void execute_invalidFileFormat_throwsCommandException() {
        ImportCommand importCommand = new ImportCommand("InvalidImportContacts.csv");
        try {
            importCommand.execute(model);
        } catch (CommandException e) {
            assertTrue(e.getMessage().contains(ImportCommand.MESSAGE_INCORRECT_FILE_FORMAT));
        }
    }

    @Test
    public void execute_validFile_importSuccess() throws CommandException {
        ImportCommand importCommand = new ImportCommand("ValidImportContacts.csv");
        Name name = new Name("John Doe");
        Phone phone = new Phone("98765432");
        Email email = new Email("johnd@example.com");
        Address address = new Address("311, Clementi Ave 2, #02-25");
        Tag tag = new Tag("friends");
        Set<Tag> tagSet = new HashSet<>();
        tagSet.add(tag);
        Person person = new Person(name, phone, email, address, tagSet);
        expectedModel.addPerson(person);
        // Ensure success of import
        assertCommandSuccess(importCommand, model,
                String.format(ImportCommand.MESSAGE_SUCCESS, "ValidImportContacts.csv"), expectedModel);
    }

    @Test
    public void execute_emptyFile_throwsCommandException() {
        ImportCommand importCommand = new ImportCommand("EmptyImportContacts.csv");
        try {
            importCommand.execute(model);
        } catch (CommandException e) {
            assertTrue(e.getMessage().contains(ImportCommand.MESSAGE_INCORRECT_FILE_FORMAT));
        }
    }

    @Test
    public void execute_multipleValidEntries_importSuccess() throws CommandException {
        ImportCommand importCommand = new ImportCommand("MultipleImportContacts.csv");
        Name name1 = new Name("Jane Doe");
        Phone phone1 = new Phone("91234567");
        Email email1 = new Email("jane@example.com");
        Address address1 = new Address("123, Main St");
        Tag tag1 = new Tag("family");
        Set<Tag> tagSet1 = new HashSet<>();
        tagSet1.add(tag1);
        Person person1 = new Person(name1, phone1, email1, address1, tagSet1);

        Name name2 = new Name("John Smith");
        Phone phone2 = new Phone("87654321");
        Email email2 = new Email("john@example.com");
        Address address2 = new Address("456, Secondary St");
        Tag tag2 = new Tag("coworkers");
        Set<Tag> tagSet2 = new HashSet<>();
        tagSet2.add(tag2);
        Person person2 = new Person(name2, phone2, email2, address2, tagSet2);

        expectedModel.addPerson(person1);
        expectedModel.addPerson(person2);

        // Ensure success of import
        assertCommandSuccess(importCommand, model,
                String.format(ImportCommand.MESSAGE_SUCCESS, "MultipleImportContacts.csv"), expectedModel);
    }

    @Test
    public void execute_invalidParseLineEntry_throwsCommandException() {
        ImportCommand importCommand = new ImportCommand("InvalidParseLineEntry.csv");
        try {
            importCommand.execute(model);
        } catch (CommandException e) {
            assertTrue(e.getMessage().contains(ImportCommand.MESSAGE_INCORRECT_FILE_FORMAT));
        }
    }

    @Test
    public void execute_invalidNameEntry_throwsCommandException() {
        ImportCommand importCommand = new ImportCommand("InvalidNameEntry.csv");
        try {
            importCommand.execute(model);
        } catch (CommandException e) {
            assertTrue(e.getMessage().contains(ImportCommand.MESSAGE_INCORRECT_FILE_FORMAT));
        }
    }
}
