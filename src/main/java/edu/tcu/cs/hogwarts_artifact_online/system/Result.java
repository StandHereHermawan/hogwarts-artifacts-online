package edu.tcu.cs.hogwarts_artifact_online.system;

public class Result {

    private boolean flag;

    private Integer code;

    private String message;

    private Object data;

    public Result() {
    }

    public Result(boolean flag, Integer code, String message) {
        this.setFlag(flag);
        this.setCode(code);
        this.setMessage(message);
    }

    public Result(boolean flag, Integer code, String message, Object data) {
        this.setFlag(flag);
        this.setCode(code);
        this.setMessage(message);
        this.setData(data);
    }

    public boolean isFlag() {
        return this.flag;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    public Object getData() {
        return this.data;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
