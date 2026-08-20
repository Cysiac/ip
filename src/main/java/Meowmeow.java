import java.util.Scanner;

public class Meowmeow {
    private static final String NAME = "Meowmeow";
    private static final String DIVIDER = "____________________________________________________________";

    public static void main(String[] args) {
        System.out.println(DIVIDER);
        System.out.println("(=^-ω-^=)  " + NAME);
        System.out.println("Hello! I'm " + NAME + ".");
        System.out.println("What can I do for you?");
        System.out.println(DIVIDER);

        // try-with-resources guarantees the scanner (and System.in) is closed
        // even if something inside the loop throws.
        try (Scanner scanner = new Scanner(System.in)) {
            // hasNextLine() lets the loop end gracefully on EOF (e.g. piped
            // input with no "bye" line) instead of nextLine() throwing.
            while (scanner.hasNextLine()) {
                String input = scanner.nextLine().trim();
                if (input.equalsIgnoreCase("bye")) {
                    System.out.println(DIVIDER);
                    System.out.println(" /\\_/\\");
                    System.out.println("( ^.^ )  Meow! Bye bye~");
                    System.out.println(" > ^ <");
                    System.out.println(DIVIDER);
                    break;
                }
                System.out.println(DIVIDER);
                System.out.println(" " + input + " Meow!");
                System.out.println(DIVIDER);
            }
        }
    }
}
