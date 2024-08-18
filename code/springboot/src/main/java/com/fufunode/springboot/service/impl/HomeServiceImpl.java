package com.fufunode.springboot.service.impl;

import com.fufunode.springboot.entity.User;
import com.fufunode.springboot.mapper.HomeMapper;
import com.fufunode.springboot.service.HomeService;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class HomeServiceImpl implements HomeService {

    @Autowired
    private HomeMapper homeMapper;

    @Override
    public Map<String, Object> getAll(Integer id) {
        // 管理员信息
        User user = homeMapper.getAdminById(id);
        // 小说总数
        Integer bookSum = homeMapper.getBookSum();
        // 用户总数
        Integer userSum = homeMapper.getUserSum();
        // 今日新增小说
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
        Calendar calendar = Calendar.getInstance();
        String startTime = sdf.format(calendar.getTime());
        calendar.add(Calendar.DAY_OF_MONTH,1);
        String endTime = sdf.format(calendar.getTime());
        Integer bookTodaySum = homeMapper.getBookToday(startTime,endTime);
        // 图表信息
        List<Map<String,Integer>> chartData = homeMapper.getChartData();

        Map<String, Object> res = new HashMap<>();
        res.put("user",user);
        res.put("bookSum",bookSum);
        res.put("userSum",userSum);
        res.put("bookTodaySum",bookTodaySum);
        res.put("chartData",chartData);
        return res;
    }
}
