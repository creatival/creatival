package com.creatival.follow.dto;

import com.creatival.like.TargetType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
@AllArgsConstructor
public class CreateFollowDTO {
	private Long targetId;
    private TargetType type;
}
