package com.creatival.chat.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BoardSearchDTO {
    private Long id;
    private String title;
    private String url;
    private String writer;
    private LocalDateTime createdAt;
}