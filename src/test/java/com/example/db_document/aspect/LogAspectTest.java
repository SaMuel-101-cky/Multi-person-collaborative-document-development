package com.example.db_document.aspect;

import com.example.db_document.annotation.Log;
import com.example.db_document.mapper.SysLogMapper;
import com.example.db_document.model.dto.DocumentCreateRequest;
import com.example.db_document.pojo.SysOperationLog;
import com.example.db_document.utils.UserContext;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LogAspectTest {

    private SysLogMapper sysLogMapper;
    private LogAspect logAspect;

    @BeforeEach
    void setUp() {
        sysLogMapper = mock(SysLogMapper.class);
        logAspect = new LogAspect();
        ReflectionTestUtils.setField(logAspect, "sysLogMapper", sysLogMapper);
    }

    @AfterEach
    void tearDown() {
        UserContext.remove();
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void doAfterReturning_insertsOperationLog() throws Exception {
        class Dummy {
            @Log(module = "模块A", action = "动作B")
            void testMethod(DocumentCreateRequest req) {}
        }

        Log annotation = Dummy.class.getDeclaredMethod("testMethod", DocumentCreateRequest.class).getAnnotation(Log.class);
        assertNotNull(annotation);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("127.0.0.1");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        UserContext.setUserId(7L);

        DocumentCreateRequest req = new DocumentCreateRequest();
        req.setName("n");
        req.setFolderId(1L);
        req.setContent("c");

        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getTarget()).thenReturn(new Dummy());
        when(joinPoint.getArgs()).thenReturn(new Object[]{req});

        Signature signature = mock(Signature.class);
        when(signature.getName()).thenReturn("testMethod");
        when(joinPoint.getSignature()).thenReturn(signature);

        logAspect.doAfterReturning(joinPoint, annotation, null);

        ArgumentCaptor<SysOperationLog> captor = ArgumentCaptor.forClass(SysOperationLog.class);
        verify(sysLogMapper).insert(captor.capture());

        SysOperationLog log = captor.getValue();
        assertEquals(7L, log.getUserId());
        assertEquals("模块A", log.getModule());
        assertEquals("动作B", log.getAction());
        assertEquals("127.0.0.1", log.getIpAddress());
        assertNotNull(log.getMethodName());
        assertTrue(log.getMethodName().contains("Dummy"));
        assertTrue(log.getMethodName().endsWith(".testMethod()"));
        assertNotNull(log.getReqParams());
        assertTrue(log.getReqParams().contains("\"name\""));
    }
}

