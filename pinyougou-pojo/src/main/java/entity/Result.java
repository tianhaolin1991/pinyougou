package entity;

import java.io.Serializable;

/**
 * 返回添加删除修改结果
 */
public class Result implements Serializable {
    private Boolean success;
    private String message;

    public Result(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
