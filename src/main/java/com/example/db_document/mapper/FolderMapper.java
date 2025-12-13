package com.example.db_document.mapper;

import com.example.db_document.pojo.Folder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FolderMapper {
    int insert(Folder folder);

    Folder selectById(@Param("id") Long id);

    /**
     * 检查给定父ID下是否存在指定名称且未被删除的文件夹
     * @param name 文件夹名称
     * @param parentId 父文件夹ID (可为 null)
     * @return 匹配的文件夹数量 (0 或 1)
     */
    int countByNameAndParentId(@Param("name") String name,@Param("parentId") Long parentId);

    List<Folder> selectByParentAndCreatorId(@Param("parentId") Long parentId,@Param("creatorId") Long creatorId);

    int softDeleteById(@Param("id") Long id);

    //用来移动文件夹
    int changeParentId(@Param("id") Long id, @Param("newParentId") Long newParentId);
}
