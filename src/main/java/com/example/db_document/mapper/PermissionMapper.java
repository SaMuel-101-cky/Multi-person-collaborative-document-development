package com.example.db_document.mapper;

import com.example.db_document.pojo.Permission;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface PermissionMapper {
    int insert(Permission permission);

    //查看是否已经有了联系
    Permission selectByDocIdAndUserId(Long documentId, Long userId);
}
