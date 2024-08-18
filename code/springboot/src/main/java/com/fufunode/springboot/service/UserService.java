package com.fufunode.springboot.service;

import com.fufunode.springboot.entity.User;
import com.fufunode.springboot.entity.UserDTO;
import com.fufunode.springboot.utils.ReturnUtil;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface UserService {
    // 查询所有用户
    List<User> getAllUsers();

    // 新增用户
    Integer save(User user);

    // 修改用户信息
    Integer modifyUser(User user);

    // 删除用户信息
    Integer delUser(Integer id);

    // 用户分页查询 查询用户总条数
    Map<String, Object> selectPage(Integer pageNum, Integer pageSize, String name, boolean isAdmin);

    // 批量删除
    Integer delUserBatch(List<Integer> ids);

    // 根据电话查询用户信息
    User getUserByPhone(String phone);

    // 上传头像
    String upload(MultipartFile file,Integer id);

    // 是否有头像 如有，返回头像的路径
    String haveAvatar(Integer id);

    // 登录
    ReturnUtil login(UserDTO user);

    // 注册
    Integer register(UserDTO user);

    // 通过名字获取用户
    User getUserByName(String name);

    // 修改密码
    Integer updatePwd(Map<String,Object> passwords);

    // 修改用户
    Integer updateUser(User user);

    // 登陆成功
    String loginSuccess(User user, boolean rememberPwd);
}
