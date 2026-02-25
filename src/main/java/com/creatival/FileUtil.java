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
		return imgUrl;
	}
}
