/**
 * A single task in Meowmeow's list, with a description, a {@link TaskType},
 * and a {@link TaskStatus}. Subclasses ({@link Todo}, {@link Deadline},
 * {@link Event}) pass their own {@code TaskType} to the constructor and add
 * their own detail fields, overriding {@link #toString()} only to append
 * those details - {@code Task} itself is never stored directly, only via
 * its subclasses.
 */
public class Task {
    protected String description;
    protected final TaskType type;
    protected TaskStatus status;

    public Task(String description, TaskType type) {
        this.description = description;
        this.type = type;
        this.status = TaskStatus.NOT_DONE;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "[" + type.getTag() + "][" + status.getIcon() + "] " + description;
    }

    /**
     * Renders the task as one line for the saved data file, e.g.
     * {@code "T | 1 | read book"}. Subclasses with extra fields
     * ({@link Deadline}, {@link Event}) override this to append their own
     * pipe-separated parts. Kept separate from {@link #toString()} so the
     * on-screen format and the on-disk format can change independently.
     */
    public String toFileString() {
        return type.getTag() + " | " + status.getFileFlag() + " | " + description;
    }
}
