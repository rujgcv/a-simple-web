package com.fufunode.springboot.service.impl;

import com.fufunode.springboot.entity.Book;
import com.fufunode.springboot.entity.BookTemporary;
import com.fufunode.springboot.entity.Message;
import com.fufunode.springboot.mapper.BookMapper;
import com.fufunode.springboot.mapper.BookTemMapper;
import com.fufunode.springboot.mapper.MessageMapper;
import com.fufunode.springboot.service.BookService;
import com.fufunode.springboot.service.BookTemService;
import com.fufunode.springboot.utils.ReturnUtil;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class BookTemServiceImpl implements BookTemService {
    @Autowired
    private BookTemMapper bookTemMapper;

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private BookService bookService;

    @Override
    @Transactional
    public ReturnUtil addBookTemporary(BookTemporary bookTem) {
        // 判断小说是否存在
        Book b = bookMapper.getBookByName(bookTem.getName());
        BookTemporary b1 = bookTemMapper.getBookByName(bookTem.getName());
        if(b != null && b1 != null){
            return new ReturnUtil(true, false, "小说已存在！", null);
        }
        Integer m = bookTemMapper.addBookTemporary(bookTem);
        if(m == 1){
            Integer id = (bookTemMapper.getBookByName(bookTem.getName())).getId();
            return new ReturnUtil(true, true, "上传成功,请等待审核通过！", id);
        }else {
            return new ReturnUtil(true, false, "上传失败！", null);
        }
    }

    @Transactional
    @Override
    public String upload(MultipartFile file, Integer id) {
        if (file.isEmpty()){
            return "封面上传失败";
        }
        // 是否有头像
        String path = bookTemMapper.haveImg(id);
        boolean hasImg = true;

        // 存在数据库的
        String savePre = "http://localhost:8080/upload/booktem/";
        String pre = "E:\\毕业论文\\code\\springboot\\src\\main\\resources\\static\\img\\booktem\\";


        if (path == null || path.equals("") || path.length() == 0){
            hasImg = false;
            String originalFilename = file.getOriginalFilename();
            String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uuid = UUID.randomUUID().toString().replace("-", "");
            // 文件名
            String fileName = uuid + ext;
            // 数据库的
            path = savePre + fileName;
        }

        // 存在本地的绝对路径
        String absolutePath = pre + path.substring(path.lastIndexOf("/")+1,path.length());

        // 数据库存储图片路径
        System.out.println(path);
        bookTemMapper.updateImg(path, id);

        try {
            if(hasImg){
                File f = new File(absolutePath);
                if(f.exists()){
                    boolean deleted = f.delete();
                    if (deleted){
                        file.transferTo(new File(absolutePath));
                        System.out.println(path);
                        return path;
                    }
                }
            }else {
                file.transferTo(new File(absolutePath));
                System.out.println(path);
                return path;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "封面上传失败";
    }

    @Override
    public String haveImg(Integer id) {
        return bookTemMapper.haveImg(id);
    }

    @Override
    public Map<String, Object> findPage(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Book> data = bookTemMapper.selectPage();
        Integer total = bookTemMapper.selectTotal();
        Map<String, Object> res = new HashMap<>();
        res.put("data",data);
        res.put("total",total);
        return res;
    }

    @Override
    @Transactional
    public Integer delBookTem(Integer id,Integer uid,boolean pass,String mess,Integer bookId) {
        BookTemporary bookTem = bookTemMapper.getBookById(id);

        String path = bookTemMapper.haveImg(id);
        if (path != null) {
            String pre = "E:\\毕业论文\\code\\springboot\\src\\main\\resources\\static\\img\\bookTem\\";
            String absolutePath = pre + path.substring(path.lastIndexOf("/")+1,path.length());
            File file = new File(absolutePath);

            // 把file变成multipartfile
            // 扒的，能用就行
            FileInputStream inputStream = null;
            try {
                inputStream = new FileInputStream(file);
                MockMultipartFile multipartFile = new MockMultipartFile(
                        "file",
                        file.getName(),
                        "APPLICATION_OCTET_STREAM",
                        inputStream);
                bookService.upload(multipartFile,bookId);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(file.exists()){
                boolean deleted = file.delete();
                if (deleted){
                    System.out.println("文件删除成功");
                }else {
                    System.out.println("文件删除失败");
                }
            }
        }
        Integer m = bookTemMapper.delBookTem(id);
        if(m != 1){
            return -1;
        }
        // 向用户发送信息
        Message message = new Message();
        message.setFromId(uid);
        message.setToId(bookTem.getUid());

        String bookname = bookTem.getName();
        String mes = "";
        if(pass){
            message.setTitle("系统通知:您的投稿已通过");
            // 通过
            mes = "您投稿的小说板块 《" + bookname + "》 已通过审核!";
        }else {
            // 不通过
            message.setTitle("系统通知:对不起,您的投稿没有通过");
            mes = "很遗憾,您投稿的小说板块 《" + bookname + "》 未能通过审核!\n"
                    + "具体原因可能是: " + mess;
        }
        message.setMessage(mes);
        messageMapper.sentMessage(message);
        return 1;
    }

}
