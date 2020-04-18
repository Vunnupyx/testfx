import java.io.File;
import java.util.Date;

public class FileTable {
    private String name;
    private long size;
    private String dateChange;

    public FileTable(String name, long size, String dateChange) {
        this.name = name;
        this.size = size;
        this.dateChange = dateChange;
    }

    public FileTable(String name, String dateChange) {
        this.name = name;
        this.dateChange = dateChange;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getDateChange() {
        return dateChange;
    }

    public void setDateChange(String dateChange) {
        this.dateChange = dateChange;
    }
}
