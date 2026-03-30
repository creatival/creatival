package com.creatival.content;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.creatival.FileUtil;
import com.creatival.content.DTO.UpdateArtDTO;
import com.creatival.content.Enum.ContentType;
import com.creatival.content.repository.ContentFileRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ContentFileService {
	private final FileUtil fileUtil;
	private final ContentFileRepository contentFileRepository;

	@Transactional
	public void createContentFileImageForContent(Content content ,List<MultipartFile> files, String category) {
		if(files == null || files.isEmpty()) {
			new IllegalArgumentException("파일이 비어있습니다.");
		}
		int sortOrder=1;
		int testNum=1;
		System.out.println(files.size());
		for(MultipartFile file : files) {
			System.out.println(testNum + "번째 반복함");
			if(file.isEmpty()) {
				continue;
			}
			try {
				System.out.println("트라이 시작");
				String fileName = fileUtil.saveImage(file, category);
				String fileUrl="/upload/images/"+category+"/" + fileName;
				ContentFile contentFile = ContentFile.createForContent("ART", fileUrl, fileName, file.getOriginalFilename() , sortOrder, content);
				System.out.println("저장된 ID: " + contentFile.getId());
				contentFileRepository.save(contentFile);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("파일 저장 및 DB 기록 중 오류 발생: " + file.getOriginalFilename(), e);
			}
			sortOrder++;
		}
	}
	@Transactional
	public void createContentFileImageForContent(Content content ,MultipartFile file, String category) {
		if(file == null || file.isEmpty()) {
			throw 	new IllegalArgumentException("파일이 비어있습니다.");
		}
		int sortOrder=1;
			try {
				String fileName = fileUtil.saveImage(file, category);
				String fileUrl="/upload/images/"+category+"/" + fileName;
				ContentFile contentFile = ContentFile.createForContent("ART", fileUrl, fileName, file.getOriginalFilename() , sortOrder, content);
				System.out.println("저장된 ID: " + contentFile.getId());
				contentFileRepository.save(contentFile);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("파일 저장 및 DB 기록 중 오류 발생: " + file.getOriginalFilename(), e);
			}
			sortOrder++;
	}
	
	public ContentFile getContentFileThumbnail(Content content) {
		if(content.getType() == ContentType.NOVEL) {
			return null;
		}
		ContentFile contentFile = contentFileRepository.findTopByContentOrderBySortOrderAsc(content);
		return contentFile;
	}
	
	public List<ContentFile> getContentFileByContent(Content content) {
		return contentFileRepository.findByContent(content);
	}
	
	@Transactional
	public void deleteImage(Long fileId) throws IOException {

	    ContentFile contentFile = contentFileRepository.findById(fileId)
	            .orElseThrow(() -> new IllegalArgumentException("파일이 존재하지 않습니다."));

	    fileUtil.deleteImage(contentFile.getFileName(), "art");

	    contentFileRepository.delete(contentFile);
	}

	public void createContentFileVideoForContent(Content content, MultipartFile videoFile, String category) {
		if(videoFile == null || videoFile.isEmpty()) {
			new IllegalArgumentException("파일이 비어있습니다.");
		}
		try {
			String fileName = fileUtil.saveVideo(videoFile, category);
			String fileUrl="/upload/videos/"+category+"/" + fileName;
			ContentFile contentFile = ContentFile.createForContent("VIDEO", fileUrl, fileName, videoFile.getOriginalFilename(), 0, content);
			System.out.println("저장된 ID: " + contentFile.getId());
			contentFileRepository.save(contentFile);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("파일 저장 및 DB 기록 중 오류 발생: " + videoFile.getOriginalFilename(), e);
		}
		
	}

	public void delete(ContentFile contentFile) {
		contentFileRepository.delete(contentFile);
		
	}

	public void createContentFileMusicForContent(Content content, MultipartFile musicFile, String category) {
		if(musicFile == null || musicFile.isEmpty()) {
			new IllegalArgumentException("파일이 비어있습니다.");
		}
		try {
			String fileName = fileUtil.saveMusic(musicFile, category);
			String fileUrl="/upload/musics/"+category+"/" + fileName;
			ContentFile contentFile = ContentFile.createForContent("MUSIC", fileUrl, fileName, musicFile.getOriginalFilename(), 0, content);
			System.out.println("저장된 ID: " + contentFile.getId());
			contentFileRepository.save(contentFile);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("파일 저장 및 DB 기록 중 오류 발생: " + musicFile.getOriginalFilename(), e);
		}
		
	}

	public void createContentFileFileForContent(Content content, MultipartFile file, String category) {
		if(file == null || file.isEmpty()) {
			new IllegalArgumentException("파일이 비어있습니다.");
		}
		try {
			String fileName = fileUtil.saveFile(file, category);
			String fileUrl="/upload/files/"+category+"/" + fileName;
			ContentFile contentFile = ContentFile.createForContent("FILE", fileUrl, fileName, file.getOriginalFilename(), 0, content);
			System.out.println("저장된 ID: " + contentFile.getId());
			contentFileRepository.save(contentFile);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("파일 저장 및 DB 기록 중 오류 발생: " + file.getOriginalFilename(), e);
		}
		
	}
}
