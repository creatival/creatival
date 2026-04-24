package com.creatival.content;

import java.util.List;

import org.springframework.stereotype.Service;

import com.creatival.chat.dto.RecommendedContentDTO;
import com.creatival.content.repository.ContentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContentSearchService {

    private final ContentRepository contentRepository;

    public List<RecommendedContentDTO> search(String keyword) {
        List<Content> contents = contentRepository.findByTitleContaining(keyword);

        return contents.stream()
                .limit(5)
                .map(this::toDto)
                .toList();
    }

    private RecommendedContentDTO toDto(Content content) {
        return new RecommendedContentDTO(
                content.getId(),
                content.getTitle(),
                content.getType().toString(),
                "/content/" + content.getType().toString().toLowerCase() + "/detail/" + content.getId(),
                content.getLikeCount(),
                content.getViewCount(),
                List.of(),
                content.isPaid()
        );
    }
}
