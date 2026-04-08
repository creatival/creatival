package com.creatival.team.dto;

import java.time.LocalDateTime;

import com.creatival.team.Checkout;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter @Builder
@AllArgsConstructor
public class ResponseCheckoutListDTO {
	private Long id;
	private String title;
	private boolean checked;
	private LocalDateTime createdAt;
	private LocalDateTime deadline;
	private LocalDateTime checkedAt;
	
	public static ResponseCheckoutListDTO from(Checkout checkout) {
		return ResponseCheckoutListDTO.builder()
				.id(checkout.getId())
				.title(checkout.getTitle())
				.checked(checkout.isChecked())
				.createdAt(checkout.getCreatedAt())
				.deadline(checkout.getDeadline())
				.checkedAt(checkout.getCheckedAt())
				.build();
	}
}
