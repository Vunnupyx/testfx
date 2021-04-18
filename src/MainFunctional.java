import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;

public class MainFunctional {

    @FXML
    private Label CPU;

    @FXML
    private TableView<CMDtable> tableCMD;

    @FXML
    private TableView<FlowTable> flowTable;

    @FXML
    private TableColumn<CMDtable, String> processCMD;

    @FXML
    private TableColumn<FlowTable, String> flowCMD;

    @FXML
    private TableColumn<FlowTable, String> flowPid;

    @FXML
    private TableColumn<FlowTable, String> flowStart;

    @FXML
    private TableColumn<FlowTable, String> flowPriority;

    @FXML
    private Label pageFaultCount;

    private Controller controller = new Controller();

    @FXML
    private Button save;

    @FXML
    void initialize() throws IOException {
        //Закрипление полей CMDTable в колонки
        processCMD.setCellValueFactory(new PropertyValueFactory<>("nameCMD"));

        //Закрипление полей FlowTable в колонки
        flowCMD.setCellValueFactory(new PropertyValueFactory<>("nameFlow"));
        flowPid.setCellValueFactory(new PropertyValueFactory<>("pid"));
        flowStart.setCellValueFactory(new PropertyValueFactory<>("start"));
        flowPriority.setCellValueFactory(new PropertyValueFactory<>("priority"));

        //Заполнить при старте таблицу процессов
        tableCMD.setItems(getCMD());

        //Обработчик события по двойному клику таблицы процессов
        tableCMD.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && tableCMD.getSelectionModel().getSelectedItem() != null) {
                try {
                    flowTable.setItems(getFlowCMD(tableCMD.getSelectionModel().getSelectedItem().getPID()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        //Таймеры обновления label'ов "CPU" "pageFaultCount"
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.8), evt -> editCPUPageFaultCount()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        //Обработчик событий по нажатию на button - Сохранить (Запуск *.fxml)
        save.setOnAction(event -> {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("saveMainFunctional.fxml"));
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

    //Метод возвращающий таблицу имен процессов
    private ObservableList<CMDtable> getCMD() throws IOException {
        ObservableList<CMDtable> tableModel = FXCollections.observableArrayList();
        Process processSecret = Runtime.getRuntime().exec("ps -eo pid,comm");

        String line;
        BufferedReader processReader = new BufferedReader(new InputStreamReader(processSecret.getInputStream()));
        processReader.readLine();
        while ((line = processReader.readLine()) != null) {
            String[] CPU = line.split("\\s+");
            tableModel.add(new CMDtable(CPU[1], CPU[2]));
        }
        processReader.close();

        return tableModel;

    }

    //Метод возвращающий таблицу имен потоков процесса
    private ObservableList<FlowTable> getFlowCMD(String PID) throws IOException {
        ObservableList<FlowTable> tableModel = FXCollections.observableArrayList();
        Process process = Runtime.getRuntime().exec("ps -o comm -Tp " + PID);
        Process processProperties = Runtime.getRuntime().exec("ps -o pid,start,nice -Tp " + PID);
        try {
            String lineProcess;
            String lineProcessProperties;
            BufferedReader processReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader processReaderProperties = new BufferedReader(new InputStreamReader(processProperties.getInputStream()));
            processReaderProperties.readLine();
            processReader.readLine();
            while ((lineProcess = processReader.readLine()) != null) {
                lineProcessProperties = processReaderProperties.readLine();
                String[] CPU = lineProcessProperties.split("\\s+");
                tableModel.add(new FlowTable(lineProcess, CPU[1], CPU[2], CPU[3]));
            }
            processReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tableModel;
    }

    //Метод изменяющий label CPU и PageFaultCount
    private void editCPUPageFaultCount() {
        String path = controller.getPath() + "/Debug/InterProcess";
        try (BufferedReader out = new BufferedReader(new FileReader(path))) {
            CPU.setText(out.readLine());
            pageFaultCount.setText(out.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
