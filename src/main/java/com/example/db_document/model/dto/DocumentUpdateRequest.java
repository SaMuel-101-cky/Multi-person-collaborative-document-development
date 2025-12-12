package com.example.db_document.model.dto;

import lombok.Data;

@Data
public class DocumentUpdateRequest {
    //文档的id
    private Long id;
    private String name;
    private String content;
}
