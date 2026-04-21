package com.creatival.sponsorship;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.creatival.like.TargetType;
import com.creatival.sponsorship.dto.ResponseSupportHistoryListDTO;
import com.creatival.team.Project;
import com.creatival.team.TeamService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/sponsorship")
public class SponsorshipController {
	private final TeamService teamService;
	private final SponsorshipService sponsorshipService;
	
	
	@GetMapping("/success")
	public String sponsorshipSuccess(@RequestParam("paymentId") String paymentId,
	                                 @RequestParam("targetType") String targetType,
	                                 @RequestParam("targetId") Long targetId,
	                                 @RequestParam(value = "teamId", required = false) Long teamId,
	                                 Model model) {
	    String redirectUrl;

	    if ("PROJECT".equalsIgnoreCase(targetType)) {
	        if (teamId == null) {
	            throw new IllegalArgumentException("프로젝트 후원 성공 이동에는 teamId가 필요합니다.");
	        }
	        redirectUrl = "/team/" + teamId + "/project/" + targetId;
	    } else if ("TEAM".equalsIgnoreCase(targetType)) {
	        redirectUrl = "/team/" + targetId;
	    } else if ("USER".equalsIgnoreCase(targetType)) {
	        redirectUrl = "/user/myPage/" + targetId;
	    } else {
	        redirectUrl = "/";
	    }

	    model.addAttribute("paymentId", paymentId);
	    model.addAttribute("redirectUrl", redirectUrl);

	    return "payment_success";
	}

    @GetMapping("/fail")
    public String sponsorshipFail(@RequestParam(value = "message", required = false) String message, Model model) {
        model.addAttribute("message", message != null ? message : "알 수 없는 오류가 발생했습니다.");
        return "payment_fail";
    }
    
    //api
    @GetMapping("/api/support/history")
    public ResponseEntity<?> getSupportHistory(@RequestParam("targetType") String targetType,
                                               @RequestParam("targetId") Long targetId,
                                               @RequestParam(value = "page",defaultValue = "0") int page,
                                               @RequestParam(value = "size",defaultValue = "5") int size) {

        TargetType parsedTargetType = TargetType.valueOf(targetType.toUpperCase());

        List<ResponseSupportHistoryListDTO> supports =
                sponsorshipService.getRecentSupportHistory(
                        parsedTargetType,
                        targetId,
                        PageRequest.of(page, size)
                );

        Map<String, Object> result = new HashMap();
        result.put("items", supports);
        result.put("hasNext", supports.size() == size);
        result.put("page", page);

        return ResponseEntity.ok(result);
    }
}
