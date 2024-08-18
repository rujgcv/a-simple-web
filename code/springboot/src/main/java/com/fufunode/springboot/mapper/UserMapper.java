package com.fufunode.springboot.mapper;

import com.fufunode.springboot.entity.User;
import com.fufunode.springboot.entity.UserDTO;
import org.apache.ibatis.annotations.*;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface UserMapper {

    // 查询所有用户
    @Select("""
            select * from t_user
            """)
    List<User> selectAll();

    // 新增用户
    @Insert("""
            insert into t_user(name,password,phone,email,is_admin) 
            values(#{name},#{password},#{phone},#{email},#{isAdmin})
            """)
    int insertUser(User user);

    // 修改用户信息
    int updateUser(User user);

    // 用户删除
    @Delete("""
            delete from t_user where id = #{id}
            """)
    Integer delUser(@Param("id") Integer id);

    // 分页查询 + 模糊查询
//    List<User> selectPage(@Param("pageNum") Integer pageNum,@Param("pageSize") Integer pageSize,@Param("name") String name);
    List<User> selectPage(@Param("name") String name,@Param("isAdmin") boolean isAdmin);

    // 查询用户总条数
    Integer selectTotal(@Param("name") String name,@Param("isAdmin") boolean isAdmin);

    // 添加头像
    @Update("""
        update t_user set avatar_url = #{avatarUrl} where id = #{id}
    """)
    Integer updateAvatar(@Param("avatarUrl") String avatarUrl,@Param("id") Integer id);

    // 是否已有头像
    @Select("""
        select avatar_url from t_user where id = #{id}
    """)
    String haveAvatar(Integer id);

    // 通过名字查询用户信息
    @Select("""
        select * from t_user where name = #{name}
    """)
    User getUserByName(String name);

    // 通过电话查询用户
    @Select("""
        select * from t_user where phone = #{phone}
    """)
    User getUserByPhone(String phone);

    // 批量删除用户
    Integer delUserBatch(List<Integer> ids);

    // 登录验证 + 验证密码
    @Select("""
        select * from t_user where name = #{name} or phone = #{name}
    """)
    User testPassword(String name);

    // 验证是否是管理员
    @Select("""
        select is_admin from t_user where id = #{id}
    """)
    Integer testAdminById(Integer id);

    // 注册
    @Insert("""
            insert into t_user(name,password,phone,email,is_admin) 
            values(#{name},#{password},#{phone},#{email},#{isAdmin})
            """)
    int register(UserDTO user);

    // 根据id查找密码
    @Select("""
        select password from t_user where id = #{id}
    """)
    String getPwdById(Integer id);

    // 修改密码
    @Update("""
        update t_user set password = #{password} where id = #{id}
    """)
    Integer updatePwd(@Param("id") Integer id,@Param("password") String password);

    @Select("""
        select name from t_user where id = #{id}
    """)
    String getNameById(Integer id);
}
