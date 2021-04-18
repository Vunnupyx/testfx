import java.text.SimpleDateFormat;
import java.util.Date;

public class FileTable {
    private String name;
    private String size;
    private Date dateChange;

    public FileTable(String name, String size, Date dateChange) {
        this.name = name;
        this.size = size;
        this.dateChange = dateChange;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getSize() {
        if (size.equals("0")) {
            return "";
        } else {
            return size;
        }
    }

    public void setSize(String size) {
        this.size = size;
    }


    public String getDateChange() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

        return dateFormat.format(dateChange);
    }

    public void setDateChange(Date dateChange) {
        this.dateChange = dateChange;
    }
}
