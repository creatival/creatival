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
import com.creatival.content.DTO.CreateFileDTO;
import com.creatival.content.DTO.CreateMusicDTO;
import com.creatival.content.DTO.CreateNovelDTO;
import com.creatival.content.DTO.CreateNovelEpisodeDTO;
import com.creatival.content.DTO.CreateVideoDTO;
import com.creatival.content.DTO.ResponseArtList;
import com.creatival.content.DTO.ResponseContentFileDTO;
import com.creatival.content.DTO.ResponseContentListForProject;
import com.creatival.content.DTO.ResponseFileDetailDTO;
import com.creatival.content.DTO.ResponseFileListDTO;
import com.creatival.content.DTO.ResponseMusicDetailDTO;
import com.creatival.content.DTO.ResponseMusicListDTO;
import com.creatival.content.DTO.ResponseNovelDetail;
import com.creatival.content.DTO.ResponseNovelEpisodeDetail;
import com.creatival.content.DTO.ResponseNovelEpisodeList;
import com.creatival.content.DTO.ResponseNovelList;
import com.creatival.content.DTO.ResponseVideoDetailDTO;
import com.creatival.content.DTO.ResponseVideoListDTO;
import com.creatival.content.DTO.UpdateArtDTO;
import com.creatival.content.DTO.UpdateFileDTO;
import com.creatival.content.DTO.UpdateMusicDTO;
import com.creatival.content.DTO.UpdateNovelDTO;
import com.creatival.content.DTO.UpdateNovelEpisodeDTO;
import com.creatival.content.DTO.UpdateVideoDTO;
import com.creatival.content.Enum.ContentType;
import com.creatival.content.Enum.Visibility;
import com.creatival.content.repository.ContentFileRepository;
import com.creatival.content.repository.ContentRepository;
import com.creatival.content.repository.EpisodeRepository;
import com.creatival.content.repository.SeriesRepository;
import com.creatival.like.LikeService;
import com.creatival.like.Likes;
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
		
		Content content = Content.builder()
				.title(createNovelDTO.getTitle())
				.description(createNovelDTO.getDescription())
				.user(user)
				.visibility(createNovelDTO.getVisibility())
				.ownerType(createNovelDTO.getOwnerType())
				.type(ContentType.NOVEL)
				.isAllowComment(createNovelDTO.isAllowComment())
				.isFanWork(createNovelDTO.isFanWork())
				.build();
		String imgurl = "/upload/images/thumbnail/";
		
		if(createNovelDTO.getThumbnailFile() != null && !createNovelDTO.getThumbnailFile().isEmpty()) {
			imgurl += fileUtil.saveImage(createNovelDTO.getThumbnailFile(), "thumbnail");
			content.setThumbnailImgUrl(imgurl);
		}
		if(content.isFanWork() && createNovelDTO.getOriginalContentId() != null) {
			content.setOriginalContent(contentRepository.findById(createNovelDTO.getOriginalContentId()).orElseThrow(() -> new IllegalArgumentException("원본 없음")));
		}
		if(createNovelDTO.getProjectTag() != null && !createNovelDTO.getProjectTag().isBlank()) {
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
		if(createArtDTO.getProjectTag() != null && !createArtDTO.getProjectTag().isBlank()) {
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
	public void createContentVideo(@Valid CreateVideoDTO createVideoDTO, Users user) throws IOException {
		String imgurl = "/upload/images/thumbnail/";
		
		if(createVideoDTO.getThumbnailFile() != null) {
			imgurl += fileUtil.saveImage(createVideoDTO.getThumbnailFile(), "thumbnail");
		}
		Content content = Content.builder()
				.title(createVideoDTO.getTitle())
				.description(createVideoDTO.getDescription())
				.user(user)
				.visibility(createVideoDTO.getVisibility())
				.ownerType(createVideoDTO.getOwnerType())
				.type(ContentType.VIDEO)
				.isAllowComment(createVideoDTO.isAllowComment())
				.isFanWork(createVideoDTO.isFanWork())
				.ThumbnailImgUrl(imgurl)
				.build();
		if(content.isFanWork() && createVideoDTO.getOriginalContentId() != null) {
			content.setOriginalContent(contentRepository.findById(createVideoDTO.getOriginalContentId()).orElseThrow(() -> new IllegalArgumentException("원본 없음")));
		}
		if(createVideoDTO.getProjectTag() != null && !createVideoDTO.getProjectTag().isBlank()) {
			Project project = teamService.getProjectTag(createVideoDTO.getProjectTag());
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
		
		String tagString = createVideoDTO.getTagString();
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
		
		contentFileService.createContentFileVideoForContent(content, createVideoDTO.getVideoFile(), "video");
	}
	public List<ResponseVideoListDTO> getVideoList(int page) {
		Pageable pageable = PageRequest.of(page, 12, Sort.by("createdAt").descending());
		Page<Content> contents = contentRepository.findByType(ContentType.VIDEO,pageable);
		
		return contents.map(content -> ResponseVideoListDTO.from(content)).toList();
	}
	public ResponseVideoDetailDTO getVideoDetailById(Long id) {
		Optional<Content> optional = contentRepository.findById(id);
		if(optional.isEmpty()) {
			return null;
		} else {
			Content content = optional.get();
			List<ContentFile> contentFile = contentFileService.getContentFileByContent(content);
			System.out.println(contentFile.getFirst().getFileName());
			return ResponseVideoDetailDTO.from(content,contentFile.getFirst());
		}
		
	}
	public Content getVideoById(Long id) {
		Optional<Content> optional = contentRepository.findById(id);
		if(optional.isEmpty()) {
			return null;
		}
		return optional.get();
		
	}
	public void updateContentVideo(@Valid UpdateVideoDTO dto) throws IOException {
		Optional<Content> optional = contentRepository.findById(dto.getId());
		if(optional.isEmpty()) {
			throw new IllegalArgumentException("수정할려는 콘텐츠가 없습니다.");
		}
		Content content = optional.get();
		content.setTitle(dto.getTitle());
		content.setDescription(dto.getDescription());
		content.setVisibility(dto.getVisibility());
		content.setAllowComment(dto.isAllowComment());
		
		if(dto.getThumbnailFile() != null && !dto.getThumbnailFile().isEmpty()) {
			String imgurl = "/upload/images/thumbnail/";
			imgurl += fileUtil.saveImage(dto.getThumbnailFile(), "thumbnail");
			content.setThumbnailImgUrl(imgurl);
		}
		
		contentRepository.save(content);
		
		
		
		
		
		if(dto.getVideoFile()!=null && !dto.getVideoFile().isEmpty()) {
			ContentFile contentFile = contentFileService.getContentFileByContent(content).getFirst();
			contentFileService.delete(contentFile);
			contentFileService.createContentFileVideoForContent(content, dto.getVideoFile(), "video");
		}
	}
	public void createContentMusic(@Valid CreateMusicDTO createMusicDTO, Users user) throws IOException {
		String imgurl = "/upload/images/thumbnail/";
		
		if(createMusicDTO.getThumbnailFile() != null) {
			imgurl += fileUtil.saveImage(createMusicDTO.getThumbnailFile(), "thumbnail");
		}
		Content content = Content.builder()
				.title(createMusicDTO.getTitle())
				.description(createMusicDTO.getDescription())
				.user(user)
				.visibility(createMusicDTO.getVisibility())
				.ownerType(createMusicDTO.getOwnerType())
				.type(ContentType.MUSIC)
				.isAllowComment(createMusicDTO.isAllowComment())
				.isFanWork(createMusicDTO.isFanWork())
				.ThumbnailImgUrl(imgurl)
				.build();
		if(content.isFanWork() && createMusicDTO.getOriginalContentId() != null) {
			content.setOriginalContent(contentRepository.findById(createMusicDTO.getOriginalContentId()).orElseThrow(() -> new IllegalArgumentException("원본 없음")));
		}
		if(createMusicDTO.getProjectTag() != null && !createMusicDTO.getProjectTag().isBlank()) {
			Project project = teamService.getProjectTag(createMusicDTO.getProjectTag());
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
		
		String tagString = createMusicDTO.getTagString();
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
		
		contentFileService.createContentFileMusicForContent(content, createMusicDTO.getMusicFile(), "music");
	}
	public List<ResponseMusicListDTO> getMusicList(int page) {
		Pageable pageable = PageRequest.of(page, 12, Sort.by("createdAt").descending());
		Page<Content> contents = contentRepository.findByType(ContentType.MUSIC,pageable);
		
		return contents.map(content -> ResponseMusicListDTO.from(content)).toList();
	}
	public ResponseMusicDetailDTO getMusicDetailById(Long id) {
		Optional<Content> optional = contentRepository.findById(id);
		if(optional.isEmpty()) {
			return null;
		} else {
			Content content = optional.get();
			List<ContentFile> contentFile = contentFileService.getContentFileByContent(content);
			System.out.println(contentFile.getFirst().getFileName());
			return ResponseMusicDetailDTO.from(content,contentFile.getFirst());
		}
	}
	
	public void updateContentMusic(@Valid UpdateMusicDTO dto) throws IOException {
		Optional<Content> optional = contentRepository.findById(dto.getId());
		if(optional.isEmpty()) {
			throw new IllegalArgumentException("수정할려는 콘텐츠가 없습니다.");
		}
		Content content = optional.get();
		content.setTitle(dto.getTitle());
		content.setDescription(dto.getDescription());
		content.setVisibility(dto.getVisibility());
		content.setAllowComment(dto.isAllowComment());
		if(dto.getThumbnailFile() != null && !dto.getThumbnailFile().isEmpty()) {
			String imgurl = "/upload/images/thumbnail/";
			imgurl += fileUtil.saveImage(dto.getThumbnailFile(), "thumbnail");
			content.setThumbnailImgUrl(imgurl);
		}
		contentRepository.save(content);
		
		
		
		
		
		if(dto.getMusicFile()!=null && !dto.getMusicFile().isEmpty()) {
			ContentFile contentFile = contentFileService.getContentFileByContent(content).getFirst();
			contentFileService.delete(contentFile);
			contentFileService.createContentFileMusicForContent(content, dto.getMusicFile(), "music");
		}
	}
	public void createContentFile(@Valid CreateFileDTO createFileDTO, Users user) throws IOException {
		Content content = Content.builder()
				.title(createFileDTO.getTitle())
				.description(createFileDTO.getDescription())
				.user(user)
				.visibility(createFileDTO.getVisibility())
				.ownerType(createFileDTO.getOwnerType())
				.type(ContentType.FILE)
				.isAllowComment(createFileDTO.isAllowComment())
				.isFanWork(createFileDTO. isFanWork())
				.build();
		if(content.isFanWork() && createFileDTO.getOriginalContentId() != null) {
			content.setOriginalContent(contentRepository.findById(createFileDTO.getOriginalContentId()).orElseThrow(() -> new IllegalArgumentException("원본 없음")));
		}
		if(createFileDTO.getProjectTag() != null && !createFileDTO.getProjectTag().isBlank()) {
			Project project = teamService.getProjectTag(createFileDTO.getProjectTag());
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
		
		String tagString = createFileDTO.getTagList();
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
		
		contentFileService.createContentFileFileForContent(content, createFileDTO.getFile(), "file");
		contentFileService.createContentFileImageForContent(content, createFileDTO.getExtraImg(), "previewImg");
		
	}
	public Page<ResponseFileListDTO> getFileList(int page) {
		Pageable pageable = PageRequest.of(page, 12, Sort.by("createdAt").descending());
		Page<Content> fileList = contentRepository.findByType(ContentType.FILE, pageable);
		return fileList.map(file -> ResponseFileListDTO.from(file));
	}
	public ResponseFileDetailDTO getfIleDetail(Long id) {
		Optional<Content> optional = contentRepository.findById(id);
		if(optional.isEmpty()) {
			throw new IllegalArgumentException("보려는 content를 찾을 수 없습니다.");
		}
		Content content = optional.get();
		List<ContentFile> files = contentFileService.getContentFileByContent(content);
		
		ContentFile file = files.stream().filter(f -> "FILE".equals(f.getFileType()))
				.findFirst().orElseThrow(() -> new IllegalStateException("필수 파일이 없습니다."));
		ContentFile previewImg = files.stream().filter(f -> "ART".equals(f.getFileType()))
				.findFirst().orElse(null);
		
		if(previewImg==null) {
			return ResponseFileDetailDTO.from(content, file);
		} else {
			return ResponseFileDetailDTO.from(content, file, previewImg);
		}
		
	}
	public void updateContentFile(Long id, @Valid UpdateFileDTO dto) {
		Optional<Content> optional = contentRepository.findById(id);
		if(optional.isEmpty()) {
			throw new IllegalArgumentException("content를 찾을 수 없습니다.");
		}
		Content content = optional.get();
		content.setTitle(dto.getTitle());
		content.setDescription(dto.getDescription());
		content.setAllowComment(dto.isAllowComment());
		content.setVisibility(dto.getVisibility());
		
		List<ContentFile> files = contentFileService.getContentFileByContent(content);
		if(dto.getFile() != null && !dto.getFile().isEmpty()) {
			ContentFile file = files.stream().filter(f -> "FILE".equals(f.getFileType()))
					.findFirst().orElseThrow(() -> new IllegalStateException("필수 파일이 없습니다."));
			contentFileService.delete(file);
			contentFileService.createContentFileFileForContent(content, dto.getFile(), "file");
		}
		if(dto.getExtraImg() != null && !dto.getExtraImg().isEmpty()) {
			ContentFile previewImg = files.stream().filter(f -> "ART".equals(f.getFileType()))
					.findFirst().orElse(null);
			if(previewImg!=null) {
				contentFileService.delete(previewImg);
			}
			
			contentFileService.createContentFileImageForContent(content, dto.getExtraImg(), "previewImg");
		}
		
		contentRepository.save(content);

		
	}
	public Episode getEpisodeById(Long targetId) {
		Optional<Episode> episode = episodeRepository.findById(targetId);
		if(episode.isEmpty()) {
			return null;
		}
		return episode.get();
	}
	
	//index 용
	
	public List<ResponseArtList> getTopArt(int qty) {
		Pageable pageable = PageRequest.of(0, qty, Sort.by("likeCount").descending());
		Page<Content> contents = contentRepository.findByType(ContentType.ART,pageable);
		
		return contents.map(content -> ResponseArtList.from(content, contentFileService.getContentFileThumbnail(content))).toList();
	}
	public List<ResponseNovelList> getTopNovel(int qty) {
		Pageable pageable = PageRequest.of(0, qty, Sort.by("likeCount").descending());
		Page<Content> contents = contentRepository.findByType(ContentType.NOVEL,pageable);
		
		return contents.map(content -> ResponseNovelList.from(content, content.getSeries())).toList();
	}
	public List<ResponseVideoListDTO> getTopVideo(int qty) {
		Pageable pageable = PageRequest.of(0, qty, Sort.by("likeCount").descending());
		Page<Content> contents = contentRepository.findByType(ContentType.VIDEO,pageable);
		
		return contents.map(content -> ResponseVideoListDTO.from(content)).toList();
	}
	public List<ResponseVideoListDTO> getTopMusic(int qty) {
		Pageable pageable = PageRequest.of(0, qty, Sort.by("likeCount").descending());
		Page<Content> contents = contentRepository.findByType(ContentType.MUSIC,pageable);
		
		return contents.map(content -> ResponseVideoListDTO.from(content)).toList();
	}
	
	public List<ResponseArtList> getArtByUser(Users user) {
		List<Content> contents = contentRepository.findByUserAndTypeOrderByCreatedAtDesc(user, ContentType.ART);
		return contents.stream().map(content -> ResponseArtList.from(content, contentFileService.getContentFileThumbnail(content))).toList();
	}
	
	public List<ResponseNovelList> getNovelByUser(Users user) {
		List<Content> contents = contentRepository.findByUserAndTypeOrderByCreatedAtDesc(user, ContentType.NOVEL);
		return contents.stream().map(content -> ResponseNovelList.from(content, content.getSeries())).toList();
	}
	
	public List<ResponseMusicListDTO> getMusicByUser(Users user) {
		List<Content> contents = contentRepository.findByUserAndTypeOrderByCreatedAtDesc(user, ContentType.MUSIC);
		return contents.stream().map(content -> ResponseMusicListDTO.from(content)).toList();
	}
	public List<ResponseVideoListDTO> getVideoByUser(Users user) {
		List<Content> contents = contentRepository.findByUserAndTypeOrderByCreatedAtDesc(user, ContentType.MUSIC);
		return contents.stream().map(content -> ResponseVideoListDTO.from(content)).toList();
	}
	
	
	// only public
	
	public List<ResponseArtList> getArtByUserOnlyPublic(Users user) {
		List<Content> contents = contentRepository.findByUserAndTypeAndVisibilityOrderByCreatedAtDesc(user, ContentType.ART,Visibility.PUBLIC);
		return contents.stream().map(content -> ResponseArtList.from(content, contentFileService.getContentFileThumbnail(content))).toList();
	}
	
	
	public List<ResponseNovelList> getNovelByUserOnlyPublic(Users user) {
		List<Content> contents = contentRepository.findByUserAndTypeAndVisibilityOrderByCreatedAtDesc(user, ContentType.NOVEL,Visibility.PUBLIC);
		return contents.stream().map(content -> ResponseNovelList.from(content, content.getSeries())).toList();
	}
	
	public List<ResponseMusicListDTO> getMusicByUserOnlyPublic(Users user) {
		List<Content> contents = contentRepository.findByUserAndTypeAndVisibilityOrderByCreatedAtDesc(user, ContentType.MUSIC,Visibility.PUBLIC);
		return contents.stream().map(content -> ResponseMusicListDTO.from(content)).toList();
	}
	public List<ResponseVideoListDTO> getVideoByUserOnlyPublic(Users user) {
		List<Content> contents = contentRepository.findByUserAndTypeAndVisibilityOrderByCreatedAtDesc(user, ContentType.VIDEO,Visibility.PUBLIC);
		return contents.stream().map(content -> ResponseVideoListDTO.from(content)).toList();
	}
	public void upViewCount(Content content) {
		content.setViewCount(content.getViewCount()+1);
		contentRepository.save(content);
	}
	public void downLikeCount(Long targetId) {
		Content content = getContent(targetId);
		if(content ==null) {
			return;
		}
		content.setLikeCount(content.getLikeCount()-1);
		contentRepository.save(content);
	}
	public void upLikeCount(Long targetId) {
		Content content = getContent(targetId);
		if(content==null) {
			return;
		}
		content.setLikeCount(content.getLikeCount()+1);
		contentRepository.save(content);
	}
	public List<ResponseArtList> getAutherArt(Users user) {
		List<Content> list = contentRepository.findTop4ByUserAndTypeOrderByCreatedAtDesc(user, ContentType.ART);
		return list.stream().map(content -> ResponseArtList.from(content, contentFileService.getContentFileThumbnail(content))).toList();
	}
	public Page<ResponseArtList> getMoreArt(int page) {
		Pageable pageable = PageRequest.of(page, 8, Sort.by("id").descending());
		Page<Content> pages = contentRepository.findByType(ContentType.ART, pageable);
		return pages.map(content -> ResponseArtList.from(content, contentFileService.getContentFileThumbnail(content)));
	}
	
	
}
