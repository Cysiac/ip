/**
 * Makes sense of the arguments a user typed after a command keyword: the
 * task number for "mark"/"unmark"/"delete", the description for "todo", the
 * "/by" of a deadline, the "/from" and "/to" of an event, and the date of a
 * "list &lt;date&gt;" query.
 *
 * <p>Which command a line invokes is still recognised by
 * {@link CommandType#fromInput(String)}; {@code Parser} takes over from there.
 * Every "I don't understand that" case is raised here as a
 * {@link MeowmeowException} carrying the exact message shown to the user, so
 * the command loop only has to catch it, not work out what went wrong.
 *
 * <p>All methods are {@code static}: a {@code Parser} holds no state, it is
 * just a home for these related parsing routines.
 */
public class Parser {

    /** Not meant to be instantiated - all behaviour is via the static methods. */
    private Parser() {
    }

    /**
     * Reads the 1-based task number for a "mark", "unmark" or "delete"
     * command. The number is not range-checked here - {@link TaskList}
     * does that when the task is actually looked up.
     *
     * @param arguments the text after the command keyword.
     * @param command   the command being run, named in the error message if
     *                  no number was given.
     * @throws MeowmeowException if no number was given, or the text is not a
     *     whole number.
     */
    public static int parseTaskNumber(String arguments, CommandType command) throws MeowmeowException {
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
    public static Todo parseTodo(String arguments) throws MeowmeowException {
        if (arguments.isEmpty()) {
            throw new MeowmeowException(" Meow? Tell me what to add, e.g. \"todo borrow book\".");
        }
        return new Todo(arguments);
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
    public static Deadline parseDeadline(String arguments) throws MeowmeowException {
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
    public static Event parseEvent(String arguments) throws MeowmeowException {
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
     * Parses the date in a "list &lt;date&gt;" query. A time, if given, is
     * accepted but ignored by the caller since the filter matches by day.
     *
     * @throws MeowmeowException if the text is not a date Meowmeow recognises.
     */
    public static TaskDateTime parseListDate(String arguments) throws MeowmeowException {
        return TaskDateTime.parse(arguments);
    }

    /**
     * Case-insensitive equivalent of {@code text.lastIndexOf(marker, fromIndex)}:
     * finds the marker regardless of how the user capitalised it, while
     * still returning an index into the original (not lowercased) text.
     */
    private static int lastIndexOfIgnoreCase(String text, String marker, int fromIndex) {
        return text.toLowerCase().lastIndexOf(marker.toLowerCase(), fromIndex);
    }
}
