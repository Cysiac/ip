package meowmeow.task;

import java.time.LocalDate;

/**
 * A task due by a given point, added via the "deadline" command. The "by"
 * value is a real {@link TaskDateTime}, so Meowmeow understands it as an
 * actual date (and optional time) rather than plain text - it is parsed on
 * input, re-formatted for display, and can be matched against a date by
 * "list &lt;date&gt;". Renders with a "[D]" tag (via {@link TaskType#DEADLINE})
 * and the due date in parentheses.
 */
public class Deadline extends Task {

    protected TaskDateTime by;

    public Deadline(String description, TaskDateTime by) {
        super(description, TaskType.DEADLINE);
        this.by = by;
    }

    /** True when this deadline falls on {@code date}. */
    @Override
    public boolean occursOn(LocalDate date) {
        return by.getDate().equals(date);
    }

    @Override
    public String toString() {
        return super.toString() + " (by: " + by + ")";
    }

    @Override
    public String toFileString() {
        return super.toFileString() + " | " + by.toFileString();
    }
}
