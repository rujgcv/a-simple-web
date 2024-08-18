package com.fufunode.springboot.controller;

import com.fufunode.springboot.entity.Review;
import com.fufunode.springboot.service.ReviewService;
import com.fufunode.springboot.utils.ReturnUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    //通过 书本id 获取所有的有关评论
    @GetMapping("/getReview")
    public Map<String,Object> getReview(@RequestParam Integer bookId,@RequestParam Integer pageNum, @RequestParam Integer pageSize){
        return reviewService.getReviewByBookId(bookId,pageNum,pageSize);
    }

    // 增加评论
    @PostMapping("/addReview")
    public void addReview(@RequestBody Review review){
        reviewService.addReview(review);
    }

    // 后端获取详细评论
    @GetMapping("/page")
    public Map<String,Object> findPage(@RequestParam Integer pageNum, @RequestParam Integer pageSize,@RequestParam String username,@RequestParam String bookname,@RequestParam String keyword){
        return reviewService.findPage(pageNum,pageSize,username,bookname,keyword);
    }

    // 单条删除评论
    @PostMapping("/del")
    public ReturnUtil delReview(@RequestBody Review review){
        int m = reviewService.delReview(review);
        if(m == 1){
            return new ReturnUtil(true, true, "删除评论成功!", null);
        }else {
            return new ReturnUtil(true, false, "删除评论失败!", null);
        }
    }

    // 批量删除评论
    @PostMapping("/del/batch")
    public ReturnUtil delReviews(@RequestBody List<Long> ids){
        int m = reviewService.delReviews(ids);
        if(m != -1){
            return new ReturnUtil(true, true, "批量删除评论成功!", null);
        }else {
            return new ReturnUtil(true, false, "批量删除评论失败!", null);
        }
    }
}
