package meowmeow.task;

import java.time.LocalDate;

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

    /**
     * Constructs a task with the given description and type, initially
     * not done.
     *
     * @param description the task text as the user typed it.
     * @param type        the kind of task (todo, deadline or event).
     */
    public Task(String description, TaskType type) {
        this.description = description;
        this.type = type;
        this.status = TaskStatus.NOT_DONE;
    }

    /** Returns the task's description text. */
    public String getDescription() {
        return description;
    }

    /** Returns whether the task is currently done or not done. */
    public TaskStatus getStatus() {
        return status;
    }

    /**
     * Sets the task's done/not-done status.
     *
     * @param status the new status.
     */
    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    /**
     * Whether this task happens on the given calendar date, used by the
     * "list &lt;date&gt;" filter. A plain {@link Todo} has no date, so the
     * base answer is {@code false}; {@link Deadline} and {@link Event}
     * override this with their own date logic.
     */
    public boolean occursOn(LocalDate date) {
        return false;
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
