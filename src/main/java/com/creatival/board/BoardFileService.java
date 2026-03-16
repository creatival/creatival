package com.creatival.board;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.creatival.FileUtil;
import com.creatival.board.dto.ResponseBoardFileDTO;
import com.creatival.board.repository.BoardFileRepository;
import com.creatival.content.Content;
import com.creatival.content.ContentFile;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class BoardFileService {

    private final BoardFileRepository boardFileRepository;
	private final FileUtil fileUtil;
	
	@Transactional
	public void createBoardFile(Board board ,List<MultipartFile> files, String category) {
		if(files == null || files.isEmpty()) {
			new IllegalArgumentException("파일이 비어있습니다.");
		}
		System.out.println(files.size());
		for(MultipartFile file : files) {
			if(file.isEmpty()) {
				continue;
			}
			try {
				String fileName = null;
				String fileUrl = null;
				String fileType = null;
				if(getFileType(file).equals("IMAGE")) {
					fileName = fileUtil.saveImage(file, category);
					fileUrl="/upload/images/"+category+"/" + fileName;
					fileType = "IMAGE";
				} else if(getFileType(file).equals("VIDEO")) {
					fileName = fileUtil.saveVideo(file, category);
					fileUrl="/upload/videos/"+category+"/" + fileName;
					fileType = "VIDEO";
				} else {
					new IllegalArgumentException("허용되지 않는 파일 유형입니다.");
				}
				
				
				BoardFile boardFile = BoardFile.builder()
						.fileUrl(fileUrl)
						.fileName(fileName)
						.originalFileName(file.getOriginalFilename())
						.fileType(fileType)
						.board(board)
						.build();
				System.out.println("저장된 ID: " + boardFile.getId());
				boardFileRepository.save(boardFile);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("파일 저장 및 DB 기록 중 오류 발생: " + file.getOriginalFilename(), e);
			}
		}
	}
	
	public String getFileType(MultipartFile file) {
	    String originalFilename = file.getOriginalFilename();
	    if (originalFilename == null) return "UNKNOWN";

	    String ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();

	    if (Arrays.asList("jpg", "jpeg", "png", "gif").contains(ext)) {
	        return "IMAGE";
	    } else if (Arrays.asList("mp4", "mov", "avi", "wmv").contains(ext)) {
	        return "VIDEO";
	    }
	    return "UNKNOWN";
	}
	
	public List<ResponseBoardFileDTO> getBoardFileById(Board board) {
		if(board == null) {
			return null;
		}
		return boardFileRepository.findByBoard(board).stream().map(boardFile -> ResponseBoardFileDTO.from(boardFile)).toList();
	}

	public void deleteById(Long id) {
		boardFileRepository.deleteById(id);
		
	}
}
