import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/resource/view/login.fxml"));
        Scene scene = new Scene(root);
        String css = this.getClass().getResource("/resource/css/login.css").toExternalForm();
        scene.getStylesheets().add(css);
        primaryStage.setTitle("Đăng nhập");

        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
