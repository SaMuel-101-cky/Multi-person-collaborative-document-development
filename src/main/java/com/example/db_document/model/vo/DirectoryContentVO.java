package com.example.db_document.model.vo;

import com.example.db_document.pojo.Document;
import com.example.db_document.pojo.Folder;
import lombok.Data;

import java.util.List;


@Data
public class DirectoryContentVO {
    // 当前目录下的文件夹列表
    private List<Folder> folders;
    // 当前目录下的文档列表
    private List<Document> documents;
}
