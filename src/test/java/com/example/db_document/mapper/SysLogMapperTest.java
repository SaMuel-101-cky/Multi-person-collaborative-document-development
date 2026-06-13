package com.example.db_document.mapper;

import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import static org.junit.jupiter.api.Assertions.*;

class SysLogMapperTest {

    @Test
    void mapperXml_isPresentAndHasExpectedStatements() throws Exception {
        ClassPathResource resource = new ClassPathResource("mapper/SysLogMapper.xml");
        assertTrue(resource.exists());

        String xml = new String(resource.getInputStream().readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
        assertTrue(xml.contains("namespace=\"com.example.db_document.mapper.SysLogMapper\""));
        assertTrue(xml.contains("id=\"insert\""));
    }
}
