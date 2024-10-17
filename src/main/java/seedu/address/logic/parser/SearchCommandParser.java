package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FIELD;

import java.util.List;

import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.SearchCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.AddressContainsKeywordsPredicate;
import seedu.address.model.person.EmailContainsKeywordsPredicate;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.PhoneContainsKeywordsPredicate;
import seedu.address.model.tag.TagContainsKeywordsPredicate;

/**
 * Parses input arguments and creates a new SearchCommand object
 */
public class SearchCommandParser implements Parser<SearchCommand> {
    /**
     * Parses the given {@code String} of arguments in the context of the SearchCommand
     * and returns an SearchCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public SearchCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_FIELD);

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_FIELD);

        EditCommand.EditPersonDescriptor editPersonDescriptor = new EditCommand.EditPersonDescriptor();

        if (!argMultimap.getValue(PREFIX_FIELD).isPresent()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, SearchCommand.MESSAGE_USAGE));
        }

        String fieldAndKeywords = argMultimap.getValue(PREFIX_FIELD).get();
        String field;
        List<String> keywords;

        field = ParserUtil.parseField(fieldAndKeywords);
        keywords = ParserUtil.parseSearchKeywords(fieldAndKeywords);

        switch (field) {
        case "Address":
            return new SearchCommand(new AddressContainsKeywordsPredicate(keywords));
        case "Email":
            return new SearchCommand(new EmailContainsKeywordsPredicate(keywords));
        case "Name":
            return new SearchCommand(new NameContainsKeywordsPredicate(keywords));
        case "Phone":
            return new SearchCommand(new PhoneContainsKeywordsPredicate(keywords));
        case "Tags":
            return new SearchCommand(new TagContainsKeywordsPredicate(keywords));
        default:
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, SearchCommand.MESSAGE_USAGE));
        }
    }
}
