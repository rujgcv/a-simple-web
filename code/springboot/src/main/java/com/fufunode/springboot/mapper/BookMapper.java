package com.fufunode.springboot.mapper;

import com.fufunode.springboot.entity.Book;
import org.apache.ibatis.annotations.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Mapper
public interface BookMapper {
    // 分页查询 + 模糊查询
    List<Book> selectPage(@Param("name") String name,@Param("writer") String writer,@Param("type") String type,@Param("order") String order);

    // 查询当前条数
    Integer selectTotal(@Param("name") String name,@Param("writer") String writer,@Param("type") String type);

    // 书删除
    @Delete("""
            delete from t_book where id = #{id}
            """)
    Integer delBook(@Param("id") Integer id);

    // 批量删除书
    Integer delBookBatch(List<Integer> ids);

    // 新增小说
    @Insert("""
        insert into t_book(name,writer,description,type,img_url) values(#{name},#{writer},#{description},#{type},#{imgUrl}) 
    """)
    Integer save(Book book);

    // 修改小说
    @Update("""
        update t_book set name = #{name},writer = #{writer},type = #{type},description = #{description} where id = #{id}
    """)
    Integer update(Book book);

    // 通过名字查询小说
    @Select("""
        select * from t_book where name = #{name}
    """)
    Book getBookByName(String name);

    // 添加封面
    @Update("""
        update t_book set img_url = #{imgUrl} where id = #{id}
    """)
    Integer updateImg(@Param("imgUrl") String imgUrl,@Param("id") Integer id);

    // 是否已有头像
    @Select("""
        select img_url from t_book where id = #{id}
    """)
    String haveImg(Integer id);

    // 根据单个id查找小说
    @Select("""
        select b.id,b.name,b.writer,b.create_time,b.img_url,b.description,b.type,ifnull(r.`read`,0) `read`,ifnull(l.`like`,0) `like`,ifnull(g.grade,0) grade
        from (select * from t_book where id = #{id}) b
        left join (select book_id,count(`read`) `read` from t_userview where `read` != 0 group by book_id) r
        on r.book_id = b.id
        left join (select book_id,count(`like`) `like` from t_userview where `like` != 0 group by book_id) l
        on l.book_id = b.id
        left join (select book_id,avg(grade) grade from t_userview where `grade` != 0 group by book_id) g
        on g.book_id = b.id
    """)
    Book selectBookById(Integer id);

    // 查询小说信息(首页)
    List<Integer> selectBookInMainPage(@Param("type") String type,@Param("size") Integer size,@Param("order") String order);

    // 查询 热门小说 信息(首页)
    List<Integer> selectPopularBook(@Param("startTime") String startTime,@Param("endTime") String endTime,@Param("type") String type,@Param("size") Integer size);

    // 小说种类
    @Select("""
        select type from t_booktype
    """)
    List<String> getAllType();

    // 最新小说
    @Select("""
        select id from t_book order by create_time desc limit #{size}
    """)
    List<Integer> selectNewBook(Integer size);

    // 查询一连串小说
    List<Book> getBookList(List<Integer> idList);

    // 查询排行榜小说(改写太麻烦了，不如重写)
    List<Book> selectRankPage(@Param("type") String type,@Param("startTime") String startTime,@Param("endTime") String endTime);

    Integer selectRankTotal(@Param("type") String type,@Param("startTime") String startTime,@Param("endTime") String endTime);

    List<Book> findBook(@Param("name") String name,@Param("writer") String writer,@Param("order") String order);
    Integer findBookTotal(@Param("name") String name,@Param("writer") String writer);

    @Select("""
        select b.id,b.name,b.img_url
        from t_book b
        join
            (
                select book_id id
                from t_userview
                where user_id = #{userId} and `like` = 1
                ) u
        on b.id = u.id
    """)
    List<Book> getLikeBook(@Param("userId") Integer userId);

    @Select("""
        select count(b.id) 
        from t_book b 
        join 
            (
            select book_id id 
            from t_userview 
            where user_id = #{userId} and `like` = 1
            ) u 
        on b.id = u.id
    """)
    Integer getLikeBookTotal(@Param("userId") Integer userId);

    @Update("""
    update t_userview set `like` = 0 where book_id = #{bid} and user_id = #{uid}
    """)
    Integer cancelLike(@Param("bid") Integer bid,@Param("uid") Integer uid);

    @Select("""
        select count(grade) from t_userview where book_id = #{bookId} and grade != 0;
    """)
    Integer getTotalGrade(@Param("bookId") Integer bookId);

    @Select("""
        select grade,`read`,`like` from t_userview where book_id = #{bid} and user_id = #{uid}
    """)
    Map<String,Integer> getMore(@Param("bid") Integer bid,@Param("uid") Integer uid);

    @Insert("""
        insert into t_userview(id,book_id,user_id) values(null,#{bid},#{uid});
    """)
    Integer addUserView(@Param("bid") Integer bid,@Param("uid") Integer uid);

    @Update("""
        update t_userview set `read` = #{read}, `like` = #{like}, `grade` = #{grade} where book_id = #{bid} and user_id = #{uid}
    """)
    Integer updateUserView(@Param("bid") Integer bid,@Param("uid") Integer uid,
                           @Param("read") Integer read,@Param("like") Integer like,@Param("grade") Integer grade);

    @Delete("""
        delete from t_userview where `like` = 0 and `read` = 0 and `grade` = 0
    """)
    void delUserView();

    @Insert("""
        insert into t_booktxt(book_id,user_id,url) values(#{bookId},#{userId},#{url})
    """)
    Integer uploadBook(@Param("url") String path,@Param("bookId") Integer bookId,@Param("userId") Integer userId);

    @Select("""
    select b.name book_name,u.name user_name,u.avatar_url,t.create_time, t.url 
    from (select * from t_booktxt where book_id = #{id}) t 
    join t_book b 
    on t.book_id = b.id 
    join t_user u 
    on t.user_id = u.id
    """)
    List<Map<String,Object>> getTXT(Integer id);

    @Select("""
        select b.name
        from (select book_id from t_booktxt where url = #{url}) t 
        join t_book b 
        on t.book_id = b.id 
    """)
    String getNameByTXT(String url);

    List<Map<String,Object>> getAllTxt(@Param("name") String name,@Param("writer") String writer,@Param("type") String type);

    Integer getAllTxtTotal(@Param("name") String name,@Param("writer") String writer,@Param("type") String type);

    @Delete("""
        delete from t_booktxt where id = #{id}
    """)
    Integer delTxt(Integer id);

    @Select("""
        select url from t_booktxt where id = #{id}
    """)
    String getTxtById(Integer id);

    Integer delTXTBatch(List<Integer> ids);

    @Select("""
        select name from t_book where id = #{id}
    """)
    String getNameById(Integer id);
}
