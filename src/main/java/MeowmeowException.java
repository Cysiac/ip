/**
 * Signals an error specific to Meowmeow (e.g. an unrecognised command, or a
 * malformed argument) that should be reported to the user as a friendly
 * explanation rather than crashing the program. A multi-line explanation is
 * passed as a single message with lines joined by "\n"; the catch site
 * splits on "\n" when boxing the message for display.
 */
public class MeowmeowException extends Exception {

    public MeowmeowException(String message) {
        super(message);
    }
}
