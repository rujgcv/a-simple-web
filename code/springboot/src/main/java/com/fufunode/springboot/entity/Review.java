package com.fufunode.springboot.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
public class Review {
    private Long id;
    private String content;
    private Integer type;
    private Integer userId;
    private String username;
    private String parentname;
    private String avatarUrl;
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createTime;
    private Integer isDel;
    private Integer bookId;
    private String bookname;
    private Long parentId;
    private Long rootId;
    private Integer like;
    private List<Review> child;
}
