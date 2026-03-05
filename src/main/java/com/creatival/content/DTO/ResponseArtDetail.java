package com.creatival.content.DTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.creatival.content.Content;
import com.creatival.content.ContentFile;
import com.creatival.content.Enum.ContentType;
import com.creatival.content.Enum.OwnerType;
import com.creatival.content.Enum.Visibility;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter @Builder
@AllArgsConstructor
public class ResponseArtDetail {
	private Long id;
    private String title;
    private String description;
    private Long viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 2차 창작 관련
    private boolean isFanWork;
    private Long originalContentId;
    private String originalContentTitle;

    // 댓글 허용 여부
    private boolean isAllowComment;

    // 작가 정보
    private Long userId;
    private String username;
    private String displayname;
    private String profileImgUrl;

    // 이미지 파일 리스트
    private List<ResponseContentFileImageDTO> fileList;
    
    public static ResponseArtDetail from(Content content, List<ContentFile> files) {
        return ResponseArtDetail.builder()
                .id(content.getId())
                .title(content.getTitle())
                .description(content.getDescription())
                .viewCount(content.getViewCount())
                .createdAt(content.getCreatedAt())
                .isFanWork(content.isFanWork())
                .isAllowComment(content.isAllowComment())
                .originalContentId(content.getOriginalContent() != null ? content.getOriginalContent().getId() : null)
                .originalContentTitle(content.getOriginalContent() != null ? content.getOriginalContent().getTitle() : null)
                .userId(content.getUser().getId())
                .username(content.getUser().getUsername())
                .displayname(content.getUser().getDisplayName())
                .profileImgUrl(content.getUser().getProfileImgUrl())
                .fileList(files.stream()
                        .map(ResponseContentFileImageDTO::from)
                        .collect(Collectors.toList()))
                .build();
    }
}
