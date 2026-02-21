package com.creatival.content;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.creatival.content.DTO.CreateNovelDTO;
import com.creatival.content.DTO.ResponseNovelDetail;
import com.creatival.content.DTO.ResponseNovelList;
import com.creatival.content.Enum.ContentType;
import com.creatival.content.repository.ContentRepository;
import com.creatival.content.repository.SeriesRepository;
import com.creatival.user.Users;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ContentService {
	private final ContentRepository contentRepository;
	private final SeriesRepository seriesRepository;
	private static final String UPLOAD_DIR = "src/main/resources/static/images/Thumbnail";
	
	// 소설 라인 // 
	@Transactional
	public void createCotentNovel(CreateNovelDTO createNovelDTO, Users user) throws IOException {
		
		String imgurl = null;
		
		if(createNovelDTO.getThumbnailFile()!=null)  {
			String fileName = UUID.randomUUID().toString()+"_"+ createNovelDTO.getThumbnailFile().getOriginalFilename();
			Path filePath = Paths.get(UPLOAD_DIR, fileName);
			Files.createDirectories(filePath.getParent());
			Files.write(filePath, createNovelDTO.getThumbnailFile().getBytes());
			imgurl = "/images/Thumbnail/"+fileName;
		}
		
		Content content = Content.builder()
				.title(createNovelDTO.getTitle())
				.description(createNovelDTO.getDescription())
				.user(user)
				.visibility(createNovelDTO.getVisibility())
				.onwerType(createNovelDTO.getOwnerType())
				.type(ContentType.NOVEL)
				.isAllowComment(createNovelDTO.isAllowComment())
				.isFanWork(createNovelDTO.isFanWork())
				.ThumbnailImgUrl(imgurl)
				.build();
		if(content.isFanWork() && createNovelDTO.getOriginalContentId() != null) {
			content.setOriginalContent(contentRepository.findById(createNovelDTO.getOriginalContentId()).orElseThrow(() -> new IllegalArgumentException("원본 없음")));
		}
		
		contentRepository.save(content);
		
		Series series = Series.builder()
				.content(content)
				.isEnd(createNovelDTO.isEnd())
				.build();
		seriesRepository.save(series);
	}
	
	public Page<ResponseNovelList> getNovelList(int page) {
		Pageable pageable = PageRequest.of(page, 12, Sort.by("createdAt").descending());
		Page<Content> contents = contentRepository.findAll(pageable);
		
		return contents.map(content -> ResponseNovelList.from(content, content.getSeries()));
	}
	
	public Content getNovel(Long id) {
		return contentRepository.findById(id).get();
	}
	
	// 소설 라인 // 
}
