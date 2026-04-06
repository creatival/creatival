package com.creatival.board;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.creatival.board.Enum.BoardType;
import com.creatival.board.dto.CreateBoardDTO;
import com.creatival.board.dto.ResponseBoardDetailDTO;
import com.creatival.board.dto.ResponseBoardFileDTO;
import com.creatival.board.dto.ResponseBoardListDTO;
import com.creatival.board.dto.UpdateBoardDTO;
import com.creatival.comment.CommentService;
import com.creatival.comment.dto.ResponseCommentDTO;
import com.creatival.content.Content;
import com.creatival.content.ContentService;
import com.creatival.team.Project;
import com.creatival.team.Team;
import com.creatival.team.TeamService;
import com.creatival.user.UserService;
import com.creatival.user.Users;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/board")
public class BoardController {

    private final BoardFileService boardFileService;

    private final BoardService boardService;
	private final ContentService contentService;
	private final TeamService teamService;
	private final UserService userService;
	private final CommentService commentService;


	@GetMapping()
	public String board(Model model, @RequestParam(value = "type", defaultValue = "NORMAL") BoardType boardType, @RequestParam(value = "page", defaultValue = "0") int page) {
		Page<ResponseBoardListDTO> list = boardService.getBoardList(boardType, page);
		model.addAttribute("type", boardType);
		model.addAttribute("boardList", list);
		return "boardpage";
	}
	
	@GetMapping("/write")
	public String createBoard(CreateBoardDTO createBoardDTO, Principal principal, RedirectAttributes redirectAttributes) {
		if(principal==null) {
			redirectAttributes.addFlashAttribute("message", "로그인은 필수 사항입니다!");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/board";
		}
		return "board_write";
	}
	
	@PostMapping("/write")
	@Transactional
	public String createBoardPost(@Valid CreateBoardDTO createBoardDTO, Principal principal, RedirectAttributes redirectAttributes) {
		if(principal==null) {
			redirectAttributes.addFlashAttribute("message", "로그인은 필수 사항입니다!");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/board/write";
		}
		
		Users user = userService.getUserByUsername(principal.getName());
		//테스트용이라 isCreator만 했음 나중에 인증받은 으로 바꿀 것
		if(createBoardDTO.getBoardType()==BoardType.CREATOR && !user.isCreator()) {
			redirectAttributes.addFlashAttribute("message", "인증받은 창작자만이 창작자 게시판에 글을 쓸 수 있습니다!");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/board/write";
		}
		// 파일이랑 텍스트 잔브 없을 경우 발동
		if((createBoardDTO.getImages()== null || createBoardDTO.getImages().isEmpty()) && (createBoardDTO.getBoardText()==null || createBoardDTO.getBoardText().isEmpty())) {
			redirectAttributes.addFlashAttribute("message", "파일(이미지 또는 영상)이나 내용이 필요합니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/board/write";
		}
		
		try {
			boardService.createBoard(createBoardDTO, user);
			redirectAttributes.addFlashAttribute("message", "성공적으로 추가되었습니다");
			redirectAttributes.addFlashAttribute("icon", "success");
			return "redirect:/board";
		}catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/board/write";
		} catch (Exception e) {
			e.getStackTrace();
			redirectAttributes.addFlashAttribute("message", "알 수 없는 오류가 발생했습니다. 페이지 관리자분께 문의 바랍니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/board/write";
		}
		
		
	}
	
	@GetMapping("/detail/{id}")
	public String boardDetail(@PathVariable("id") Long boardId, Model model, Principal principal) {
		List<ResponseBoardFileDTO> boardFiles = boardFileService.getBoardFileById(boardService.getBoardById(boardId));
		boardService.viewCountUpById(boardId);
		ResponseBoardDetailDTO dto = boardService.getBoardDetailById(boardId);
		model.addAttribute("board", dto);
		model.addAttribute("fileList", boardFiles);
		List<ResponseCommentDTO> list = commentService.getCommentListByBoard(boardId);
		model.addAttribute("comments", list);
		int commentCount = commentService.getCountByBoard(boardId);
		model.addAttribute("commentCount", commentCount);
		return "board_detail";
	}
	
	@GetMapping("/edit/{id}")
	public String boardEdit(@PathVariable("id") Long boardId, Model model, Principal principal, RedirectAttributes redirectAttributes) {
		Board board = boardService.getBoardById(boardId);
		if(principal==null) {
			redirectAttributes.addFlashAttribute("message", "로그인이 필수적으로 필요합니다!");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/board/detail/"+boardId;
		}
		if(board==null) {
			redirectAttributes.addFlashAttribute("message", "수정할려는 게시물이 없습니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/";
		}
		if(!board.getUser().getUsername().equals(principal.getName())) {
			redirectAttributes.addFlashAttribute("message", "귀하에게는 수정할 권한이 없습니다.");
			redirectAttributes.addFlashAttribute("icon", "warning");
			return "redirect:/board/detail/"+boardId;
		}
		List<ResponseBoardFileDTO> list = boardFileService.getBoardFileById(board);
		model.addAttribute("updateBoardDTO", UpdateBoardDTO.from(board, list));
		return "board_edit";
	}
	
	@PostMapping("/edit/{id}")
	public String boardEdit(@Valid @ModelAttribute("updateBoardDTO") UpdateBoardDTO dto,@PathVariable("id") Long boardId, Model model, Principal principal, RedirectAttributes redirectAttributes) {
		if(principal==null) {
			redirectAttributes.addFlashAttribute("message", "로그인이 필수적으로 필요합니다!");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/board/detail/"+boardId;
		}
		Board board = boardService.getBoardById(boardId);
		if(board==null) {
			redirectAttributes.addFlashAttribute("message", "수정할려는 게시물이 없습니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/";
		}
		if(!board.getUser().getUsername().equals(principal.getName())) {
			redirectAttributes.addFlashAttribute("message", "귀하에게는 수정할 권한이 없습니다.");
			redirectAttributes.addFlashAttribute("icon", "warning");
			return "redirect:/board/detail/"+boardId;
		}
		redirectAttributes.addFlashAttribute("message", "수정완료되었습니다.");
		redirectAttributes.addFlashAttribute("icon", "success");
		boardService.updateBoard(dto, boardId);
		return "redirect:/board/detail/"+boardId;
	}
	
	@GetMapping("/delete/{id}")
	public String deeteBoard(@PathVariable("id") Long boardId, Principal principal,RedirectAttributes redirectAttributes) {
		if(principal==null) {
			redirectAttributes.addFlashAttribute("message", "로그인이 필수적으로 필요합니다!");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/board/detail/"+boardId;
		}
		Board board = boardService.getBoardById(boardId);
		if(board==null) {
			redirectAttributes.addFlashAttribute("message", "수정할려는 게시물이 없습니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/";
		}
		if(!board.getUser().getUsername().equals(principal.getName())) {
			redirectAttributes.addFlashAttribute("message", "귀하에게는 삭제할 권한이 없습니다.");
			redirectAttributes.addFlashAttribute("icon", "warning");
			return "redirect:/board/detail/"+boardId;
		}
		redirectAttributes.addFlashAttribute("message", "삭제완료되었습니다!");
		redirectAttributes.addFlashAttribute("icon", "success");
		boardService.deleteBoard(board);
		return "redirect:/board";
	}
	
	@GetMapping("/info/{type}")
	@ResponseBody
	public Map<String, String> getInfo(@PathVariable("type") String type, @RequestParam("id") Long id) {
        Map<String, String> response = new HashMap<>();
        String name = null;

        try {
            switch (type) {
                case "content":
                    name = contentService.getContent(id).getTitle();
                    break;
                case "project":
                    name = teamService.getProjectById(id).getTitle();
                    break;
                case "team":
                    name = teamService.getTeamById(id).getName();
                    break;
            }
            response.put("name", name != null ? name : "");
        } catch (Exception e) {
            response.put("name", "");
        }
        return response;
    }
}
