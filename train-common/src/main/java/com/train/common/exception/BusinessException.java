package com.train.common.exception;

import com.train.common.enums.BusinessExceptionEnum;

/***
 * @author Mr.Liu
 * @date 2023/4/21 21:45
 * 最好继承 RuntimeException
 * 如果继承 Exception 那么就必须在方法 try catch 或者 在方法上抛出异常
 */

public class BusinessException extends RuntimeException {

    private BusinessExceptionEnum error;

    public BusinessException(BusinessExceptionEnum error) {
        this.error = error;
    }

    public BusinessExceptionEnum getError() {
        return error;
    }

    public void setError(BusinessExceptionEnum error) {
        this.error = error;
    }

    /**
     * 不写入堆栈信息，提高性能
     * 不重写 该方法 会把堆栈信息写入到日志中
     */
    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

}
