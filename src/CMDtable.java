public class CMDtable {
    private String nameCMD;
    private String PID;

    CMDtable(String PID, String nameCMD){
        this.PID = PID;
        this.nameCMD = nameCMD;
    }

    public String getNameCMD() {
        return nameCMD;
    }

    public void setNameCMD(String nameCMD) {
        this.nameCMD = nameCMD;
    }

    public String getPID() {
        return PID;
    }

    public void setPID(String PID) {
        this.PID = PID;
    }
}
