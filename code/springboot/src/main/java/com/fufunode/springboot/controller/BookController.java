package com.fufunode.springboot.controller;

import com.fufunode.springboot.entity.Book;
import com.fufunode.springboot.entity.BookTemporary;
import com.fufunode.springboot.entity.User;
import com.fufunode.springboot.service.BookService;
import com.fufunode.springboot.utils.ReturnUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/book")
public class BookController {

    @Autowired
    private BookService bookService;

    // 分页查询书籍
    @GetMapping("/page")
    public Map<String, Object> findPage(@RequestParam Integer pageNum, @RequestParam Integer pageSize, @RequestParam String name,@RequestParam String writer, @RequestParam String type,@RequestParam String order) {
        return bookService.selectPage(pageNum,pageSize,name,writer,type,order);
    }

    // 批量删除
    @PostMapping("/del/batch")
    public ReturnUtil delBooks(@RequestBody List<Integer> ids){
        int m = bookService.delBookBatch(ids);
        if(m == -1){
            return new ReturnUtil(true, false, "请选择删除的对象！", null);
        }else {
            return new ReturnUtil(true, true, "批量删除成功！", null);
        }
    }

    // 删除书
    @PostMapping("/del")
    public ReturnUtil delUser(@RequestBody User user){
        bookService.delBook(user.getId());
        return new ReturnUtil(true, true, "删除成功！", null);
    }

    // 添加小说+编辑小说
    @PostMapping("/update")
    public ReturnUtil modify(@RequestBody Book book){
        int m = bookService.modifyBook(book);
        if (m == -1) {
            return new ReturnUtil(true, false, "小说已存在！", null);
        }
        // 已经避免重名
        int id = bookService.getIdByName(book.getName());
        return new ReturnUtil(true, true, "成功!", id);
    }

    // 上传封面
    @PostMapping("/upload")
    public String upload(@RequestParam MultipartFile file, @RequestParam Integer id){
        return bookService.upload(file,id);
    }

    // 获取首页信息
    @GetMapping("/getAll")
    public Map<String, Object> getMainData(){
        return bookService.getMainData();
    }

    // 获取热门小说
    @GetMapping("/getPopular")
    public List<Book> getPopular(){
        return bookService.getPopularBook();
    }

    // 获取单本小说信息
    @GetMapping("/getBook")
    public Book getSingleBook(@RequestParam Integer id){
        return bookService.getSingleBook(id);
    }

    // 获取一串小说信息
    @PostMapping("/getBookList")
    public List<Book> getBookList(@RequestBody List<Integer> idList){
        return bookService.getBookList(idList);
    }

    // 根据种类获取最新小说
//    @GetMapping("/getTypeBook")
//    public List<Book> getTypeBookList(@RequestParam Integer pageNum,@RequestParam String type){
//        return bookService.g
//    }

    // 排行榜小说
    @GetMapping("/rank")
    public Map<String, Object> getRank(@RequestParam Integer pageNum, @RequestParam Integer pageSize, @RequestParam String type, @RequestParam String date) {
        return bookService.getRankBook(pageNum,pageSize,type,date);
    }

    // 搜索小说(书名，作者名混杂)
    @GetMapping("/findBook")
    public Map<String, Object> findBook(@RequestParam Integer pageNum, @RequestParam Integer pageSize, @RequestParam String name,@RequestParam String writer,@RequestParam String order) {
        return bookService.findBook(pageNum,pageSize,name,writer,order);
    }

    // 根据用户id获取用户收藏小说
    @GetMapping("/likebook")
    public Map<String, Object> getLikeBook(@RequestParam Integer pageNum, @RequestParam Integer pageSize, @RequestParam Integer userId) {
        return bookService.getLikeBook(pageNum,pageSize,userId);
    }

    // 用户取消收藏
    @GetMapping("/cancelLike")
    public ReturnUtil cancelLike(@RequestParam Integer bid,@RequestParam Integer uid){
        int m = bookService.cancelLike(bid,uid);
        if (m == 1) {
            return new ReturnUtil(true, true, "取消收藏成功!", null);
        }else {
            return new ReturnUtil(true, false, "未知错误!", null);
        }
    }

    // 获取 小说总收藏 和用户的 评分、是否看过、是否收藏
    @GetMapping("/getMore")
    public Map<String, Object> getMore(@RequestParam Integer bid,@RequestParam Integer uid){
        return bookService.getMore(bid,uid);
    }

    // 修改用户的 评分、是否看过、是否收藏
    @GetMapping("/updateUserView")
    public ReturnUtil updateUserView(@RequestParam Integer bid,@RequestParam Integer uid,
        @RequestParam Integer read,@RequestParam Integer like,@RequestParam Integer grade){
        int m = bookService.updateUserView(bid,uid,read,like,grade);
        if (m == 1) {
            return new ReturnUtil(true, true, "成功!", null);
        }else {
            return new ReturnUtil(true, false, "未知错误!", null);
        }
    }

    // 删除无用的记录
    @GetMapping("/delUserView")
    public void delUserView(){
        bookService.delUserView();
    }

    // 上传txt文件
    @PostMapping("/text/upload")
    public ReturnUtil uploadBook(@RequestParam MultipartFile file, @RequestParam Integer bookId,@RequestParam Integer userId){
        int m = bookService.uploadBook(file,bookId,userId);
        if (m == 1) {
            return new ReturnUtil(true, true, "上传文件成功!", null);
        }else {
            return new ReturnUtil(true, false, "上传文件失败!", null);
        }
    }

    // 根据书本id查询txt信息
    @GetMapping("/txt/getAll")
    public List<Map<String,Object>> getTXT(@RequestParam Integer id){
        return bookService.getTXT(id);
    }

    // 下载txt到本地
    @GetMapping("/txt/download")
    public void download(HttpServletResponse response,@RequestParam String url,@RequestParam boolean isOnLine){
        bookService.download(response,url,isOnLine);
    }

    // 获取所有txt信息
    @GetMapping("/txt/page")
    public Map<String, Object> getAllTxt(@RequestParam Integer pageNum, @RequestParam Integer pageSize, @RequestParam String name,@RequestParam String writer, @RequestParam String type) {
        return bookService.getAllTxt(pageNum,pageSize,name,writer,type);
    }

    // 删除txt文件
    @GetMapping("/txt/del")
    public ReturnUtil delTxt(@RequestParam Integer id){
        int m = bookService.delTxt(id);
        if(m == 1){
            return new ReturnUtil(true, true, "删除文件成功!", null);
        }else {
            return new ReturnUtil(true, false, "删除文件失败!", null);
        }
    }

    // 批量删除txt
    @PostMapping("/txt/del/batch")
    public ReturnUtil delTXTs(@RequestBody List<Integer> ids){
        int m = bookService.delTXTs(ids);
        if(m == 1){
            return new ReturnUtil(true, true, "批量删除成功！", null);
        }else if(m == -1){
            return new ReturnUtil(true, false, "请选择删除的对象！", null);
        }else {
            return new ReturnUtil(true, false, "批量删除失败！", null);
        }
    }
}
