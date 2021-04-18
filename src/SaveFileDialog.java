import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.*;

public class SaveFileDialog {

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

    //Метод записывающий открытые процессы файловым менеджером в файл
    private void saveFile() {
        Controller controller = new Controller();
        String path = controller.getPath() + "/Debug";
        try {
            if (!new File(path).exists())
                path = controller.getPath();

            String outPathFile = path + "/" + name.getText();
                try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outPathFile)))) {
                    for (String process : controller.getOpenProcess())
                        out.println(process);
                }
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Файл не сохранён!").showAndWait();
        }
    }
}
