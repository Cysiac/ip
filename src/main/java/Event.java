/**
 * A task spanning a start and end point, added via the "event" command.
 * "from" and "to" are kept as plain strings for now rather than a real
 * date/time type. Renders with an "[E]" tag (via {@link TaskType#EVENT})
 * and both times in parentheses.
 */
public class Event extends Task {

    protected String from;
    protected String to;

    public Event(String description, String from, String to) {
        super(description, TaskType.EVENT);
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
