package com.fufunode.springboot.service.impl;

import com.fufunode.springboot.entity.Book;
import com.fufunode.springboot.entity.Message;
import com.fufunode.springboot.entity.Review;
import com.fufunode.springboot.mapper.BookMapper;
import com.fufunode.springboot.mapper.MessageMapper;
import com.fufunode.springboot.mapper.ReviewMapper;
import com.fufunode.springboot.mapper.UserMapper;
import com.fufunode.springboot.service.BookService;
import com.fufunode.springboot.service.ReviewService;
import com.fufunode.springboot.utils.ReturnUtil;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewMapper reviewMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private BookMapper bookMapper;


    // 通过 书本id 获取所有的有关评论
    // 感谢伟大的网友TvT
    @Override
    public Map<String,Object> getReviewByBookId(Integer bookId,Integer pageNum,Integer pageSize) {
        Integer currentPage = (pageNum - 1)*pageSize;
        Map<String,Object> res = new HashMap<>();
        List<Review> initialList = reviewMapper.getReviewByBookId(bookId,currentPage,pageSize);      //查询小说根评论id

        if(initialList.size() == 0){
            res.put("data",initialList);
            res.put("total",0);
            return res;
        }

        List<Long> ids = new ArrayList<>();
        for(Review review : initialList){
            ids.add(review.getId());
        }

        List<Review> item = reviewMapper.getReviewByBookIdChildren(ids);
        initialList.addAll(item);
        Integer total= reviewMapper.getReviewByBookIdTotal(bookId);
        res.put("total",total);
        List<Review> resultMap = new ArrayList<>();        //定义返回评论结果，重新封装返回结果

        /** 第一次循环，将根节点拿出来 **/
        for (Review review : initialList){
            Integer type = review.getType();
            if (0 == type){
                resultMap.add(review);
            }
        }
        /** 第二次循环，将不是根节点 根据root_id(根节点id)归类 **/
        for (Review review : initialList){
            if (review.getType() != 0){
                /** 不是根节点的数据，对根节点数据进行遍历，然后跟非根节点数据的rootId进行匹配，符合添加到该child节点中 **/
                for (Review rootReview:resultMap){
                    if (rootReview.getId().equals(review.getRootId())){
                        List<Review> list = null;
                        if(rootReview.getChild() != null){
                            list = rootReview.getChild();
                        }else{
                            list = new ArrayList<>();
                        }
                        list.add(review);
                        rootReview.setChild(list);
                        break;
                    }
                }
            }
        }

        res.put("data",resultMap);

        return res;
    }

    @Override
    @Transactional
    public void addReview(Review review) {
        if(review.getRootId() == -1){
            review.setRootId(null);
        }
        if(review.getParentId() == -1){
            review.setParentId(null);
        }

        reviewMapper.addReview(review); // 插入评论

        if(review.getType() != 0){
            // 向被回复的人发送信息
            Message message = new Message();
            message.setFromId(review.getUserId());

            Review r = reviewMapper.getSingleReviewByReviewId(review.getParentId());
            message.setToId(r.getUserId());

            message.setTitle(review.getUsername() + " 回复了我的评论");
            String mes = review.getContent() + "\n" + "我在 <a href=\"http://localhost:5173/book?id=" + review.getBookId() + "\">" + bookMapper.getNameById(review.getBookId()) + "</a> 的评论: " + r.getContent();
            message.setMessage(mes);
            messageMapper.sentMessage(message);
        }
    }

    @Override
    public Map<String, Object> findPage(Integer pageNum, Integer pageSize, String username, String bookname, String keyword) {
        PageHelper.startPage(pageNum,pageSize);
        List<Review> data = reviewMapper.selectPage(username,bookname,keyword);
        Integer total = reviewMapper.selectTotal(username,bookname,keyword);
        Map<String, Object> res = new HashMap<>();
        res.put("data",data);
        res.put("total",total);
        return res;
    }

    @Override
    @Transactional
    public Integer delReview(Review review) {
        if(review.getType() == 0){ // 删父先删子
            List<Long> ids = new ArrayList<>();
            ids.add(review.getId());
            List<Review> reviews = reviewMapper.getReviewByBookIdChildren(ids);

            List<Long> cids = new ArrayList<>();
            for(Review r : reviews){
                cids.add(r.getId());
                // 直接用下面的批量删除
                delReviews(cids);
            }
        }else {
            Message message = new Message();
            message.setFromId(0);

            message.setToId(review.getUserId());

            message.setTitle("系统通知:您的评论已被删除");
            String mes = "因为言论不当\n" + "您在 <a href=\"http://localhost:5173/book?id=" + review.getBookId() + "\">" + bookMapper.getNameById(review.getBookId()) + "</a> 的评论: " + review.getContent() + "\n已被管理员删除";
            message.setMessage(mes);
            messageMapper.sentMessage(message);
        }
        return reviewMapper.delReviewById(review.getId());
    }

    @Override
    @Transactional
    public Integer delReviews(List<Long> ids) {
        List<Review> reviews = reviewMapper.getReviews(ids);

        Message message = new Message();
        for(Review review : reviews){
            if (review.getType() == 0) {
                List<Long> ids1 = new ArrayList<>();
                ids1.add(review.getId());
                List<Review> rs = reviewMapper.getReviewByBookIdChildren(ids1);

                List<Long> cids = new ArrayList<>();
                if(!rs.isEmpty()){
                    for(Review r : rs){
                        cids.add(r.getId());

                        message.setFromId(0);

                        message.setToId(review.getUserId());

                        message.setTitle("系统通知:您的评论已被删除");
                        String mes = "因为言论不当\n" + "您在 <a href=\"http://localhost:5173/book?id=" + review.getBookId() + "\">" + bookMapper.getNameById(review.getBookId()) + "</a> 的评论: " + review.getContent() + "\n已被管理员删除";
                        message.setMessage(mes);
                        messageMapper.sentMessage(message);
                    }
                    reviewMapper.delReviews(cids);
                }
            }else{
                message.setFromId(0);

                message.setToId(review.getUserId());

                message.setTitle("系统通知:您的评论已被删除");
                String mes = "因为言论不当\n" + "您在 <a href=\"http://localhost:5173/book?id=" + review.getBookId() + "\">" + bookMapper.getNameById(review.getBookId()) + "</a> 的评论: " + review.getContent() + "\n已被管理员删除";
                message.setMessage(mes);
                messageMapper.sentMessage(message);
            }
        }
        return reviewMapper.delReviews(ids);
    }
}
