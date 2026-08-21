/**
 * A plain task with no associated date or time, added via the "todo"
 * command. Renders with a "[T]" tag ahead of the usual status/description.
 */
public class Todo extends Task {

    public Todo(String description) {
        super(description);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
