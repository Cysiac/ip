package meowmeow;

import meowmeow.command.Command;
import meowmeow.parser.Parser;
import meowmeow.storage.Storage;
import meowmeow.task.TaskList;
import meowmeow.ui.Ui;

/**
 * Meowmeow, a small command-line task tracker. This class is now just the
 * wiring: it holds the three collaborators - {@link Ui} for talking to the
 * user, {@link Storage} for the save file, {@link TaskList} for the tasks
 * themselves - and {@link #run()} reads commands and routes each one to
 * them. Understanding a command is {@link Parser}'s job; each command is
 * then a {@link Command} object that runs itself.
 *
 * <p>The console app uses {@link #run()}; the JavaFX GUI holds a
 * {@code Meowmeow} instead and calls {@link #getResponse(String)} once per
 * line the user types.
 */
public class Meowmeow {

    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;

    /**
     * Wires up the collaborators and loads any previously saved tasks. The
     * save-file location is given as path segments (e.g. {@code "data"},
     * {@code "meowmeow.txt"}) that {@link Storage} joins with the current
     * OS's separator.
     *
     * <p>Unlike the assignment's sample constructor there is no try/catch
     * around the load: {@link Storage#load()} deals with a missing or
     * partly-corrupt file itself (starting empty, or skipping only the
     * unreadable lines with a warning through {@link Ui}) instead of
     * throwing, so there is no loading failure for this constructor to
     * recover from. Any such warning is therefore printed before the
     * welcome banner, exactly as in the sample.
     *
     * @param first the first segment of the save-file path, e.g. {@code "data"}.
     * @param more  any further path segments, e.g. {@code "meowmeow.txt"}.
     */
    public Meowmeow(String first, String... more) {
        ui = new Ui();
        storage = new Storage(ui, first, more);
        tasks = new TaskList(storage.load());
    }

    /**
     * Greets the user, then reads and runs commands until an
     * {@link meowmeow.command.ExitCommand ExitCommand} ("bye") or the end of input. The {@link Ui} (and
     * with it {@code System.in}) is closed on the way out.
     */
    public void run() {
        ui.print(ui.showWelcome());
        try {
            boolean isExit = false;
            // hasNextCommand() lets the loop also end gracefully on
            // end-of-input, e.g. piped input with no "bye" line.
            while (!isExit && ui.hasNextCommand()) {
                String fullCommand = ui.readCommand();
                if (fullCommand.isEmpty()) {
                    // Blank lines aren't a command worth acting on.
                    continue;
                }
                // Parser and every Command report a problem by throwing a
                // MeowmeowException rather than printing, so this one catch
                // shows the friendly error for all of them.
                try {
                    Command command = Parser.parse(fullCommand);
                    ui.print(command.execute(tasks, ui, storage));
                    isExit = command.isExit();
                } catch (MeowmeowException e) {
                    ui.print(ui.showError(e.getMessage()));
                }
            }
        } finally {
            ui.close();
        }
    }

    /**
     * Runs one line of user input and returns Meowmeow's reply, for the GUI.
     * A {@link MeowmeowException} (an unknown command, a bad task number) is
     * caught and its message returned as the reply, mirroring how
     * {@link #run()} shows errors in the console.
     *
     * @param input one line the user typed into the GUI.
     * @return the text Meowmeow should show in response.
     */
    public String getResponse(String input) {
        try {
            return Parser.parse(input).execute(tasks, ui, storage);
        } catch (MeowmeowException e) {
            return ui.showError(e.getMessage());
        }
    }

    /**
     * Starts Meowmeow, saving to {@code ./data/meowmeow.txt}.
     *
     * @param args command-line arguments (unused).
     */
    public static void main(String[] args) {
        new Meowmeow("data", "meowmeow.txt").run();
    }
}
