/**
 * Adds a task to the list - the shared outcome of the "todo", "deadline"
 * and "event" commands. {@link Parser} has already built the right
 * {@link Task} subclass, so the three commands differ only in how they are
 * parsed, not in what "add" does.
 */
public class AddCommand extends Command {
    private final Task task;

    public AddCommand(Task task) {
        this.task = task;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        tasks.add(task);
        storage.save(tasks.asList());
        ui.showAdded(task, tasks.size());
    }
}
