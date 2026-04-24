package com.creatival.chat.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RecommendedContentDTO {
    private Long id;
    private String title;
    private String type;
    private String url;
    private Long likeCount;
    private Long viewCount;
    private List<String> tags;
    private boolean paid;
}