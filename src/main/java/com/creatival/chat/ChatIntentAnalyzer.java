package com.creatival.chat;

import org.springframework.stereotype.Component;

import com.creatival.chat.Enum.ChatIntentType;

@Component
public class ChatIntentAnalyzer {

    public ChatIntentType analyze(String message) {
        if (message == null || message.isBlank()) {
            return ChatIntentType.UNKNOWN;
        }

        String msg = message.toLowerCase();

        if (containsAny(msg, "후원 내역", "내 후원", "후원 기록", "후원 목록")) {
            return ChatIntentType.SPONSORSHIP_HISTORY;
        }

        if (containsAny(msg, "내 팀", "참여한 팀", "팀 목록", "내 프로젝트")) {
            return ChatIntentType.TEAM_LIST;
        }

        if (containsAny(msg, "업로드", "작품", "콘텐츠", "글 등록", "게시")) {
            return ChatIntentType.CONTENT_GUIDE;
        }

        if (containsAny(msg, "후원", "결제", "팀", "프로젝트", "로그인", "회원가입", "좋아요", "북마크")) {
            return ChatIntentType.GENERAL_GUIDE;
        }
        if (containsAny(msg, "추천", "추천해줘", "볼만한", "인기 작품", "인기 콘텐츠")) {
            return ChatIntentType.CONTENT_RECOMMEND;
        }
        if (containsAny(msg, "저작권", "이용약관", "정책", "규정", "신고", "운영 원칙")) {
            return ChatIntentType.POLICY_QUESTION;
        }

        return ChatIntentType.UNKNOWN;
    }

    private boolean containsAny(String msg, String... keywords) {
        for (String keyword : keywords) {
            if (msg.contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}