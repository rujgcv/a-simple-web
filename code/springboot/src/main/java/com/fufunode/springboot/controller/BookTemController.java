package com.fufunode.springboot.controller;

import com.fufunode.springboot.entity.BookTemporary;
import com.fufunode.springboot.service.BookTemService;
import com.fufunode.springboot.utils.ReturnUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/booktem")
public class BookTemController {

    @Autowired
    private BookTemService bookTemService;

    // 把用户上传的小说放到书本审核表
    @PostMapping("/addbook")
    public ReturnUtil addBookTemporary(@RequestBody BookTemporary bookTem){
        return bookTemService.addBookTemporary(bookTem);
    }

    // 上传封面
    @PostMapping("/upload")
    public String upload(@RequestParam MultipartFile file, @RequestParam Integer id){
        return bookTemService.upload(file,id);
    }

    // 获取所有需要审核的书
    @GetMapping("/page")
    public Map<String, Object> findPage(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        return bookTemService.findPage(pageNum,pageSize);
    }

    // 删除
    @GetMapping("/del")
    public ReturnUtil delBookTem(@RequestParam Integer id,@RequestParam Integer uid,@RequestParam boolean pass,@RequestParam String message,@RequestParam Integer bookId){
        int m = bookTemService.delBookTem(id,uid,pass,message,bookId);
        if(m == 1){
            return new ReturnUtil(true, true, "成功!", null);
        }else {
            return new ReturnUtil(true, false, "未知错误!", null);
        }
    }

}
