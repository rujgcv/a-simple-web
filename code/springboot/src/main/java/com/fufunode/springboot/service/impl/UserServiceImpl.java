package com.fufunode.springboot.service.impl;

import com.fufunode.springboot.entity.Review;
import com.fufunode.springboot.entity.User;
import com.fufunode.springboot.entity.UserDTO;
import com.fufunode.springboot.mapper.UserMapper;
import com.fufunode.springboot.service.UserService;
import com.fufunode.springboot.utils.JwtUtil;
import com.fufunode.springboot.utils.Md5Util;
import com.fufunode.springboot.utils.ReturnUtil;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<User> getAllUsers() {
        return userMapper.selectAll();
    }

    @Transactional
    @Override
    public Integer save(User user) {
        user.setPassword(Md5Util.md5(user.getPassword()));
        User u= userMapper.getUserByName(user.getName());
        if (u !=null) return -1;

        u= userMapper.getUserByPhone(user.getPhone());
        if (u !=null) return -2;
        userMapper.insertUser(user);
        return 0;
    }

    @Transactional
    @Override
    public Integer modifyUser(User user) {
        if (user.getId() == -1) {
            String password = Md5Util.md5("888888");
            user.setPassword(password);
            return save(user);
        } else {
            User u1 = userMapper.getUserByName(user.getName());
            if (u1 != null && u1.getId() != user.getId()) return -1;
            User u= userMapper.getUserByPhone(user.getPhone());
            if (u != null && u.getId() != user.getId()) return -2;
            userMapper.updateUser(user);
        }
        return 0;
    }

    @Transactional
    @Override
    public Integer delUser(Integer id) {
        String path = userMapper.haveAvatar(id);
        if (path != null) {
            String pre = "E:\\毕业论文\\code\\springboot\\src\\main\\resources\\static\\img\\book\\";
            String absolutePath = pre + path.substring(path.lastIndexOf("/")+1,path.length());
            File file = new File(absolutePath);
            if(file.exists()){
                boolean deleted = file.delete();
                if (deleted){
                    System.out.println("文件删除成功");
                }else {
                    System.out.println("文件删除失败");
                }
            }
        }
        return userMapper.delUser(id);
    }

    @Override
    public Map<String, Object> selectPage(Integer pageNum, Integer pageSize, String name, boolean isAdmin) {
        PageHelper.startPage(pageNum,pageSize);
//        pageNum = (pageNum - 1) * pageSize;
//        List<User> data = userMapper.selectPage(pageNum, pageSize, name);
        List<User> data = userMapper.selectPage(name,isAdmin);
        Integer total = userMapper.selectTotal(name,isAdmin);
        Map<String, Object> res = new HashMap<>();
        res.put("data",data);
        res.put("total",total);
        return res;
    }

    @Transactional
    @Override
    public Integer delUserBatch(List<Integer> ids) {
        if(ids == null || ids.isEmpty()){
            return -1;
        }
        for(int i=0;i<ids.size();i++){
            String path = userMapper.haveAvatar(ids.get(i));
            if (path != null) {
                String pre = "E:\\毕业论文\\code\\springboot\\src\\main\\resources\\static\\img\\avatar\\";
                String absolutePath = pre + path.substring(path.lastIndexOf("/")+1,path.length());
                File file = new File(absolutePath);
                if(file.exists()){
                    boolean deleted = file.delete();
                    if (deleted){
                        System.out.println("文件删除成功");
                    }else {
                        System.out.println("文件删除失败");
                    }
                }
            }
        }
        return userMapper.delUserBatch(ids);
    }

    @Override
    public User getUserByPhone(String phone) {
        return userMapper.getUserByPhone(phone);
    }

    @Transactional
    @Override
    public String upload(MultipartFile file,Integer id) {
        if (file.isEmpty()){
            return "图片上传失败";
        }
        // 是否有头像
        String path = userMapper.haveAvatar(id);
        boolean hasAvatar = true;

        String savePre = "http://localhost:8080/upload/avatar/";
        String pre = "E:\\毕业论文\\code\\springboot\\src\\main\\resources\\static\\img\\avatar\\";

        if (path == null || path.equals("") || path.length() == 0){
            hasAvatar = false;
            String originalFilename = file.getOriginalFilename();
            String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uuid = UUID.randomUUID().toString().replace("-", "");
            // 文件名
            String fileName = uuid + ext;
            // 上传图片
            path = savePre + fileName;
        }

        // 存在本地的绝对路径
        String absolutePath = pre + path.substring(path.lastIndexOf("/")+1,path.length());

        // 数据库存储图片路径
        System.out.println(path);
        userMapper.updateAvatar(path, id);
        try {
            if(hasAvatar){
                File f = new File(absolutePath);
                if(f.exists()){
                    boolean deleted = f.delete();
                    if (deleted){
                        file.transferTo(new File(absolutePath));
                        System.out.println(path);
                        return path;
                    }
                }
            }else {
                file.transferTo(new File(absolutePath));
                System.out.println(path);
                return path;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "图片上传失败";
    }

    @Override
    public String haveAvatar(Integer id) {
        return userMapper.haveAvatar(id);
    }

    @Override
    public ReturnUtil login(UserDTO user) {
        user.setName(user.getName().trim());
        user.setPassword(user.getPassword().trim());
        if(user.getName() == null || user.getName().length() == 0){
            return new ReturnUtil(true, false, "用户名或手机号不能为空!", null);
        }
        if(user.getPassword() == null || user.getPassword().length() == 0){
            return new ReturnUtil(true, false, "密码不能为空!", null);
        }
        User u = userMapper.testPassword(user.getName());
        if(u == null) {
            return new ReturnUtil(true, false, "用户不存在!", null);
        }
        String password = Md5Util.md5(user.getPassword());
        // 后续加上md5
        if(!password.equals(u.getPassword())){
            return new ReturnUtil(true, false, "密码不正确!", null);
        }
        if(user.isAdmin()){
            Integer admin = userMapper.testAdminById(u.getId());
            if(admin != 1){
                return new ReturnUtil(true, false, "该用户没有管理员权限!", null);
            }
        }
        return new ReturnUtil(true, true, "登录成功!", loginSuccess(u,user.isRememberPwd()));
    }

    public String loginSuccess(User user, boolean rememberPwd){
        Map<String,Object> claims = new HashMap<>();
        claims.put("id",user.getId());
        claims.put("username",user.getName());
        claims.put("admin",user.isAdmin());
        Integer lastTime = 1;
        if(rememberPwd){
            lastTime = 30;
        }
        return JwtUtil.getToken(claims,lastTime);
    }

    @Transactional
    @Override
    public Integer register(UserDTO user) {
        user.setPassword(Md5Util.md5(user.getPassword()));
        User u = userMapper.getUserByName(user.getName());
        if (u != null){
            return -1;
        }
        u = userMapper.getUserByPhone(user.getPhone());
        if(u != null){
            return -2;
        }
        userMapper.register(user);
        return 0;
    }

    @Override
    public User getUserByName(String name) {
        return userMapper.getUserByName(name);
    }

    @Override
    public Integer updatePwd(Map<String, Object> passwords) {
        Integer id = (Integer) passwords.get("id");
        String oldPassword = (String)passwords.get("oldPassword");
        String newPassword = (String)passwords.get("newPassword");
        String rightPassword = userMapper.getPwdById(id);
        System.out.println(!rightPassword.equals(Md5Util.md5(oldPassword)));
        if(!rightPassword.equals(Md5Util.md5(oldPassword))){
            return -1;
        }
        Integer i = userMapper.updatePwd(id,Md5Util.md5(newPassword));
        if (i != 1){
            return -2;
        }
        return 0;
    }

    @Override
    public Integer updateUser(User user) {
        User u = userMapper.getUserByName(user.getName());
        if(u != null && u.getId() != user.getId()) return -1;
        u= userMapper.getUserByPhone(user.getPhone());
        if (u != null && u.getId() != user.getId()) return -2;
        userMapper.updateUser(user);
        return 0;
    }


}
