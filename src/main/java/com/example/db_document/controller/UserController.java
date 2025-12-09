package com.example.db_document.controller;

import com.example.db_document.model.dto.*;
import com.example.db_document.pojo.JsonResult;
import com.example.db_document.pojo.User;
import com.example.db_document.servcie.UserService;
import com.example.db_document.model.vo.UserVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


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
    public JsonResult<User> login(@RequestBody  LoginRequest req) {
        User user = userService.login(req.getAccount(), req.getPassword());
        return JsonResult.success(user);
    }

    @PostMapping("/update-avatar")
    public JsonResult<User> updateAvatar(@RequestBody UpdateAvatarRequest req) {
        User user = userService.updateAvatar(req.getUserId(), req.getAvatarUrl());
        return JsonResult.success(user);
    }

    @PostMapping("/update/info")
    public JsonResult<User> updateUserInfo(@RequestBody @Valid UserUpdateRequest req) {
        // 假设你有办法获取当前登录用户的 ID (比如从 Token 或 Session)
        // Long currentUserId = UserContext.getUserId();
        // 这里暂时用 req 里的，但在真实项目中，不要信任前端传来的 userId，要用 Token 里的
        User user = userService.updateUserInfo(req.getUserId(), req);
        return JsonResult.success(user);
    }

    @PostMapping("/change-password")
    public JsonResult<String> changePassword(@RequestBody UpdatePasswordRequest req) {
        userService.updatePassword(req.getUserId(), req.getOldPassword(), req.getNewPassword());
        return JsonResult.success("修改密码成功");
    }

    @GetMapping("/detail/{userId}")
    public JsonResult<UserVO> getUserDetail(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        // 应该转换成 UserVO (View Object)，只包含 nickname, bio, phone, email, avatarUrl
        UserVO userVO = convertToVO(user);
        return JsonResult.success(userVO);
    }

    public UserVO convertToVO(User user){
        UserVO userVO = new UserVO();
        userVO.setUserId(user.getId());
        userVO.setNickname(user.getNickname());
        userVO.setPhoneNum(user.getPhoneNum());
        userVO.setEmail(user.getEmail());
        userVO.setBio(user.getBio());
        return userVO;
    }
}
