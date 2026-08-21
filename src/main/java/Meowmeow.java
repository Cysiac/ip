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
                } else if (taskCount < tasks.length) {
                    tasks[taskCount] = new Task(input);
                    taskCount++;
                    printBoxed(" Meow! I've added this task:", "   " + tasks[taskCount - 1]);
                } else {
                    printBoxed(" Sorry, I can't remember any more than " + MAX_ITEMS + " things! Meow?");
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
