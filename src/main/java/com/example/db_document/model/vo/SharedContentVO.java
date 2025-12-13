package com.example.db_document.model.vo;

import com.example.db_document.pojo.Document;
import lombok.Data;

import java.util.List;

@Data
public class SharedContentVO {
    private List<Document> documents;
}
