package com.creatival.chat;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.creatival.board.BoardSearchService;
import com.creatival.chat.dto.BoardSearchDTO;
import com.creatival.chat.dto.QueryPlanDTO;
import com.creatival.chat.dto.RecommendedContentDTO;
import com.creatival.chat.dto.ResponseChatDTO;
import com.creatival.content.ContentQueryService;
import com.creatival.sponsorship.Sponsorship;
import com.creatival.sponsorship.SponsorshipService;
import com.creatival.team.Team;
import com.creatival.team.TeamService;
import com.creatival.user.UserService;
import com.creatival.user.Users;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatGptService chatGptService;

    private final SponsorshipService sponsorshipService;
    private final TeamService teamService;
    private final UserService userService;

    private final ContentQueryService contentQueryService;
    private final BoardSearchService boardSearchService;

    public ResponseChatDTO ask(String userMessage, Authentication authentication) {

        if (isCopyrightInfringementIssue(userMessage)) {
            return new ResponseChatDTO(
                    handleCopyrightInfringementMessage(),
                    Collections.emptyList(),
                    Collections.emptyList()
            );
        }

        if (isCopyrightGeneralQuestion(userMessage)) {
            return new ResponseChatDTO(
                    chatGptService.askPolicyGuide(userMessage),
                    Collections.emptyList(),
                    Collections.emptyList()
            );
        }

        if (isSimpleConversation(userMessage)) {
            return new ResponseChatDTO(
                    chatGptService.askFreeTalk(userMessage),
                    Collections.emptyList(),
                    Collections.emptyList()
            );
        }

        try {
            QueryPlanDTO plan = chatGptService.analyzeQueryPlan(userMessage);

            if (plan == null || plan.getIntent() == null || plan.getIntent().isBlank()) {
                return new ResponseChatDTO(
                        chatGptService.askFreeTalk(userMessage),
                        Collections.emptyList(),
                        Collections.emptyList()
                );
            }

            return switch (plan.getIntent()) {

                case "TEAM_LIST" -> new ResponseChatDTO(
                        handleTeamList(userMessage, authentication),
                        Collections.emptyList(),
                        Collections.emptyList()
                );

                case "SPONSORSHIP_HISTORY" -> new ResponseChatDTO(
                        handleSponsorshipHistory(userMessage, authentication),
                        Collections.emptyList(),
                        Collections.emptyList()
                );

                case "CONTENT_SEARCH", "CONTENT_RECOMMEND" -> handleContentPlan(userMessage, plan);

                case "BOARD_SEARCH" -> handleBoardPlan(userMessage, plan);

                case "GENERAL_GUIDE" -> new ResponseChatDTO(
                        chatGptService.askGuide(userMessage),
                        Collections.emptyList(),
                        Collections.emptyList()
                );

                default -> new ResponseChatDTO(
                        chatGptService.askFreeTalk(userMessage),
                        Collections.emptyList(),
                        Collections.emptyList()
                );
            };

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseChatDTO(
                    chatGptService.askFreeTalk(userMessage),
                    Collections.emptyList(),
                    Collections.emptyList()
            );
        }
    }


    private ResponseChatDTO handleContentPlan(String userMessage, QueryPlanDTO plan) {
        List<RecommendedContentDTO> results = contentQueryService.queryContents(plan);

        if (results == null || results.isEmpty()) {
            return new ResponseChatDTO(
                    "조건에 맞는 콘텐츠를 찾지 못했어요.",
                    Collections.emptyList(),
                    Collections.emptyList()
            );
        }

        String rawData = results.stream()
                .map(c -> """
                        제목: %s
                        타입: %s
                        좋아요: %d
                        조회수: %d
                        태그: %s
                        """.formatted(
                        c.getTitle(),
                        c.getType(),
                        c.getLikeCount(),
                        c.getViewCount(),
                        c.getTags() == null || c.getTags().isEmpty() ? "없음" : String.join(", ", c.getTags())
                ))
                .collect(Collectors.joining("\n\n"));

        String instruction = "CONTENT_RECOMMEND".equals(plan.getIntent())
                ? "아래 추천 결과를 자연스럽게 설명해주세요."
                : "아래 검색 결과를 자연스럽게 설명해주세요.";

        String reply = chatGptService.answerWithUserData(userMessage, instruction, rawData);

        return new ResponseChatDTO(reply, results, Collections.emptyList());
    }

    private ResponseChatDTO handleBoardPlan(String userMessage, QueryPlanDTO plan) {

        String keyword = plan.getKeyword();

        List<BoardSearchDTO> results = boardSearchService.search(keyword);

        if (results == null || results.isEmpty()) {
            return new ResponseChatDTO(
                    "'" + (keyword == null ? "" : keyword) + "' 키워드로 찾은 게시글이 없어요.",
                    Collections.emptyList(),
                    Collections.emptyList()
            );
        }

        String rawData = results.stream()
                .map(b -> """
                        제목: %s
                        작성자: %s
                        작성일: %s
                        """.formatted(
                        b.getTitle(),
                        b.getWriter(),
                        b.getCreatedAt()
                ))
                .collect(Collectors.joining("\n\n"));

        String reply = chatGptService.answerWithUserData(
                userMessage,
                "아래는 게시글 검색 결과입니다. 제공된 정보만 사용해서 자연스럽게 설명해주세요.",
                rawData
        );

        return new ResponseChatDTO(reply, Collections.emptyList(), results);
    }

    private String handleSponsorshipHistory(String userMessage, Authentication authentication) {
        Users user = extractUser(authentication);
        if (user == null) return "후원 내역 조회는 로그인 후 이용할 수 있어요.";

        List<Sponsorship> sponsorships = sponsorshipService.getSponsorshipsByUser(user);

        if (sponsorships == null || sponsorships.isEmpty()) {
            return "아직 후원 내역이 없어요.";
        }

        String rawData = sponsorships.stream()
                .limit(5)
                .map(s -> "후원금액: " + s.getAmount() + "원")
                .collect(Collectors.joining("\n"));

        return chatGptService.answerWithUserData(userMessage,
                "아래는 사용자의 후원 내역입니다. 자연스럽게 설명해주세요.",
                rawData);
    }

    private String handleTeamList(String userMessage, Authentication authentication) {
        Users user = extractUser(authentication);
        if (user == null) return "팀 정보 조회는 로그인 후 이용할 수 있어요.";

        List<Team> teams = teamService.getTeamsByMember(user);

        if (teams == null || teams.isEmpty()) {
            return "현재 참여 중인 팀이 없어요.";
        }

        String rawData = teams.stream()
                .limit(5)
                .map(team -> "팀 이름: " + team.getName())
                .collect(Collectors.joining("\n"));

        return chatGptService.answerWithUserData(userMessage,
                "아래는 사용자의 팀 목록입니다. 자연스럽게 설명해주세요.",
                rawData);
    }

    private Users extractUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) return null;

        String username = authentication.getName();
        if (username == null || "anonymousUser".equals(username)) return null;

        return userService.getUserByUsername(username);
    }

    private boolean isSimpleConversation(String message) {
        if (message == null || message.isBlank()) return true;

        String msg = message.trim().toLowerCase();

        if (msg.length() > 10) return false;

        return containsAny(msg,
                "안녕", "ㅎㅇ", "하이", "hello", "hi",
                "고마워", "감사", "땡큐",
                "뭐해", "잘자", "반가워",
                "오케이", "ㅇㅋ", "ㅋㅋ", "ㅎㅎ");
    }

    private boolean isCopyrightGeneralQuestion(String message) {
        return message != null && message.contains("저작권");
    }

    private boolean isCopyrightInfringementIssue(String message) {
        if (message == null) return false;

        return containsAny(message,
                "침해", "도용", "불펌", "표절", "무단 사용", "신고", "내 저작권"
        );
    }

    private String handleCopyrightInfringementMessage() {
        return """
                저작권 침해가 의심된다면 먼저 증거를 정리해 두는 것이 좋아요.

                현재 Creatival에는 공식 신고 절차나 정책 페이지는 없습니다.

                일반적으로는 게시물 URL, 캡처, 업로드 날짜, 원본 파일 등을 확보하고,
                피해가 크다면 전문가 상담을 고려하는 것이 안전합니다.
                """;
    }

    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) return true;
        }
        return false;
    }
}