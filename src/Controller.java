import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

import static com.sun.deploy.uitoolkit.ToolkitStore.dispose;

public class Controller{
    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    public MenuItem close;
    @FXML
    private MenuItem about;
    @FXML
    void initialize() {
        //Обработчик событий по нажатию на пунтк меню - Выход
        close.setOnAction(event -> System.exit(0));
        //Обработчик событий по нажатию на пунтк меню - О программе
        about.setOnAction(event -> {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("about.fxml"));
            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Parent root = loader.getRoot();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.showAndWait();
        });
    }
}