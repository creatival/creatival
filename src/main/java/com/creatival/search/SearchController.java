package com.creatival.search;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/search")
    public String search(@RequestParam(value = "keyword", required = false) String keyword,
                         @RequestParam(value = "type", defaultValue = "general") String type,
                         Model model) {

        keyword = keyword == null ? "" : keyword.trim();

        model.addAttribute("keyword", keyword);
        model.addAttribute("type", type);

        if (keyword.isBlank()) {
            model.addAttribute("userResults", Collections.emptyList());
            model.addAttribute("teamResults", Collections.emptyList());
            model.addAttribute("projectResults", Collections.emptyList());
            model.addAttribute("contentResults", Collections.emptyList());
            model.addAttribute("boardResults", Collections.emptyList());

            model.addAttribute("userPreviewResults", Collections.emptyList());
            model.addAttribute("teamPreviewResults", Collections.emptyList());
            model.addAttribute("projectPreviewResults", Collections.emptyList());
            model.addAttribute("contentPreviewResults", Collections.emptyList());
            model.addAttribute("boardPreviewResults", Collections.emptyList());

            return "search/result";
        }

        List<?> userResults;
        List<?> teamResults;
        List<?> projectResults;
        List<?> contentResults;
        List<?> boardResults;

        if ("tag".equalsIgnoreCase(type)) {
            userResults = searchService.searchUsersByTag(keyword);
            teamResults = searchService.searchTeamsByTag(keyword);
            projectResults = searchService.searchProjectsByTag(keyword);
            contentResults = searchService.searchContentsByTag(keyword);
            boardResults = Collections.emptyList(); // 보드 태그 검색 없음
            model.addAttribute("isTagSearch", true);
        } else {
            userResults = searchService.searchUsers(keyword);
            teamResults = searchService.searchTeams(keyword);
            projectResults = searchService.searchProjects(keyword);
            contentResults = searchService.searchContents(keyword);
            boardResults = searchService.searchBoards(keyword);
            model.addAttribute("isTagSearch", false);
        }

        model.addAttribute("userResults", userResults);
        model.addAttribute("teamResults", teamResults);
        model.addAttribute("projectResults", projectResults);
        model.addAttribute("contentResults", contentResults);
        model.addAttribute("boardResults", boardResults);

        model.addAttribute("userPreviewResults", limit(userResults, 3));
        model.addAttribute("teamPreviewResults", limit(teamResults, 3));
        model.addAttribute("projectPreviewResults", limit(projectResults, 3));
        model.addAttribute("contentPreviewResults", limit(contentResults, 3));
        model.addAttribute("boardPreviewResults", limit(boardResults, 3));

        return "search_result";
    }

    private <T> List<T> limit(List<T> list, int size) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        return list.size() > size ? list.subList(0, size) : list;
    }
}