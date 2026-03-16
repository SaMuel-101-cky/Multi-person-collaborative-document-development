package com.example.db_document.model.dto;

import lombok.Data;

@Data
public class DocUpdateUpdateRequest {
    private Long id;
    private String vectorClock;
    private byte[] updateData;
    private Boolean isSnapshot;
    private Long parentUpdateId;
}
