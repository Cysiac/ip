import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Reads and writes Meowmeow's task list to a plain-text file on disk, so
 * tasks survive between runs. The file path is fixed (relative to wherever
 * the program is started) and each task is stored as one pipe-separated
 * line - see {@link Task#toFileString()} for the exact format.
 *
 * <p>Only saving is implemented for now; loading an existing file on
 * startup can be added later.
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
