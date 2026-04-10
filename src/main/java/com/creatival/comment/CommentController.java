package com.creatival.comment;

import java.lang.ProcessBuilder.Redirect;
import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.creatival.board.Board;
import com.creatival.board.BoardService;
import com.creatival.content.Content;
import com.creatival.content.ContentService;
import com.creatival.tag.TagService;
import com.creatival.team.TeamService;
import com.creatival.user.UserService;
import com.creatival.user.Users;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/comment")
public class CommentController {
	private final CommentService commentService;
	private final UserService userService;
	private final ContentService contentService;
	private final BoardService boardService;
	
	
	@PostMapping("/content/write/{id}")
	public String createComment(@PathVariable("id") Long id,@RequestParam("text") String text, Principal principal, RedirectAttributes redirectAttributes) {
		Content content = contentService.getContent(id);
		if(content==null) {
			redirectAttributes.addFlashAttribute("message", "해당하는 게시물을 찾을 수 없습니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "/";
		}
		if(principal == null) {
			redirectAttributes.addFlashAttribute("message", "댓글을 작성하기 위해서는 로그인이 필수입니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/content/"+ content.getType().toString().toLowerCase() + "/detail/"+id;
		}
		Users user = userService.getUserByUsername(principal.getName());
		try {
			commentService.createCommentForContent(id, text, user);
			return "redirect:/content/"+ content.getType().toString().toLowerCase() + "/detail/"+id;
		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("message", "오류가 발생했습니다. 관리자께 문의바랍니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/content/"+ content.getType().toString().toLowerCase() + "/detail/"+id;
		}
	}
	//id는 comment id
	@PostMapping("/content/update/{id}")
	public String updateComment(@PathVariable("id") Long id,@RequestParam("text") String text, Principal principal, RedirectAttributes redirectAttributes) {
		Comment comment = commentService.getCommentById(id);
		if(comment== null) {
			redirectAttributes.addFlashAttribute("message", "수정할려는 댓글을 찾을 수 없습니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/";
		}
		if(principal == null) {
			redirectAttributes.addFlashAttribute("message", "댓글을 수정하기 위해서는 로그인이 필수입니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/content/" + comment.getContent().getType().toString().toLowerCase() + "/detail/"+comment.getContent().getId();
		}
		
		if(!comment.getUser().getUsername().equals(principal.getName())) {
			redirectAttributes.addFlashAttribute("message", "작성자 본인만 수정할 수 있습니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/content/" + comment.getContent().getType().toString().toLowerCase() + "/detail/"+comment.getContent().getId();
		}
		try {
			redirectAttributes.addFlashAttribute("message", "수정 완료되었습니다.");
			redirectAttributes.addFlashAttribute("icon", "success");
			commentService.updateCommentForContent(id, text);
			return "redirect:/content/" + comment.getContent().getType().toString().toLowerCase() + "/detail/"+comment.getContent().getId();
		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("message", "오류가 발생했습니다. 관리자께 문의바랍니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/content/" + comment.getContent().getType().toString().toLowerCase() + "/detail/"+comment.getContent().getId();
		}
	}
	
	@GetMapping("/content/delete/{id}")
	public String deleteComment(@PathVariable("id") Long id, Principal principal, RedirectAttributes redirectAttributes) {
		Comment comment = commentService.getCommentById(id);
		if(comment== null) {
			redirectAttributes.addFlashAttribute("message", "수정할려는 댓글을 찾을 수 없습니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/";
		}
		if(principal == null) {
			redirectAttributes.addFlashAttribute("message", "댓글을 수정하기 위해서는 로그인이 필수입니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/content/art/detail/"+comment.getContent().getId();
		}
		
		if(!comment.getUser().getUsername().equals(principal.getName()) || !comment.getContent().getUser().getUsername().equals(principal.getName())) {
			redirectAttributes.addFlashAttribute("message", "작성자 본인 혹은 콘텐츠 생성자만 삭제할 수 있습니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/content/art/detail/"+comment.getContent().getId();
		}
		
		commentService.delete(comment);
		redirectAttributes.addFlashAttribute("message", "삭제되었습니다.");
		redirectAttributes.addFlashAttribute("icon", "success");
		return "redirect:/content/"+comment.getContent().getType().toString().toLowerCase()+"/detail/"+comment.getContent().getId();
	}
	
	@PostMapping("/content/reply/write/{id}")
	public String createReplyComment(@PathVariable("id") Long id,@RequestParam("text") String text, Principal principal, RedirectAttributes redirectAttributes) {
		
		Comment comment = commentService.getCommentById(id);
		if(principal == null) {
			redirectAttributes.addFlashAttribute("message", "댓글을 작성하기 위해서는 로그인이 필수입니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/content/"+comment.getContent().getType().toString().toLowerCase()+"/detail/"+comment.getContent().getId();
		}
		Users user = userService.getUserByUsername(principal.getName());
		try {
			commentService.createCommentForComment(id, text, user);
			return "redirect:/content/"+comment.getContent().getType().toString().toLowerCase()+"/detail/"+comment.getContent().getId();
		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("message", "오류가 발생했습니다. 관리자께 문의바랍니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/content/"+comment.getContent().getType().toString().toLowerCase()+"/detail/"+comment.getContent().getId();
		}
	}
	
	@PostMapping("/board/write/{id}")
	public String createCommentForBoard(@PathVariable("id") Long id,@RequestParam("text") String text, Principal principal, RedirectAttributes redirectAttributes) {
		Board board = boardService.getBoardById(id);
		if(board == null) {
			redirectAttributes.addFlashAttribute("message", "해당하는 board가 존재하지 않습니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "/";
		}
		if(principal == null) {
			redirectAttributes.addFlashAttribute("message", "댓글을 작성하기 위해서는 로그인이 필수입니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/board/detail/"+ id;
		}
		Users user = userService.getUserByUsername(principal.getName());
		try {
			commentService.createCommentForBoard(id, text, user);
			return "redirect:/board/detail/"+ id;
		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("message", "오류가 발생했습니다. 관리자께 문의바랍니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/board/detail/"+ id;
		}
		
	}
	@PostMapping("/board/update/{id}")
	public String updateCommentForBoard(@PathVariable("id") Long id,@RequestParam("text") String text, Principal principal, RedirectAttributes redirectAttributes) {
		Comment comment = commentService.getCommentById(id);
		if(comment== null) {
			redirectAttributes.addFlashAttribute("message", "수정할려는 댓글을 찾을 수 없습니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/";
		}
		if(principal == null) {
			redirectAttributes.addFlashAttribute("message", "댓글을 수정하기 위해서는 로그인이 필수입니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/board/detail/"+comment.getBoard().getId();
		}
		
		if(!comment.getUser().getUsername().equals(principal.getName())) {
			redirectAttributes.addFlashAttribute("message", "작성자 본인만 수정할 수 있습니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/board/detail/"+comment.getBoard().getId();
		}
		try {
			redirectAttributes.addFlashAttribute("message", "수정 완료되었습니다.");
			redirectAttributes.addFlashAttribute("icon", "success");
			commentService.updateCommentForContent(id, text);
			return "redirect:/board/detail/"+comment.getBoard().getId();
		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("message", "오류가 발생했습니다. 관리자께 문의바랍니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/board/detail/"+comment.getBoard().getId();
		}
	}
	@GetMapping("/board/delete/{id}")
	public String deleteCommentForBoard(@PathVariable("id") Long id, Principal principal, RedirectAttributes redirectAttributes) {
		Comment comment = commentService.getCommentById(id);
		if(comment== null) {
			redirectAttributes.addFlashAttribute("message", "수정할려는 댓글을 찾을 수 없습니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/";
		}
		if(principal == null) {
			redirectAttributes.addFlashAttribute("message", "댓글을 수정하기 위해서는 로그인이 필수입니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/board/detail/"+comment.getBoard().getId();
		}
		
		if(!comment.getUser().getUsername().equals(principal.getName()) || !comment.getBoard().getUser().getUsername().equals(principal.getName())) {
			redirectAttributes.addFlashAttribute("message", "작성자 본인 혹은 콘텐츠 생성자만 삭제할 수 있습니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/board/detail/"+comment.getBoard().getId();
		}
		
		commentService.delete(comment);
		redirectAttributes.addFlashAttribute("message", "삭제되었습니다.");
		redirectAttributes.addFlashAttribute("icon", "success");
		return "redirect:/board/detail/"+comment.getBoard().getId();
	}
	@PostMapping("/board/reply/write/{id}")
	public String createReplyCommentForBoard(@PathVariable("id") Long id,@RequestParam("text") String text, Principal principal, RedirectAttributes redirectAttributes) {
		
		Comment comment = commentService.getCommentById(id);
		if(principal == null) {
			redirectAttributes.addFlashAttribute("message", "댓글을 작성하기 위해서는 로그인이 필수입니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/board/detail/"+comment.getBoard().getId();
		}
		Users user = userService.getUserByUsername(principal.getName());
		try {
			commentService.createCommentForComment(id, text, user);
			return "redirect:/board/detail/"+comment.getBoard().getId();
		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("message", "오류가 발생했습니다. 관리자께 문의바랍니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/board/detail/"+comment.getBoard().getId();
		}
	}
	
	@PostMapping("/team/write/{id}")
	public String createCommentForTeam(@PathVariable("id") Long id, @RequestParam("text") String text, Principal principal, RedirectAttributes redirectAttributes) {
		if(principal == null) {
			redirectAttributes.addFlashAttribute("isLogMsg", true);
			return "redirect:/team/"+id;
		}
		Users user = userService.getUserByUsername(principal.getName());
		try {
			commentService.createCommentForTeam(id, text, user);
			return "redirect:/team/"+id;
		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("message", "오류가 발생했습니다. 관리자께 문의바랍니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/team/"+id;
		}
	}
	@PostMapping("/team/update/{id}")
	public String updateCommentForTeam(@PathVariable("id") Long id,@RequestParam("text") String text, Principal principal, RedirectAttributes redirectAttributes) {
		Comment comment = commentService.getCommentById(id);
		if(comment== null) {
			redirectAttributes.addFlashAttribute("message", "수정할려는 댓글을 찾을 수 없습니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/";
		}
		if(principal == null) {
			redirectAttributes.addFlashAttribute("message", "댓글을 수정하기 위해서는 로그인이 필수입니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/team/"+comment.getTeam().getId() + "/comments";
		}
		
		if(!comment.getUser().getUsername().equals(principal.getName())) {
			redirectAttributes.addFlashAttribute("message", "작성자 본인만 수정할 수 있습니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/team/"+comment.getTeam().getId() + "/comments";
		}
		try {
			redirectAttributes.addFlashAttribute("message", "수정 완료되었습니다.");
			redirectAttributes.addFlashAttribute("icon", "success");
			commentService.updateCommentForContent(id, text);
			return "redirect:/team/"+comment.getTeam().getId() + "/comments";
		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("message", "오류가 발생했습니다. 관리자께 문의바랍니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/team/"+comment.getTeam().getId() + "/comments";	
		}
	}
	@GetMapping("/team/delete/{id}")
	public String deleteCommentForTeam(@PathVariable("id") Long id, Principal principal, RedirectAttributes redirectAttributes) {
		Comment comment = commentService.getCommentById(id);
		if(comment== null) {
			redirectAttributes.addFlashAttribute("message", "수정할려는 댓글을 찾을 수 없습니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/";
		}
		if(principal == null) {
			redirectAttributes.addFlashAttribute("message", "댓글을 수정하기 위해서는 로그인이 필수입니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/team/"+comment.getTeam().getId() + "/comments";
		}
		
		if(!comment.getUser().getUsername().equals(principal.getName()) || !comment.getTeam().getUser().getUsername().equals(principal.getName())) {
			redirectAttributes.addFlashAttribute("message", "작성자 본인만 삭제할 수 있습니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/team/"+comment.getTeam().getId() + "/comments";
		}
		
		commentService.delete(comment);
		redirectAttributes.addFlashAttribute("message", "삭제되었습니다.");
		redirectAttributes.addFlashAttribute("icon", "success");
		return "redirect:/team/"+comment.getTeam().getId() + "/comments";
	}
	
	
	//episode 부분
	
	@PostMapping("/episode/write/{id}")
	public String createCommentForEpisode(@PathVariable("id") Long id, @RequestParam("text") String text, Principal principal, RedirectAttributes redirectAttributes) {
		if(principal == null) {
			redirectAttributes.addFlashAttribute("isLogMsg", true);
			return "redirect:/content/novel/episode/"+id;
		}
		Users user = userService.getUserByUsername(principal.getName());
		try {
			commentService.createCommentForEpisode(id, text, user);
			return "redirect:/content/novel/episode/"+id;
		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("message", "오류가 발생했습니다. 관리자께 문의바랍니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/content/novel/episode/"+id;
		}
	}
	@PostMapping("/episode/update/{id}")
	public String updateCommentForEpisode(@PathVariable("id") Long id,@RequestParam("text") String text, Principal principal, RedirectAttributes redirectAttributes) {
		Comment comment = commentService.getCommentById(id);
		if(comment== null) {
			redirectAttributes.addFlashAttribute("message", "수정할려는 댓글을 찾을 수 없습니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/";
		}
		if(principal == null) {
			redirectAttributes.addFlashAttribute("message", "댓글을 수정하기 위해서는 로그인이 필수입니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/content/novel/episode/"+comment.getEpisode().getId();
		}
		
		if(!comment.getUser().getUsername().equals(principal.getName())) {
			redirectAttributes.addFlashAttribute("message", "작성자 본인만 수정할 수 있습니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/content/novel/episode/"+comment.getEpisode().getId();
		}
		try {
			redirectAttributes.addFlashAttribute("message", "수정 완료되었습니다.");
			redirectAttributes.addFlashAttribute("icon", "success");
			commentService.updateCommentForContent(id, text);
			return "redirect:/content/novel/episode/"+comment.getEpisode().getId();
		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("message", "오류가 발생했습니다. 관리자께 문의바랍니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/content/novel/episode/"+comment.getEpisode().getId();
		}
	}
	
	@GetMapping("/episode/delete/{id}")
	public String deleteCommentForEpisode(@PathVariable("id") Long id, Principal principal, RedirectAttributes redirectAttributes) {
		Comment comment = commentService.getCommentById(id);
		if(comment== null) {
			redirectAttributes.addFlashAttribute("message", "수정할려는 댓글을 찾을 수 없습니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/";
		}
		if(principal == null) {
			redirectAttributes.addFlashAttribute("message", "댓글을 수정하기 위해서는 로그인이 필수입니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/content/novel/episode/"+comment.getEpisode().getId();
		}
		
		if(!comment.getUser().getUsername().equals(principal.getName()) || !comment.getEpisode().getSeries().getContent().getUser().getUsername().equals(principal.getName())) {
			redirectAttributes.addFlashAttribute("message", "작성자 본인 혹은 콘텐츠 생성자만 삭제할 수 있습니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/content/novel/episode/"+comment.getEpisode().getId();
		}
		
		commentService.delete(comment);
		redirectAttributes.addFlashAttribute("message", "삭제되었습니다.");
		redirectAttributes.addFlashAttribute("icon", "success");
		return "redirect:/content/novel/episode/"+comment.getEpisode().getId();
	}
	
	@PostMapping("/episode/reply/write/{id}")
	public String createReplyCommentForEpisode(@PathVariable("id") Long id,@RequestParam("text") String text, Principal principal, RedirectAttributes redirectAttributes) {
		
		Comment comment = commentService.getCommentById(id);
		if(principal == null) {
			redirectAttributes.addFlashAttribute("message", "댓글을 작성하기 위해서는 로그인이 필수입니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/content/novel/episode/"+comment.getEpisode().getId();
		}
		Users user = userService.getUserByUsername(principal.getName());
		try {
			commentService.createCommentForComment(id, text, user);
			return "redirect:/content/novel/episode/"+comment.getEpisode().getId();
		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("message", "오류가 발생했습니다. 관리자께 문의바랍니다.");
			redirectAttributes.addFlashAttribute("icon", "error");
			return "redirect:/content/novel/episode/"+comment.getEpisode().getId();
		}
	}
}
