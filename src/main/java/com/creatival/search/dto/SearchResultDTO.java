package com.creatival.search.dto;

import com.creatival.like.TargetType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchResultDTO {
    private Long id;
    private String title;
    private String description;
    private String thumbnailUrl;
    private TargetType type;
    private String targetUrl;
}