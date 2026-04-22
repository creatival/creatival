package com.creatival.chat.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResponseChatDTO {
    private String reply;
    private List<RecommendedContentDTO> recommendations;
}