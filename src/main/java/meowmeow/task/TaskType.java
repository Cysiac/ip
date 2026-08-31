package meowmeow.task;

/**
 * The kind of {@link Task} a user can add: a plain {@code Todo}, a
 * {@code Deadline} due by some point, or an {@code Event} spanning a time
 * range. Each constant carries the single-letter tag used when rendering a
 * task (e.g. "[T]"), so the tag lives in one place instead of being
 * hardcoded in every subclass's {@code toString()}.
 */
public enum TaskType {
    /** A plain task with no date or time. */
    TODO("T"),
    /** A task due by a single point in time. */
    DEADLINE("D"),
    /** A task spanning a start and end point. */
    EVENT("E");

    private final String tag;

    TaskType(String tag) {
        this.tag = tag;
    }

    /** Returns the single-letter tag used when rendering a task, e.g. {@code "T"}. */
    public String getTag() {
        return tag;
    }
}
