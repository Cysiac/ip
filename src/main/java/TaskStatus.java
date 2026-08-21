/**
 * Whether a {@link Task} is done or not done. Replaces a plain
 * {@code boolean} so call sites read as {@code TaskStatus.DONE} instead of
 * an unexplained {@code true}/{@code false}. Each constant also carries the
 * status icon shown in a task's listing (e.g. "[X]") and the confirmation
 * message Meowmeow prints when a task is switched to that status, so both
 * "mark" and "unmark" can share one code path instead of branching on a
 * boolean flag.
 */
public enum TaskStatus {
    DONE("X", " Nice! I've marked this task as done, meow:"),
    NOT_DONE(" ", " OK, I've marked this task as not done yet, meow:");

    private final String icon;
    private final String confirmationMessage;

    TaskStatus(String icon, String confirmationMessage) {
        this.icon = icon;
        this.confirmationMessage = confirmationMessage;
    }

    public String getIcon() {
        return icon;
    }

    public String getConfirmationMessage() {
        return confirmationMessage;
    }
}
