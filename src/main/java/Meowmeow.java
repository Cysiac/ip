public class Meowmeow {

    // Tasks are saved here after every change so they survive between runs.
    // The path segments are passed separately so Storage can join them with
    // the right separator for the current OS; it stays relative to the
    // working directory (the project root, normally) so the app is portable
    // between machines. Storage holds only this path, so one shared instance
    // is fine.
    private static final Storage STORAGE = new Storage("data", "meowmeow.txt");

    public static void main(String[] args) {
        // try-with-resources guarantees the Ui's scanner (and System.in) is
        // closed even if something inside the loop throws.
        try (Ui ui = new Ui()) {
            ui.showWelcome();

            // TaskList wraps a growable ArrayList, so there's no artificial
            // cap on how many tasks can be stored (unlike a fixed-size
            // array). It starts from whatever was saved on the last run
            // (empty on a first run).
            TaskList tasks = new TaskList(STORAGE.load());

            // The "conversation" label lets "bye" (handled inside the switch,
            // itself inside the try below) exit the loop directly.
            conversation:
            while (ui.hasNextCommand()) {
                String input = ui.readCommand();
                if (input.isEmpty()) {
                    // Blank lines aren't a command or a task worth storing.
                    continue;
                }
                // Every command below reports a problem by throwing a
                // MeowmeowException rather than printing directly, so this
                // one catch shows the friendly error for all of them.
                // Command.fromInput itself throws that way for unrecognised
                // input, so unknown commands are handled by the same catch.
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
                        setTaskStatus(Parser.parseTaskNumber(arguments, command), TaskStatus.DONE, tasks, ui);
                        break;
                    }
                    case UNMARK: {
                        setTaskStatus(Parser.parseTaskNumber(arguments, command), TaskStatus.NOT_DONE, tasks, ui);
                        break;
                    }
                    case DELETE: {
                        deleteTask(Parser.parseTaskNumber(arguments, command), tasks, ui);
                        break;
                    }
                    case TODO: {
                        addTask(Parser.parseTodo(arguments), tasks, ui);
                        break;
                    }
                    case DEADLINE: {
                        addTask(Parser.parseDeadline(arguments), tasks, ui);
                        break;
                    }
                    case EVENT: {
                        addTask(Parser.parseEvent(arguments), tasks, ui);
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
        }
    }

    /**
     * Sets the status of the task at the given 1-based position (as shown
     * by "list") and shows the confirmation for that status, shared by
     * "mark" and "unmark".
     *
     * @throws MeowmeowException if no task has that position.
     */
    private static void setTaskStatus(int taskNumber, TaskStatus status, TaskList tasks, Ui ui)
            throws MeowmeowException {
        // tasks.get rejects an out-of-range position with the "doesn't
        // exist" message.
        Task task = tasks.get(taskNumber);
        task.setStatus(status);
        STORAGE.save(tasks.asList());
        ui.showStatusChange(status, task);
    }

    /**
     * Removes the task at the given 1-based position (as shown by "list")
     * and shows a confirmation.
     *
     * @throws MeowmeowException if no task has that position.
     */
    private static void deleteTask(int taskNumber, TaskList tasks, Ui ui) throws MeowmeowException {
        // tasks.delete rejects an out-of-range position with the "doesn't
        // exist" message.
        Task removed = tasks.delete(taskNumber);
        STORAGE.save(tasks.asList());
        ui.showRemoved(removed, tasks.size());
    }

    /**
     * Stores a newly created task and shows the standard confirmation,
     * shared by the "todo"/"deadline"/"event" commands so each one doesn't
     * repeat the confirmation message.
     */
    private static void addTask(Task task, TaskList tasks, Ui ui) {
        tasks.add(task);
        STORAGE.save(tasks.asList());
        ui.showAdded(task, tasks.size());
    }
}
