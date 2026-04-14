package com.creatival.sponsorship.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponsePortOneDTO<T> {
	private T data;
}
