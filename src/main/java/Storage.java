import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads and writes Meowmeow's task list to a plain-text file on disk, so
 * tasks survive between runs. The file path is fixed (relative to wherever
 * the program is started) and each task is stored as one pipe-separated
 * line - see {@link Task#toFileString()} for the exact format.
 */
public class Storage {
    private final Path file;

    /**
     * @param filePath location of the data file, e.g. {@code "./data/meowmeow.txt"}.
     */
    public Storage(String filePath) {
        this.file = Path.of(filePath);
    }

    /**
     * Reads the saved task list back from disk. Returns an empty list if
     * the file doesn't exist yet (a normal first run, not an error). A
     * line that can't be understood is skipped with a warning rather than
     * aborting the load, so one corrupt line doesn't lose every task.
     */
    public ArrayList<Task> load() {
        ArrayList<Task> tasks = new ArrayList<>();
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
     * {@code "D | 0 | return book | June 6th"}. Returns {@code null} if the
     * line doesn't match any known format (unknown type tag, or too few
     * fields) so the caller can skip it.
     */
    private Task parseTask(String line) {
        // -1 limit keeps trailing empty fields, so a task whose last part
        // is blank still splits into the expected number of pieces.
        String[] parts = line.split(" \\| ", -1);
        if (parts.length < 3) {
            return null;
        }
        String typeTag = parts[0].trim();
        boolean isDone = parts[1].trim().equals(TaskStatus.DONE.getFileFlag());
        String description = parts[2];

        Task task;
        switch (typeTag) {
        case "T":
            task = new Todo(description);
            break;
        case "D":
            if (parts.length < 4) {
                return null;
            }
            task = new Deadline(description, parts[3]);
            break;
        case "E":
            if (parts.length < 5) {
                return null;
            }
            task = new Event(description, parts[3], parts[4]);
            break;
        default:
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
