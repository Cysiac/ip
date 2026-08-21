import java.util.ArrayList;
import java.util.Scanner;

public class Meowmeow {
    private static final String NAME = "Meowmeow";
    private static final String DIVIDER = "____________________________________________________________";

    public static void main(String[] args) {
        printBoxed("(=^-ω-^=)  " + NAME, "Hello! I'm " + NAME + ".", "What can I do for you?");

        // ArrayList<Task> grows as needed, so there's no artificial cap on
        // how many tasks can be stored (unlike a fixed-size array).
        ArrayList<Task> tasks = new ArrayList<>();

        // try-with-resources guarantees the scanner (and System.in) is closed
        // even if something inside the loop throws.
        try (Scanner scanner = new Scanner(System.in)) {
            // hasNextLine() lets the loop end gracefully on EOF (e.g. piped
            // input with no "bye" line) instead of nextLine() throwing.
            while (scanner.hasNextLine()) {
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    // Blank lines aren't a command or a task worth storing.
                    continue;
                }
                // Every command below reports a problem by throwing a
                // MeowmeowException rather than printing directly, so this
                // one catch prints the friendly error for all of them.
                try {
                    if (input.equalsIgnoreCase("bye")) {
                        printBoxed(" /\\_/\\", "( ^.^ )  Meow! Bye bye~", " > ^ <");
                        break;
                    } else if (input.equalsIgnoreCase("list")) {
                        String[] lines = new String[tasks.size() + 1];
                        lines[0] = " Here are the tasks in your list, meow:";
                        for (int i = 0; i < tasks.size(); i++) {
                            lines[i + 1] = " " + (i + 1) + "." + tasks.get(i);
                        }
                        printBoxed(lines);
                    } else if (input.equalsIgnoreCase("unmark")) {
                        // "unmark" with no number given: without this, it
                        // would fall through and get added as a task
                        // literally called "unmark".
                        throw new MeowmeowException(" Meow? Tell me which task number to unmark.");
                    } else if (input.regionMatches(true, 0, "unmark ", 0, 7)) {
                        // "unmark N" reverses "mark N": the N-th listed task
                        // (1-based) goes back to not done.
                        setTaskDone(input.substring(7).trim(), false, tasks);
                    } else if (input.equalsIgnoreCase("mark")) {
                        // Same guard as "unmark" above, for a bare "mark".
                        throw new MeowmeowException(" Meow? Tell me which task number to mark.");
                    } else if (input.regionMatches(true, 0, "mark ", 0, 5)) {
                        // "mark N" marks the N-th listed task (1-based) as done.
                        setTaskDone(input.substring(5).trim(), true, tasks);
                    } else if (input.equalsIgnoreCase("delete")) {
                        // "delete" with no number given: same guard as bare
                        // "mark"/"unmark", so it isn't stored as a literal task.
                        throw new MeowmeowException(" Meow? Tell me which task number to delete.");
                    } else if (input.regionMatches(true, 0, "delete ", 0, 7)) {
                        // "delete N" removes the N-th listed task (1-based).
                        deleteTask(input.substring(7).trim(), tasks);
                    } else if (input.equalsIgnoreCase("todo") || input.regionMatches(true, 0, "todo ", 0, 5)) {
                        // "todo <description>" adds a plain, undated task.
                        String description = input.length() > 4 ? input.substring(4).trim() : "";
                        if (description.isEmpty()) {
                            throw new MeowmeowException(" Meow? Tell me what to add, e.g. \"todo borrow book\".");
                        }
                        addTask(new Todo(description), tasks);
                    } else if (input.equalsIgnoreCase("deadline") || input.regionMatches(true, 0, "deadline ", 0, 9)) {
                        // "deadline <description> /by <when>" adds a task due
                        // by a given point, kept as plain text for now.
                        String rest = input.length() > 8 ? input.substring(8).trim() : "";
                        // Search from the end, case-insensitively: the marker
                        // closest to the end is the real flag, even if the
                        // description text happens to also contain "/by", and
                        // "/BY"/"/By" work the same as "/by".
                        int marker = lastIndexOfIgnoreCase(rest, "/by", rest.length());
                        String description = marker < 0 ? "" : rest.substring(0, marker).trim();
                        String by = marker < 0 ? "" : rest.substring(marker + 3).trim();
                        if (marker < 0 || description.isEmpty() || by.isEmpty()) {
                            throw new MeowmeowException(" Meow? Use \"deadline <description> /by <when>\", e.g.\n"
                                    + " \"deadline return book /by Sunday\".");
                        }
                        addTask(new Deadline(description, by), tasks);
                    } else if (input.equalsIgnoreCase("event") || input.regionMatches(true, 0, "event ", 0, 6)) {
                        // "event <description> /from <start> /to <end>" adds a
                        // task spanning a time range, kept as plain text for now.
                        String rest = input.length() > 5 ? input.substring(5).trim() : "";
                        // Search from the end, same reasoning as "deadline"
                        // above: the rightmost "/to" is the real flag, and the
                        // real "/from" is the rightmost one before it. Both
                        // searches are case-insensitive.
                        int toMarker = lastIndexOfIgnoreCase(rest, "/to", rest.length());
                        int fromMarker = toMarker < 0 ? -1 : lastIndexOfIgnoreCase(rest, "/from", toMarker - 1);
                        String description = fromMarker < 0 ? "" : rest.substring(0, fromMarker).trim();
                        String from = fromMarker < 0 ? "" : rest.substring(fromMarker + 5, toMarker).trim();
                        String to = toMarker < 0 ? "" : rest.substring(toMarker + 3).trim();
                        if (fromMarker < 0 || description.isEmpty() || from.isEmpty() || to.isEmpty()) {
                            throw new MeowmeowException(
                                    " Meow? Use \"event <description> /from <start> /to <end>\", e.g.\n"
                                    + " \"event project meeting /from Mon 2pm /to 4pm\".");
                        }
                        addTask(new Event(description, from, to), tasks);
                    } else {
                        // No known command matched: rather than storing the line
                        // as a typeless task, tell the user what's understood.
                        throw new MeowmeowException(" Meow? I don't know what that means.\n"
                                + " Try: todo, deadline, event, list, mark, unmark, delete, bye");
                    }
                } catch (MeowmeowException e) {
                    printBoxed(e.getMessage().split("\n"));
                }
            }
        }
    }

    /**
     * Sets the done status of the task at the given 1-based position (as
     * shown by "list") and prints a confirmation, shared by "mark" and
     * "unmark". Throws MeowmeowException on invalid input (not a number, or
     * out of range) instead of crashing.
     */
    private static void setTaskDone(String indexText, boolean done, ArrayList<Task> tasks)
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
        if (done) {
            task.markAsDone();
            printBoxed(" Nice! I've marked this task as done, meow:", "   " + task);
        } else {
            task.markAsNotDone();
            printBoxed(" OK, I've marked this task as not done yet, meow:", "   " + task);
        }
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
     * Stores a newly created task and prints the standard confirmation,
     * shared by the "todo"/"deadline"/"event" commands so each one doesn't
     * repeat the confirmation message.
     */
    private static void addTask(Task task, ArrayList<Task> tasks) {
        tasks.add(task);
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
