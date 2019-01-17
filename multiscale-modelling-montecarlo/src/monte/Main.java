package monte;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Monte Carlo");
        primaryStage.setScene(new Scene(root, 1000, 600));
        String css =this.getClass().getResource("style.css").toExternalForm();
        root.getStylesheets().add(css);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
