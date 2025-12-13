package com.example.db_document.mapper;

import com.example.db_document.pojo.Permission;
import com.example.db_document.pojo.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface PermissionMapper {
    int insert(Permission permission);

    //查看是否已经有了联系，查的是编辑和所有的权限，用来编辑内容时候使用
    Permission selectByDocIdAndUserId(Long documentId, Long userId);

    Permission selectByDocIdAndUserIdCollaborate(Long documentId, Long userId);
    //软删除
    int deleteByDocIdAndUserId(Long documentId, Long userId);

    List<User> selectUsersByDocumentId(Long documentId);
}
