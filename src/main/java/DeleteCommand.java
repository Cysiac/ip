/**
 * Removes the task at a given 1-based position (the number shown by
 * "list") - the "delete" command.
 */
public class DeleteCommand extends Command {
    private final int taskNumber;

    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws MeowmeowException {
        // tasks.delete rejects an out-of-range position with the "doesn't
        // exist" message.
        Task removed = tasks.delete(taskNumber);
        storage.save(tasks.asList());
        ui.showRemoved(removed, tasks.size());
    }
}
