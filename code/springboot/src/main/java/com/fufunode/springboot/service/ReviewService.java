package com.fufunode.springboot.service;

import com.fufunode.springboot.entity.Review;
import com.fufunode.springboot.utils.ReturnUtil;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

public interface ReviewService {
    //通过 书本id 获取所有的有关评论
    Map<String,Object> getReviewByBookId(Integer bookId,Integer pageNum,Integer pageSize);

    // 增加评论
     void addReview(Review review);

    // 后端获取详细评论
    Map<String,Object> findPage(Integer pageNum, Integer pageSize, String username, String bookname, String keyword);

    // 单条删除评论
    Integer delReview(@RequestBody Review review);

    // 批量删除评论
    Integer delReviews(List<Long> ids);
}
