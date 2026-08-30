import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads and writes Meowmeow's task list to a plain-text file on disk, so
 * tasks survive between runs. Each task is stored as one pipe-separated
 * line - see {@link Task#toFileString()} for the exact format.
 *
 * <p>The path is always <em>relative</em> to the working directory (never
 * something machine-specific like {@code C:\data}) and is built from
 * separate name segments via {@link Path#of(String, String...)}, so the
 * same code locates the file correctly on Windows, macOS and Linux.
 *
 * <p>First-run friendly: {@link #load()} treats a missing file or missing
 * folder as "no tasks yet" rather than an error, and {@link #save()}
 * creates the folder before writing.
 */
public class Storage {
    private final Path file;

    /**
     * @param first the first segment of the relative path, e.g. {@code "data"}.
     * @param more  any further segments, e.g. {@code "meowmeow.txt"}. Passing
     *              the segments separately (instead of one {@code "data/meowmeow.txt"}
     *              string) keeps the path separator out of our code so it
     *              stays correct on every OS.
     */
    public Storage(String first, String... more) {
        this.file = Path.of(first, more);
    }

    /**
     * Reads the saved task list back from disk. Returns an empty list if
     * the file doesn't exist yet (a normal first run, not an error). A
     * line that can't be understood is skipped with a warning rather than
     * aborting the load, so one corrupt line doesn't lose every task.
     */
    public ArrayList<Task> load() {
        ArrayList<Task> tasks = new ArrayList<>();
        // Covers both "someone just cloned the repo" (no ./data folder at
        // all) and "folder exists but no save yet": Files.exists is false
        // in both cases, so a first run simply starts with no tasks.
        if (!Files.exists(file)) {
            return tasks;
        }
        try {
            List<String> lines = Files.readAllLines(file);
            for (String line : lines) {
                if (line.isBlank()) {
                    continue;
                }
                Task task = parseTask(line);
                if (task == null) {
                    System.out.println("Meow... I skipped a line I couldn't read: " + line);
                } else {
                    tasks.add(task);
                }
            }
        } catch (IOException e) {
            System.out.println("Meow... I couldn't read your saved tasks: " + e.getMessage());
        }
        return tasks;
    }

    /**
     * Rebuilds one {@link Task} from a saved line such as
     * {@code "D | 0 | return book | 2019-12-02 1800"}. Returns {@code null}
     * for any line that isn't in the expected format - unknown type tag, a
     * done-flag that isn't {@code 0} or {@code 1}, fewer fields than the type
     * needs, or a date part that no longer parses - so a corrupted file loses
     * only the bad lines, not all of them.
     */
    private Task parseTask(String line) {
        // -1 limit keeps trailing empty fields, so a task whose last part
        // is blank still splits into the expected number of pieces.
        String[] parts = line.split(" \\| ", -1);
        if (parts.length < 3) {
            return null;
        }
        String typeTag = parts[0].trim();
        String doneFlag = parts[1].trim();
        String description = parts[2];

        // Reject a garbled flag instead of silently treating it as "not
        // done" - a flag we don't recognise means the line is corrupted.
        if (!doneFlag.equals(TaskStatus.DONE.getFileFlag())
                && !doneFlag.equals(TaskStatus.NOT_DONE.getFileFlag())) {
            return null;
        }
        boolean isDone = doneFlag.equals(TaskStatus.DONE.getFileFlag());

        Task task;
        try {
            switch (typeTag) {
            case "T":
                task = new Todo(description);
                break;
            case "D":
                if (parts.length < 4) {
                    return null;
                }
                task = new Deadline(description, TaskDateTime.parse(parts[3].trim()));
                break;
            case "E":
                if (parts.length < 5) {
                    return null;
                }
                task = new Event(description,
                        TaskDateTime.parse(parts[3].trim()),
                        TaskDateTime.parse(parts[4].trim()));
                break;
            default:
                return null;
            }
        } catch (MeowmeowException unreadableDate) {
            // A saved date we can no longer parse (e.g. a file written by an
            // older Meowmeow that stored free text). Treat the line as
            // corrupt - the same "skip it with a warning" contract as any
            // other malformed line.
            return null;
        }
        if (isDone) {
            task.setStatus(TaskStatus.DONE);
        }
        return task;
    }

    /**
     * Writes the whole task list to disk, overwriting any previous
     * contents. The parent folder (e.g. {@code ./data}) is created if it
     * doesn't exist yet. A failure here is reported as a warning rather
     * than crashing the program, since losing a save is not fatal to the
     * current session.
     */
    public void save(ArrayList<Task> tasks) {
        try {
            // Files.createDirectories is a no-op if the folder already
            // exists, so it's safe to call on every save.
            Path parent = file.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            // try-with-resources closes (and flushes) the writer even if
            // writing a line throws partway through.
            try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(file))) {
                for (Task task : tasks) {
                    writer.println(task.toFileString());
                }
            }
        } catch (IOException e) {
            System.out.println("Meow... I couldn't save your tasks: " + e.getMessage());
        }
    }
}
