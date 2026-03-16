package com.example.db_document.mapper;

import com.example.db_document.pojo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional // 自动回滚，无需手动管理 SqlSession
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void testInsert() {
        User user = new User();
        user.setNickname("testuser");
        user.setPassword("password123");
        user.setEmail("test@example.com");
        user.setPhoneNum("13800138000");

        int result = userMapper.insert(user);
        assertEquals(1, result);
        assertNotNull(user.getId()); // 验证ID自动生成
    }

    @Test
    void testSelectById() {
        User result = userMapper.selectById(1L);
        // 使用数据库中已存在的数据进行测试
        if (result != null) {
            assertEquals(1L, result.getId());
        }
    }

    @Test
    void testSelectByEmail() {
        User user = new User();
        user.setNickname("emailtest");
        user.setPassword("password123");
        user.setEmail("testemail@example.com");
        user.setPhoneNum("13900138000");
        userMapper.insert(user);

        User result = userMapper.selectByEmail("testemail@example.com");
        assertNotNull(result);
        assertEquals("emailtest", result.getNickname());
    }

    @Test
    void testSelectByPhone() {
        User user = new User();
        user.setNickname("phonetest");
        user.setPassword("password123");
        user.setEmail("phonetest@example.com");
        user.setPhoneNum("13700138000");
        userMapper.insert(user);

        User result = userMapper.selectByPhone("13700138000");
        assertNotNull(result);
        assertEquals("phonetest", result.getNickname());
    }

    @Test
    void testSelectByNickname() {
        User user = new User();
        user.setNickname("uniquenickname");
        user.setPassword("password123");
        user.setEmail("uniquenick@example.com");
        user.setPhoneNum("13600138000");
        userMapper.insert(user);

        User result = userMapper.selectByNickname("uniquenickname");
        assertNotNull(result);
        assertEquals("uniquenickname", result.getNickname());
    }

    @Test
    void testSelectByAccount() {
        User user = new User();
        user.setNickname("accounttest");
        user.setPassword("password123");
        user.setEmail("accounttest@example.com");
        user.setPhoneNum("13500138000");
        userMapper.insert(user);

        // 测试邮箱作为账号登录
        User result = userMapper.selectByAccount("accounttest@example.com");
        assertNotNull(result);
        assertEquals("accounttest", result.getNickname());

        // 测试手机号作为账号登录
        User result2 = userMapper.selectByAccount("13500138000");
        assertNotNull(result2);
        assertEquals("accounttest", result2.getNickname());
    }

    @Test
    void testSelectByNicknameLike() {
        User user = new User();
        user.setNickname("searchtest");
        user.setPassword("password123");
        user.setEmail("searchtest@example.com");
        user.setPhoneNum("13400138000");
        userMapper.insert(user);

        var result = userMapper.selectByNicknameLike("search");
        assertNotNull(result);
        assertTrue(result.stream().anyMatch(u -> u.getNickname().contains("search")));
    }

    @Test
    void testUpdateAvatarById() {
        User user = new User();
        user.setNickname("avatartest");
        user.setPassword("password123");
        user.setEmail("avatartest@example.com");
        user.setPhoneNum("13300138000");
        userMapper.insert(user);

        Long userId = user.getId();
        String newAvatarUrl = "http://example.com/newavatar.jpg";

        int result = userMapper.updateAvatarById(userId, newAvatarUrl);
        assertEquals(1, result);

        User updatedUser = userMapper.selectById(userId);
        assertEquals(newAvatarUrl, updatedUser.getAvatarUrl());
    }

    @Test
    void testUpdatePasswordById() {
        User user = new User();
        user.setNickname("passwordtest");
        user.setPassword("oldpassword");
        user.setEmail("passwordtest@example.com");
        user.setPhoneNum("13200138000");
        userMapper.insert(user);

        Long userId = user.getId();
        String newPassword = "newpassword123";

        int result = userMapper.updatePasswordById(userId, newPassword);
        assertEquals(1, result);
    }

    @Test
    void testUpdateDynamic() {
        User user = new User();
        user.setNickname("dynamictest");
        user.setPassword("password123");
        user.setEmail("dynamictest@example.com");
        user.setPhoneNum("13100138000");
        userMapper.insert(user);

        Long userId = user.getId();

        // 更新用户信息
        User updateInfo = new User();
        updateInfo.setId(userId);
        updateInfo.setNickname("UpdatedNickname");
        updateInfo.setBio("Updated bio information");
        updateInfo.setEmail("updatedemail@example.com");

        int result = userMapper.updateDynamic(updateInfo);
        assertEquals(1, result);

        User updatedUser = userMapper.selectById(userId);
        assertEquals("UpdatedNickname", updatedUser.getNickname());
        assertEquals("Updated bio information", updatedUser.getBio());
        assertEquals("updatedemail@example.com", updatedUser.getEmail());
    }
}