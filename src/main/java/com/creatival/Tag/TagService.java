package com.creatival.tag;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.creatival.FileUtil;
import com.creatival.content.repository.ContentFileRepository;
import com.creatival.user.Users;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TagService {
	private final TagRepository tagRepository;
	private final TagToUsersRepository tagToUsersRepository;
	
	public void createTagForUser(Users user, String tagName) {
		Tag tag;
		if(tagRepository.findByTagText(tagName).isEmpty()) {
			tag = Tag.builder().tagText(tagName).user(user).build();
			tagRepository.save(tag);
		} else {
			tag = tagRepository.findByTagText(tagName).get();
		}
		
		TagToUsers tagToUsers = TagToUsers.from(user, tag);
		tagToUsersRepository.save(tagToUsers);
	}
	
	public List<ResponseTagDTO> getTagForUser(Users user){
		List<TagToUsers> mappings = tagToUsersRepository.findByUser(user);
		return mappings.stream()
	            .map(m -> new ResponseTagDTO(
	                    m.getTag().getId(), 
	                    m.getTag().getTagText()
	                ))
	                .collect(Collectors.toList());
	}
	
	@Transactional
	public void deleteMapping(Long userId, Long tagId) {

	    // 2. UserID와 TagID로 매핑 데이터 삭제
	    tagToUsersRepository.deleteByUserIdAndTagId(userId, tagId);
	}
}
