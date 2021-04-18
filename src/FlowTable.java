public class FlowTable {
    private String nameFlow;
    private String pid;
    private String start;
    private String priority;
    FlowTable(String nameFlow, String pid, String start, String priority){
        this.nameFlow = nameFlow;
        this.pid = pid;
        this.start = start;
        this.priority = priority;
    }

    public String getNameFlow() {
        return nameFlow;
    }

    public void setNameFlow(String nameFlow) {
        this.nameFlow = nameFlow;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}
