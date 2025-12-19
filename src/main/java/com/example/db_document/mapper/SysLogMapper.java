package com.example.db_document.mapper;

import com.example.db_document.pojo.SysOperationLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysLogMapper {
    int insert(SysOperationLog log);
}
