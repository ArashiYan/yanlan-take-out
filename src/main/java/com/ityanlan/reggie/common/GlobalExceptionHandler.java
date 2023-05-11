package com.ityanlan.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {


    /**
     * 异常处理方法
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandle(SQLIntegrityConstraintViolationException exception){
        log.info(exception.getMessage());
        if (exception.getMessage().contains("Duplicate entry")){
            String[] s = exception.getMessage().split(" ");
            return R.error(s[2]+"已存在");
        }

        return R.error("位置错误");
    }

    /**
     * 处理自定义异常
     * @param exception
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandle(CustomException exception){
        log.info(exception.getMessage());
        return R.error(exception.getMessage());
    }
}
