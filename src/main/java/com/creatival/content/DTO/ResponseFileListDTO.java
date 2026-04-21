package com.creatival.content.DTO;

import com.creatival.content.Content;
import com.creatival.content.Enum.ContentType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter @Builder
@AllArgsConstructor
public class ResponseFileListDTO {
	private Long id;
	private String displayname;
	private String username;
	private String title;
	private boolean paid;
	
	public static ResponseFileListDTO from(Content content) {
		return ResponseFileListDTO.builder()
				.id(content.getId())
				.displayname(content.getUser().getDisplayName())
				.username(content.getUser().getUsername())
				.title(content.getTitle())
				.paid(content.isPaid())
				.build();
	}
}
