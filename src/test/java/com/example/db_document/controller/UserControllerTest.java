package com.example.db_document.controller;

import com.example.db_document.config.JwtConfig;
import com.example.db_document.model.dto.LoginRequest;
import com.example.db_document.model.dto.RegisterRequest;
import com.example.db_document.model.dto.UpdateAvatarRequest;
import com.example.db_document.model.dto.UpdatePasswordRequest;
import com.example.db_document.model.dto.UserUpdateRequest;
import com.example.db_document.model.vo.LoginRespVO;
import com.example.db_document.model.vo.UserVO;
import com.example.db_document.pojo.JsonResult;
import com.example.db_document.pojo.User;
import com.example.db_document.service.UserService;
import com.example.db_document.utils.JwtUtil;
import com.example.db_document.utils.UserContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    private UserService userService;
    private UserController userController;

    @BeforeEach
    void setUp() {
        JwtConfig jwtConfig = new JwtConfig();
        jwtConfig.setSecretKey("+Q/XM0GPFAz5og7ZcianQHwulfGzoVpx5Kt7BSq+Dzs=");
        jwtConfig.setExpirationTime(86400000);
        new JwtUtil().setJwtConfig(jwtConfig);

        userService = mock(UserService.class);
        userController = new UserController();
        ReflectionTestUtils.setField(userController, "userService", userService);
    }

    @AfterEach
    void tearDown() {
        UserContext.remove();
    }

    @Test
    void register_delegatesToService() {
        RegisterRequest request = new RegisterRequest();
        request.setPhoneNum("12312345678");
        request.setPassword("password123");
        request.setEmail("test@example.com");

        JsonResult<User> resp = userController.register(request);

        assertEquals(200, resp.getCode());
        verify(userService).register(request);
    }

    @Test
    void login_generatesTokenAndReturnsUserInfo() {
        LoginRequest request = new LoginRequest();
        request.setAccount("testuser");
        request.setPassword("password123");

        User user = new User();
        user.setId(1L);
        user.setNickname("testuser");
        user.setPassword("secret");

        when(userService.login("testuser", "password123")).thenReturn(user);

        JsonResult<LoginRespVO> resp = userController.login(request);

        assertEquals(200, resp.getCode());
        assertNotNull(resp.getData());
        assertNotNull(resp.getData().getToken());
        assertFalse(resp.getData().getToken().isBlank());
        assertNotNull(resp.getData().getUserInfo());
        assertNull(resp.getData().getUserInfo().getPassword());
        verify(userService).login("testuser", "password123");
    }

    @Test
    void updateAvatar_delegatesToService() {
        UserContext.setUserId(1L);

        UpdateAvatarRequest request = new UpdateAvatarRequest();
        request.setAvatarUrl("http://example.com/avatar.jpg");

        User updated = new User();
        updated.setId(1L);
        updated.setAvatarUrl("http://example.com/avatar.jpg");

        when(userService.updateAvatar(1L, "http://example.com/avatar.jpg")).thenReturn(updated);

        JsonResult<User> resp = userController.updateAvatar(request);

        assertEquals(200, resp.getCode());
        assertEquals(updated, resp.getData());
        verify(userService).updateAvatar(1L, "http://example.com/avatar.jpg");
    }

    @Test
    void updateUserInfo_delegatesToService() {
        UserContext.setUserId(1L);

        UserUpdateRequest request = new UserUpdateRequest();
        request.setNickname("Test User");

        User updated = new User();
        updated.setId(1L);
        updated.setNickname("Test User");

        when(userService.updateUserInfo(1L, request)).thenReturn(updated);

        JsonResult<User> resp = userController.updateUserInfo(request);

        assertEquals(200, resp.getCode());
        assertEquals(updated, resp.getData());
        verify(userService).updateUserInfo(1L, request);
    }

    @Test
    void changePassword_delegatesToService() {
        UserContext.setUserId(1L);
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setOldPassword("oldpass123");
        request.setNewPassword("newpass123");

        JsonResult<String> resp = userController.changePassword(request);

        assertEquals(200, resp.getCode());
        assertEquals("修改密码成功", resp.getData());
        verify(userService).updatePassword(1L, "oldpass123", "newpass123");
    }

    @Test
    void getMyInfo_hidesPassword() {
        UserContext.setUserId(1L);
        User user = new User();
        user.setId(1L);
        user.setPassword("secret");

        when(userService.getUserById(1L)).thenReturn(user);

        JsonResult<User> resp = userController.getMyInfo();

        assertEquals(200, resp.getCode());
        assertNotNull(resp.getData());
        assertNull(resp.getData().getPassword());
    }

    @Test
    void getUserDetail_returnsVO() {
        Long userId = 1L;
        User user = new User();
        user.setId(1L);
        user.setNickname("nick");
        user.setAvatarUrl("a");
        user.setBio("b");

        when(userService.getUserById(1L)).thenReturn(user);

        JsonResult<UserVO> resp = userController.getUserDetail(userId);

        assertEquals(200, resp.getCode());
        assertEquals(1L, resp.getData().getUserId());
        assertEquals("nick", resp.getData().getNickname());
    }

    @Test
    void searchUserByNickname_mapsToVOList() {
        User user = new User();
        user.setId(1L);
        user.setNickname("test");

        when(userService.getUserByNickname("test")).thenReturn(List.of(user));

        JsonResult<List<UserVO>> resp = userController.searchUserByNickname("test");

        assertEquals(200, resp.getCode());
        assertEquals(1, resp.getData().size());
        assertEquals(1L, resp.getData().get(0).getUserId());
        assertEquals("test", resp.getData().get(0).getNickname());
    }
}
