package com.creatival.content;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
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
import com.creatival.content.DTO.CreateArtDTO;
import com.creatival.content.DTO.CreateNovelDTO;
import com.creatival.content.DTO.CreateNovelEpisodeDTO;
import com.creatival.content.DTO.ResponseArtList;
import com.creatival.content.DTO.ResponseContentListForProject;
import com.creatival.content.DTO.ResponseNovelDetail;
import com.creatival.content.DTO.ResponseNovelEpisodeDetail;
import com.creatival.content.DTO.ResponseNovelEpisodeList;
import com.creatival.content.DTO.ResponseNovelList;
import com.creatival.content.DTO.UpdateArtDTO;
import com.creatival.content.DTO.UpdateNovelDTO;
import com.creatival.content.DTO.UpdateNovelEpisodeDTO;
import com.creatival.content.Enum.ContentType;
import com.creatival.content.repository.ContentRepository;
import com.creatival.content.repository.EpisodeRepository;
import com.creatival.content.repository.SeriesRepository;
import com.creatival.tag.TagService;
import com.creatival.team.Project;
import com.creatival.team.TeamMember;
import com.creatival.team.TeamService;
import com.creatival.user.UserRepository;
import com.creatival.user.UserService;
import com.creatival.user.Users;


import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ContentService {

    private final TeamService teamService;

    private final TagService tagService;

    private final ContentFileService contentFileService;
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
	
	public Content getContent(Long id) {
		Optional<Content> content = contentRepository.findById(id);
		if(content.isPresent()) {
			return content.get();
		}
		return null;
	}
	
	public List<ResponseContentListForProject> getContentByProject(Project project) {
		List<Content> list = contentRepository.findByProject(project);
		List<ResponseContentListForProject> contents = new ArrayList<>();
		for(Content content : list) {
			if(content.getType() == ContentType.NOVEL) {
				contents.add(ResponseContentListForProject.fromNovel(content));
			} else if(content.getType()==ContentType.ART) {
				ContentFile contentFile = contentFileService.getContentFileThumbnail(content);
				contents.add(ResponseContentListForProject.fromArt(content, contentFile.getFileUrl()));
			}
		}
		return contents;
	}
	// 공통 라인 //
	
	// 소설 라인 // 
	@Transactional
	public void createCotentNovel(CreateNovelDTO createNovelDTO, Users user) throws IOException {
		String imgurl = "/upload/images/thumbnail/";
		
		if(createNovelDTO.getThumbnailFile() != null) {
			imgurl += fileUtil.saveImage(createNovelDTO.getThumbnailFile(), "thumbnail");
		}
		Content content = Content.builder()
				.title(createNovelDTO.getTitle())
				.description(createNovelDTO.getDescription())
				.user(user)
				.visibility(createNovelDTO.getVisibility())
				.ownerType(createNovelDTO.getOwnerType())
				.type(ContentType.NOVEL)
				.isAllowComment(createNovelDTO.isAllowComment())
				.isFanWork(createNovelDTO.isFanWork())
				.ThumbnailImgUrl(imgurl)
				.build();
		if(content.isFanWork() && createNovelDTO.getOriginalContentId() != null) {
			content.setOriginalContent(contentRepository.findById(createNovelDTO.getOriginalContentId()).orElseThrow(() -> new IllegalArgumentException("원본 없음")));
		}
		if(!createNovelDTO.getProjectTag().isBlank() || createNovelDTO.getProjectTag() != null) {
			Project project = teamService.getProjectTag(createNovelDTO.getProjectTag());
			if(project==null) {
				throw new IllegalArgumentException("projectTag가 존재하지 않는 tag입니다!");
			}
			TeamMember member = teamService.getMemberForTeamByUser(project.getTeam(), user);
			if(member == null) {
				throw new IllegalArgumentException("오직 해당 프로젝트의 팀에 소속된 멤버들만 추가할 수 있습니다!");
			}
			content.setProject(project);
		}
		contentRepository.save(content);
		
		String tagString = createNovelDTO.getTagString();
	    if (tagString != null && !tagString.isEmpty()) {
	        // 쉼표로 구분된 문자열을 배열로 변환
	        String[] tags = tagString.split(",");
	        
	        for (String tagName : tags) {
	            String trimmedTag = tagName.trim();
	            if (!trimmedTag.isEmpty()) {
	                // 태그를 저장하고 소설과 연결하는 로직 호출
	                // 예: tagService.addTagToContent(novel, trimmedTag);
	            	tagService.createTagForContent(content, tagName, user.getUsername());
	            }
	        }
	    }
		
		Series series = Series.builder()
				.content(content)
				.isEnd(createNovelDTO.isEnd())
				.build();
		seriesRepository.save(series);
	}
	
	public Page<ResponseNovelList> getNovelList(int page) {
		Pageable pageable = PageRequest.of(page, 12, Sort.by("createdAt").descending());
		Page<Content> contents = contentRepository.findByType(ContentType.NOVEL,pageable);
		
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
		String imgUrl = "/upload/images/thumbnail/";
		content.setThumbnailImgUrl(imgUrl+fileUtil.saveImage(img, "thumbnail"));
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
	
	@Transactional
	public Long getFirstEpisodeId(Content content) {
	    Series series = content.getSeries();
	    
	    if (series == null) {
	        return null;
	    }

	    return episodeRepository.findFirstBySeriesOrderByEpisodeNumAsc(series)
	            .map(Episode::getId)
	            .orElse(null); 
	}
	// 소설 라인 // 
	
	// 그림 라인 //
	public void createContentArt(CreateArtDTO createArtDTO, Users user) {
		if(createArtDTO.getImages().isEmpty() || createArtDTO.getImages() == null) {
			throw new IllegalArgumentException("파일이 비어있습니다.");
		}
		
		Content content = Content.builder()
				.user(user)
				.ownerType(createArtDTO.getOwnerType())
				.type(ContentType.ART)
				.title(createArtDTO.getTitle())
				.description(createArtDTO.getDescription())
				.ThumbnailImgUrl(createArtDTO.getThumbnailImgUrl())
				.visibility(createArtDTO.getVisibility())
				.isAllowComment(createArtDTO.isAllowComment())
				.isFanWork(createArtDTO.isFanWork())
				.build();
		if(content.isFanWork() && createArtDTO.getOriginalContentId() != null) {
			content.setOriginalContent(contentRepository.findById(createArtDTO.getOriginalContentId()).orElseThrow(() -> new IllegalArgumentException("원본 없음")));
		}
		if(!createArtDTO.getProjectTag().isBlank() || createArtDTO.getProjectTag() != null) {
			Project project = teamService.getProjectTag(createArtDTO.getProjectTag());
			if(project==null) {
				throw new IllegalArgumentException("projectTag가 존재하지 않는 tag입니다!");
			}
			TeamMember member = teamService.getMemberForTeamByUser(project.getTeam(), user);
			if(member == null) {
				throw new IllegalArgumentException("오직 해당 프로젝트의 팀에 소속된 멤버들만 추가할 수 있습니다!");
			}
			content.setProject(project);
		}
		
		contentRepository.save(content);
		System.out.println(createArtDTO.getImages().size() + "개수");
		contentFileService.createContentFileImageForContent(content, createArtDTO.getImages(), "art");
	}
	
	public Page<ResponseArtList> getArtList(int page) {
		Pageable pageable = PageRequest.of(page, 12, Sort.by("createdAt").descending());
		Page<Content> contents = contentRepository.findByType(ContentType.ART,pageable);
		
		return contents.map(content -> ResponseArtList.from(content, contentFileService.getContentFileThumbnail(content)));
	}
	
	
	public Content getArt(Long id) {
		Optional<Content> content = contentRepository.findById(id);
		if(content.isPresent()) {
			return content.get();
		}
		return null;
	}
	
	@Transactional
	public void updateContentArt(Long id, UpdateArtDTO dto) throws IOException {

	    Content content = contentRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("Content not found"));

	    content.setTitle(dto.getTitle());
	    content.setDescription(dto.getDescription());
	    content.setVisibility(dto.getVisibility());
	    content.setAllowComment(dto.isAllowComment());
	    
	    if (dto.getDeleteFileIds() != null && !dto.getDeleteFileIds().isEmpty()) {
	        for (Long fileId : dto.getDeleteFileIds()) {
	            contentFileService.deleteImage(fileId);
	        }
	    }

	    if (dto.getNewFiles() != null) {
	        for (MultipartFile file : dto.getNewFiles()) {
	            if (!file.isEmpty()) {
	                contentFileService.createContentFileImageForContent(content, dto.getNewFiles(), "art");
	            }
	        }
	    }
	}
}
