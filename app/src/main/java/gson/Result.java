package gson;

import java.util.Map;

public class Result {
    private Map<String, ?> data;
    private String msg;
    private int status;

    public Map<String, ?> getData() {
        return data;
    }

    public String getMsg() {
        return msg;
    }

    public int getStatus() {
        return status;
    }
}
