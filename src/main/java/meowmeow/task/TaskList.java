package meowmeow.task;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import meowmeow.MeowmeowException;

/**
 * The list of tasks Meowmeow is tracking, plus the operations that act on
 * it: adding, deleting, looking up by position, and filtering by date.
 *
 * <p>Positions are given as <em>1-based</em> indices - the same numbers a
 * user sees next to each task in "list" - and {@link #get(int)} /
 * {@link #delete(int)} validate them, throwing {@link MeowmeowException}
 * for a number outside the list. Keeping that check here means the command
 * handlers no longer each repeat it.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /** Constructs an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Constructs a task list holding the given tasks (e.g. the ones just
     * loaded from disk). The tasks are copied into a new list, so later
     * changes here do not write back through the caller's collection.
     */
    public TaskList(List<Task> initialTasks) {
        this.tasks = new ArrayList<>(initialTasks);
    }

    /** Returns the number of tasks in the list. */
    public int size() {
        return tasks.size();
    }

    /** Adds a task to the end of the list. */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Returns the task at the given 1-based position.
     *
     * @throws MeowmeowException if no task has that position.
     */
    public Task get(int oneBasedIndex) throws MeowmeowException {
        return tasks.get(toListIndex(oneBasedIndex));
    }

    /**
     * Removes and returns the task at the given 1-based position.
     *
     * @throws MeowmeowException if no task has that position.
     */
    public Task delete(int oneBasedIndex) throws MeowmeowException {
        return tasks.remove(toListIndex(oneBasedIndex));
    }

    /**
     * Returns the tasks occurring on the given date - deadlines due that day
     * and events whose span covers it (see {@link Task#occursOn(LocalDate)}) -
     * in list order.
     */
    public List<Task> findOn(LocalDate date) {
        ArrayList<Task> matches = new ArrayList<>();
        for (Task task : tasks) {
            if (task.occursOn(date)) {
                matches.add(task);
            }
        }
        return matches;
    }

    /**
     * Returns a read-only view of every task, in list order - for display and
     * for saving to disk. It is unmodifiable so callers change the list only
     * through this class's own methods.
     */
    public List<Task> asList() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Converts a user-facing 1-based position to a 0-based list index,
     * rejecting anything outside the list with the message Meowmeow has
     * always shown for a non-existent task.
     */
    private int toListIndex(int oneBasedIndex) throws MeowmeowException {
        if (oneBasedIndex < 1 || oneBasedIndex > tasks.size()) {
            throw new MeowmeowException(" Meow? Task " + oneBasedIndex + " doesn't exist in your list.");
        }
        return oneBasedIndex - 1;
    }
}
