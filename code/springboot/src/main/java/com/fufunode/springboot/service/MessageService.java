package com.fufunode.springboot.service;

import java.util.Map;

public interface MessageService {
    Map<String,Object> getAllById(Integer id);

    void delDatedMessage();

    Integer hasNoRead(Integer id);
}
