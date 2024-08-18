package com.fufunode.springboot.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookTemporary {
    private Integer id;
    private Integer uid;
    private String name;
    private String writer;
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    private String imgUrl;
    private String description;
    private String type;
    private Double grade;
    private Integer read;
    private Integer like;
}
