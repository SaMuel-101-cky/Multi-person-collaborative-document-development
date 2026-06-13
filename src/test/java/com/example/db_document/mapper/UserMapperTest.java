package com.example.db_document.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    @Test
    void mapperXml_isPresentAndHasExpectedStatements() throws Exception {
        ClassPathResource resource = new ClassPathResource("mapper/UserMapper.xml");
        assertTrue(resource.exists());

        String xml = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        assertTrue(xml.contains("namespace=\"com.example.db_document.mapper.UserMapper\""));
        assertTrue(xml.contains("id=\"insert\""));
        assertTrue(xml.contains("id=\"selectByEmail\""));
        assertTrue(xml.contains("id=\"selectByPhone\""));
        assertTrue(xml.contains("id=\"selectByAccount\""));
        assertTrue(xml.contains("id=\"selectByNickname\""));
        assertTrue(xml.contains("id=\"selectById\""));
        assertTrue(xml.contains("id=\"selectByNicknameLike\""));
        assertTrue(xml.contains("id=\"updateAvatarById\""));
        assertTrue(xml.contains("id=\"updatePasswordById\""));
        assertTrue(xml.contains("id=\"updateDynamic\""));
    }
}
