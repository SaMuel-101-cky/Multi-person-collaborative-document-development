package com.example.db_document.mapper;

import com.example.db_document.pojo.DocUpdate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DocUpdateMapper {
    int insert(DocUpdate docUpdate);

    DocUpdate selectById(@Param("id") Long id);

    DocUpdate selectLatestByDocumentId(@Param("documentId") Long documentId);

    DocUpdate selectByDocumentIdAndVectorClock(@Param("documentId") Long documentId, @Param("vectorClock") String vectorClock);

    List<DocUpdate> selectByDocumentId(@Param("documentId") Long documentId);

    List<DocUpdate> selectChildrenByDocumentIdAndParentUpdateId(@Param("documentId") Long documentId, @Param("parentUpdateId") Long parentUpdateId);

    int updateDynamic(DocUpdate docUpdate);

    int deleteById(@Param("id") Long id);

    int deleteByDocumentId(@Param("documentId") Long documentId);
}
