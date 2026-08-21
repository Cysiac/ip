import java.util.Scanner;

public class Meowmeow {
    private static final String NAME = "Meowmeow";
    private static final String DIVIDER = "____________________________________________________________";
    private static final int MAX_ITEMS = 100;

    public static void main(String[] args) {
        printBoxed("(=^-ω-^=)  " + NAME, "Hello! I'm " + NAME + ".", "What can I do for you?");

        // Fixed-size store for the tasks the user adds. The spec caps the
        // course project at 100 tasks for this stage, so a plain array
        // (rather than a resizable list) is sufficient.
        Task[] tasks = new Task[MAX_ITEMS];
        int taskCount = 0;

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
                } else if (input.equalsIgnoreCase("bye")) {
                    printBoxed(" /\\_/\\", "( ^.^ )  Meow! Bye bye~", " > ^ <");
                    break;
                } else if (input.equalsIgnoreCase("list")) {
                    String[] lines = new String[taskCount + 1];
                    lines[0] = " Here are the tasks in your list, meow:";
                    for (int i = 0; i < taskCount; i++) {
                        lines[i + 1] = " " + (i + 1) + "." + tasks[i];
                    }
                    printBoxed(lines);
                } else if (input.equalsIgnoreCase("unmark")) {
                    // "unmark" with no number given: without this, it would
                    // fall through and get added as a task literally called
                    // "unmark".
                    printBoxed(" Meow? Tell me which task number to unmark.");
                } else if (input.regionMatches(true, 0, "unmark ", 0, 7)) {
                    // "unmark N" reverses "mark N": the N-th listed task
                    // (1-based) goes back to not done.
                    setTaskDone(input.substring(7).trim(), false, tasks, taskCount);
                } else if (input.equalsIgnoreCase("mark")) {
                    // Same guard as "unmark" above, for a bare "mark".
                    printBoxed(" Meow? Tell me which task number to mark.");
                } else if (input.regionMatches(true, 0, "mark ", 0, 5)) {
                    // "mark N" marks the N-th listed task (1-based) as done.
                    setTaskDone(input.substring(5).trim(), true, tasks, taskCount);
                } else if (input.equalsIgnoreCase("todo") || input.regionMatches(true, 0, "todo ", 0, 5)) {
                    // "todo <description>" adds a plain, undated task.
                    String description = input.length() > 4 ? input.substring(4).trim() : "";
                    if (description.isEmpty()) {
                        printBoxed(" Meow? Tell me what to add, e.g. \"todo borrow book\".");
                    } else {
                        taskCount = addTask(new Todo(description), tasks, taskCount);
                    }
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
                        printBoxed(" Meow? Use \"deadline <description> /by <when>\", e.g.",
                                " \"deadline return book /by Sunday\".");
                    } else {
                        taskCount = addTask(new Deadline(description, by), tasks, taskCount);
                    }
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
                        printBoxed(" Meow? Use \"event <description> /from <start> /to <end>\", e.g.",
                                " \"event project meeting /from Mon 2pm /to 4pm\".");
                    } else {
                        taskCount = addTask(new Event(description, from, to), tasks, taskCount);
                    }
                } else {
                    // No known command matched: rather than storing the line
                    // as a typeless task, tell the user what's understood.
                    printBoxed(" Meow? I don't know what that means.",
                            " Try: todo, deadline, event, list, mark, unmark, bye");
                }
            }
        }
    }

    /**
     * Sets the done status of the task at the given 1-based position (as
     * shown by "list") and prints a confirmation, shared by "mark" and
     * "unmark". Invalid input (not a number, or out of range) prints a
     * friendly error instead of crashing.
     */
    private static void setTaskDone(String indexText, boolean done, Task[] tasks, int taskCount) {
        int index;
        try {
            index = Integer.parseInt(indexText);
        } catch (NumberFormatException e) {
            printBoxed(" That's not a task number I recognise, meow?");
            return;
        }
        if (index < 1 || index > taskCount) {
            printBoxed(" Meow? Task " + index + " doesn't exist in your list.");
            return;
        }
        Task task = tasks[index - 1];
        if (done) {
            task.markAsDone();
            printBoxed(" Nice! I've marked this task as done, meow:", "   " + task);
        } else {
            task.markAsNotDone();
            printBoxed(" OK, I've marked this task as not done yet, meow:", "   " + task);
        }
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
     * repeat the capacity check and confirmation message. Returns the
     * updated task count (unchanged if the list was already full).
     */
    private static int addTask(Task task, Task[] tasks, int taskCount) {
        if (taskCount >= tasks.length) {
            printBoxed(" Sorry, I can't remember any more than " + MAX_ITEMS + " things! Meow?");
            return taskCount;
        }
        tasks[taskCount] = task;
        taskCount++;
        String taskWord = taskCount == 1 ? "task" : "tasks";
        printBoxed(" Meow! I've added this task:",
                "   " + task,
                " Now you have " + taskCount + " " + taskWord + " in the list, meow!");
        return taskCount;
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
