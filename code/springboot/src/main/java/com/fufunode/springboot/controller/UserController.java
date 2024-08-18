package com.fufunode.springboot.controller;

import com.fufunode.springboot.entity.User;
import com.fufunode.springboot.entity.UserDTO;
import com.fufunode.springboot.mapper.UserMapper;
import com.fufunode.springboot.service.UserService;
import com.fufunode.springboot.utils.JwtUtil;
import com.fufunode.springboot.utils.ReturnUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    // 新增用户信息
    @PostMapping("/add")
    public ReturnUtil save(@RequestBody User user){
        int m = userService.save(user);
        if(m == -1){
            return new ReturnUtil(true,false,"用户名已存在",null);
        }else if(m == -2){
            return new ReturnUtil(true,false,"手机号已被使用",null);
        }else {
            return new ReturnUtil(true, true, "注册成功！", null);
        }
    }

    // 修改用户信息(从系统)
    @PostMapping("/update")
    public ReturnUtil modify(@RequestBody User user,@RequestParam boolean isAdmin){
        user.setAdmin(isAdmin);
        int m = userService.modifyUser(user);
        if (m == -1) {
            return new ReturnUtil(true, false, "用户已存在！", null);
        }else if(m == -2){
            return new ReturnUtil(true,false,"手机号已被使用",null);
        } else {
            return new ReturnUtil(true, true, "成功！", null);
        }
    }

    // 查询所有用户信息
    @GetMapping("/all")
    public List<User> getUser(){
        return userService.getAllUsers();
    }

    // 删除用户
    @PostMapping("/del")
    public ReturnUtil delUser(@RequestBody User user){
        userService.delUser(user.getId());
        return new ReturnUtil(true, true, "删除成功！", null);
    }

    // 批量删除用户
    @PostMapping("/del/batch")
    public ReturnUtil delUsers(@RequestBody List<Integer> ids){
        int m = userService.delUserBatch(ids);
        if(m == -1){
            return new ReturnUtil(true, false, "请选择删除的对象！", null);
        }else {
            return new ReturnUtil(true, true, "批量删除成功！", null);
        }
    }

    // 分页查询
    // @RequestParam接收 ?pageNum=1&pageSize=10
    @GetMapping("/page")
    public Map<String, Object> findPage(@RequestParam Integer pageNum,@RequestParam Integer pageSize,@RequestParam String name,@RequestParam boolean isAdmin) {
        return userService.selectPage(pageNum,pageSize,name,isAdmin);
    }

    // 上传头像
    @PostMapping("/upload")
    public String upload(@RequestParam MultipartFile file,@RequestParam Integer id){
        return userService.upload(file,id);
    }

    // 登录
    @PostMapping("/login")
    public ReturnUtil login(@RequestBody UserDTO user){
        ReturnUtil r = userService.login(user);
        return r;
    }

    // 注册
    @PostMapping("/register")
    public ReturnUtil register(@RequestBody UserDTO user){
        int m = userService.register(user);
        if(m == -1){
            return new ReturnUtil(true,false,"用户名已存在",null);
        }else if(m == -2){
            return new ReturnUtil(true,false,"手机号已被使用",null);
        }else {
            return new ReturnUtil(true, true, "注册成功！", null);
        }
    }

    // 获取当前用户信息
    @GetMapping("/userInfo")
    public ReturnUtil userInfo(@RequestHeader(name = "Authorization")String token){
        Map<String, Object> map = JwtUtil.parseToken(token);
        String name = (String) map.get("username");

        User u = userService.getUserByName(name);
        if(u == null){
            return new ReturnUtil(true,false,"未知错误",null);
        }
        return new ReturnUtil(true,true,"",u);
    }

    // 修改用户密码
    @PostMapping("/updatePwd")
    public ReturnUtil updatePwd(@RequestBody Map<String,Object> passwords){
        int m = userService.updatePwd(passwords);
        if(m == -1){
            return new ReturnUtil(true,false,"原密码不一致",null);
        }else if(m == -2){
            return new ReturnUtil(true,false,"未知错误",null);
        }else {
            return new ReturnUtil(true,true,"修改密码成功!",null);
        }
    }

    // 修改用户信息(用户手动)
    @PostMapping("/update/person")
    public ReturnUtil modify(@RequestBody User user){
        int m = userService.updateUser(user);
        if (m == -1) {
            return new ReturnUtil(true, false, "用户已存在！", null);
        }else if(m == -2){
            return new ReturnUtil(true,false,"手机号已被使用",null);
        } else {
            return new ReturnUtil(true, true, "修改成功！", userService.loginSuccess(user,false));
        }
    }

}
