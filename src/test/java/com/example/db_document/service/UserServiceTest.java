package com.example.db_document.service;

import com.example.db_document.exception.BusinessException;
import com.example.db_document.mapper.UserMapper;
import com.example.db_document.model.dto.RegisterRequest;
import com.example.db_document.model.dto.UserUpdateRequest;
import com.example.db_document.pojo.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLogin_Success() {
        String account = "test@example.com";
        String password = "password123";

        User user = new User();
        user.setId(1L);
        user.setNickname("testuser");
        user.setPassword("password123");
        user.setEmail("test@example.com");

        when(userMapper.selectByAccount(account)).thenReturn(user);

        User result = userService.login(account, password);
        assertNotNull(result);
        assertNull(result.getPassword());
        assertEquals("testuser", result.getNickname());
        verify(userMapper).selectByAccount(account);
    }

    @Test
    void testLogin_EmptyAccount() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.login("", "password123"));
        assertEquals("账号不能为空", exception.getMessage());
    }

    @Test
    void testLogin_EmptyPassword() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.login("test@example.com", ""));
        assertEquals("密码不能为空", exception.getMessage());
    }

    @Test
    void testLogin_UserNotFound() {
        when(userMapper.selectByAccount("nonexistent@example.com")).thenReturn(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.login("nonexistent@example.com", "password123"));
        assertEquals("用户不存在", exception.getMessage());
    }

    @Test
    void testLogin_WrongPassword() {
        User user = new User();
        user.setPassword("correctPassword");

        when(userMapper.selectByAccount("test@example.com")).thenReturn(user);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.login("test@example.com", "wrongPassword"));
        assertEquals("账号或密码错误", exception.getMessage());
    }

    @Test
    void testRegisterByPhone_Success() {
        String phone = "13800138000";
        String password = "password123";

        when(userMapper.selectByPhone(phone)).thenReturn(null);
        doAnswer(invocation -> {
            // 获取传入 insert 方法的 User 对象
            User userToInsert = invocation.getArgument(0);
            // 模拟数据库自增 ID（根据你实际的 ID 类型选择 Long/Integer）
            userToInsert.setId(1L); // 如果是 Integer 类型就写 1
            return 1; // 返回插入成功的影响行数
        }).when(userMapper).insert(any(User.class));

        assertDoesNotThrow(() -> userService.registerByPhone(phone, password));
        verify(userMapper).selectByPhone(phone);
        verify(userMapper).insert(any(User.class));
    }

    @Test
    void testRegisterByPhone_InvalidFormat() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.registerByPhone("123456", "password123"));
        assertEquals("电话号码格式不正确", exception.getMessage());
    }

    @Test
    void testRegisterByPhone_DuplicatePhone() {
        String phone = "13800138000";
        User existingUser = new User();

        when(userMapper.selectByPhone(phone)).thenReturn(existingUser);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.registerByPhone(phone, "password123"));
        assertEquals("该号码已被注册", exception.getMessage());
    }

    @Test
    void testRegisterByEmail_Success() {
        String email = "test@example.com";
        String password = "password123";

        when(userMapper.selectByEmail(email)).thenReturn(null);
        doAnswer(invocation -> {
            User userToInsert = invocation.getArgument(0);
            userToInsert.setId(1L);
            return 1;
        }).when(userMapper).insert(any(User.class));

        assertDoesNotThrow(() -> userService.registerByEmail(email, password));
        verify(userMapper).selectByEmail(email);
        verify(userMapper).insert(any(User.class));
    }

    @Test
    void testRegisterByEmail_InvalidFormat() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.registerByEmail("invalid-email", "password123"));
        assertEquals("邮箱格式不正确", exception.getMessage());
    }

    @Test
    void testRegisterByEmail_DuplicateEmail() {
        String email = "test@example.com";
        User existingUser = new User();

        when(userMapper.selectByEmail(email)).thenReturn(existingUser);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.registerByEmail(email, "password123"));
        assertEquals("该邮箱已被注册", exception.getMessage());
    }

    @Test
    void testRegister_WithPhone() {
        RegisterRequest request = new RegisterRequest();
        request.setPhoneNum("13800138000");
        request.setPassword("password123");

        when(userMapper.selectByPhone(anyString())).thenReturn(null);
        doAnswer(invocation -> {
            User userToInsert = invocation.getArgument(0);
            userToInsert.setId(1L);
            return 1;
        }).when(userMapper).insert(any(User.class));

        assertDoesNotThrow(() -> userService.register(request));
    }

    @Test
    void testRegister_WithEmail() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        when(userMapper.selectByEmail(anyString())).thenReturn(null);
        doAnswer(invocation -> {
            User userToInsert = invocation.getArgument(0);
            userToInsert.setId(1L);
            return 1;
        }).when(userMapper).insert(any(User.class));

        assertDoesNotThrow(() -> userService.register(request));
    }

    @Test
    void testUpdateAvatar_Success() {
        Long userId = 1L;
        String newAvatarUrl = "http://example.com/avatar.jpg";

        when(userMapper.updateAvatarById(userId, newAvatarUrl)).thenReturn(1);

        User user = new User();
        user.setId(userId);
        when(userMapper.selectById(userId)).thenReturn(user);

        User result = userService.updateAvatar(userId, newAvatarUrl);
        assertNotNull(result);
        assertNull(result.getPassword());
        verify(userMapper).updateAvatarById(userId, newAvatarUrl);
        verify(userMapper).selectById(userId);
    }

    @Test
    void testUpdateAvatar_EmptyUrl() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.updateAvatar(1L, ""));
        assertEquals("头像URL不能为空", exception.getMessage());
    }

    @Test
    void testUpdateAvatar_UserNotFound() {
        when(userMapper.updateAvatarById(1L, "http://example.com/avatar.jpg")).thenReturn(0);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.updateAvatar(1L, "http://example.com/avatar.jpg"));
        assertEquals("头像更新失败，用户可能不存在", exception.getMessage());
    }

    @Test
    void testUpdateUserInfo_Success() {
        Long userId = 1L;
        UserUpdateRequest req = new UserUpdateRequest();
        req.setNickname("NewNickname");
        req.setPhoneNum("13800138001");
        req.setEmail("new@example.com");
        req.setBio("New bio");

        when(userMapper.selectByNickname(anyString())).thenReturn(null);
        when(userMapper.selectByPhone(anyString())).thenReturn(null);
        when(userMapper.selectByEmail(anyString())).thenReturn(null);
        when(userMapper.updateDynamic(any(User.class))).thenReturn(1);

        User user = new User();
        user.setId(userId);
        when(userMapper.selectById(userId)).thenReturn(user);

        User result = userService.updateUserInfo(userId, req);
        assertNotNull(result);
        assertNull(result.getPassword());
        verify(userMapper).updateDynamic(any(User.class));
        verify(userMapper).selectById(userId);
    }

    @Test
    void testUpdateUserInfo_DuplicateNickname() {
        Long userId = 1L;
        UserUpdateRequest req = new UserUpdateRequest();
        req.setNickname("ExistingNickname");

        User existingUser = new User();
        existingUser.setId(2L);
        when(userMapper.selectByNickname("ExistingNickname")).thenReturn(existingUser);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.updateUserInfo(userId, req));
        assertEquals("该昵称已被使用", exception.getMessage());
    }

    @Test
    void testUpdateUserInfo_DuplicatePhone() {
        Long userId = 1L;
        UserUpdateRequest req = new UserUpdateRequest();
        req.setPhoneNum("13800138001");

        User existingUser = new User();
        existingUser.setId(2L);
        when(userMapper.selectByPhone("13800138001")).thenReturn(existingUser);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.updateUserInfo(userId, req));
        assertEquals("该手机号已被注册", exception.getMessage());
    }

    @Test
    void testUpdateUserInfo_DuplicateEmail() {
        Long userId = 1L;
        UserUpdateRequest req = new UserUpdateRequest();
        req.setEmail("existing@example.com");

        User existingUser = new User();
        existingUser.setId(2L);
        when(userMapper.selectByEmail("existing@example.com")).thenReturn(existingUser);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.updateUserInfo(userId, req));
        assertEquals("该邮箱已被注册", exception.getMessage());
    }

    @Test
    void testUpdatePassword_Success() {
        Long userId = 1L;
        String oldPassword = "oldPassword123";
        String newPassword = "newPassword123";

        User user = new User();
        user.setId(userId);
        user.setPassword(oldPassword);
        when(userMapper.selectById(userId)).thenReturn(user);
        when(userMapper.updatePasswordById(userId, newPassword)).thenReturn(1);

        User result = userService.updatePassword(userId, oldPassword, newPassword);
        assertNotNull(result);
        assertNull(result.getPassword());
        verify(userMapper).selectById(userId);
        verify(userMapper).updatePasswordById(userId, newPassword);
    }

    @Test
    void testUpdatePassword_EmptyNewPassword() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.updatePassword(1L, "oldPassword", ""));
        assertEquals("密码不能为空", exception.getMessage());
    }

    @Test
    void testUpdatePassword_UserNotFound() {
        when(userMapper.selectById(1L)).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.updatePassword(1L, "oldPassword", "newPassword"));
        assertEquals("用户不存在", exception.getMessage());
    }

    @Test
    void testUpdatePassword_WrongOldPassword() {
        User user = new User();
        user.setPassword("correctOldPassword");
        when(userMapper.selectById(1L)).thenReturn(user);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.updatePassword(1L, "wrongOldPassword", "newPassword"));
        assertEquals("旧密码不正确", exception.getMessage());
    }

    @Test
    void testUpdatePassword_UpdateFailed() {
        User user = new User();
        user.setId(1L);
        user.setPassword("oldPassword");
        when(userMapper.selectById(1L)).thenReturn(user);
        when(userMapper.updatePasswordById(1L, "newPassword")).thenReturn(0);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.updatePassword(1L, "oldPassword", "newPassword"));
        assertEquals("密码更新失败，用户可能不存在", exception.getMessage());
    }

    @Test
    void testGenerateByUUID() {
        String nickname1 = UserService.generateByUUID();
        String nickname2 = UserService.generateByUUID();

        assertTrue(nickname1.startsWith("User_"));
        assertTrue(nickname2.startsWith("User_"));
        assertEquals(13, nickname1.length());
        assertNotEquals(nickname1, nickname2);
    }

    @Test
    void testgetUserById_Success() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setNickname("testuser");
        user.setPassword("password123");

        when(userMapper.selectById(userId)).thenReturn(user);

        User result = userService.getUserById(userId);
        assertNotNull(result);
        assertNull(result.getPassword());
        assertEquals("testuser", result.getNickname());
    }

    @Test
    void testgetUserById_UserNotFound() {
        when(userMapper.selectById(1L)).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.getUserById(1L));
        assertEquals("用户不存在", exception.getMessage());
    }

    @Test
    void testgetUserByNickname_Success() {
        String nickname = "test";
        User user1 = new User();
        User user2 = new User();

        when(userMapper.selectByNicknameLike(nickname)).thenReturn(Arrays.asList(user1, user2));

        List<User> result = userService.getUserByNickname(nickname);
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testgetUserByNickname_NotFound() {
        when(userMapper.selectByNicknameLike("nonexistent")).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.getUserByNickname("nonexistent"));
        assertEquals("用户不存在", exception.getMessage());
    }
}