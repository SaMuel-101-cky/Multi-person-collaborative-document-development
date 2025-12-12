package com.example.db_document.mapper;

import com.example.db_document.pojo.Document;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DocumentMapper {
    int insert(Document document);

    Document selectById(@Param("id") Long id);

    int softDeleteById(@Param("id") Long id);

    List<Document> selectByFolderId(@Param("folderId") Long folderId);

    int countByNameAndFolderId(@Param("name") String name, @Param("folderId") Long folderId);

    int changeFolderId(@Param("documentId") Long documentId, @Param("newFolderId") Long newFolderId);

    int updateDynamic(Document document);
}
