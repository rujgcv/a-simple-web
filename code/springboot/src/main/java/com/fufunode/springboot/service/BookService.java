package com.fufunode.springboot.service;

import com.fufunode.springboot.entity.Book;
import com.fufunode.springboot.entity.BookTemporary;
import com.fufunode.springboot.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface BookService {
    // 小说分页查询 查询用户总条数
    Map<String, Object> selectPage(Integer pageNum, Integer pageSize, String name, String writer, String type,String order);

    // 删除小说信息
    Integer delBook(Integer id);

    // 新增小说
    Integer save(Book book);

    // 修改小说
    Integer modifyBook(Book book);

    // 批量删除
    Integer delBookBatch(List<Integer> ids);

    // 根据名字查小说Id
    Integer getIdByName(String name);

    // 上传封面
    String upload(MultipartFile file, Integer id);

    // 是否有封面 如有，返回封面的路径
    String haveImg(Integer id);

    // 获取首页信息
    Map<String, Object> getMainData();

    // 获取单本小说信息
    Book getSingleBook(Integer id);

    // 获取一串小说信息
    List<Book> getBookList(List<Integer> idList);

    // 获取热门小说
    List<Book> getPopularBook();

    // 获取排行榜小说
    Map<String, Object> getRankBook(Integer pageNum, Integer pageSize, String type, String date);

    // 查找小说
    Map<String, Object> findBook(Integer pageNum, Integer pageSize, String name, String writer, String order);

    // 根据用户id搜索收藏小说
    Map<String, Object> getLikeBook(Integer pageNum, Integer pageSize, Integer userId);

    // 取消收藏
    Integer cancelLike(Integer bid,Integer uid);

    // 获取 小说总收藏 和用户的 评分、是否看过、是否收藏
    Map<String, Object> getMore(Integer bid,Integer uid);

    // 修改用户的 评分、是否看过、是否收藏
    Integer updateUserView(Integer bid, Integer uid, Integer read, Integer like, Integer grade);

    // 删除无用的记录
    void delUserView();

    // 上传txt文件
    Integer uploadBook(MultipartFile file, Integer bookId,Integer userId);

    // 根据书本id查询txt信息
    List<Map<String,Object>> getTXT(Integer id);

    // 下载txt到本地
    void download(HttpServletResponse response, String url,boolean isOnLine);

    // 获取所有txt信息
    Map<String, Object> getAllTxt(Integer pageNum, Integer pageSize, String name, String writer, String type);

    // 删除txt文件
    Integer delTxt(@RequestParam Integer id);

    // 批量删除txt
    Integer delTXTs(@RequestBody List<Integer> ids);
}
