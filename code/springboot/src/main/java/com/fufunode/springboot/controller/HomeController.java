package com.fufunode.springboot.controller;

import com.fufunode.springboot.service.HomeService;
import com.fufunode.springboot.utils.ReturnUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/home")
public class HomeController {
    @Autowired
    private HomeService homeService;

    @GetMapping("/getAll")
    public Map<String, Object> getAll(@RequestParam Integer id){
        return homeService.getAll(id);
    }
}
