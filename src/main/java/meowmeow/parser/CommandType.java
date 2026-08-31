package meowmeow.parser;

import meowmeow.MeowmeowException;

/**
 * The set of command keywords Meowmeow understands, each paired with the
 * word a user types to invoke it (e.g. {@code MARK} for "mark"). Matching
 * and argument-stripping are both driven off the same {@code keyword}
 * field, so (unlike separate hardcoded string literals and substring
 * offsets) the two can never drift out of sync.
 *
 * <p>This enum is only about recognising <em>which</em> command a line is;
 * {@link Parser} turns that into a concrete {@link meowmeow.command.Command Command} object to run.
 */
public enum CommandType {
    /** Adds a plain todo. */
    TODO("todo"),
    /** Adds a task due by a given point. */
    DEADLINE("deadline"),
    /** Adds a task spanning a start and end point. */
    EVENT("event"),
    /** Lists every task, or the tasks on one day. */
    LIST("list"),
    /** Marks a task as done. */
    MARK("mark"),
    /** Marks a task as not done. */
    UNMARK("unmark"),
    /** Deletes a task by its position. */
    DELETE("delete"),
    /** Says goodbye and ends the session. */
    BYE("bye");

    private final String keyword;

    CommandType(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns {@code true} if {@code input} is exactly this command's keyword,
     * or the keyword followed by a space and further text (its arguments).
     * Case-insensitive, matching the rest of Meowmeow's input handling.
     */
    private boolean matches(String input) {
        return input.equalsIgnoreCase(keyword)
                || input.regionMatches(true, 0, keyword + " ", 0, keyword.length() + 1);
    }

    /** Returns the keyword a user types to invoke this command, e.g. "mark". */
    public String keyword() {
        return keyword;
    }

    /**
     * Returns everything in {@code input} after this command's keyword,
     * trimmed. Empty for a bare keyword with no arguments (e.g. "mark" -> "").
     */
    public String argumentsOf(String input) {
        return input.length() > keyword.length() ? input.substring(keyword.length()).trim() : "";
    }

    /**
     * Finds the command whose keyword matches the start of {@code input}.
     * Throws MeowmeowException, with the same message Meowmeow has always
     * shown for unrecognised input, if none match.
     */
    public static CommandType fromInput(String input) throws MeowmeowException {
        for (CommandType command : values()) {
            if (command.matches(input)) {
                return command;
            }
        }
        throw new MeowmeowException(" Meow? I don't know what that means.\n Try: " + helpText());
    }

    /**
     * Returns the comma-separated keyword list shown in the "unknown command"
     * message, generated from the enum's own values so it can't drift out
     * of sync with the commands actually implemented.
     */
    private static String helpText() {
        StringBuilder builder = new StringBuilder();
        for (CommandType command : values()) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(command.keyword);
        }
        return builder.toString();
    }
}
