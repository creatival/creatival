package com.creatival.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchIntentDTO {
    private String intent;   // CONTENT_SEARCH or BOARD_SEARCH
    private String keyword;  // 실제 검색어
}