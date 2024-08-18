package com.fufunode.springboot.service.impl;

import com.fufunode.springboot.entity.BookTemporary;
import com.fufunode.springboot.entity.User;
import com.fufunode.springboot.mapper.BookMapper;
import com.fufunode.springboot.mapper.ReviewMapper;
import com.fufunode.springboot.service.BookService;
import com.github.pagehelper.PageHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fufunode.springboot.entity.Book;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class BookServiceImpl implements BookService {
    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private ReviewMapper reviewMapper;

    @Override
    public Map<String, Object> selectPage(Integer pageNum, Integer pageSize, String name, String writer, String type,String order) {
        PageHelper.startPage(pageNum,pageSize);
        List<Book> data = bookMapper.selectPage(name,writer,type,order);
        Integer total = bookMapper.selectTotal(name,writer,type);
        Map<String, Object> res = new HashMap<>();
        res.put("data",data);
        res.put("total",total);
        return res;
    }

    @Transactional
    @Override
    public Integer delBook(Integer id) {
        String path = bookMapper.haveImg(id);
        if (path != null) {
            String pre = "E:\\毕业论文\\code\\springboot\\src\\main\\resources\\static\\img\\book\\";
            String absolutePath = pre + path.substring(path.lastIndexOf("/")+1,path.length());
            File file = new File(absolutePath);
            if(file.exists()){
                boolean deleted = file.delete();
                if (deleted){
                    System.out.println("文件删除成功");
                }else {
                    System.out.println("文件删除失败");
                }
            }
        }
        List<Integer> ids = new ArrayList<>();
        ids.add(id);
        reviewMapper.delReviewsByBookId(ids);
        return bookMapper.delBook(id);
    }

    @Transactional
    @Override
    public Integer save(Book book) {
        Book b = bookMapper.getBookByName(book.getName());
        if (b != null ) return -1;
        bookMapper.save(book);
        return 0;
    }

    @Transactional
    @Override
    public Integer modifyBook(Book book) {
        if (book.getId() == -1) {
            return save(book);
        } else {
            bookMapper.update(book);
        }
        return 0;
    }

    @Transactional
    @Override
    public Integer delBookBatch(List<Integer> ids) {
        if(ids == null || ids.isEmpty()){
            return -1;
        }
        for(int i=0;i<ids.size();i++){
            String path = bookMapper.haveImg(ids.get(i));
            if (path != null) {
                String pre = "E:\\毕业论文\\code\\springboot\\src\\main\\resources\\static\\img\\book\\";
                String absolutePath = pre + path.substring(path.lastIndexOf("/")+1,path.length());
                File file = new File(absolutePath);
                if(file.exists()){
                    boolean deleted = file.delete();
                    if (deleted){
                        System.out.println("文件删除成功");
                    }else {
                        System.out.println("文件删除失败");
                    }
                }
            }
        }
        reviewMapper.delReviewsByBookId(ids);
        return bookMapper.delBookBatch(ids);
    }

    @Override
    public Integer getIdByName(String name) {
        Book book = bookMapper.getBookByName(name);
        return book.getId();
    }

    @Transactional
    @Override
    public String upload(MultipartFile file, Integer id) {
        if (file.isEmpty()){
            return "封面上传失败";
        }
        // 是否有图片
        String path = bookMapper.haveImg(id);
        boolean hasImg = true;

        // 存在数据库的
        String savePre = "http://localhost:8080/upload/book/";
        String pre = "E:\\毕业论文\\code\\springboot\\src\\main\\resources\\static\\img\\book\\";


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
        bookMapper.updateImg(path, id);

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
        return bookMapper.haveImg(id);
    }

    @Override
    public Map<String, Object> getMainData() {
        Map<String, Object> res = new HashMap<>();

        // 热门小说(一个月内阅读最多)
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH,1);
        String endTime = sdf.format(calendar.getTime());
        calendar.add(Calendar.MONTH,-1);
        String startTime = sdf.format(calendar.getTime());
        List<Integer> mainBookList = bookMapper.selectPopularBook(startTime,endTime,null,4);
        res.put("mainBookList", mainBookList);
        List<Integer> popularBookList = bookMapper.selectPopularBook(startTime,endTime,null,13);
        res.put("popularBookList", popularBookList);

        // 每个种类小说
        List<Map<String,Object>> typeBookList = new ArrayList<>();

        List<String> allType = bookMapper.getAllType();
        for(String type: allType){
            Map<String,Object> typeMap = new HashMap<>();
            List<Integer> typeList = bookMapper.selectBookInMainPage(type,7,"like");
            typeMap.put("type",type);
            typeMap.put("idList",typeList);
            typeBookList.add(typeMap);
        }
        res.put("typeBookList",typeBookList);

        // 最新小说
        List<Integer> newBookList = bookMapper.selectNewBook(22);
        res.put("newBookList", newBookList);

        // 评分最高
        List<Integer> mostGradeBookList = bookMapper.selectBookInMainPage(null,13,"grade");
        res.put("mostGradeBookList", mostGradeBookList);
        // 最多阅读
        List<Integer> mostLikeBookList = bookMapper.selectBookInMainPage(null,13,"read");
        res.put("mostLikeBookList", mostLikeBookList);
        // 最多收藏
        List<Integer> mostReadBookList = bookMapper.selectBookInMainPage(null,13,"like");
        res.put("mostReadBookList", mostReadBookList);

        return res;

    }

    @Override
    public Book getSingleBook(Integer id) {
        Book book = bookMapper.selectBookById(id);
        double grade = book.getGrade();
        String grade1 = String.format("%.2f",grade);
        double g = Double.parseDouble(grade1);
        book.setGrade(g);
        return book;
    }

    @Override
    public List<Book> getBookList(List<Integer> idList) {
        List<Book> bookList = new ArrayList<>();
        for(Integer id : idList){
            bookList.add(bookMapper.selectBookById(id));
        }
        return bookList;
    }

    @Override
    public List<Book> getPopularBook() {
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH,1);
        String endTime = sdf.format(calendar.getTime());
        calendar.add(Calendar.MONTH,-1);
        String startTime = sdf.format(calendar.getTime());
        List<Integer> popularBookList = bookMapper.selectPopularBook(startTime,endTime,null,12);
        return getBookList(popularBookList);
    }

    @Override
    public Map<String, Object> getRankBook(Integer pageNum, Integer pageSize, String type, String date) {
        PageHelper.startPage(pageNum,pageSize);
        String startTime = null;
        String endTime = null;

        if(type.equals("全部")){
            type = null;
        }

        if(date.equals("date")){
            SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH,1);
            endTime = sdf.format(calendar.getTime());
            calendar.add(Calendar.MONTH,-1);
            startTime = sdf.format(calendar.getTime());
        }else if(date.equals("month")){
            SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
            Calendar calendar = Calendar.getInstance();
            endTime = sdf.format(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH,-1);
            startTime = sdf.format(calendar.getTime());
        }

        List<Book> data = bookMapper.selectRankPage(type,startTime,endTime);
        Integer total = bookMapper.selectRankTotal(type,startTime,endTime);
        Map<String, Object> res = new HashMap<>();
        res.put("data",data);
        res.put("total",total);
        return res;
    }

    @Override
    public Map<String, Object> findBook(Integer pageNum, Integer pageSize, String name, String writer, String order) {
        PageHelper.startPage(pageNum,pageSize);
        List<Book> data = bookMapper.findBook(name,writer,order);
        Integer total = bookMapper.findBookTotal(name,writer);
        Map<String, Object> res = new HashMap<>();
        res.put("data",data);
        res.put("total",total);
        return res;
    }

    @Override
    public Map<String, Object> getLikeBook(Integer pageNum, Integer pageSize, Integer userId) {
        PageHelper.startPage(pageNum,pageSize);
        List<Book> data = bookMapper.getLikeBook(userId);
        Integer total = bookMapper.getLikeBookTotal(userId);
        Map<String, Object> res = new HashMap<>();
        res.put("data",data);
        res.put("total",total);
        return res;
    }

    @Override
    @Transactional
    public Integer cancelLike(Integer bid, Integer uid) {
        return bookMapper.cancelLike(bid,uid);
    }

    @Override
    @Transactional
    public Map<String, Object> getMore(Integer bid, Integer uid) {
        Map<String,Object> map = new HashMap<>();
        Integer totalGrade = bookMapper.getTotalGrade(bid);
        map.put("totalGrade",totalGrade);
        Map<String,Integer> userView = bookMapper.getMore(bid,uid);
        // 为空，创建新的
        if(userView == null){
            bookMapper.addUserView(bid,uid);
            userView = bookMapper.getMore(bid,uid);
        }
        map.put("userView",userView);

        return map;
    }

    @Override
    @Transactional
    public Integer updateUserView(Integer bid, Integer uid, Integer read, Integer like, Integer grade) {
        return bookMapper.updateUserView(bid,uid,read,like,grade);
    }

    @Override
    @Transactional
    public void delUserView() {
        bookMapper.delUserView();
    }

    @Override
    @Transactional
    public Integer uploadBook(MultipartFile file, Integer bookId, Integer userId) {
        if (file.isEmpty()){
            return -1;
        }
//        // 是否有图片
//        String path = bookMapper.haveImg(id);
//        boolean hasImg = true;

        // 存在数据库的
        String savePre = "http://localhost:8080/upload/txt/";
        String pre = "E:\\毕业论文\\code\\springboot\\src\\main\\resources\\static\\img\\txt\\";

        String originalFilename = file.getOriginalFilename();
        String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uuid = UUID.randomUUID().toString().replace("-", "");
        // 文件名
        String fileName = uuid + ext;
        // 数据库的
        String path = savePre + fileName;

        // 存在本地的绝对路径
        String absolutePath = pre + path.substring(path.lastIndexOf("/")+1,path.length());

        // 数据库存储路径
        System.out.println(path);
        Integer m = bookMapper.uploadBook(path, bookId, userId);

        try {
            if (m == 1){
                file.transferTo(new File(absolutePath));
                System.out.println(path);
                return m;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public List<Map<String,Object>> getTXT(Integer id) {
        return bookMapper.getTXT(id);
    }

    @Override
    public void download(HttpServletResponse response, String url,boolean isOnLine) {
        String path = url;
        String pre = "E:\\毕业论文\\code\\springboot\\src\\main\\resources\\static\\img\\txt\\";
        String absolutePath = pre + path.substring(path.lastIndexOf("/")+1,path.length());
        String filename = bookMapper.getNameByTXT(url);
        filename = filename + ".txt";

        File file = new File(absolutePath);
        InputStream inputStream = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            inputStream = new BufferedInputStream(fileInputStream);

            response.reset();
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setContentType("text/html; charset=utf-8");
            if (isOnLine) {
                // 在线打开方式 文件名应该编码成UTF-8
                response.setHeader("Content-Disposition", "inline; filename=" + URLEncoder.encode(filename, "UTF-8"));
            } else {
                //纯下载方式 文件名应该编码成UTF-8
                response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(filename, "UTF-8"));
            }

            byte[] buffer = new byte[1024];
            int len;
            OutputStream outputStream = response.getOutputStream();
            while ((len = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public Map<String, Object> getAllTxt(Integer pageNum, Integer pageSize, String name, String writer, String type) {
        PageHelper.startPage(pageNum,pageSize);
        List<Map<String,Object>> data = bookMapper.getAllTxt(name,writer,type);
        Integer total = bookMapper.getAllTxtTotal(name,writer,type);
        Map<String, Object> res = new HashMap<>();
        res.put("data",data);
        res.put("total",total);
        return res;
    }

    @Override
    @Transactional
    public Integer delTxt(Integer id) {
        String path = bookMapper.getTxtById(id);
        String pre = "E:\\毕业论文\\code\\springboot\\src\\main\\resources\\static\\img\\txt\\";
        String absolutePath = pre + path.substring(path.lastIndexOf("/")+1,path.length());
        File file = new File(absolutePath);
        if(file.exists()){
            boolean deleted = file.delete();
            if (deleted){
                System.out.println("文件删除成功");
            }else {
                System.out.println("文件删除失败");
            }
        }

        return bookMapper.delTxt(id);
    }

    @Override
    @Transactional
    public Integer delTXTs(List<Integer> ids) {
        if(ids == null || ids.isEmpty()){
            return -1;
        }
        for(int i=0;i<ids.size();i++){
            String path = bookMapper.getTxtById(ids.get(i));
            String pre = "E:\\毕业论文\\code\\springboot\\src\\main\\resources\\static\\img\\txt\\";
            String absolutePath = pre + path.substring(path.lastIndexOf("/")+1,path.length());
            File file = new File(absolutePath);
            if(file.exists()){
                boolean deleted = file.delete();
                if (deleted){
                    System.out.println("文件删除成功");
                }else {
                    System.out.println("文件删除失败");
                }
            }
        }
        return bookMapper.delTXTBatch(ids);
    }
}
