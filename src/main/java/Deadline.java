/**
 * A task due by a given point, added via the "deadline" command. The "by"
 * text is kept as a plain string for now rather than a real date/time
 * type. Renders with a "[D]" tag (via {@link TaskType#DEADLINE}) and the
 * due text in parentheses.
 */
public class Deadline extends Task {

    protected String by;

    public Deadline(String description, String by) {
        super(description, TaskType.DEADLINE);
        this.by = by;
    }

    @Override
    public String toString() {
        return super.toString() + " (by: " + by + ")";
    }
}
