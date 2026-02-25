package com.creatival.content;

import java.time.LocalDateTime;
import java.util.List;

import com.creatival.user.userRole;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @Builder
@AllArgsConstructor
@Entity
public class Series {
	
	public Series() {
		// TODO Auto-generated constructor stub
	}
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "content_id")
	private Content content;
	
	private boolean isEnd=false;
	
	@Column(nullable = false)
	private int totalEpisode=0;
	
	@OneToMany(mappedBy = "series", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Episode> episode;
}
