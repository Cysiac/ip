package meowmeow.task;

import java.time.LocalDate;

/**
 * A task spanning a start and end point, added via the "event" command.
 * "from" and "to" are real {@link TaskDateTime} values, so Meowmeow parses
 * them on input, re-formats them for display, and can tell whether a given
 * date falls within the event's span (used by "list &lt;date&gt;"). Renders
 * with an "[E]" tag (via {@link TaskType#EVENT}) and both endpoints in
 * parentheses.
 */
public class Event extends Task {

    protected TaskDateTime from;
    protected TaskDateTime to;

    /**
     * Constructs an event with the given description and start/end points.
     *
     * @param description the task text as the user typed it.
     * @param from        the date (and optional time) the event starts.
     * @param to          the date (and optional time) the event ends.
     */
    public Event(String description, TaskDateTime from, TaskDateTime to) {
        super(description, TaskType.EVENT);
        this.from = from;
        this.to = to;
    }

    /** True when {@code date} is on the start day, the end day, or in between. */
    @Override
    public boolean occursOn(LocalDate date) {
        return !date.isBefore(from.getDate()) && !date.isAfter(to.getDate());
    }

    @Override
    public String toString() {
        return super.toString() + " (from: " + from + " to: " + to + ")";
    }

    @Override
    public String toFileString() {
        return super.toFileString() + " | " + from.toFileString() + " | " + to.toFileString();
    }
}
