/**
 * The kind of {@link Task} a user can add: a plain {@code Todo}, a
 * {@code Deadline} due by some point, or an {@code Event} spanning a time
 * range. Each constant carries the single-letter tag used when rendering a
 * task (e.g. "[T]"), so the tag lives in one place instead of being
 * hardcoded in every subclass's {@code toString()}.
 */
public enum TaskType {
    TODO("T"),
    DEADLINE("D"),
    EVENT("E");

    private final String tag;

    TaskType(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }
}
