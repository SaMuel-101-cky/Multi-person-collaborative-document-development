package com.example.db_document.model.dto;
import lombok.Data;

@Data
public class DocUpdateCreateRequest {
    private Long documentId;
    private String vectorClock;
    private byte[] updateData;
    private Boolean isSnapshot;
    private Long parentUpdateId;
}
