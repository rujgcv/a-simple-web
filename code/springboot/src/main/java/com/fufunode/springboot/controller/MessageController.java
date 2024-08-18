package com.fufunode.springboot.controller;

import com.fufunode.springboot.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    // 根据id获得所有邮件
    @GetMapping("/getAll")
    public Map<String,Object> getAllMessage(@RequestParam Integer id){
        return messageService.getAllById(id);
    }

    // 删除邮件
    @GetMapping("/del")
    public void delDatedMessage(){
        messageService.delDatedMessage();
    }

    // 根据id查询是否有未读信息
    @GetMapping("/hasNoRead")
    public Integer hasNoRead(@RequestParam Integer id){
        return messageService.hasNoRead(id);
    }
}
