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
                            listTasksOn(arguments, tasks, ui);
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
                        setTaskStatus(arguments, TaskStatus.DONE, tasks, ui);
                        break;
                    }
                    case UNMARK: {
                        // Same guard as "mark" above, for a bare "unmark".
                        if (arguments.isEmpty()) {
                            throw new MeowmeowException(" Meow? Tell me which task number to unmark.");
                        }
                        setTaskStatus(arguments, TaskStatus.NOT_DONE, tasks, ui);
                        break;
                    }
                    case DELETE: {
                        // Same guard again, for a bare "delete".
                        if (arguments.isEmpty()) {
                            throw new MeowmeowException(" Meow? Tell me which task number to delete.");
                        }
                        deleteTask(arguments, tasks, ui);
                        break;
                    }
                    case TODO: {
                        // "todo <description>" adds a plain, undated task.
                        if (arguments.isEmpty()) {
                            throw new MeowmeowException(" Meow? Tell me what to add, e.g. \"todo borrow book\".");
                        }
                        addTask(new Todo(arguments), tasks, ui);
                        break;
                    }
                    case DEADLINE: {
                        // "deadline <description> /by <when>" adds a task due
                        // by a given point.
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
                        addTask(new Deadline(deadlineDescription, TaskDateTime.parse(by)), tasks, ui);
                        break;
                    }
                    case EVENT: {
                        // "event <description> /from <start> /to <end>" adds a
                        // task spanning a time range.
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
                        addTask(new Event(eventDescription, start, end), tasks, ui);
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
     * "mark" and "unmark". Throws MeowmeowException on invalid input (not a
     * number, or out of range) instead of crashing.
     */
    private static void setTaskStatus(String indexText, TaskStatus status, TaskList tasks, Ui ui)
            throws MeowmeowException {
        int index;
        try {
            index = Integer.parseInt(indexText);
        } catch (NumberFormatException e) {
            throw new MeowmeowException(" That's not a task number I recognise, meow?");
        }
        // tasks.get rejects an out-of-range position with the "doesn't
        // exist" message.
        Task task = tasks.get(index);
        task.setStatus(status);
        STORAGE.save(tasks.asList());
        ui.showStatusChange(status, task);
    }

    /**
     * Removes the task at the given 1-based position (as shown by "list")
     * and shows a confirmation. Throws MeowmeowException on invalid input
     * (not a number, or out of range) instead of crashing.
     */
    private static void deleteTask(String indexText, TaskList tasks, Ui ui) throws MeowmeowException {
        int index;
        try {
            index = Integer.parseInt(indexText);
        } catch (NumberFormatException e) {
            throw new MeowmeowException(" That's not a task number I recognise, meow?");
        }
        // tasks.delete rejects an out-of-range position with the "doesn't
        // exist" message.
        Task removed = tasks.delete(index);
        STORAGE.save(tasks.asList());
        ui.showRemoved(removed, tasks.size());
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
     * Filters the list to the tasks occurring on the given date - deadlines
     * due that day, and events whose span covers it (see
     * {@link Task#occursOn(java.time.LocalDate)}) - and hands them to the UI
     * to display. The date is accepted in any of {@link TaskDateTime}'s input
     * formats; a time, if given, is ignored since the match is by day.
     */
    private static void listTasksOn(String dateText, TaskList tasks, Ui ui) throws MeowmeowException {
        TaskDateTime when = TaskDateTime.parse(dateText);
        ui.showTasksOn(when.toDateString(), tasks.findOn(when.getDate()));
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
