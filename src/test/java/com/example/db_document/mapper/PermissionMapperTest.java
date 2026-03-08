package com.example.db_document.mapper;

import com.example.db_document.model.vo.UserVO;
import com.example.db_document.pojo.Permission;
import com.example.db_document.pojo.PermissionType;
import com.example.db_document.pojo.User;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PermissionMapperTest {

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    private SqlSession sqlSession;
    private PermissionMapper permissionMapper;

    private static long testIdCounter = 1;

    @BeforeEach
    void setUp() {
        sqlSession = sqlSessionFactory.openSession();
        permissionMapper = sqlSession.getMapper(PermissionMapper.class);
    }

    private long getNextTestId() {
        return testIdCounter++;
    }

    @Test
    void testInsert() {
        long docId = getNextTestId() * 100;
        long userId = getNextTestId() * 100;

        Permission permission = new Permission();
        permission.setDocumentId(docId);
        permission.setUserId(userId);
        permission.setPermissionType(PermissionType.EDITOR);

        int result = permissionMapper.insert(permission);
        assertEquals(1, result);
    }

    @Test
    void testSelectByDocIdAndUserId() {
        long docId = getNextTestId() * 100;
        long userId = getNextTestId() * 100;

        Permission permission = new Permission();
        permission.setDocumentId(docId);
        permission.setUserId(userId);
        permission.setPermissionType(PermissionType.VIEWER);
        permissionMapper.insert(permission);

        Permission result = permissionMapper.selectByDocIdAndUserId(docId, userId);
        assertNotNull(result);
        assertEquals(docId, result.getDocumentId());
        assertEquals(userId, result.getUserId());
    }

    @Test
    void testSelectByDocIdAndUserIdCollaborate() {
        long docId = getNextTestId() * 100;
        long userId = getNextTestId() * 100;

        Permission permission = new Permission();
        permission.setDocumentId(docId);
        permission.setUserId(userId);
        permission.setPermissionType(PermissionType.EDITOR);
        permissionMapper.insert(permission);

        Permission result = permissionMapper.selectByDocIdAndUserIdCollaborate(docId, userId);
        assertNotNull(result);
        assertEquals(docId, result.getDocumentId());
        assertEquals(userId, result.getUserId());
    }

    @Test
    void testDeleteByDocIdAndUserId() {
        long docId = getNextTestId() * 100;
        long userId = getNextTestId() * 100;

        Permission permission = new Permission();
        permission.setDocumentId(docId);
        permission.setUserId(userId);
        permission.setPermissionType(PermissionType.EDITOR);
        permissionMapper.insert(permission);

        int result = permissionMapper.deleteByDocIdAndUserId(docId, userId);
        assertEquals(1, result);
    }

    @Test
    void testSelectUsersByDocumentId() {
        long docId = getNextTestId() * 100;
        long userId = getNextTestId() * 100;

        Permission permission = new Permission();
        permission.setDocumentId(docId);
        permission.setUserId(userId);
        permission.setPermissionType(PermissionType.EDITOR);
        permissionMapper.insert(permission);

        List<User> result = permissionMapper.selectUsersByDocumentId(docId);
        assertNotNull(result);
    }

    @Test
    void testSelectUserVOByDocumentId() {
        long docId = getNextTestId() * 100;
        long userId = getNextTestId() * 100;

        Permission permission = new Permission();
        permission.setDocumentId(docId);
        permission.setUserId(userId);
        permission.setPermissionType(PermissionType.EDITOR);
        permissionMapper.insert(permission);

        List<UserVO> result = permissionMapper.selectUserVOByDocumentId(docId);
        assertNotNull(result);
    }

    @Test
    void testCountByDocumentId() {
        long docId = getNextTestId() * 100;
        long userId1 = getNextTestId() * 100;
        long userId2 = getNextTestId() * 100;

        Permission permission1 = new Permission();
        permission1.setDocumentId(docId);
        permission1.setUserId(userId1);
        permission1.setPermissionType(PermissionType.EDITOR);
        permissionMapper.insert(permission1);

        Permission permission2 = new Permission();
        permission2.setDocumentId(docId);
        permission2.setUserId(userId2);
        permission2.setPermissionType(PermissionType.VIEWER);
        permissionMapper.insert(permission2);

        int result = permissionMapper.countByDocumentId(docId);
        assertEquals(2, result);
    }
}