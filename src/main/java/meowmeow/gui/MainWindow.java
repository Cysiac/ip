package meowmeow.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import meowmeow.Meowmeow;

/**
 * Controller for the main window. Wires the input field and Send button to
 * {@link Meowmeow#getResponse(String)} and appends a pair of
 * {@link DialogBox}es (the user's line, then Meowmeow's reply) to the
 * scrolling conversation for each command entered.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Meowmeow meowmeow;

    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private final Image meowmeowImage = new Image(this.getClass().getResourceAsStream("/images/DaMeowmeow.png"));

    /** Keeps the scroll pane pinned to the newest message as the conversation grows. */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the {@link Meowmeow} instance this window talks to.
     *
     * @param meowmeow the backend to route user input through.
     */
    public void setMeowmeow(Meowmeow meowmeow) {
        this.meowmeow = meowmeow;
    }

    /**
     * Handles one line of user input: shows it, shows Meowmeow's reply, and
     * clears the input field. Bound to both the Send button and the Enter
     * key in the FXML.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }
        String response = meowmeow.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getMeowmeowDialog(response, meowmeowImage)
        );
        userInput.clear();
    }
}
