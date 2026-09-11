package meowmeow.gui;

import java.io.IOException;
import java.util.Random;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import meowmeow.Meowmeow;
import meowmeow.ui.MessageStyle;
import meowmeow.ui.Ui;

/**
 * The JavaFX entry point. Loads the main window from FXML, hands it a
 * {@link Meowmeow} instance to talk to, and shows the stage.
 *
 * <p>Unlike the console entry point ({@link meowmeow.Meowmeow#main}), the GUI
 * seeds its {@link Ui} with a real random source rather than a fixed one:
 * only the console's output is asserted byte-for-byte by the console UI
 * test plan.
 */
public class Main extends Application {

    private final Meowmeow meowmeow =
            new Meowmeow(new Ui(MessageStyle.RICH, new Random()), "data", "meowmeow.txt");

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane root = fxmlLoader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Meowmeow");
            stage.setMinHeight(220);
            stage.setMinWidth(417);
            fxmlLoader.<MainWindow>getController().setMeowmeow(meowmeow);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
