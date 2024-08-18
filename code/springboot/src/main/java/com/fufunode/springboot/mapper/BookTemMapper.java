package com.fufunode.springboot.mapper;

import com.fufunode.springboot.entity.Book;
import com.fufunode.springboot.entity.BookTemporary;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BookTemMapper {
    @Insert("""
        insert into t_book_tem(name,writer,description,type,uid) values(#{name},#{writer},#{description},#{type},#{uid}) 
    """)
    Integer addBookTemporary(BookTemporary bookTem);

    @Select("""
        select * from t_book_tem where name = #{name}
    """)
    BookTemporary getBookByName(String name);

    // 添加封面
    @Update("""
        update t_book_tem set img_url = #{imgUrl} where id = #{id}
    """)
    Integer updateImg(@Param("imgUrl") String imgUrl, @Param("id") Integer id);

    // 是否已有头像
    @Select("""
        select img_url from t_book_tem where id = #{id}
    """)
    String haveImg(Integer id);

    @Select("""
        select * from t_book_tem
    """)
    List<Book> selectPage();

    @Select("""
        select count(id) from t_book_tem
    """)
    Integer selectTotal();

    @Delete("""
        delete from t_book_tem where id = #{id}
    """)
    Integer delBookTem(Integer id);

    @Select("""
        select * from t_book_tem where id = #{id}
    """)
    BookTemporary getBookById(Integer id);
}
