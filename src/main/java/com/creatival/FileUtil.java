package com.creatival;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileUtil {
	private final String rootPath = "C:/creatival_file";
	// 카테고리를 Enum으로 바꿀까 고민 중 나중에 고려할 것
	public String saveImage(MultipartFile img, String category) throws IOException {
		String imgUrl=null;
		String fileName = UUID.randomUUID().toString()+"_"+ img.getOriginalFilename();
		Path filePath = Paths.get(rootPath+"/images/"+category, fileName);
		Files.createDirectories(filePath.getParent());
		Files.write(filePath, img.getBytes());
		imgUrl = "/upload/images/"+category+"/"+fileName; //보안 및 html에서 C:를 안 받기에 가상 경로를 넣어줌
		System.out.println("saveImage 물리 저장 끝");
		return fileName;
	}
	public String saveVideo(MultipartFile video, String category) throws IOException {
		String videoUrl=null;
		String fileName = UUID.randomUUID().toString()+"_"+ video.getOriginalFilename();
		Path filePath = Paths.get(rootPath+"/videos/"+category, fileName);
		Files.createDirectories(filePath.getParent());
		Files.write(filePath, video.getBytes());
		videoUrl = "/upload/videos/"+category+"/"+fileName; //보안 및 html에서 C:를 안 받기에 가상 경로를 넣어줌
		System.out.println("saveVideo 물리 저장 끝");
		return fileName;
	}
	public void deleteImage(String fileName, String category) throws IOException {
	    Path filePath = Paths.get(rootPath + "/images/" + category, fileName);

	    if (Files.exists(filePath)) {
	        Files.delete(filePath);
	        System.out.println("파일 물리 삭제 완료: " + fileName);
	    } else {
	        System.out.println("삭제할 파일이 존재하지 않음: " + fileName);
	    }
	}
}
