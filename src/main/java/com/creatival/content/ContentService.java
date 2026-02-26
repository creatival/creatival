package com.creatival.content;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.creatival.FileUtil;
import com.creatival.content.DTO.CreateNovelDTO;
import com.creatival.content.DTO.CreateNovelEpisodeDTO;
import com.creatival.content.DTO.ResponseNovelDetail;
import com.creatival.content.DTO.ResponseNovelEpisodeDetail;
import com.creatival.content.DTO.ResponseNovelEpisodeList;
import com.creatival.content.DTO.ResponseNovelList;
import com.creatival.content.DTO.UpdateNovelDTO;
import com.creatival.content.DTO.UpdateNovelEpisodeDTO;
import com.creatival.content.Enum.ContentType;
import com.creatival.content.repository.ContentRepository;
import com.creatival.content.repository.EpisodeRepository;
import com.creatival.content.repository.SeriesRepository;
import com.creatival.user.UserRepository;
import com.creatival.user.UserService;
import com.creatival.user.Users;


import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ContentService {
	private final ContentRepository contentRepository;
	private final SeriesRepository seriesRepository;
	private final EpisodeRepository episodeRepository;
	private final UserService userService;
	private final FileUtil fileUtil;
	
	
	//공통 라인 //
	
	public Episode getPrevEpisode(Episode episode) {
		Series series = episode.getSeries();
		Optional<Episode> prevEpisode = episodeRepository.findFirstBySeriesAndEpisodeNumLessThanOrderByEpisodeNumDesc(series, episode.getEpisodeNum());
		if(prevEpisode.isEmpty()) {
			System.out.println("이전 에피소드 없음");
			return null;
		}
		return prevEpisode.get();
	}
	public Episode getNextEpisode(Episode episode) {
		Series series = episode.getSeries();
		Optional<Episode> nextEpisode = episodeRepository.findFirstBySeriesAndEpisodeNumGreaterThanOrderByEpisodeNumAsc(series, episode.getEpisodeNum());
		if(nextEpisode.isEmpty()) {
			System.out.println("다음 에피소드 없음");
			return null;
		}
		return nextEpisode.get();
	}
	// 공통 라인 //
	
	// 소설 라인 // 
	@Transactional
	public void createCotentNovel(CreateNovelDTO createNovelDTO, Users user) throws IOException {
		String imgurl = null;
		
		if(createNovelDTO.getThumbnailFile() != null) {
			imgurl = fileUtil.saveImage(createNovelDTO.getThumbnailFile(), "thumbnail");
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
	
	public void delete(Content content) {
		contentRepository.delete(content);
	}
	
	public void updateContentNovel(UpdateNovelDTO updateNovelDTO, Long id, String username) {
		Optional<Content> optional = contentRepository.findById(id);
		if(optional.isEmpty()) {
			new IllegalArgumentException("소설 수정 정보가 전달되지 않았습니다.");
		}
		Content content = optional.get();
		Users user = userService.getUserByUsername(username);
		if(!content.getUser().getId().equals(user.getId())) { //id는 Long으로 이루워져있는데 놀랍게도 Long은 개체라서 이게 맞단다.
			new IllegalArgumentException("본인이 쓴 소설만 수정할 수 있습니다.");
		}
		content.setTitle(updateNovelDTO.getTitle());
		content.setDescription(updateNovelDTO.getDescription());
		content.setVisibility(updateNovelDTO.getVisibility());
		content.setAllowComment(updateNovelDTO.isAllowComment());
		
		Series series = content.getSeries();
		series.setEnd(updateNovelDTO.isEnd());
		
		contentRepository.save(content);
		seriesRepository.save(series);
	}

	public void updateNovelThumbnail(Long id, MultipartFile img, String name) throws IOException {
		Optional<Content> optional = contentRepository.findById(id);
		if(optional.isEmpty()) {
			return;
		}
		Content content = optional.get();
		content.setThumbnailImgUrl(fileUtil.saveImage(img, "thumbnail"));
		contentRepository.save(content);
	}

	
	public void createNovelEpisode(Long id,CreateNovelEpisodeDTO createNovelEpisodeDTO, String username) {
		Content content = contentRepository.findById(id).get();
		Series series = content.getSeries();
		Integer maxNum = episodeRepository.findByMaxEpisodeNumBySeries(series);
		
		if(maxNum == null) maxNum=0;
		
		Episode episode = Episode.builder()
				.title(createNovelEpisodeDTO.getTitle())
				.novelContent(createNovelEpisodeDTO.getNovelContent())
				.isFree(createNovelEpisodeDTO.isFree())
				.note(createNovelEpisodeDTO.getNote())
				.series(series)
				.episodeNum(maxNum+1)
				.build();
		episodeRepository.save(episode);
		
		// 시리즈에 저장되는 총 화 수
		int totalCount = episodeRepository.countBySeries(series);
		series.setTotalEpisode(totalCount);
		seriesRepository.save(series);
	}
	
	public Page<ResponseNovelEpisodeList> getEpisodeBySeries(Series series,int page) {
		Pageable pageable = PageRequest.of(page, 10, Sort.by("episodeNum").descending());
		Page<Episode> episodeList = episodeRepository.findBySeriesOrderByEpisodeNumAsc(series, pageable);
		
		return episodeList.map(episode -> ResponseNovelEpisodeList.from(episode));
	}
	
	public Episode getNovelEpisode(Long episodeId) {
		Episode episode = episodeRepository.findById(episodeId).get();
		return episode;
	}
	public void updateContentNovelEpisode(UpdateNovelEpisodeDTO updateNovelEpisodeDTO, String name) {
		Episode episode = episodeRepository.findById(updateNovelEpisodeDTO.getId()).get();
		if(!episode.getSeries().getContent().getUser().getUsername().equals(name)) {
			new IllegalArgumentException("에피소드 수정은 본인만 할 수 있습니다.");
		}
		episode.setTitle(updateNovelEpisodeDTO.getTitle());
		episode.setNovelContent(updateNovelEpisodeDTO.getNovelContent());
		episode.setNote(updateNovelEpisodeDTO.getNote());
		episode.setFree(updateNovelEpisodeDTO.isFree());
		episode.setDeleted(updateNovelEpisodeDTO.isDeleted());
		
		episodeRepository.save(episode);
	}
	
	public void deleteNovelEpisode(Episode episode) {
		episodeRepository.delete(episode);
	}
	// 소설 라인 // 
}
