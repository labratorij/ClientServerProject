package ViewChat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class CHatPane extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Parent panel = null;
        try {
            panel = FXMLLoader.load(getClass().getResource("Viewjust.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(panel, 400,400);

        primaryStage.setTitle("FXML Window");
        primaryStage.setScene(scene);
        primaryStage.show();

    }
}
