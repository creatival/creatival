package com.creatival.chat;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.creatival.board.BoardSearchService;
import com.creatival.chat.Enum.ChatIntentType;
import com.creatival.chat.dto.BoardSearchDTO;
import com.creatival.chat.dto.ChatIntentDTO;
import com.creatival.chat.dto.QueryPlanDTO;
import com.creatival.chat.dto.RecommendedContentDTO;
import com.creatival.chat.dto.ResponseChatDTO;
import com.creatival.chat.dto.SearchIntentDTO;
import com.creatival.content.ContentQueryService;
import com.creatival.content.ContentRecommendationService;
import com.creatival.content.ContentSearchService;
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

    private final ChatIntentAnalyzer chatIntentAnalyzer;
    private final ChatGptService chatGptService;

    private final SponsorshipService sponsorshipService;
    private final TeamService teamService;
    private final UserService userService;

    private final ContentRecommendationService contentRecommendationService;
    private final ContentSearchService contentSearchService;
    private final BoardSearchService boardSearchService;
    private final ContentQueryService contentQueryService;

    public ResponseChatDTO ask(String userMessage, Authentication authentication) {
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

    private ResponseChatDTO handleContentRecommend(String userMessage) {
        List<RecommendedContentDTO> recommendations =
                contentRecommendationService.getRecommendations(userMessage);

        if (recommendations == null || recommendations.isEmpty()) {
            return new ResponseChatDTO(
                    "지금 추천드릴 만한 콘텐츠를 찾지 못했어요.",
                    Collections.emptyList(),
                    Collections.emptyList()
            );
        }

        String rawData = recommendations.stream()
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
                        (c.getTags() == null || c.getTags().isEmpty()) ? "없음" : String.join(", ", c.getTags())
                ))
                .collect(Collectors.joining("\n\n"));

        String reply = chatGptService.recommendWithData(userMessage, rawData);

        return new ResponseChatDTO(
                reply,
                recommendations,
                Collections.emptyList()
        );
    }

    private ResponseChatDTO handleContentSearch(String userMessage) {
        String keyword = extractKeyword(userMessage);

        if (keyword.isBlank()) {
            return new ResponseChatDTO(
                    "검색할 키워드를 조금 더 구체적으로 입력해주세요. 예: 아리아 검색해줘, 판타지 소설 찾아줘",
                    Collections.emptyList(),
                    Collections.emptyList()
            );
        }

        List<RecommendedContentDTO> results = contentSearchService.search(keyword);

        if (results == null || results.isEmpty()) {
            return new ResponseChatDTO(
                    "'" + keyword + "' 키워드로 찾은 콘텐츠가 없어요.",
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
                        """.formatted(
                        c.getTitle(),
                        c.getType(),
                        c.getLikeCount(),
                        c.getViewCount()
                ))
                .collect(Collectors.joining("\n\n"));

        String reply = chatGptService.answerWithUserData(
                userMessage,
                """
                아래는 콘텐츠 검색 결과입니다.
                제공된 정보만 사용해서 자연스럽게 검색 결과를 설명해주세요.
                없는 정보는 지어내지 말고, 너무 길지 않게 작성해주세요.
                링크 안내는 직접 쓰지 말고, 사용자가 아래 카드에서 확인할 수 있다고 자연스럽게 유도해주세요.
                """,
                rawData
        );

        return new ResponseChatDTO(
                reply,
                results,
                Collections.emptyList()
        );
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

        return new ResponseChatDTO(
                reply,
                results,
                Collections.emptyList()
        );
    }
    private ResponseChatDTO handleBoardSearch(String userMessage) {
        String keyword = extractKeyword(userMessage);

        if (keyword.isBlank()) {
            return new ResponseChatDTO(
                    "찾고 싶은 게시글 키워드를 조금 더 구체적으로 입력해주세요. 예: 팀 모집 게시글 찾아줘",
                    Collections.emptyList(),
                    Collections.emptyList()
            );
        }

        List<BoardSearchDTO> results = boardSearchService.search(keyword);

        if (results == null || results.isEmpty()) {
            return new ResponseChatDTO(
                    "'" + keyword + "' 키워드로 찾은 게시글이 없어요.",
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
                """
                아래는 게시글 검색 결과입니다.
                제공된 정보만 사용해서 자연스럽게 검색 결과를 설명해주세요.
                링크를 직접 문장에 넣지 말고, 아래 카드에서 확인할 수 있다고 자연스럽게 안내해주세요.
                없는 정보는 지어내지 말고, 너무 길지 않게 작성해주세요.
                """,
                rawData
        );

        return new ResponseChatDTO(
                reply,
                Collections.emptyList(),
                results
        );
    }
    private ResponseChatDTO handleSmartSearch(String userMessage) {
        SearchIntentDTO searchIntent = chatGptService.analyzeSearchIntent(userMessage);

        String intent = searchIntent.getIntent();
        String keyword = searchIntent.getKeyword() == null ? "" : searchIntent.getKeyword().trim();

        if (keyword.isBlank()) {
            return new ResponseChatDTO(
                    "검색할 키워드를 잘 이해하지 못했어요. 작품명이나 게시글 제목을 조금 더 구체적으로 말씀해 주세요.",
                    Collections.emptyList(),
                    Collections.emptyList()
            );
        }

        if ("BOARD_SEARCH".equalsIgnoreCase(intent)) {
            return handleBoardSearchByKeyword(userMessage, keyword);
        }

        return handleContentSearchByKeyword(userMessage, keyword);
    }
    
    private ResponseChatDTO handleContentSearchByKeyword(String userMessage, String keyword) {
        List<RecommendedContentDTO> results = contentSearchService.search(keyword);

        if (results == null || results.isEmpty()) {
            return new ResponseChatDTO(
                    "'" + keyword + "' 키워드로 찾은 콘텐츠가 없어요.",
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
                        """.formatted(
                        c.getTitle(),
                        c.getType(),
                        c.getLikeCount(),
                        c.getViewCount()
                ))
                .collect(Collectors.joining("\n\n"));

        String reply = chatGptService.answerWithUserData(
                userMessage,
                "아래는 콘텐츠 검색 결과입니다. 제공된 정보만 사용해서 자연스럽게 설명해주세요.",
                rawData
        );

        return new ResponseChatDTO(reply, results, Collections.emptyList());
    }

    private ResponseChatDTO handleBoardSearchByKeyword(String userMessage, String keyword) {
        List<BoardSearchDTO> results = boardSearchService.search(keyword);

        if (results == null || results.isEmpty()) {
            return new ResponseChatDTO(
                    "'" + keyword + "' 키워드로 찾은 게시글이 없어요.",
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
        if (user == null) {
            return "후원 내역 조회는 로그인 후 이용할 수 있어요.";
        }

        List<Sponsorship> sponsorships = sponsorshipService.getSponsorshipsByUser(user);

        if (sponsorships == null || sponsorships.isEmpty()) {
            return "아직 후원 내역이 없어요.";
        }

        String rawData = sponsorships.stream()
                .limit(5)
                .map(s -> "후원금액: " + s.getAmount() + "원")
                .collect(Collectors.joining("\n"));

        return chatGptService.answerWithUserData(
                userMessage,
                """
                아래는 사용자의 최근 후원 내역입니다.
                없는 내용은 지어내지 말고, 사용자가 물은 맥락에 맞게 자연스럽게 설명해주세요.
                답변은 친근하지만 과하지 않게 2~4문장 정도로 작성해주세요.
                """,
                rawData
        );
    }

    private String handleTeamList(String userMessage, Authentication authentication) {
        Users user = extractUser(authentication);
        if (user == null) {
            return "팀 정보 조회는 로그인 후 이용할 수 있어요.";
        }

        List<Team> teams = teamService.getTeamsByMember(user);

        if (teams == null || teams.isEmpty()) {
            return "현재 참여 중인 팀이 없어요.";
        }

        String rawData = teams.stream()
                .limit(5)
                .map(team -> "팀 이름: " + team.getName())
                .collect(Collectors.joining("\n"));

        return chatGptService.answerWithUserData(
                userMessage,
                """
                아래는 사용자가 참여 중인 팀 목록입니다.
                제공된 팀 이름만 사용해서 자연스럽게 설명해주세요.
                없는 팀이나 추가 정보는 지어내지 말아주세요.
                답변은 짧고 자연스럽게 작성해주세요.
                """,
                rawData
        );
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
                """
                아래는 게시글 검색 결과입니다.
                제공된 정보만 사용해서 자연스럽게 설명해주세요.
                링크는 직접 말하지 말고 아래 카드에서 확인하도록 유도해주세요.
                """,
                rawData
        );

        return new ResponseChatDTO(
                reply,
                Collections.emptyList(),
                results
        );
    }

    private Users extractUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        String username = authentication.getName();

        if (username == null || "anonymousUser".equals(username)) {
            return null;
        }

        return userService.getUserByUsername(username);
    }

    private String extractKeyword(String message) {
        String keyword = extractKeywordByRule(message);

        if (needsGptKeywordFix(keyword)) {
            String gptKeyword = chatGptService.extractSearchKeyword(message);
            if (gptKeyword != null && !gptKeyword.isBlank()) {
                return gptKeyword.trim();
            }
        }

        return keyword.trim();
    }

    private String extractKeywordByRule(String message) {
        if (message == null || message.isBlank()) {
            return "";
        }

        String text = message.trim();

        String[] patterns = {
                "^(.+?)라는걸",
                "^(.+?)라는 거",
                "^(.+?)라는 이름",
                "^(.+?)라는 제목",
                "^(.+?)이라는걸",
                "^(.+?)이라는 거",
                "^(.+?)이라는 이름",
                "^(.+?)이라고 하는",
                "^(.+?)라고 하는",
                "^(.+?)이라고",
                "^(.+?)라고",
                "^(.+?)인",
                "^(.+?)관련",
                "^(.+?)에 대한",
                "^(.+?)에 관한"
        };

        for (String regex : patterns) {
            Matcher matcher = Pattern.compile(regex).matcher(text);
            if (matcher.find()) {
                return matcher.group(1).trim();
            }
        }

        String cleaned = text
                .replace("게시판", "")
                .replace("게시글", "")
                .replace("보드", "")
                .replace("글", "")
                .replace("콘텐츠", "")
                .replace("작품", "")
                .replace("검색해줘", "")
                .replace("찾아줘", "")
                .replace("찾아", "")
                .replace("보여줘", "")
                .replace("있어?", "")
                .replace("있나?", "")
                .replace("뭐 있어?", "")
                .trim();

        return cleanTrailingWords(cleaned);
    }

    private String cleanTrailingWords(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return "";
        }

        return keyword
                .replaceAll("\\s*(이라는걸|라는걸|이라는 거|라는 거|이라는 이름|라는 이름|이라는 제목|라는 제목)$", "")
                .replaceAll("\\s*(이라고 하는|라고 하는|이라고|라고|인|관련|제목)$", "")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private boolean needsGptKeywordFix(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return true;
        }

        if (keyword.length() > 25) {
            return true;
        }

        String[] suspiciousWords = {
                "걸", "거", "라는", "이라고", "라고", "검색해줘", "찾아줘", "보여줘"
        };

        for (String word : suspiciousWords) {
            if (keyword.contains(word)) {
                return true;
            }
        }

        return false;
    }
    
    private boolean looksLikeSearchRequest(String message) {
        if (message == null || message.isBlank()) return false;

        return containsAny(message,
                "찾아줘", "검색", "검색해줘", "보여줘", "찾아", "있어?",
                "작품", "콘텐츠", "게시글", "보드", "글");
    }

    private boolean isSimpleConversation(String message) {
        if (message == null || message.isBlank()) {
            return true;
        }

        String msg = message.trim().toLowerCase();

        return containsAny(msg,
                "안녕", "ㅎㅇ", "하이", "hello", "hi",
                "고마워", "감사", "땡큐",
                "뭐해", "잘자", "반가워", "좋아", "오케이", "ㅇㅋ",
                "ㅋㅋ", "ㅎㅎ", "와", "오", "흠");
    }

    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
	
    
}