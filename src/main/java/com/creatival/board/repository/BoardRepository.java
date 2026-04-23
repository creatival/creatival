package com.creatival.board.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.creatival.board.Board;
import java.util.List;
import com.creatival.board.Enum.BoardType;


public interface BoardRepository extends JpaRepository<Board, Long> {
	Page<Board> findByBoardType(BoardType boardType, Pageable pageable);
	
	@Modifying
	@Query("UPDATE Board b SET b.viewCount = b.viewCount + 1 WHERE b.id = :id")
	void incrementViewCount(@Param("id") Long id);
	
	List<Board> findByTitleContaining(String keyword);
	
	@Query("""
		    select distinct b
		    from Board b
		    where lower(b.title) like lower(concat('%', :keyword, '%'))
		       or lower(b.boardText) like lower(concat('%', :keyword, '%'))
		    order by b.id desc
		""")
		List<Board> searchBoards(@Param("keyword") String keyword);
}
