package com.fufunode.springboot.service;

import com.fufunode.springboot.entity.BookTemporary;
import com.fufunode.springboot.utils.ReturnUtil;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface BookTemService {
    // 把用户上传的小说放到书本审核表
    ReturnUtil addBookTemporary(BookTemporary bookTem);

    // 上传封面
    String upload(MultipartFile file, Integer id);

    // 是否有封面 如有，返回封面的路径
    String haveImg(Integer id);

    // 获取所有需要审核的书
    Map<String, Object> findPage(Integer pageNum, Integer pageSize);

    // 删除书
    Integer delBookTem(Integer id,Integer uid,boolean pass,String mess,Integer bookId);
}
