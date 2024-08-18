package com.fufunode.springboot.mapper;

import com.fufunode.springboot.entity.Book;
import com.fufunode.springboot.entity.Review;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface ReviewMapper {

    // 获取当前页数的根id
    @Select("""
        select * 
        from (select * from t_reviews where is_del = 0 and book_id = #{bookId} and type = 0 limit ${currentPage},${pageSize} ) r 
        left join (select id user_id,name username,avatar_url from t_user) u 
        on  r.user_id = u.user_id
        order by create_time asc
    """)
    List<Review> getReviewByBookId(@Param("bookId") Integer bookId,@Param("currentPage") Integer currentPage,@Param("pageSize") Integer pageSize);

    List<Review> getReviewByBookIdChildren(List<Long> ids);

    @Select("""
        select count(id) 
        from (select * from t_reviews where is_del = 0 and book_id = #{bookId}) r 
        join (select id user_id,name username,avatar_url from t_user) u 
        on  r.user_id = u.user_id
    """)
    Integer getReviewByBookIdTotal(Integer bookId);

    @Select("""
        select * 
        from (select * from t_reviews where is_del = 0 and id = #{id}) r 
        join (select id user_id,name username,avatar_url from t_user) u 
        on  r.user_id = u.user_id
        order by create_time desc
    """)
    Review getSingleReviewByReviewId(Long id);

    @Insert("""
        insert into t_reviews(type,content,user_id,book_id,parent_id,root_id) values(#{type},#{content},#{userId},#{bookId},#{parentId},#{rootId})
    """)
    Integer addReview(Review review);

    List<Review> selectPage(@Param("username") String username,@Param("bookname") String bookname,@Param("keyword") String keyword);

    Integer selectTotal(@Param("username") String username,@Param("bookname") String bookname,@Param("keyword") String keyword);

    @Update("""
        update t_reviews set is_del = 1 where id = #{id}
    """)
    Integer delReviewById(Long id);

    List<Review> getReviews(List<Long> ids);

    Integer delReviews(List<Long> ids);

    Integer delReviewsByBookId(List<Integer> ids);
}
