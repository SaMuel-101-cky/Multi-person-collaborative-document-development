package com.example.db_document.controller;

import com.example.db_document.model.dto.LoginRequest;
import com.example.db_document.model.dto.RegisterRequest;
import com.example.db_document.model.dto.UpdateAvatarRequest;
import com.example.db_document.model.dto.UpdatePasswordRequest;
import com.example.db_document.model.dto.UserUpdateRequest;
import com.example.db_document.pojo.User;
import com.example.db_document.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void testRegister() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setPhoneNum("12312345678");
        request.setPassword("password123");
        request.setEmail("test@example.com");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"phoneNum\":\"12312345678\",\"password\":\"password123\",\"email\":\"test@example.com\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void testLoginByNickname() throws Exception {
        LoginRequest request = new LoginRequest();
        //测试账号名称登录
        request.setAccount("testuser");
        request.setPassword("password123");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"account\":\"testuser\",\"password\":\"password123\"}"))
                .andExpect(status().isOk());               //此处断言过于简单，后续可以增加对返回结果的断言，验证是否包含token等信息
    }

    @Test
    void testLoginByEmail() throws Exception {
        LoginRequest request = new LoginRequest();
        //测试账号注册邮箱登录
        request.setAccount("test@example.com");
        request.setPassword("password123");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"account\":\"test@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isOk());               //此处断言过于简单，后续可以增加对返回结果的断言，验证是否包含token等信息
    }

    @Test
    void testLoginByPhoneNum() throws Exception {
        LoginRequest request = new LoginRequest();
        //测试账号注册手机号码登录
        request.setAccount("12312345678");
        request.setPassword("password123");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"account\":\"12312345678\",\"password\":\"password123\"}"))
                .andExpect(status().isOk());               //此处断言过于简单，后续可以增加对返回结果的断言，验证是否包含token等信息
    }

    @Test
    void testUpdateAvatar() throws Exception {
        UpdateAvatarRequest request = new UpdateAvatarRequest();
        request.setAvatarUrl("http://example.com/avatar.jpg");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/update/avatar")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"avatarUrl\":\"http://example.com/avatar.jpg\"}")
                .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateInfo() throws Exception {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setNickname("Test User");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/update/info")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nickname\":\"Test User\"}")
                .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk());
    }

    @Test
    void testChangePassword() throws Exception {
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setOldPassword("oldpass123");
        request.setNewPassword("newpass123");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"oldPassword\":\"oldpass123\",\"newPassword\":\"newpass123\"}")
                .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk());
    }

    @Test
    void testMe() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/me")
                .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk());
    }

    @Test
    void testDetail() throws Exception {
        Long userId = 1L;
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/detail/{userId}", userId))
                .andExpect(status().isOk());
    }

    @Test
    void testSearch() throws Exception {
        String nickname = "test";
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/search?nickname={nickname}", nickname))
                .andExpect(status().isOk());
    }
}