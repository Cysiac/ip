package meowmeow.command;

import meowmeow.MeowmeowException;
import meowmeow.storage.Storage;
import meowmeow.task.TaskList;
import meowmeow.ui.Ui;

/**
 * One user command, ready to run. {@link meowmeow.parser.Parser#parse(String) Parser.parse} turns a raw
 * input line into a concrete subclass ({@link AddCommand},
 * {@link DeleteCommand}, {@link MarkCommand}, {@link ListCommand},
 * {@link ExitCommand}); the command loop in {@link meowmeow.Meowmeow Meowmeow} then just calls
 * {@link #execute} and checks {@link #isExit}.
 *
 * <p>All the "does this input make sense" checking has already happened in
 * {@code Parser} by the time a {@code Command} exists, so {@code execute}
 * only has to deal with what can still go wrong at run time - e.g. a task
 * number that names no task.
 */
public abstract class Command {

    /**
     * Carries out this command against the app's state.
     *
     * @param tasks   the task list to read or change.
     * @param ui      where the result is shown to the user.
     * @param storage used to save the list after a change.
     * @throws MeowmeowException if the command cannot be completed (e.g. no
     *     task has the requested number).
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws MeowmeowException;

    /**
     * Whether the app should stop reading commands after this one. Only
     * {@link ExitCommand} overrides this to {@code true}.
     */
    public boolean isExit() {
        return false;
    }
}
