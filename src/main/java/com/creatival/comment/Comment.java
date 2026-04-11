package com.creatival.comment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.creatival.board.Board;
import com.creatival.content.Content;
import com.creatival.content.Episode;
import com.creatival.tag.Tag;
import com.creatival.tag.TagToContent;
import com.creatival.tag.TagToProject;
import com.creatival.tag.TagToTeam;
import com.creatival.tag.TagToUsers;
import com.creatival.team.Team;
import com.creatival.user.Users;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@Entity
@NoArgsConstructor
public class Comment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private Users user;
	
	@Column(columnDefinition = "TEXT", nullable = false)
	private String text;
	
	@CreationTimestamp
	private LocalDateTime createdAt;
	
	@UpdateTimestamp
	private LocalDateTime updatedAt;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id")
	private Comment parentComment;
	
	@OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL,orphanRemoval = true)
	private List<Comment> children = new ArrayList<>();
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "content_id")
	private Content content;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "team_id")
	private Team team;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "board_id")
	private Board board;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "episode_id")
	private Episode episode;
	
	private Long likeCount;
}
