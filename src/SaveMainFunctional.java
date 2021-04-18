import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.*;

public class SaveMainFunctional {

    @FXML
    private Button ok;

    @FXML
    private TextField name;
    @FXML
    void initialize() {
        //Обработчик событий по нажатию на "ОК"  (Сохранить файл)
        ok.setOnAction(event -> {
            saveFile();
            Stage stage = (Stage) ok.getScene().getWindow();
            stage.close();
        });
    }

    //Метод записывающий в файл CPU и pageFaultCount
    private void saveFile() {
        Controller controller = new Controller();
        String pathWrite = controller.getPath() + "/Debug";
        String pathRead = controller.getPath() + "/Debug/InterProcess";
        try {
            if (!new File(pathWrite).exists())
                pathWrite = controller.getPath();

            String outPathFile = pathWrite + "/" + name.getText();
        try (BufferedReader outRead = new BufferedReader(new FileReader(pathRead))) {
            try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outPathFile)))) {
                out.println(outRead.readLine());
                out.println(outRead.readLine());
            }
        }
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Файл не сохранён!").showAndWait();
        }
    }
}

