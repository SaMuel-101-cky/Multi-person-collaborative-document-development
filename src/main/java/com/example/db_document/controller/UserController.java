package com.example.db_document.controller;

import com.example.db_document.handler.DocumentSocketHandler;
import com.example.db_document.model.dto.*;
import com.example.db_document.model.vo.LoginRespVO;
import com.example.db_document.pojo.JsonResult;
import com.example.db_document.pojo.User;
import com.example.db_document.service.UserService;
import com.example.db_document.model.vo.UserVO;
import com.example.db_document.utils.JwtUtil;
import com.example.db_document.utils.UserContext;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@CrossOrigin(origins = "http://localhost:5173")

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public JsonResult<User> register(@RequestBody RegisterRequest req) {    //这里用jsonObject可以吗
        userService.register(req);
        return JsonResult.success(null);
    }

    @PostMapping("/login")
    public JsonResult<LoginRespVO> login(@RequestBody  LoginRequest req) {
        User user = userService.login(req.getAccount(), req.getPassword());
        String token = JwtUtil.generateToken(user.getId(), user.getNickname());
        
        // 登录时断开该用户已有的 WebSocket 连接，并保存新 token 踢出旧 HTTP token
        DocumentSocketHandler.kickUser(user.getId());
        JwtUtil.saveActiveToken(user.getId(), token);
        user.setPassword(null);
        LoginRespVO resp = new LoginRespVO(token, user);
        return JsonResult.success(resp);
    }

    @GetMapping("/ping")
    public JsonResult<String> ping() {
        UserContext.getUserId();
        return JsonResult.success("ok");
    }

    @PostMapping("/update/avatar")
    public JsonResult<User> updateAvatar(@RequestBody UpdateAvatarRequest req) {
        Long userId = UserContext.getUserId();
        User user = userService.updateAvatar(userId, req.getAvatarUrl());
        return JsonResult.success(user);
    }

    @PostMapping("/update/info")
    public JsonResult<User> updateUserInfo(@RequestBody @Valid UserUpdateRequest req) {
        Long userId = UserContext.getUserId();
        User user = userService.updateUserInfo(userId, req);
        return JsonResult.success(user);
    }

    @PostMapping("/change-password")
    public JsonResult<String> changePassword(@RequestBody UpdatePasswordRequest req) {
        Long userId = UserContext.getUserId();
        userService.updatePassword(userId, req.getOldPassword(), req.getNewPassword());
        return JsonResult.success("修改密码成功");
    }

    //作为公共公开名片，“我查看别人，所以不用token来获取userId”
    @GetMapping("/detail/{userId}")
    public JsonResult<UserVO> getUserDetail(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        UserVO userVO = convertToVO(user);
        return JsonResult.success(userVO);
    }

    @GetMapping("/me")
    public JsonResult<User> getMyInfo() {
        Long myId = UserContext.getUserId();
        User user = userService.getUserById(myId);
        user.setPassword(null); // 只要不给密码，给自己看手机号是没问题的
        return JsonResult.success(user);
    }

    //在共享文档时，根据ID或者昵称查找用户
    @GetMapping("/search")
    public JsonResult<List<UserVO>> searchUserByNickname(
            @RequestParam("nickname")  String nickname) {
        List<User> users = userService.getUserByNickname(nickname);
        List<UserVO> userVO = users.stream().map(this::convertToVO).toList();
        return JsonResult.success(userVO);
    }


    public UserVO convertToVO(User user){
        UserVO userVO = new UserVO();
        userVO.setUserId(user.getId());
        userVO.setNickname(user.getNickname());
        userVO.setAvatarUrl(user.getAvatarUrl());
        userVO.setBio(user.getBio());
        return userVO;
    }
}
