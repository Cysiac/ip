import java.util.Scanner;

public class Meowmeow {
    private static final String NAME = "Meowmeow";
    private static final String DIVIDER = "____________________________________________________________";
    private static final int MAX_ITEMS = 100;

    public static void main(String[] args) {
        printBoxed("(=^-ω-^=)  " + NAME, "Hello! I'm " + NAME + ".", "What can I do for you?");

        // Fixed-size store for the items the user adds. The spec caps the
        // course project at 100 tasks for this stage, so a plain array
        // (rather than a resizable list) is sufficient.
        String[] items = new String[MAX_ITEMS];
        int itemCount = 0;

        // try-with-resources guarantees the scanner (and System.in) is closed
        // even if something inside the loop throws.
        try (Scanner scanner = new Scanner(System.in)) {
            // hasNextLine() lets the loop end gracefully on EOF (e.g. piped
            // input with no "bye" line) instead of nextLine() throwing.
            while (scanner.hasNextLine()) {
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    // Blank lines aren't a command or a task worth storing.
                    continue;
                } else if (input.equalsIgnoreCase("bye")) {
                    printBoxed(" /\\_/\\", "( ^.^ )  Meow! Bye bye~", " > ^ <");
                    break;
                } else if (input.equalsIgnoreCase("list")) {
                    String[] lines = new String[itemCount];
                    for (int i = 0; i < itemCount; i++) {
                        lines[i] = " " + (i + 1) + ". " + items[i];
                    }
                    printBoxed(lines);
                } else if (itemCount < items.length) {
                    items[itemCount] = input;
                    itemCount++;
                    printBoxed(" added: " + input + " Meow!");
                } else {
                    printBoxed(" Sorry, I can't remember any more than " + MAX_ITEMS + " things! Meow?");
                }
            }
        }
    }

    /**
     * Prints a block of lines surrounded by the divider, matching Meowmeow's
     * standard reply format. Used for every message so the boxed layout
     * only needs to be written once.
     */
    private static void printBoxed(String... lines) {
        System.out.println(DIVIDER);
        for (String line : lines) {
            System.out.println(line);
        }
        System.out.println(DIVIDER);
    }
}
