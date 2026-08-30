import java.util.ArrayList;
import java.util.Scanner;

public class Meowmeow {
    private static final String NAME = "Meowmeow";
    private static final String DIVIDER = "____________________________________________________________";

    // Tasks are saved here after every change so they survive between runs.
    // The path segments are passed separately so Storage can join them with
    // the right separator for the current OS; it stays relative to the
    // working directory (the project root, normally) so the app is portable
    // between machines. Storage holds only this path, so one shared instance
    // is fine.
    private static final Storage STORAGE = new Storage("data", "meowmeow.txt");

    public static void main(String[] args) {
        printBoxed("(=^-ω-^=)  " + NAME, "Hello! I'm " + NAME + ".", "What can I do for you?");

        // ArrayList<Task> grows as needed, so there's no artificial cap on
        // how many tasks can be stored (unlike a fixed-size array). The
        // list starts from whatever was saved on the last run (empty on a
        // first run).
        ArrayList<Task> tasks = STORAGE.load();

        // try-with-resources guarantees the scanner (and System.in) is closed
        // even if something inside the loop throws.
        try (Scanner scanner = new Scanner(System.in)) {
            // hasNextLine() lets the loop end gracefully on EOF (e.g. piped
            // input with no "bye" line) instead of nextLine() throwing. The
            // "conversation" label lets "bye" (handled inside the switch,
            // itself inside the try below) exit the loop directly.
            conversation:
            while (scanner.hasNextLine()) {
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    // Blank lines aren't a command or a task worth storing.
                    continue;
                }
                // Every command below reports a problem by throwing a
                // MeowmeowException rather than printing directly, so this
                // one catch prints the friendly error for all of them.
                // Command.fromInput itself throws that way for unrecognised
                // input, so unknown commands are handled by the same catch.
                try {
                    Command command = Command.fromInput(input);
                    String arguments = command.argumentsOf(input);
                    switch (command) {
                    case BYE: {
                        printBoxed(" /\\_/\\", "( ^.^ )  Meow! Bye bye~", " > ^ <");
                        break conversation;
                    }
                    case LIST: {
                        // "list" alone shows everything; "list <date>" shows
                        // only the deadlines/events happening on that day.
                        if (arguments.isEmpty()) {
                            listAllTasks(tasks);
                        } else {
                            listTasksOn(arguments, tasks);
                        }
                        break;
                    }
                    case MARK: {
                        // Bare "mark" with no number given: without this
                        // guard, it would fall through and try to parse ""
                        // as a task number.
                        if (arguments.isEmpty()) {
                            throw new MeowmeowException(" Meow? Tell me which task number to mark.");
                        }
                        setTaskStatus(arguments, TaskStatus.DONE, tasks);
                        break;
                    }
                    case UNMARK: {
                        // Same guard as "mark" above, for a bare "unmark".
                        if (arguments.isEmpty()) {
                            throw new MeowmeowException(" Meow? Tell me which task number to unmark.");
                        }
                        setTaskStatus(arguments, TaskStatus.NOT_DONE, tasks);
                        break;
                    }
                    case DELETE: {
                        // Same guard again, for a bare "delete".
                        if (arguments.isEmpty()) {
                            throw new MeowmeowException(" Meow? Tell me which task number to delete.");
                        }
                        deleteTask(arguments, tasks);
                        break;
                    }
                    case TODO: {
                        // "todo <description>" adds a plain, undated task.
                        if (arguments.isEmpty()) {
                            throw new MeowmeowException(" Meow? Tell me what to add, e.g. \"todo borrow book\".");
                        }
                        addTask(new Todo(arguments), tasks);
                        break;
                    }
                    case DEADLINE: {
                        // "deadline <description> /by <when>" adds a task due
                        // by a given point, kept as plain text for now.
                        // Search from the end, case-insensitively: the marker
                        // closest to the end is the real flag, even if the
                        // description text happens to also contain "/by", and
                        // "/BY"/"/By" work the same as "/by".
                        int byMarker = lastIndexOfIgnoreCase(arguments, "/by", arguments.length());
                        String deadlineDescription = byMarker < 0 ? "" : arguments.substring(0, byMarker).trim();
                        String by = byMarker < 0 ? "" : arguments.substring(byMarker + 3).trim();
                        if (byMarker < 0 || deadlineDescription.isEmpty() || by.isEmpty()) {
                            throw new MeowmeowException(" Meow? Use \"deadline <description> /by <when>\", e.g.\n"
                                    + " \"deadline return book /by 2/12/2019 1800\".");
                        }
                        // TaskDateTime.parse turns the "/by" text into a real
                        // date; it throws MeowmeowException (caught below) if
                        // the text isn't a date Meowmeow recognises.
                        addTask(new Deadline(deadlineDescription, TaskDateTime.parse(by)), tasks);
                        break;
                    }
                    case EVENT: {
                        // "event <description> /from <start> /to <end>" adds a
                        // task spanning a time range, kept as plain text for now.
                        // Search from the end, same reasoning as "deadline"
                        // above: the rightmost "/to" is the real flag, and the
                        // real "/from" is the rightmost one before it. Both
                        // searches are case-insensitive.
                        int toMarker = lastIndexOfIgnoreCase(arguments, "/to", arguments.length());
                        int fromMarker = toMarker < 0 ? -1 : lastIndexOfIgnoreCase(arguments, "/from", toMarker - 1);
                        String eventDescription = fromMarker < 0 ? "" : arguments.substring(0, fromMarker).trim();
                        String from = fromMarker < 0 ? "" : arguments.substring(fromMarker + 5, toMarker).trim();
                        String to = toMarker < 0 ? "" : arguments.substring(toMarker + 3).trim();
                        if (fromMarker < 0 || eventDescription.isEmpty() || from.isEmpty() || to.isEmpty()) {
                            throw new MeowmeowException(
                                    " Meow? Use \"event <description> /from <start> /to <end>\", e.g.\n"
                                    + " \"event project meeting /from 2/12/2019 1400 /to 2/12/2019 1600\".");
                        }
                        // Both endpoints are parsed into real dates (parse
                        // throws, caught below, on unrecognised text).
                        TaskDateTime start = TaskDateTime.parse(from);
                        TaskDateTime end = TaskDateTime.parse(to);
                        if (!start.isNotAfter(end)) {
                            throw new MeowmeowException(" Meow? An event can't end before it starts.");
                        }
                        addTask(new Event(eventDescription, start, end), tasks);
                        break;
                    }
                    default:
                        // Unreachable: every Command constant has a case
                        // above. Kept so the compiler can warn if a new
                        // constant is ever added without handling it here.
                        throw new IllegalStateException("Unhandled command: " + command);
                    }
                } catch (MeowmeowException e) {
                    printBoxed(e.getMessage().split("\n"));
                }
            }
        }
    }

    /**
     * Sets the status of the task at the given 1-based position (as shown
     * by "list") and prints the confirmation for that status, shared by
     * "mark" and "unmark". Throws MeowmeowException on invalid input (not a
     * number, or out of range) instead of crashing.
     */
    private static void setTaskStatus(String indexText, TaskStatus status, ArrayList<Task> tasks)
            throws MeowmeowException {
        int index;
        try {
            index = Integer.parseInt(indexText);
        } catch (NumberFormatException e) {
            throw new MeowmeowException(" That's not a task number I recognise, meow?");
        }
        if (index < 1 || index > tasks.size()) {
            throw new MeowmeowException(" Meow? Task " + index + " doesn't exist in your list.");
        }
        Task task = tasks.get(index - 1);
        task.setStatus(status);
        STORAGE.save(tasks);
        printBoxed(status.getConfirmationMessage(), "   " + task);
    }

    /**
     * Removes the task at the given 1-based position (as shown by "list")
     * and prints a confirmation. Throws MeowmeowException on invalid input
     * (not a number, or out of range) instead of crashing.
     */
    private static void deleteTask(String indexText, ArrayList<Task> tasks) throws MeowmeowException {
        int index;
        try {
            index = Integer.parseInt(indexText);
        } catch (NumberFormatException e) {
            throw new MeowmeowException(" That's not a task number I recognise, meow?");
        }
        if (index < 1 || index > tasks.size()) {
            throw new MeowmeowException(" Meow? Task " + index + " doesn't exist in your list.");
        }
        Task removed = tasks.remove(index - 1);
        STORAGE.save(tasks);
        String taskWord = tasks.size() == 1 ? "task" : "tasks";
        printBoxed(" Meow! I've removed this task:",
                "   " + removed,
                " Now you have " + tasks.size() + " " + taskWord + " in the list, meow!");
    }

    /**
     * Case-insensitive equivalent of {@code text.lastIndexOf(marker, fromIndex)}:
     * finds the marker regardless of how the user capitalized it, while
     * still returning an index into the original (not lowercased) text.
     */
    private static int lastIndexOfIgnoreCase(String text, String marker, int fromIndex) {
        return text.toLowerCase().lastIndexOf(marker.toLowerCase(), fromIndex);
    }

    /**
     * Prints the whole task list, numbered from 1, under the standard
     * header - the behaviour of a bare "list".
     */
    private static void listAllTasks(ArrayList<Task> tasks) {
        String[] lines = new String[tasks.size() + 1];
        lines[0] = " Here are the tasks in your list, meow:";
        for (int i = 0; i < tasks.size(); i++) {
            lines[i + 1] = " " + (i + 1) + "." + tasks.get(i);
        }
        printBoxed(lines);
    }

    /**
     * Prints only the tasks occurring on the given date - deadlines due
     * that day, and events whose span covers it (see
     * {@link Task#occursOn(java.time.LocalDate)}). The date is accepted in
     * any of {@link TaskDateTime}'s input formats; a time, if given, is
     * ignored since the match is by day.
     *
     * <p>The numbers shown here restart at 1 for this filtered view and are
     * <em>not</em> the positions "mark"/"unmark"/"delete" expect - a bare
     * "list" remains the reference for those.
     */
    private static void listTasksOn(String dateText, ArrayList<Task> tasks) throws MeowmeowException {
        TaskDateTime when = TaskDateTime.parse(dateText);
        ArrayList<String> lines = new ArrayList<>();
        for (Task task : tasks) {
            if (task.occursOn(when.getDate())) {
                lines.add(" " + (lines.size() + 1) + "." + task);
            }
        }
        if (lines.isEmpty()) {
            printBoxed(" Nothing on " + when.toDateString() + " - free day, meow!");
            return;
        }
        lines.add(0, " Here are the tasks on " + when.toDateString() + ", meow:");
        printBoxed(lines.toArray(new String[0]));
    }

    /**
     * Stores a newly created task and prints the standard confirmation,
     * shared by the "todo"/"deadline"/"event" commands so each one doesn't
     * repeat the confirmation message.
     */
    private static void addTask(Task task, ArrayList<Task> tasks) {
        tasks.add(task);
        STORAGE.save(tasks);
        String taskWord = tasks.size() == 1 ? "task" : "tasks";
        printBoxed(" Meow! I've added this task:",
                "   " + task,
                " Now you have " + tasks.size() + " " + taskWord + " in the list, meow!");
    }

    /**
     * Prints a block of lines surrounded by the divider, matching Meowmeow's
     * standard reply format. Used for every message so the boxed layout
     * only needs to be written once.
     */
    private static void printBoxed(String... lines) {
        System.out.println(DIVIDER);
        for (String line : lines) {
            System.out.println(line);
        }
        System.out.println(DIVIDER);
    }
}
