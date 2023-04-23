package com.train.common.response;

import com.train.common.enums.ResultCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class DBResult<T> {

    /**
     * 业务上的成功或失败
     */
    private int code = 200;

    /**
     * 返回信息
     */
    private String msg;

    /**
     * 返回泛型数据，自定义类型
     */
    private T result;

    public DBResult(int code, String message) {
        this.code = code;
        this.msg = message;
        this.result = null;
    }

    private DBResult(ResultCodeEnum resultCodeEnum, T result) {
        if (resultCodeEnum != null){
            this.code = resultCodeEnum.getCode();
            this.msg = resultCodeEnum.getMsg();
        }
        this.result = result;
    }

    public static <T> DBResult<T> success(T result) {
        return new DBResult<>(ResultCodeEnum.SUCCESS, result);
    }

    public static <T> DBResult<T> success() {
        return DBResult.success(null);
    }

    public static <T> DBResult<T> success(ResultCodeEnum resultCodeEnum) {
        return new DBResult<>(resultCodeEnum, null);
    }

    public static <T> DBResult<T> fail(String msg) {
        return new DBResult<>(ResultCodeEnum.ERROR.getCode(), msg);
    }

    public static <T> DBResult<T> fail() {
        return DBResult.fail("");
    }

    public static <T> DBResult<T> fail(T result) {
        return new DBResult<>(ResultCodeEnum.LOGIC, result);
    }

    public static <T> DBResult<T> fail(ResultCodeEnum resultCodeEnum, T result) {
        return new DBResult<>(resultCodeEnum, result);
    }

    public static <T> DBResult<T> fail(ResultCodeEnum resultCodeEnum) {
        return new DBResult<>(resultCodeEnum, null);
    }

    public static <T> DBResult<T> error(ResultCodeEnum resultCodeEnum) {
        return new DBResult<>(resultCodeEnum, null);
    }

    public static <T> DBResult<T> error(ResultCodeEnum resultCodeEnum, T result) {
        return new DBResult<>(resultCodeEnum, result);
    }
}
