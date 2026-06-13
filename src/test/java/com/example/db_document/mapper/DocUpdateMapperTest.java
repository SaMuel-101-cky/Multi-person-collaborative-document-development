package com.example.db_document.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DocUpdateMapperTest {

    @Test
    void mapperXml_isPresentAndHasExpectedStatements() throws Exception {
        ClassPathResource resource = new ClassPathResource("mapper/DocUpdateMapper.xml");
        assertTrue(resource.exists());

        String xml = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        assertTrue(xml.contains("namespace=\"com.example.db_document.mapper.DocUpdateMapper\""));
        assertTrue(xml.contains("id=\"insert\""));
        assertTrue(xml.contains("id=\"insertBatch\""));
        assertTrue(xml.contains("id=\"selectById\""));
        assertTrue(xml.contains("id=\"selectLatestByDocumentId\""));
        assertTrue(xml.contains("id=\"selectByDocumentIdAndVectorClock\""));
        assertTrue(xml.contains("id=\"selectByDocumentId\""));
        assertTrue(xml.contains("id=\"selectChildrenByDocumentIdAndParentUpdateId\""));
        assertTrue(xml.contains("id=\"updateDynamic\""));
        assertTrue(xml.contains("id=\"deleteById\""));
        assertTrue(xml.contains("id=\"deleteByDocumentId\""));
    }
}

