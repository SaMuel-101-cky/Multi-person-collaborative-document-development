package com.example.db_document.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DocumentMapperTest {

    @Test
    void mapperXml_isPresentAndHasExpectedStatements() throws Exception {
        ClassPathResource resource = new ClassPathResource("mapper/DocumentMapper.xml");
        assertTrue(resource.exists());

        String xml = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        assertTrue(xml.contains("namespace=\"com.example.db_document.mapper.DocumentMapper\""));
        assertTrue(xml.contains("id=\"insert\""));
        assertTrue(xml.contains("id=\"selectById\""));
        assertTrue(xml.contains("id=\"softDeleteById\""));
        assertTrue(xml.contains("id=\"updateDynamic\""));
        assertTrue(xml.contains("id=\"selectByFolderAndCreatorId\""));
        assertTrue(xml.contains("id=\"countByNameAndFolderId\""));
        assertTrue(xml.contains("id=\"selectSharedDocuments\""));
        assertTrue(xml.contains("id=\"changeFolderId\""));
    }
}
