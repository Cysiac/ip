package meowmeow.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import meowmeow.Meowmeow;

/**
 * The JavaFX entry point. Loads the main window from FXML, hands it a
 * {@link Meowmeow} instance to talk to, and shows the stage.
 */
public class Main extends Application {

    private final Meowmeow meowmeow = new Meowmeow("data", "meowmeow.txt");

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
