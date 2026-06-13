package com.example.db_document.pojo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class JsonResultTest {

    @Test
    void success_setsCodeAndMessage() {
        JsonResult<String> resp = JsonResult.success("ok");
        assertEquals(200, resp.getCode());
        assertEquals("success", resp.getMsg());
        assertEquals("ok", resp.getData());
    }

    @Test
    void error_setsCodeAndMessageAndNullData() {
        JsonResult<String> resp = JsonResult.error("bad");
        assertEquals(500, resp.getCode());
        assertEquals("bad", resp.getMsg());
        assertNull(resp.getData());
    }
}

