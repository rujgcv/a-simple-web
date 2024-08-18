package com.fufunode.springboot.mapper;

import com.fufunode.springboot.entity.Message;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MessageMapper {

    @Insert("""
        insert into t_message(from_id,to_id,title,message) values(#{fromId},#{toId},#{title},#{message})
    """)
    Integer sentMessage(Message message);

    @Select("""
        select * from t_message where to_id = #{toId} order by create_time desc
    """)
    List<Message> selectById(Integer toId);

    @Select("""
        select count(id) from t_message where to_id = #{toId}
    """)
    Integer getTotal(Integer toId);

    @Update("""
        update t_message set `read` = 1 where to_id = #{toId}
    """)
    Integer updateRead(Integer toId);

    @Delete("""
        delete from t_message where create_time < #{endTime} or create_time = #{endTime}
    """)
    Integer delDatedMessage(String endTime);

    @Select("""
        select count(id) from t_message where to_id = #{toId} and  `read` = 0
    """)
    Integer hasNoRead(Integer toId);
}
