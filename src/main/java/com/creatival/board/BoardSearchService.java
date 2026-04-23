package com.creatival.board;

import java.util.List;

import org.springframework.stereotype.Service;

import com.creatival.board.repository.BoardRepository;
import com.creatival.chat.dto.BoardSearchDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardSearchService {

    private final BoardRepository boardRepository;

    public List<BoardSearchDTO> search(String keyword) {
        List<Board> boards = boardRepository.findByTitleContaining(keyword);

        return boards.stream()
                .limit(5)
                .map(b -> new BoardSearchDTO(
                        b.getId(),
                        b.getTitle(),
                        "/board/detail/" + b.getId(),
                        b.getUser().getDisplayName(),
                        b.getCreatedAt()
                ))
                .toList();
    }
}