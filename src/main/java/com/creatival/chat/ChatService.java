package com.creatival.chat;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.creatival.chat.Enum.ChatIntentType;
import com.creatival.chat.dto.ResponseChatDTO;
import com.creatival.chat.dto.RecommendedContentDTO;
import com.creatival.content.ContentRecommendationService;
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

    public ResponseChatDTO ask(String userMessage, Authentication authentication) {
        ChatIntentType intent = chatIntentAnalyzer.analyze(userMessage);

        return switch (intent) {
            case SPONSORSHIP_HISTORY -> new ResponseChatDTO(
                    handleSponsorshipHistory(userMessage, authentication),
                    null
            );
            case TEAM_LIST -> new ResponseChatDTO(
                    handleTeamList(userMessage, authentication),
                    null
            );
            case CONTENT_RECOMMEND -> handleContentRecommend(userMessage);
            case CONTENT_GUIDE, GENERAL_GUIDE -> new ResponseChatDTO(
                    chatGptService.askGuide(userMessage),
                    null
            );
            case UNKNOWN -> new ResponseChatDTO(
                    chatGptService.askFreeTalk(userMessage),
                    null
            );
            case POLICY_QUESTION -> new ResponseChatDTO(
                    chatGptService.askPolicyGuide(userMessage),
                    Collections.emptyList()
            );
        };
    }

    private ResponseChatDTO handleContentRecommend(String userMessage) {
        List<RecommendedContentDTO> recommendations = contentRecommendationService.getRecommendations(userMessage);

        if (recommendations == null || recommendations.isEmpty()) {
            return new ResponseChatDTO("지금 추천드릴 만한 콘텐츠를 찾지 못했어요.", null);
        }

        String rawData = recommendations.stream()
                .map(c -> """
                        제목: %s
                        타입: %s
                        좋아요: %d
                        조회수: %d
                        태그: %s
                        링크: %s
                        """.formatted(
                        c.getTitle(),
                        c.getType(),
                        c.getLikeCount(),
                        c.getViewCount(),
                        c.getTags() == null || c.getTags().isEmpty() ? "없음" : String.join(", ", c.getTags()),
                        c.getUrl()
                ))
                .collect(Collectors.joining("\n\n"));

        String reply = chatGptService.recommendWithData(userMessage, rawData);

        return new ResponseChatDTO(reply, recommendations);
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
}