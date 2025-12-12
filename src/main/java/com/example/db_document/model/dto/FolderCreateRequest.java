package com.example.db_document.model.dto;
import lombok.Data;

@Data
public class FolderCreateRequest {
    private String name;
    private Long parentId;
}
