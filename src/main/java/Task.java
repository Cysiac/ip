/**
 * A single task in Meowmeow's list, with a description, a done status, and
 * a type tag ("T" for todo, etc.) shown in its rendered form. Deadline
 * tasks also carry a "by" string, and event tasks a "from"/"to" pair,
 * shown in parentheses after the description; these are kept as plain
 * text for now rather than a real date/time type.
 */
public class Task {
    protected String description;
    protected boolean isDone;
    protected String type;
    protected String by;
    protected String from;
    protected String to;

    public Task(String description, String type) {
        this.description = description;
        this.type = type;
        this.isDone = false;
    }

    public Task(String description, String type, String by) {
        this(description, type);
        this.by = by;
    }

    public Task(String description, String type, String from, String to) {
        this(description, type);
        this.from = from;
        this.to = to;
    }

    public String getDescription() {
        return description;
    }

    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    public boolean isDone() {
        return isDone;
    }

    public void markAsDone() {
        isDone = true;
    }

    public void markAsNotDone() {
        isDone = false;
    }

    @Override
    public String toString() {
        String base = "[" + type + "][" + getStatusIcon() + "] " + description;
        if (type.equals("D")) {
            base += " (by: " + by + ")";
        } else if (type.equals("E")) {
            base += " (from: " + from + " to: " + to + ")";
        }
        return base;
    }
}
