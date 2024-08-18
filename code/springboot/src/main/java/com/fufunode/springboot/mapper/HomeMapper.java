package com.fufunode.springboot.mapper;

import com.fufunode.springboot.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface HomeMapper {

    // 通过id获得管理员信息
    @Select("""
        select * from t_user where id = #{id}
    """)
    User getAdminById(Integer id);

    // 小说总数
    @Select("""
        select count(id) from t_book
    """)
    Integer getBookSum();

    // 用户总数
    @Select("""
        select count(id) from t_user
    """)
    Integer getUserSum();

    // 今日新增小说
    @Select("""
        select count(id) from t_book where create_time between #{startTime} and #{endTime}
    """)
    Integer getBookToday(@Param("startTime") String startTime,@Param("endTime") String endTime);

    // 图表信息
    @Select("""
        select t.type name,ifnull(b.number,0) value
        from t_booktype t
        left join (select type,count(id) number from t_book group by type) b
        on t.type = b.type
    """)
    List<Map<String,Integer>> getChartData();
}
