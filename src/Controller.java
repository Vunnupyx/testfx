
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import static com.sun.deploy.uitoolkit.ToolkitStore.dispose;

public class Controller {
    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    public Label directory;
    @FXML
    private TableView<FileTable> tableFiles;
    @FXML
    private TableColumn<FileTable, String> nameColumn;

    @FXML
    private TableColumn<FileTable, Long> sizeColumn;

    @FXML
    private TableColumn<FileTable, String> dateChangesColumn;
    @FXML
    public MenuItem close;
    @FXML
    private MenuItem about;
    private ObservableList<FileTable> tableModel = FXCollections.observableArrayList();

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
        directory.setText("/home");
        nameColumn.setCellValueFactory(new PropertyValueFactory<FileTable, String>("name"));
        sizeColumn.setCellValueFactory(new PropertyValueFactory<FileTable, Long>("size"));
        dateChangesColumn.setCellValueFactory(new PropertyValueFactory<FileTable, String>("dateChange"));
        File file = new File(directory.getText());
        File[] f = file.listFiles();
        String[] newFiles = file.list();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        for (int i = 0; i < newFiles.length; i++) {
            tableModel.add(new FileTable(newFiles[i], f[i].length(), sdf.format(new Date(f[i].lastModified()))));
        }
        tableFiles.setItems(tableModel);
    }


}