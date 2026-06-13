package com.example.db_document.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class PermissionMapperTest {

    void mapperXml_isPresentAndHasExpectedStatements() throws Exception {
        ClassPathResource resource = new ClassPathResource("mapper/PermissionMapper.xml");
        assertTrue(resource.exists());

        String xml = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        assertTrue(xml.contains("namespace=\"com.example.db_document.mapper.PermissionMapper\""));
        assertTrue(xml.contains("id=\"insert\""));
        assertTrue(xml.contains("id=\"selectByDocIdAndUserId\""));
        assertTrue(xml.contains("id=\"selectByDocIdAndUserIdCollaborate\""));
        assertTrue(xml.contains("id=\"selectUsersByDocumentId\""));
        assertTrue(xml.contains("id=\"selectUserVOByDocumentId\""));
        assertTrue(xml.contains("id=\"deleteByDocIdAndUserId\""));
        assertTrue(xml.contains("id=\"countByDocumentId\""));
    }
}
