package com.train.common.enums;

import lombok.Getter;

/**
 * @author Mr.Liu
 * @email yxml2580@163.com
 * @createDate 2023/4/23 14:41
 */
@Getter
public enum ResultCodeEnum {

    SUCCESS(200, "操作成功"),
    ERROR(500, "操作失败"),
    PARAM_ERROR(400, "参数错误"),
    NOT_FOUND(404, "资源不存在"),
    UNAUTHORIZED(401, "未认证"),
    FORBIDDEN(403, "无权限"),
    LOGIC(90001, "业务异常");

    private int code;

    private String msg;

    ResultCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
