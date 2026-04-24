package com.creatival.content;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.creatival.chat.dto.RecommendedContentDTO;
import com.creatival.content.Enum.ContentType;
import com.creatival.content.Enum.Visibility;
import com.creatival.content.repository.ContentRepository;
import com.creatival.tag.TagToContent;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContentRecommendationService {

    private final ContentRepository contentRepository;

    public List<RecommendedContentDTO> getRecommendations(String userMessage) {
        String keywordTag = extractTagKeyword(userMessage);
        ContentType contentType = extractTypeKeyword(userMessage);

        List<Content> contents = contentRepository.findByVisibility(Visibility.PUBLIC);

        return contents.stream()
                .filter(content -> contentType == null || content.getType() == contentType)
                .sorted((a, b) -> Long.compare(score(b, keywordTag), score(a, keywordTag)))
                .limit(5)
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private long score(Content content, String keywordTag) {
        long score = 0L;

        score += safeLikeCount(content) * 3L;
        score += safeViewCount(content);

        if (keywordTag != null && content.getTagToContent() != null) {
            long matchedTagCount = content.getTagToContent().stream()
                    .map(this::extractTagText)
                    .filter(tagText -> tagText != null)
                    .filter(tagText -> tagText.toLowerCase(Locale.ROOT).contains(keywordTag))
                    .count();

            score += matchedTagCount * 5L;
        }

        return score;
    }

    private RecommendedContentDTO toDto(Content content) {
        List<String> tags = content.getTagToContent() == null
                ? List.of()
                : content.getTagToContent().stream()
                    .map(this::extractTagText)
                    .filter(tagText -> tagText != null && !tagText.isBlank())
                    .distinct()
                    .limit(5)
                    .collect(Collectors.toList());

        return new RecommendedContentDTO(
                content.getId(),
                content.getTitle(),
                content.getType().toString(),
                buildContentUrl(content),
                safeLikeCount(content),
                safeViewCount(content),
                tags,
                content.isPaid()
        );
    }

    private String extractTagText(TagToContent tagToContent) {
        if (tagToContent == null || tagToContent.getTag() == null) {
            return null;
        }

        return tagToContent.getTag().getTagText();
    }

    private String buildContentUrl(Content content) {
        return "/content/" + content.getType().toString().toLowerCase() + "/detail/" + content.getId();
    }

    private Long safeLikeCount(Content content) {
        return content.getLikeCount() == null ? 0L : content.getLikeCount();
    }

    private Long safeViewCount(Content content) {
        return content.getViewCount() == null ? 0L : content.getViewCount();
    }

    private String extractTagKeyword(String userMessage) {
        if (userMessage == null || userMessage.isBlank()) {
            return null;
        }

        String msg = userMessage.toLowerCase(Locale.ROOT);

        String[] knownTags = {
                "판타지", "액션", "로맨스", "힐링", "드라마", "코미디",
                "모험", "스릴러", "일상", "무협", "게임", "학원"
        };

        for (String tag : knownTags) {
            if (msg.contains(tag.toLowerCase(Locale.ROOT))) {
                return tag.toLowerCase(Locale.ROOT);
            }
        }

        return null;
    }
    
    private ContentType extractTypeKeyword(String userMessage) {
        if (userMessage == null || userMessage.isBlank()) {
            return null;
        }

        String msg = userMessage.toLowerCase();

        if (msg.contains("소설") || msg.contains("novel")) {
            return ContentType.NOVEL;
        }
        if (msg.contains("만화") || msg.contains("코믹") || msg.contains("comic")) {
            return ContentType.COMIC;
        }
        if (msg.contains("그림") || msg.contains("아트") || msg.contains("art") || msg.contains("일러스트")) {
            return ContentType.ART;
        }
        if (msg.contains("음악") || msg.contains("music")) {
            return ContentType.MUSIC;
        }
        if (msg.contains("파일") || msg.contains("file")) {
            return ContentType.FILE;
        }
        if (msg.contains("영상") || msg.contains("비디오") || msg.contains("video")) {
            return ContentType.VIDEO;
        }

        return null;
    }
}