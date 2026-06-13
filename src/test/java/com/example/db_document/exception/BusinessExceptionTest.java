package com.example.db_document.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BusinessExceptionTest {

    @Test
    void messageIsPreserved() {
        BusinessException e = new BusinessException("boom");
        assertEquals("boom", e.getMessage());
    }
}

