package com.creatival.like.dto;

import com.creatival.like.TargetType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
@AllArgsConstructor
public class CreateLikeDTO {
	private Long targetId;
    private TargetType type;
}
