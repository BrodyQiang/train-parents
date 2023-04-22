package com.train.common.response;

public class DBResult<T> {

    /**
     * 业务上的成功或失败
     */
    private boolean success = true;

    /**
     * 返回信息
     */
    private String message;

    /**
     * 返回泛型数据，自定义类型
     */
    private T result;

    public DBResult() {
    }

    public DBResult(T result) {
        this.result = result;
    }

    public static <T> DBResult success(T result) {
        DBResult res = new DBResult();
        res.setSuccess(true);
        res.setMessage("操作成功");
        res.setResult(result);
        return res;
    }

    public static <T> DBResult fail(String message) {
        DBResult res = new DBResult();
        res.setSuccess(false);
        res.setMessage(message);
        res.setResult(null);
        return res;
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

    public T getResult() {
        return result;
    }

    public void setResult(T content) {
        this.result = content;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("DBResult{");
        sb.append("success=").append(success);
        sb.append(", message='").append(message).append('\'');
        sb.append(", result=").append(result);
        sb.append('}');
        return sb.toString();
    }
}
