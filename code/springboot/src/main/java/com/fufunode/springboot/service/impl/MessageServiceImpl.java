package com.fufunode.springboot.service.impl;

import com.fufunode.springboot.entity.Message;
import com.fufunode.springboot.mapper.MessageMapper;
import com.fufunode.springboot.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Override
    @Transactional
    public Map<String, Object> getAllById(Integer id) {
        List<Message> messages = messageMapper.selectById(id);
        Integer total = messageMapper.getTotal(id);
        Map<String,Object> map = new HashMap<>();
        map.put("messages",messages);
        map.put("total",total);
        // 将当前所有的邮件改为已读
        messageMapper.updateRead(id);
        return map;
    }

    @Override
    @Transactional
    public void delDatedMessage() {
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH,1);
        calendar.add(Calendar.MONTH,-1);
        String endTime = sdf.format(calendar.getTime());
        messageMapper.delDatedMessage(endTime);
    }

    @Override
    public Integer hasNoRead(Integer id) {
        return messageMapper.hasNoRead(id);
    }
}
