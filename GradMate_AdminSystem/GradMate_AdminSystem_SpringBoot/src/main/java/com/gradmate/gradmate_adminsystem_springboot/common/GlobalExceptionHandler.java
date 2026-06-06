package com.gradmate.gradmate_adminsystem_springboot.common;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ApiException.class)
    public Result<Void> handleApi(ApiException e) {
        return Result.fail(e.getMessage());
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public Result<Void> handleDuplicate() {
        return Result.fail("数据重复，请检查唯一字段");
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        return Result.fail(e.getMessage());
    }
}
