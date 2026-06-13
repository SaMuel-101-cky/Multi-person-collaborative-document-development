package com.example.db_document;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class DbDocumentApplicationTests {

    @Test
    void applicationHasMainMethod() throws Exception {
        assertNotNull(DbDocumentApplication.class.getMethod("main", String[].class));
    }

}
