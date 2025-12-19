package com.example.db_document.service;

import com.example.db_document.exception.BusinessException;
import com.example.db_document.mapper.UserMapper;
import com.example.db_document.model.dto.RegisterRequest;
import com.example.db_document.model.dto.UserUpdateRequest;
import com.example.db_document.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public UserService(){
    }

//电话号码，昵称，邮箱都用这个
    public User login(String account, String password){
        if (account == null || (account = account.trim()).isEmpty()) {
            throw new IllegalArgumentException("账号不能为空");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }

        User user = userMapper.selectByAccount(account);
        if (user == null){
            throw new IllegalArgumentException("用户不存在");
        }
        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("账号或密码错误"); // 或者返回 null
        }

        System.out.println("登录成功，欢迎 " + user.getNickname());
        user.setPassword(null); // 登录成功后，不返回密码，确保安全
        return user;
    }

    public void register(RegisterRequest req) {
        if (req.getPhoneNum() != null && !req.getPhoneNum().isBlank()) {
            this.registerByPhone(req.getPhoneNum(), req.getPassword());
        } else {
            this.registerByEmail(req.getEmail(), req.getPassword());
        }
    }

    public void registerByPhone(String phone, String password) {
        if (phone == null || (phone = phone.trim()).isEmpty()) {
            throw new IllegalArgumentException("电话号码不能为空");
        }
        //中国大陆手机号判断
        String phoneRegex = "^1[3-9]\\d{9}$";
        if (!java.util.regex.Pattern.matches(phoneRegex, phone)) {
            throw new IllegalArgumentException("电话号码格式不正确");
        }

        // 检查手机号是否重复
        User existUser = userMapper.selectByPhone(phone); // 调用接口方法
        if (existUser != null) {
            throw new BusinessException("该号码已被注册");
        }

        User newUser = new User();
        newUser.setPhoneNum(phone);

        String nickname = generateByUUID();
        newUser.setNickname(nickname);
        // 实际开发中密码必须加密，不能存明文！
        // newUser.setPassword(passwordEncoder.encode(password));
        newUser.setPassword(password);
        userMapper.insert(newUser);
        System.out.println("注册成功，新用户ID是: " + newUser.getId());
    }

    public void registerByEmail(String email, String password) {
        if (email == null || (email = email.trim()).isEmpty()) {
            throw new IllegalArgumentException("邮箱不能为空");
        }

        String emailRegex = "^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z0-9]{2,6}$";
        if (!java.util.regex.Pattern.matches(emailRegex, email)) {
            throw new IllegalArgumentException("邮箱格式不正确");
        }

        // 检查邮箱是否重复
        User existUser = userMapper.selectByEmail(email); // 调用接口方法
        if (existUser != null) {
            throw new BusinessException("该邮箱已被注册");
        }

        // 没有重复，就开始创建一个新的user
        User newUser = new User();
        String nickname = generateByUUID();
        newUser.setEmail(email);
        newUser.setNickname(nickname);
        // 实际开发中密码必须加密，不能存明文！
        // newUser.setPassword(passwordEncoder.encode(password));
        newUser.setPassword(password);
        userMapper.insert(newUser);
        // 因为配置了 keyProperty="id"，执行完上面这行后，
        // newUser.getId() 就会自动变成数据库生成的 ID (比如 1001)
        System.out.println("注册成功，新用户ID是: " + newUser.getId());
    }

    public static String generateByUUID() {
        // 截取 UUID 的前8位，足够随机且不长
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        return "User_" + uuid.substring(0, 8);
    }

    public User updateAvatar(Long userId, String newAvatarUrl) {
        if (newAvatarUrl == null || (newAvatarUrl = newAvatarUrl.trim()).isEmpty()) {
            throw new IllegalArgumentException("头像URL不能为空");
        }

        int rowsAffected = userMapper.updateAvatarById(userId, newAvatarUrl);
        if (rowsAffected == 0) {
            throw new BusinessException("头像更新失败，用户可能不存在");
        }

        System.out.println("头像更新成功，新的头像URL是: " + newAvatarUrl);
        User user = userMapper.selectById(userId);
        user.setPassword(null); // 确保不返回密码
        return user;
    }

    @Transactional(rollbackFor = Exception.class) // 开启事务：报错回滚
    public User updateUserInfo(Long userId, UserUpdateRequest req) {

        // 1. 唯一性检查 (Uniqueness Check)
        //检查昵称
        if (req.getNickname() != null) {
            User exist = userMapper.selectByNickname(req.getNickname());
            // 如果查到了人，且这个人的ID不是当前用户，说明被别人占用了
            if (exist != null && !exist.getId().equals(userId)) {
                throw new BusinessException("该昵称已被使用");
            }
        }

        // 检查手机号
        if (req.getPhoneNum() != null) {
            User exist = userMapper.selectByPhone(req.getPhoneNum());
            if (exist != null && !exist.getId().equals(userId)) {
                throw new BusinessException("该手机号已被注册");
            }
        }

        // 检查邮箱
        if (req.getEmail() != null) {
            User exist = userMapper.selectByEmail(req.getEmail());
            if (exist != null && !exist.getId().equals(userId)) {
                throw new BusinessException("该邮箱已被注册");
            }
        }

        // 2. 组装 Pojo 对象进行更新
        User updateEntity = new User();
        updateEntity.setId(userId);
        updateEntity.setNickname(req.getNickname());
        updateEntity.setPhoneNum(req.getPhoneNum());
        updateEntity.setEmail(req.getEmail());
        updateEntity.setBio(req.getBio());

        // 3. 执行动态 SQL 更新
        int rows = userMapper.updateDynamic(updateEntity);
        if (rows == 0) {
            throw new BusinessException("更新失败，可能是参数全为空或用户不存在");
        }

        // 4. 返回最新的用户信息
        User latestUser = userMapper.selectById(userId);
        latestUser.setPassword(null);
        return latestUser;
    }

    public User updatePassword(Long userId,String oldPassword,String newPassword) {
        if (newPassword == null || newPassword.isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (!user.getPassword().equals(oldPassword)) {
            throw new IllegalArgumentException("旧密码不正确");
        }

        int rowsAffected = userMapper.updatePasswordById(userId, newPassword);
        if (rowsAffected == 0) {
            throw new BusinessException("密码更新失败，用户可能不存在");
        }

        System.out.println("密码更新成功");
        User newUser = userMapper.selectById(userId);
        newUser.setPassword(null); // 确保不返回密码
        return newUser;
    }

    //暂时还没用到
    public User getUserById(Long userId){
        User user = userMapper.selectById(userId);
        if(user == null){
            throw new BusinessException("用户不存在");
        }
        user.setPassword(null);
        return user;
    }

    public List<User> getUserByNickname(String nickname){
        List<User> users = userMapper.selectByNicknameLike(nickname);
        if(users == null){
            throw new BusinessException("用户不存在");
        }
        return users;
    }
}
