package com.creatival.content;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.stereotype.Service;

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
	
	@Transactional
	public void createCotentNovel(ContentCreateDTO.createNovelDTO createNovelDTO, Users user) throws IOException {
		
		String imgurl = null;
		
		if(createNovelDTO.getThumbnailFile()!=null)  {
			String fileName = UUID.randomUUID().toString()+"_"+ createNovelDTO.getThumbnailFile().getOriginalFilename();
			Path filePath = Paths.get(UPLOAD_DIR, fileName);
			Files.createDirectories(filePath.getParent());
			Files.write(filePath, createNovelDTO.getThumbnailFile().getBytes());
			imgurl = "/img/user/"+fileName;
		}
		
		Content content = Content.builder()
				.title(createNovelDTO.getTitle())
				.description(createNovelDTO.getDescription())
				.user(user)
				.visibility(createNovelDTO.getVisibility())
				.onwerType(createNovelDTO.getOwnerType())
				.type("NOVEL")
				.isAllowComment(createNovelDTO.isAllowComment())
				.isFanWork(createNovelDTO.isFanWork())
				.ThumbanilImgUrl(imgurl)
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
}
