package meowmeow.ui;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import meowmeow.task.Task;
import meowmeow.task.TaskStatus;

/**
 * Everything the user directly sees or types: reading command lines from
 * stdin, and building every message Meowmeow shows back. All message text
 * (the banners, the per-command confirmations) lives here, so the rest of
 * the program can talk about <em>what</em> happened without repeating
 * <em>how</em> it reads.
 *
 * <p>Each {@code show...} method <em>returns</em> its message as a plain
 * {@code String} rather than printing it, so the same text can be sent to
 * the console (wrapped in the divider by {@link #print(String)}) or shown
 * in the GUI as-is. Only {@link #print(String)} and {@link #showWarning}
 * write to {@code System.out}.
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

    /**
     * Prints {@code body} to the console wrapped in the standard divider
     * block, matching Meowmeow's console reply format. The GUI does not use
     * this - it shows the {@code show...} strings directly.
     *
     * @param body the message body, as returned by a {@code show...} method.
     */
    public void print(String body) {
        System.out.println(box(body));
    }

    /** Returns the welcome banner shown once at startup. */
    public String showWelcome() {
        return join("(=^-ω-^=)  " + NAME, "Hello! I'm " + NAME + ".", "What can I do for you?");
    }

    /** Returns the farewell banner shown in response to "bye". */
    public String showFarewell() {
        return join(" /\\_/\\", "( ^.^ )  Meow! Bye bye~", " > ^ <");
    }

    /**
     * Returns an error message. A message may already contain {@code \n} to
     * span several lines; it is passed through unchanged.
     *
     * @param message the error text to show.
     * @return the message, ready to display.
     */
    public String showError(String message) {
        return join(message.split("\n"));
    }

    /**
     * Shows a non-fatal warning from {@link meowmeow.storage.Storage Storage} (an unreadable line, a
     * failed save). Printed straight to {@code System.out} as a plain line
     * rather than returned, since it can happen while loading - before the
     * conversation proper - and outside any command's response.
     *
     * @param message the warning text to show.
     */
    public void showWarning(String message) {
        System.out.println(message);
    }

    /**
     * Returns the confirmation that a task was added, with the new task
     * count in the right singular/plural wording.
     *
     * @param task      the task that was just added.
     * @param taskCount the number of tasks now in the list.
     * @return the confirmation message.
     */
    public String showAdded(Task task, int taskCount) {
        return join(" Meow! I've added this task:",
                "   " + task,
                " Now you have " + taskCount + " " + taskWord(taskCount) + " in the list, meow!");
    }

    /**
     * Returns the confirmation that a task was removed, with the new task
     * count in the right singular/plural wording.
     *
     * @param task      the task that was just removed.
     * @param taskCount the number of tasks now in the list.
     * @return the confirmation message.
     */
    public String showRemoved(Task task, int taskCount) {
        return join(" Meow! I've removed this task:",
                "   " + task,
                " Now you have " + taskCount + " " + taskWord(taskCount) + " in the list, meow!");
    }

    /**
     * Returns the confirmation that a task's done/not-done status changed.
     *
     * @param status the task's new status.
     * @param task   the task whose status changed.
     * @return the confirmation message.
     */
    public String showStatusChange(TaskStatus status, Task task) {
        return join(status.getConfirmationMessage(), "   " + task);
    }

    /**
     * Returns the whole task list, numbered from 1 - the response to a bare
     * "list".
     *
     * @param tasks the tasks to list, in list order.
     * @return the numbered list message.
     */
    public String showTasks(List<Task> tasks) {
        return numberedList(" Here are the tasks in your list, meow:", tasks);
    }

    /**
     * Returns the tasks matching a "list &lt;date&gt;" query. {@code matches}
     * is already filtered to that day by the caller; {@code dateLabel} is the
     * day shown in the header (e.g. {@code "Dec 2 2019"}). The numbers here
     * restart at 1 for this filtered view.
     *
     * @param dateLabel the date as it should read in the message.
     * @param matches   the tasks occurring on that date, in list order.
     * @return the numbered list message, or a "free day" line if empty.
     */
    public String showTasksOn(String dateLabel, List<Task> matches) {
        if (matches.isEmpty()) {
            return " Nothing on " + dateLabel + " - free day, meow!";
        }
        return numberedList(" Here are the tasks on " + dateLabel + ", meow:", matches);
    }

    /**
     * Returns the tasks matching a "find &lt;keyword&gt;" query. {@code matches}
     * is already filtered by the caller; the numbers here restart at 1 for
     * this filtered view. An empty result gets its own line.
     *
     * @param matches the tasks whose description contains the keyword, in
     *     list order.
     * @return the numbered list message, or a "no matches" line if empty.
     */
    public String showMatchingTasks(List<Task> matches) {
        if (matches.isEmpty()) {
            return " No matching tasks, meow!";
        }
        return numberedList(" Here are the matching tasks in your list, meow:", matches);
    }

    /**
     * Returns {@code tasks} as a 1-based numbered list under {@code header}.
     * Shared by the "list", "list &lt;date&gt;" and "find" responses, which
     * differ only in that header line and their empty-result wording (which
     * the callers handle before calling here).
     *
     * @param header the line shown above the numbered tasks.
     * @param tasks  the tasks to number, in the order given.
     * @return the header, then one " {@code n.<task>}" line per task.
     */
    private String numberedList(String header, List<Task> tasks) {
        String numbered = IntStream.range(0, tasks.size())
                .mapToObj(i -> " " + (i + 1) + "." + tasks.get(i))
                .collect(Collectors.joining("\n"));
        return numbered.isEmpty() ? header : header + "\n" + numbered;
    }

    /** Returns "task" for a count of 1, "tasks" otherwise. */
    private String taskWord(int count) {
        return count == 1 ? "task" : "tasks";
    }

    /** Joins message lines with a newline into a single body string. */
    private String join(String... lines) {
        return String.join("\n", lines);
    }

    /** Wraps a message body between the divider lines for console output. */
    private String box(String body) {
        return DIVIDER + "\n" + body + "\n" + DIVIDER;
    }

    /** Closes the underlying scanner (and with it {@code System.in}). */
    @Override
    public void close() {
        scanner.close();
    }
}
