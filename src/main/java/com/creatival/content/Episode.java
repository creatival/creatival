package com.creatival.content;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.creatival.comment.Comment;
import com.creatival.user.userRole;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
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
public class Episode {
	
	public Episode() {
		// TODO Auto-generated constructor stub
	}
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "series_id")
	private Series series;
	
	@Column(nullable = false)
	private Integer episodeNum;
	
	@Column(nullable = false)
	private String title;
	
	@Lob
	private String novelContent;
	
	@Builder.Default
	boolean isFree=true;
	@Builder.Default
	boolean isDeleted=false;
	
	@Builder.Default
	private Long viewCount=0L;
	@Builder.Default
	private Long likeCount=0L;
	
	@Lob
	private String note;
	
	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;
	
	@UpdateTimestamp
	@Column(nullable = false)
	private LocalDateTime updatedAt;
	
	@OneToMany(mappedBy = "episode", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ContentFile> contentFile;
	
	@OneToMany(mappedBy = "episode" ,cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Comment> comments;
}
