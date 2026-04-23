package com.creatival.content;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.creatival.chat.dto.QueryPlanDTO;
import com.creatival.chat.dto.RecommendedContentDTO;
import com.creatival.content.Enum.ContentType;
import com.creatival.content.Enum.Visibility;
import com.creatival.content.repository.ContentRepository;
import com.creatival.tag.TagToContent;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContentQueryService {

    private final ContentRepository contentRepository;
    private final Random random = new Random();

    public List<RecommendedContentDTO> queryContents(QueryPlanDTO plan) {
        List<Content> contents = contentRepository.findByVisibility(Visibility.PUBLIC);
        // Visibility.PUBLIC은 실제 enum 값에 맞게 수정

        List<Content> filtered = contents.stream()
                .filter(this::isUsableContent)
                .filter(content -> matchesKeyword(content, plan.getKeyword()))
                .filter(content -> matchesContentType(content, plan.getContentType()))
                .filter(content -> matchesTag(content, plan.getTag()))
                .sorted(buildComparator(plan))
                .limit(20) // 먼저 후보군 확보
                .collect(Collectors.toList());

        List<Content> diversified = diversifyResults(filtered);

        return diversified.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private List<Content> diversifyResults(List<Content> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return List.of();
        }

        if (candidates.size() <= 5) {
            return candidates;
        }

        List<Content> result = new ArrayList<>();

        // 상위 2개는 유지
        int fixedTopCount = Math.min(2, candidates.size());
        for (int i = 0; i < fixedTopCount; i++) {
            result.add(candidates.get(i));
        }

        // 나머지 후보군에서 랜덤 섞기
        List<Content> remaining = new ArrayList<>(candidates.subList(fixedTopCount, candidates.size()));
        Collections.shuffle(remaining, random);

        int need = 5 - result.size();
        for (int i = 0; i < Math.min(need, remaining.size()); i++) {
            result.add(remaining.get(i));
        }

        return result;
    }

    private boolean isUsableContent(Content content) {
        if (content == null) return false;
        if (content.getTitle() == null || content.getTitle().isBlank()) return false;
        return true;
    }

    private boolean matchesKeyword(Content content, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return true;
        }

        String lowerKeyword = keyword.toLowerCase(Locale.ROOT);

        boolean inTitle = content.getTitle() != null &&
                content.getTitle().toLowerCase(Locale.ROOT).contains(lowerKeyword);

        boolean inDescription = content.getDescription() != null &&
                content.getDescription().toLowerCase(Locale.ROOT).contains(lowerKeyword);

        boolean inTags = content.getTagToContent() != null &&
                content.getTagToContent().stream()
                        .map(this::extractTagText)
                        .filter(tag -> tag != null)
                        .anyMatch(tag -> tag.toLowerCase(Locale.ROOT).contains(lowerKeyword));

        return inTitle || inDescription || inTags;
    }

    private boolean matchesContentType(Content content, String contentType) {
        if (contentType == null || contentType.isBlank()) {
            return true;
        }

        try {
            ContentType type = ContentType.valueOf(contentType.toUpperCase(Locale.ROOT));
            return content.getType() == type;
        } catch (Exception e) {
            return true;
        }
    }

    private boolean matchesTag(Content content, String tag) {
        if (tag == null || tag.isBlank()) {
            return true;
        }

        String lowerTag = tag.toLowerCase(Locale.ROOT);

        return content.getTagToContent() != null &&
                content.getTagToContent().stream()
                        .map(this::extractTagText)
                        .filter(tagText -> tagText != null)
                        .anyMatch(tagText -> tagText.toLowerCase(Locale.ROOT).contains(lowerTag));
    }

    private Comparator<Content> buildComparator(QueryPlanDTO plan) {
        String sort = plan.getSort();

        if ("LATEST".equalsIgnoreCase(sort)) {
            return Comparator.comparing(Content::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()));
        }

        if ("POPULAR".equalsIgnoreCase(sort)) {
            return Comparator
                    .comparing(this::safeLikeCount, Comparator.reverseOrder())
                    .thenComparing(this::safeViewCount, Comparator.reverseOrder());
        }

        return Comparator.comparing((Content c) -> calculateScore(c, plan)).reversed();
    }

    private long calculateScore(Content content, QueryPlanDTO plan) {
        long score = 0L;

        score += safeLikeCount(content) * 3L;
        score += safeViewCount(content);

        if (plan.getTag() != null && matchesTag(content, plan.getTag())) {
            score += 10L;
        }

        if (plan.getContentType() != null && matchesContentType(content, plan.getContentType())) {
            score += 5L;
        }

        if (plan.getKeyword() != null && !plan.getKeyword().isBlank()) {
            String lowerKeyword = plan.getKeyword().toLowerCase(Locale.ROOT);

            if (content.getTitle() != null &&
                content.getTitle().toLowerCase(Locale.ROOT).contains(lowerKeyword)) {
                score += 20L;
            }

            if (content.getDescription() != null &&
                content.getDescription().toLowerCase(Locale.ROOT).contains(lowerKeyword)) {
                score += 8L;
            }
        }

        if (content.getCreatedAt() != null &&
            content.getCreatedAt().isAfter(LocalDateTime.now().minusDays(14))) {
            score += 5L;
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
                tags
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
}