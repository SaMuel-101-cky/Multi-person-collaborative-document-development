package com.example.db_document.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocUpdate {
    private Long id;
    private Long documentId;
    private String vectorClock;
    private byte[] updateData;
    private Boolean isSnapshot;
    private Long parentUpdateId;
    private LocalDateTime createdAt;
}
