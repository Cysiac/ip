package meowmeow.parser;

import meowmeow.MeowmeowException;
import meowmeow.command.AddCommand;
import meowmeow.command.Command;
import meowmeow.command.DeleteCommand;
import meowmeow.command.ExitCommand;
import meowmeow.command.FindCommand;
import meowmeow.command.ListCommand;
import meowmeow.command.MarkCommand;
import meowmeow.task.Deadline;
import meowmeow.task.Event;
import meowmeow.task.TaskDateTime;
import meowmeow.task.TaskStatus;
import meowmeow.task.Todo;

/**
 * Turns a full input line into a ready-to-run {@link Command}: it works out
 * which command the line invokes, reads that command's arguments (the task
 * number for "mark"/"unmark"/"delete", the description for "todo", the
 * "/by" of a deadline, the "/from" and "/to" of an event, the date of a
 * "list &lt;date&gt;" query, the keyword of a "find &lt;keyword&gt;" query)
 * and returns the matching {@code Command} object.
 *
 * <p>Every "I don't understand that" case is raised here as a
 * {@link MeowmeowException} carrying the exact message shown to the user, so
 * the command loop only has to catch it, not work out what went wrong.
 *
 * <p>All methods are {@code static}: a {@code Parser} holds no state, it is
 * just a home for these related parsing routines.
 */
public class Parser {

    /** Prevents instantiation - all behaviour is via the static methods. */
    private Parser() {
    }

    /**
     * Turns a full input line into the {@link Command} it asks for.
     *
     * <p>{@link CommandType#fromInput(String)} recognises which command the
     * line invokes (throwing for an unknown one); this method then reads
     * that command's arguments and builds the matching {@code Command}.
     *
     * @throws MeowmeowException if the line is not a known command, or its
     *     arguments do not make sense (missing, malformed, or a date
     *     Meowmeow does not recognise).
     */
    public static Command parse(String fullCommand) throws MeowmeowException {
        CommandType type = CommandType.fromInput(fullCommand);
        String arguments = type.argumentsOf(fullCommand);
        switch (type) {
            case BYE:
                return new ExitCommand();
            case LIST:
                // "list" alone lists everything; "list <date>" filters by day.
                return arguments.isEmpty()
                        ? new ListCommand()
                        : new ListCommand(TaskDateTime.parse(arguments));
            case FIND:
                return new FindCommand(parseKeyword(arguments));
            case MARK:
                return new MarkCommand(parseTaskNumber(arguments, type), TaskStatus.DONE);
            case UNMARK:
                return new MarkCommand(parseTaskNumber(arguments, type), TaskStatus.NOT_DONE);
            case DELETE:
                return new DeleteCommand(parseTaskNumber(arguments, type));
            case TODO:
                return new AddCommand(parseTodo(arguments));
            case DEADLINE:
                return new AddCommand(parseDeadline(arguments));
            case EVENT:
                return new AddCommand(parseEvent(arguments));
            default:
                // Unreachable: every CommandType constant is handled above.
                // Kept so the compiler warns if a new constant is added
                // without a case here.
                throw new IllegalStateException("Unhandled command: " + type);
        }
    }

    /**
     * Reads the 1-based task number for a "mark", "unmark" or "delete"
     * command. The number is not range-checked here - {@link meowmeow.task.TaskList TaskList} does
     * that when the task is actually looked up.
     *
     * @param arguments the text after the command keyword.
     * @param command   the command being run, named in the error message if
     *                  no number was given.
     * @throws MeowmeowException if no number was given, or the text is not a
     *     whole number.
     */
    private static int parseTaskNumber(String arguments, CommandType command) throws MeowmeowException {
        if (arguments.isEmpty()) {
            throw new MeowmeowException(" Meow? Tell me which task number to " + command.keyword() + ".");
        }
        try {
            return Integer.parseInt(arguments);
        } catch (NumberFormatException notANumber) {
            throw new MeowmeowException(" That's not a task number I recognise, meow?");
        }
    }

    /**
     * Builds the {@link Todo} described by a "todo" command's arguments.
     *
     * @throws MeowmeowException if no description was given.
     */
    private static Todo parseTodo(String arguments) throws MeowmeowException {
        if (arguments.isEmpty()) {
            throw new MeowmeowException(" Meow? Tell me what to add, e.g. \"todo borrow book\".");
        }
        return new Todo(arguments);
    }

    /**
     * Reads the keyword a "find" command should search task descriptions
     * for.
     *
     * @param arguments the text after the "find" keyword.
     * @return the search keyword.
     * @throws MeowmeowException if no keyword was given.
     */
    private static String parseKeyword(String arguments) throws MeowmeowException {
        if (arguments.isEmpty()) {
            throw new MeowmeowException(" Meow? Tell me what to search for, e.g. \"find book\".");
        }
        return arguments;
    }

    /**
     * Builds the {@link Deadline} described by a "deadline" command's
     * arguments, in the form {@code <description> /by <when>}.
     *
     * <p>The "/by" marker is searched for from the end and
     * case-insensitively, so the marker closest to the end is the real flag
     * even if the description text also contains "/by", and "/BY"/"/By" work
     * the same.
     *
     * @throws MeowmeowException if the "/by" marker, the description or the
     *     date is missing, or the date is not one Meowmeow recognises.
     */
    private static Deadline parseDeadline(String arguments) throws MeowmeowException {
        int byMarker = lastIndexOfIgnoreCase(arguments, "/by", arguments.length());
        String description = byMarker < 0 ? "" : arguments.substring(0, byMarker).trim();
        String by = byMarker < 0 ? "" : arguments.substring(byMarker + 3).trim();
        if (byMarker < 0 || description.isEmpty() || by.isEmpty()) {
            throw new MeowmeowException(" Meow? Use \"deadline <description> /by <when>\", e.g.\n"
                    + " \"deadline return book /by 2/12/2019 1800\".");
        }
        // TaskDateTime.parse throws MeowmeowException if the text is not a
        // date Meowmeow recognises.
        return new Deadline(description, TaskDateTime.parse(by));
    }

    /**
     * Builds the {@link Event} described by an "event" command's arguments,
     * in the form {@code <description> /from <start> /to <end>}.
     *
     * <p>Same end-first, case-insensitive marker search as
     * {@link #parseDeadline}: the rightmost "/to" is the real flag, and the
     * real "/from" is the rightmost one before it.
     *
     * @throws MeowmeowException if a marker, the description or an endpoint
     *     is missing, an endpoint is not a date Meowmeow recognises, or the
     *     end is before the start.
     */
    private static Event parseEvent(String arguments) throws MeowmeowException {
        int toMarker = lastIndexOfIgnoreCase(arguments, "/to", arguments.length());
        int fromMarker = toMarker < 0 ? -1 : lastIndexOfIgnoreCase(arguments, "/from", toMarker - 1);
        String description = fromMarker < 0 ? "" : arguments.substring(0, fromMarker).trim();
        String from = fromMarker < 0 ? "" : arguments.substring(fromMarker + 5, toMarker).trim();
        String to = toMarker < 0 ? "" : arguments.substring(toMarker + 3).trim();
        if (fromMarker < 0 || description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            throw new MeowmeowException(
                    " Meow? Use \"event <description> /from <start> /to <end>\", e.g.\n"
                    + " \"event project meeting /from 2/12/2019 1400 /to 2/12/2019 1600\".");
        }
        TaskDateTime start = TaskDateTime.parse(from);
        TaskDateTime end = TaskDateTime.parse(to);
        if (!start.isNotAfter(end)) {
            throw new MeowmeowException(" Meow? An event can't end before it starts.");
        }
        return new Event(description, start, end);
    }

    /**
     * Returns the case-insensitive equivalent of
     * {@code text.lastIndexOf(marker, fromIndex)}: finds the marker regardless
     * of how the user capitalised it, while still returning an index into the
     * original (not lowercased) text.
     */
    private static int lastIndexOfIgnoreCase(String text, String marker, int fromIndex) {
        return text.toLowerCase().lastIndexOf(marker.toLowerCase(), fromIndex);
    }
}
