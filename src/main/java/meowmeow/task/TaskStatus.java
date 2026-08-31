package meowmeow.task;

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
    /** The task is finished. */
    DONE("X", "1", " Nice! I've marked this task as done, meow:"),
    /** The task is not finished yet. */
    NOT_DONE(" ", "0", " OK, I've marked this task as not done yet, meow:");

    private final String icon;
    private final String fileFlag;
    private final String confirmationMessage;

    TaskStatus(String icon, String fileFlag, String confirmationMessage) {
        this.icon = icon;
        this.fileFlag = fileFlag;
        this.confirmationMessage = confirmationMessage;
    }

    /** Returns the icon shown in a task listing, e.g. {@code "X"} for done. */
    public String getIcon() {
        return icon;
    }

    /**
     * Returns the status as it appears in the saved data file: "1" for done,
     * "0" for not done. Kept here (next to the icon) so the file encoding
     * lives in one place rather than being hardcoded in {@link Task}.
     */
    public String getFileFlag() {
        return fileFlag;
    }

    /** Returns the message Meowmeow prints when a task is switched to this status. */
    public String getConfirmationMessage() {
        return confirmationMessage;
    }
}
