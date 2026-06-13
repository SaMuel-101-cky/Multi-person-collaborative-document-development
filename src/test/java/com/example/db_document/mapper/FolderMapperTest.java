package com.example.db_document.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class FolderMapperTest {

    @Test
    void mapperXml_isPresentAndHasExpectedStatements() throws Exception {
        ClassPathResource resource = new ClassPathResource("mapper/FolderMapper.xml");
        assertTrue(resource.exists());

        String xml = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        assertTrue(xml.contains("namespace=\"com.example.db_document.mapper.FolderMapper\""));
        assertTrue(xml.contains("id=\"insert\""));
        assertTrue(xml.contains("id=\"selectById\""));
        assertTrue(xml.contains("id=\"countByNameAndParentId\""));
        assertTrue(xml.contains("id=\"selectByParentAndCreatorId\""));
        assertTrue(xml.contains("id=\"softDeleteById\""));
        assertTrue(xml.contains("id=\"changeParentId\""));
    }
}
