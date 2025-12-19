package com.example.db_document.model.vo;

import com.example.db_document.pojo.Document;
import lombok.Data;

@Data
public class DocumentDetailVO {
    // 包含原始文档的所有信息
    private Document document;
    // 新增：是否是共享/协作文档
    private boolean isShared;
}
