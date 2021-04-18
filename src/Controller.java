import java.io.*;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.util.*;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import javafx.util.Duration;


public class Controller {
    @FXML
    public Label directory;
    @FXML
    private TableView<FileTable> tableFiles;
    @FXML
    private TableColumn<FileTable, String> nameColumn;

    @FXML
    private TableColumn<FileTable, String> sizeColumn;

    @FXML
    private TableColumn<FileTable, String> dateChangesColumn;

    @FXML
    public MenuItem close;

    @FXML
    private MenuItem about;

    @FXML
    private MenuItem mainFunctional;

    @FXML
    private MenuItem save;

    @FXML
    private Button back;

    @FXML
    private MenuItem deleteContextMenu;

    public static List<String> openProcess = new ArrayList<>();

    public List<String> getOpenProcess() {
        return openProcess;
    }

    private static double l_idles;
    private static double l_totals;

    @FXML
    void initialize() {

        //label - директория, устанавливается значением ../КорневаяПапка
        directory.setText(getPath());

        //Обработчик событий по нажатию на пунтк меню - Сохранить (Запуск *.fxml)
        save.setOnAction(event -> {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("saveFileDialog.fxml"));
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

        //Обработчик событий по нажатию на пунтк меню - Выход (Завершает процесс)
        close.setOnAction(event -> System.exit(0));

        //Обработчик событий по нажатию на пунтк меню - О программе (Запуск *.fxml)
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
            stage.setResizable(false);
            stage.showAndWait();
        });

        //Обработчик событий по нажатию на пунтк меню - Основной функционал (Запуск *.fxml)
        mainFunctional.setOnAction(event -> {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("mainFunctional.fxml"));
            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Parent root = loader.getRoot();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.showAndWait();
        });

        //Обработчик событий по нажатию на кнопку "<" - назад (Дальше папки ../КорневаяПапка не уходит, ограничение!)
        back.setOnAction(event -> {
            if (!directory.getText().equals(getPath())) {
                File file = new File(directory.getText());
                directory.setText(file.getParent());
                tableFiles.setItems(fillTable());
            }
        });

        //Закрипление полей FileTable в колонки
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        sizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
        dateChangesColumn.setCellValueFactory(new PropertyValueFactory<>("dateChange"));

        //Заполнение таблицы - используется метод "fillTable()"
        tableFiles.setItems(fillTable());

        //Обработчик событий по нажатию на контекстное меню "delete".
        deleteContextMenu.setOnAction(event -> {
            if (tableFiles.getSelectionModel().getSelectedItem() != null) {
                String nameSelectedFile = tableFiles.getSelectionModel().getSelectedItem().getName();
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Deleting...");
                alert.setContentText("Are you sure want to delete the file " + nameSelectedFile + " ?");
                Optional<ButtonType> answer = alert.showAndWait();
                if (answer.isPresent() && answer.get() == ButtonType.OK) {
                    Boolean result = remove(nameSelectedFile);
                    if (result) {
                        showSimpleAlert("File deleted", nameSelectedFile + " was deleted successfully.");
                        tableFiles.setItems(fillTable());
                    } else {
                        showSimpleAlert("Failed", nameSelectedFile + " could not be deleted");
                    }
                } else {
                    showSimpleAlert("Deletion cancelled", "Deletion process cancelled");
                }
            } else new Alert(Alert.AlertType.ERROR, "No file Selected").showAndWait();

        });

        //Обработчик события по двойному клику таблицы - "открыть файл/папку"
        tableFiles.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2 && tableFiles.getSelectionModel().getSelectedItem() != null)
                OPEN();
        });

        //Таймер обновления значений
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.8), evt -> editCPU()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

    }

    //Метод - Определяет процент CPU
    private void editCPU() {
        try {
            BufferedReader processReader = new BufferedReader(new FileReader("/proc/stat"));
            String line = processReader.readLine();
            String[] times = line.split("\\s+");
            String path = getPath() + "/Debug/InterProcess";
            try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(path)))) {
                    out.println(calcCPU(times));
                    out.println(editPageFaultCount());
            }
            processReader.close();
        } catch (IOException e) {

            e.printStackTrace();
        }

    }

    //Метод вычисляющий значение CPU
    private String calcCPU(String[] times) {
        double idle = Double.parseDouble(times[3]) + Double.parseDouble(times[4]);
        double idle_delta = 0.0;
        double total_delta = 0.0;
        double total = 0.0;
        for (int i = 1; i < times.length; i++)
            total += Double.parseDouble(times[i]);
        if (l_idles != 0.0) {
            idle_delta = idle - l_idles;
            total_delta = total - l_totals;
        }
        l_idles = idle;
        l_totals = total;
        return Math.round(100 * ((total_delta - idle_delta) / total_delta)) + "%";
    }

    //Метод - Опредяляет количество страничных ошибок
    private String editPageFaultCount() {
        try {
            Process process = Runtime.getRuntime().exec("grep pgfault /proc/vmstat");
            BufferedReader processReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = processReader.readLine();
            String[] arrayLine = line.split("\\s+");
            return  "Количество страничных ошибок: " + arrayLine[1];
        } catch (IOException e) {
            e.printStackTrace();
            return "Ошибка";
        }
    }

    //Метод удаляющий файл/папку - Используется в обработчике события по нажатию на контекстное меню "delete"
    private Boolean remove(String name) {
        File file = new File(directory.getText() + "/" + name);
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                if (!Files.isSymbolicLink(f.toPath())) {
                    removeDir(f);
                }
            }
        }
        return file.delete();
    }

    //Дополняющий метод remove. Рекурсивный метод удаления всех файлов папки
    private void removeDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                removeDir(f);
            }
        }
        file.delete();
    }

    //Метод вызывающий информационное окно
    private static void showSimpleAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    //Метод возвращающий таблицу имен файлов директории
    private ObservableList<FileTable> fillTable() {
        ObservableList<FileTable> tableModel = FXCollections.observableArrayList();
        File[] files = new File(directory.getText()).listFiles();
        String[] nameFiles = new File(directory.getText()).list();
        if (nameFiles == null || files == null) {
            new Alert(Alert.AlertType.ERROR, "Ошибка доступа").showAndWait();
            return tableModel;
        }
        for (int i = 0; i < nameFiles.length; i++) {
            if (nameFiles[i].indexOf(".") != 0)
                tableModel.add(new FileTable(nameFiles[i], Long.toString(files[i].length()), new Date(files[i].lastModified())));

        }
        return tableModel;
    }

    //Метод открытия папки
    private void openDir(String path) {
        directory.setText(path);
        tableFiles.setItems(fillTable());
    }

    //Метод определяющий какой из методов использовать openDir или openFile
    private void OPEN() {
        try {
            String pathFile = URLDecoder.decode(directory.getText() + "/" + tableFiles.getSelectionModel().getSelectedItem().getName(), "UTF-8");
            File file = new File(pathFile);
            if (file.isDirectory()) {
                openDir(pathFile);

            } else if (file.isFile()) {
                openFile(pathFile);
            } else new Alert(Alert.AlertType.ERROR, "Ошибка доступа").showAndWait();

        } catch (UnsupportedEncodingException e) {
            new Alert(Alert.AlertType.ERROR, "Ошибка кодировки").showAndWait();
        }
    }

    //Метод открытия файла
    private void openFile(String path) {
        try {
            Process process = Runtime.getRuntime().exec("xdg-open " + path);
            Process processed = Runtime.getRuntime().exec("ps T -o pid,start,cmd");

            try (BufferedReader processReader = new BufferedReader(new InputStreamReader(processed.getInputStream()))) {
                processReader.readLine();
                String line;
                while ((line = processReader.readLine()) != null) {
                    if (line.contains(tableFiles.getSelectionModel().getSelectedItem().getName()))
                        openProcess.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Метод возвращащий путь в директорию "../КорневаяПапка"
    public String getPath() {
        try {
            File file = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
            String mainDirectory = file.getParentFile().getParent();
            return URLDecoder.decode(mainDirectory, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "/home";
    }
}


