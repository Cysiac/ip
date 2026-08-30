/**
 * Switches a task to done or not-done - the "mark" and "unmark" commands.
 * They share one class because {@link TaskStatus} already carries the
 * difference between them (the icon and the confirmation message), so only
 * the target status differs.
 */
public class MarkCommand extends Command {
    private final int taskNumber;
    private final TaskStatus status;

    /**
     * @param taskNumber the 1-based position (as shown by "list") to change.
     * @param status     the status to switch that task to.
     */
    public MarkCommand(int taskNumber, TaskStatus status) {
        this.taskNumber = taskNumber;
        this.status = status;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws MeowmeowException {
        // tasks.get rejects an out-of-range position with the "doesn't
        // exist" message.
        Task task = tasks.get(taskNumber);
        task.setStatus(status);
        storage.save(tasks.asList());
        ui.showStatusChange(status, task);
    }
}
