package com.creatival.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QueryPlanDTO {
    private String intent;       // CONTENT_SEARCH, CONTENT_RECOMMEND, BOARD_SEARCH, TEAM_LIST ...
    private String target;       // CONTENT, BOARD, TEAM, SPONSORSHIP
    private String keyword;      // "용슬"
    private String contentType;  // NOVEL, COMIC, ART, MUSIC ...
    private String tag;          // "판타지"
    private String sort;         // RECOMMEND, POPULAR, LATEST
}