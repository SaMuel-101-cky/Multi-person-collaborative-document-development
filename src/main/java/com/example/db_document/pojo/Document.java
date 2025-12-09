package com.example.db_document.pojo;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Document {
    private Long id;
    private String name;
    private Long folderId;
    private String content;
    private Long creatorId;
    private Integer isDeleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;


    public Document(){
    }

    public Document(Long id, String name, String content, Long creatorId,Long folderId, Integer isDeleted, LocalDateTime createTime, LocalDateTime updateTime) {
        this.id = id;
        this.name = name;
        this.content = content;
        this.creatorId = creatorId;
        this.folderId = folderId;
        this.isDeleted = isDeleted;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

}

