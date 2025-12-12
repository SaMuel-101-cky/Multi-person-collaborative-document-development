package com.example.db_document.handler;

import com.example.db_document.exception.BusinessException;
import com.example.db_document.pojo.JsonResult; // 引入你的 Result 类
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    /**
     * 专门捕获 @Valid 校验失败抛出的异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public JsonResult<Void> handleValidationException(MethodArgumentNotValidException e) {
        // 1. 获取校验结果
        BindingResult bindingResult = e.getBindingResult();

        // 2. 提取第一条错误提示 (例如："头像地址不能为空")
        String errorMsg = Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage();

        // 3. 包装成统一返回格式
        return JsonResult.error(errorMsg);
    }


    @ExceptionHandler(BusinessException.class)
    public JsonResult<String> handleBusinessException(BusinessException e) {
        // 这里拦截到的，都是我们自己手动抛出的逻辑错误
        return JsonResult.error(e.getMessage());
    }

    // 2. 拦截【非法参数异常】
    @ExceptionHandler(IllegalArgumentException.class)
    public JsonResult<String> handleIllegalArgument(IllegalArgumentException e) {
        return JsonResult.error(e.getMessage());
    }

    // 3. 拦截【所有其他未知的系统异常】(兜底)
    @ExceptionHandler(Exception.class)
    public JsonResult<String> handleException(Exception e) {
        e.printStackTrace(); // 打印报错给程序员看
        return JsonResult.error("系统内部错误，请联系管理员"); // 给用户看友好的提示
    }
}
