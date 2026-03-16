package com.example.db_document.mapper;

import com.example.db_document.pojo.SysOperationLog;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class SysLogMapperTest {

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    private SqlSession sqlSession;
    private SysLogMapper sysLogMapper;

    @BeforeEach
    void setUp() {
        sqlSession = sqlSessionFactory.openSession();
        sysLogMapper = sqlSession.getMapper(SysLogMapper.class);
    }

    @Test
    void testInsert() {
        SysOperationLog log = new SysOperationLog();
        log.setUserId(100L);
        log.setModule("user");
        log.setAction("login");
        log.setMethodName("UserController.login");
        log.setReqParams("{\"account\":\"test@example.com\",\"password\":\"***\"}");
        log.setIpAddress("127.0.0.1");
        log.setCreateTime(LocalDateTime.now());

        int result = sysLogMapper.insert(log);
        assertEquals(1, result);
    }

    @Test
    void testInsertWithAllFields() {
        SysOperationLog log = new SysOperationLog();
        log.setId(999L);
        log.setUserId(200L);
        log.setModule("document");
        log.setAction("create");
        log.setMethodName("DocumentController.createDocument");
        log.setReqParams("{\"name\":\"Test Document\"}");
        log.setIpAddress("192.168.1.100");
        log.setCreateTime(LocalDateTime.now());

        int result = sysLogMapper.insert(log);
        assertEquals(1, result);
    }
}