package com.example.db_document.handler;

import com.example.db_document.exception.BusinessException;
import com.example.db_document.pojo.JsonResult;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleValidationException_returnsFirstFieldErrorMessage() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldError()).thenReturn(new FieldError("obj", "field", "bad field"));

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        JsonResult<Void> resp = handler.handleValidationException(ex);
        assertEquals(500, resp.getCode());
        assertEquals("bad field", resp.getMsg());
    }

    @Test
    void handleBusinessException_returnsMessage() {
        JsonResult<String> resp = handler.handleBusinessException(new BusinessException("biz"));
        assertEquals(500, resp.getCode());
        assertEquals("biz", resp.getMsg());
    }

    @Test
    void handleIllegalArgument_returnsMessage() {
        JsonResult<String> resp = handler.handleIllegalArgument(new IllegalArgumentException("arg"));
        assertEquals(500, resp.getCode());
        assertEquals("arg", resp.getMsg());
    }

    @Test
    void handleException_returnsGenericMessage() {
        JsonResult<String> resp = handler.handleException(new RuntimeException("x"));
        assertEquals(500, resp.getCode());
        assertEquals("系统内部错误，请联系管理员", resp.getMsg());
    }
}

