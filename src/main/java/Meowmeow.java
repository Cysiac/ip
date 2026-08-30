/**
 * Meowmeow, a small command-line task tracker. This class is now just the
 * wiring: it holds the three collaborators - {@link Ui} for talking to the
 * user, {@link Storage} for the save file, {@link TaskList} for the tasks
 * themselves - and {@link #run()} reads commands and routes each one to
 * them. The work of understanding a command lives in {@link Command} and
 * {@link Parser}.
 */
public class Meowmeow {

    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;

    /**
     * Wires up the collaborators and loads any previously saved tasks. The
     * save-file location is given as path segments (e.g. {@code "data"},
     * {@code "meowmeow.txt"}) that {@link Storage} joins with the current
     * OS's separator.
     *
     * <p>Unlike the assignment's sample constructor there is no try/catch
     * around the load: {@link Storage#load()} deals with a missing or
     * partly-corrupt file itself (starting empty, or skipping only the
     * unreadable lines with a warning through {@link Ui}) instead of
     * throwing, so there is no loading failure for this constructor to
     * recover from. Any such warning is therefore printed before the
     * welcome banner, exactly as in the sample.
     */
    public Meowmeow(String first, String... more) {
        ui = new Ui();
        storage = new Storage(ui, first, more);
        tasks = new TaskList(storage.load());
    }

    /**
     * Greets the user, then reads and handles commands until "bye" or the
     * end of input. The {@link Ui} (and with it {@code System.in}) is
     * closed on the way out.
     */
    public void run() {
        ui.showWelcome();
        try {
            // The "conversation" label lets "bye" (handled in the switch,
            // itself inside the inner try) leave the loop directly.
            conversation:
            while (ui.hasNextCommand()) {
                String input = ui.readCommand();
                if (input.isEmpty()) {
                    // Blank lines aren't a command or a task worth storing.
                    continue;
                }
                // Every command reports a problem by throwing a
                // MeowmeowException rather than printing, so this one catch
                // shows the friendly error for all of them - including the
                // "unknown command" thrown by Command.fromInput.
                try {
                    Command command = Command.fromInput(input);
                    String arguments = command.argumentsOf(input);
                    switch (command) {
                    case BYE: {
                        ui.showFarewell();
                        break conversation;
                    }
                    case LIST: {
                        // "list" alone shows everything; "list <date>" shows
                        // only the deadlines/events happening on that day.
                        if (arguments.isEmpty()) {
                            ui.showTasks(tasks.asList());
                        } else {
                            TaskDateTime when = Parser.parseListDate(arguments);
                            ui.showTasksOn(when.toDateString(), tasks.findOn(when.getDate()));
                        }
                        break;
                    }
                    case MARK: {
                        setTaskStatus(Parser.parseTaskNumber(arguments, command), TaskStatus.DONE);
                        break;
                    }
                    case UNMARK: {
                        setTaskStatus(Parser.parseTaskNumber(arguments, command), TaskStatus.NOT_DONE);
                        break;
                    }
                    case DELETE: {
                        deleteTask(Parser.parseTaskNumber(arguments, command));
                        break;
                    }
                    case TODO: {
                        addTask(Parser.parseTodo(arguments));
                        break;
                    }
                    case DEADLINE: {
                        addTask(Parser.parseDeadline(arguments));
                        break;
                    }
                    case EVENT: {
                        addTask(Parser.parseEvent(arguments));
                        break;
                    }
                    default:
                        // Unreachable: every Command constant has a case
                        // above. Kept so the compiler can warn if a new
                        // constant is ever added without handling it here.
                        throw new IllegalStateException("Unhandled command: " + command);
                    }
                } catch (MeowmeowException e) {
                    ui.showError(e.getMessage());
                }
            }
        } finally {
            ui.close();
        }
    }

    public static void main(String[] args) {
        new Meowmeow("data", "meowmeow.txt").run();
    }

    /**
     * Sets the status of the task at the given 1-based position (as shown
     * by "list") and shows the confirmation for that status, shared by
     * "mark" and "unmark".
     *
     * @throws MeowmeowException if no task has that position.
     */
    private void setTaskStatus(int taskNumber, TaskStatus status) throws MeowmeowException {
        // tasks.get rejects an out-of-range position with the "doesn't
        // exist" message.
        Task task = tasks.get(taskNumber);
        task.setStatus(status);
        storage.save(tasks.asList());
        ui.showStatusChange(status, task);
    }

    /**
     * Removes the task at the given 1-based position (as shown by "list")
     * and shows a confirmation.
     *
     * @throws MeowmeowException if no task has that position.
     */
    private void deleteTask(int taskNumber) throws MeowmeowException {
        // tasks.delete rejects an out-of-range position with the "doesn't
        // exist" message.
        Task removed = tasks.delete(taskNumber);
        storage.save(tasks.asList());
        ui.showRemoved(removed, tasks.size());
    }

    /**
     * Stores a newly created task and shows the standard confirmation,
     * shared by the "todo"/"deadline"/"event" commands so each one doesn't
     * repeat the confirmation message.
     */
    private void addTask(Task task) {
        tasks.add(task);
        storage.save(tasks.asList());
        ui.showAdded(task, tasks.size());
    }
}
