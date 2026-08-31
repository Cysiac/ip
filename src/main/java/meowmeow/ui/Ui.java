package meowmeow.ui;

import java.util.List;
import java.util.Scanner;

import meowmeow.task.Task;
import meowmeow.task.TaskStatus;

/**
 * Everything the user directly sees or types: reading command lines from
 * stdin, and printing every message Meowmeow shows back. All console text
 * (the divider, the banners, the per-command confirmations) lives here, so
 * the rest of the program can talk about <em>what</em> happened without
 * repeating <em>how</em> it is displayed.
 *
 * <p>{@code Ui} owns the {@link Scanner} over {@code System.in} and
 * implements {@link AutoCloseable} so a caller can hold it in a
 * try-with-resources block and be sure the scanner (and {@code System.in})
 * is closed even if the command loop throws.
 */
public class Ui implements AutoCloseable {
    private static final String NAME = "Meowmeow";
    private static final String DIVIDER = "____________________________________________________________";

    private final Scanner scanner;

    /** Constructs a UI that reads commands from standard input. */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Returns {@code true} while there is another line of input to read. Lets
     * the command loop end gracefully on end-of-input (e.g. piped input with
     * no "bye" line) instead of {@link #readCommand()} throwing.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /** Returns the next line of input, trimmed of surrounding whitespace. */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /** Prints the welcome banner shown once at startup. */
    public void showWelcome() {
        printBoxed("(=^-ω-^=)  " + NAME, "Hello! I'm " + NAME + ".", "What can I do for you?");
    }

    /** Prints the farewell banner shown in response to "bye". */
    public void showFarewell() {
        printBoxed(" /\\_/\\", "( ^.^ )  Meow! Bye bye~", " > ^ <");
    }

    /**
     * Shows an error message. A message may contain {@code \n} to span
     * several lines inside the one boxed block, so it is split here.
     */
    public void showError(String message) {
        printBoxed(message.split("\n"));
    }

    /**
     * Shows a non-fatal warning from {@link meowmeow.storage.Storage Storage} (an unreadable line, a
     * failed save). Printed as a plain line rather than a boxed block,
     * since it can happen while loading, before the conversation proper.
     */
    public void showWarning(String message) {
        System.out.println(message);
    }

    /**
     * Confirms that a task was added, and reports the new task count with
     * the right singular/plural wording.
     *
     * @param task      the task that was just added.
     * @param taskCount the number of tasks now in the list.
     */
    public void showAdded(Task task, int taskCount) {
        printBoxed(" Meow! I've added this task:",
                "   " + task,
                " Now you have " + taskCount + " " + taskWord(taskCount) + " in the list, meow!");
    }

    /**
     * Confirms that a task was removed, and reports the new task count with
     * the right singular/plural wording.
     *
     * @param task      the task that was just removed.
     * @param taskCount the number of tasks now in the list.
     */
    public void showRemoved(Task task, int taskCount) {
        printBoxed(" Meow! I've removed this task:",
                "   " + task,
                " Now you have " + taskCount + " " + taskWord(taskCount) + " in the list, meow!");
    }

    /**
     * Confirms that a task's done/not-done status changed.
     *
     * @param status the task's new status.
     * @param task   the task whose status changed.
     */
    public void showStatusChange(TaskStatus status, Task task) {
        printBoxed(status.getConfirmationMessage(), "   " + task);
    }

    /**
     * Prints the whole task list, numbered from 1 - the response to a bare
     * "list".
     */
    public void showTasks(List<Task> tasks) {
        String[] lines = new String[tasks.size() + 1];
        lines[0] = " Here are the tasks in your list, meow:";
        for (int i = 0; i < tasks.size(); i++) {
            lines[i + 1] = " " + (i + 1) + "." + tasks.get(i);
        }
        printBoxed(lines);
    }

    /**
     * Prints the tasks matching a "list &lt;date&gt;" query. {@code matches}
     * is already filtered to that day by the caller; {@code dateLabel} is the
     * day shown in the header (e.g. {@code "Dec 2 2019"}). The numbers here
     * restart at 1 for this filtered view.
     *
     * @param dateLabel the date as it should read in the message.
     * @param matches   the tasks occurring on that date, in list order.
     */
    public void showTasksOn(String dateLabel, List<Task> matches) {
        if (matches.isEmpty()) {
            printBoxed(" Nothing on " + dateLabel + " - free day, meow!");
            return;
        }
        String[] lines = new String[matches.size() + 1];
        lines[0] = " Here are the tasks on " + dateLabel + ", meow:";
        for (int i = 0; i < matches.size(); i++) {
            lines[i + 1] = " " + (i + 1) + "." + matches.get(i);
        }
        printBoxed(lines);
    }

    /**
     * Prints the tasks matching a "find &lt;keyword&gt;" query. {@code matches}
     * is already filtered by the caller; the numbers here restart at 1 for
     * this filtered view. An empty result gets its own line.
     *
     * @param matches the tasks whose description contains the keyword, in
     *     list order.
     */
    public void showMatchingTasks(List<Task> matches) {
        if (matches.isEmpty()) {
            printBoxed(" No matching tasks, meow!");
            return;
        }
        String[] lines = new String[matches.size() + 1];
        lines[0] = " Here are the matching tasks in your list, meow:";
        for (int i = 0; i < matches.size(); i++) {
            lines[i + 1] = " " + (i + 1) + "." + matches.get(i);
        }
        printBoxed(lines);
    }

    /** "task" for a count of 1, "tasks" otherwise. */
    /** Returns "task" for a count of 1, "tasks" otherwise. */
    private String taskWord(int count) {
        return count == 1 ? "task" : "tasks";
    }

    /**
     * Prints a block of lines surrounded by the divider, matching Meowmeow's
     * standard reply format. Every message goes through here so the boxed
     * layout is written once.
     */
    private void printBoxed(String... lines) {
        System.out.println(DIVIDER);
        for (String line : lines) {
            System.out.println(line);
        }
        System.out.println(DIVIDER);
    }

    /** Closes the underlying scanner (and with it {@code System.in}). */
    @Override
    public void close() {
        scanner.close();
    }
}
