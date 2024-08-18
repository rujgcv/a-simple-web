package com.fufunode.springboot.service;

import java.util.Map;

public interface HomeService {
    // 获取主页所有信息
    Map<String, Object> getAll(Integer id);
}
