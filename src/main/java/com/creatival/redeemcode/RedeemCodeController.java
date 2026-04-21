package com.creatival.redeemcode;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.creatival.content.Content;
import com.creatival.content.ContentService;
import com.creatival.content.Episode;
import com.creatival.like.TargetType;
import com.creatival.user.UserService;
import com.creatival.user.Users;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/redeem")
public class RedeemCodeController {

    private final RedeemCodeService redeemCodeService;
    private final UserService userService;
    private final ContentService contentService;

    @PostMapping("/create/content/{id}")
    public String createContentRedeemCode(@PathVariable("id") Long contentId,
                                          Principal principal,
                                          RedirectAttributes redirectAttributes) {

        if (principal == null) {
            redirectAttributes.addFlashAttribute("message", "로그인이 필요합니다.");
            redirectAttributes.addFlashAttribute("icon", "warning");
            return "redirect:/user/login";
        }
        
        Content content = contentService.getContent(contentId);
        if(content == null) {
        	redirectAttributes.addFlashAttribute("message", "콘텐츠가 없습니다.");
            redirectAttributes.addFlashAttribute("icon", "warning");
            return "redirect:/";
        }

        Users user = userService.getUserByUsername(principal.getName());

        try {
            RedeemCode redeemCode = redeemCodeService.createCode(TargetType.CONTENT, contentId, user, null);
            redirectAttributes.addFlashAttribute("message", "리딤 코드가 발급되었습니다: " + redeemCode.getCode());
            redirectAttributes.addFlashAttribute("icon", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            redirectAttributes.addFlashAttribute("icon", "error");
        }

        return "redirect:/content/"+content.getType().toString().toLowerCase() + "/detail/" + contentId;
    }

    @PostMapping("/create/episode/{id}")
    public String createEpisodeRedeemCode(@PathVariable("id") Long episodeId,
                                          Principal principal,
                                          RedirectAttributes redirectAttributes) {

        if (principal == null) {
            redirectAttributes.addFlashAttribute("message", "로그인이 필요합니다.");
            redirectAttributes.addFlashAttribute("icon", "warning");
            return "redirect:/user/login";
        }

        Users user = userService.getUserByUsername(principal.getName());
        Episode episode = contentService.getEpisodeById(episodeId);
        
        if(episode==null) {
        	redirectAttributes.addFlashAttribute("message", "콘텐츠가 없습니다.");
            redirectAttributes.addFlashAttribute("icon", "warning");
            return "redirect:/";
        }

        try {
            RedeemCode redeemCode = redeemCodeService.createCode(TargetType.EPISODE, episodeId, user, null);
            redirectAttributes.addFlashAttribute("message", "리딤 코드가 발급되었습니다: " + redeemCode.getCode());
            redirectAttributes.addFlashAttribute("icon", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            redirectAttributes.addFlashAttribute("icon", "error");
        }

        return "redirect:/content/"+episode.getSeries().getContent().getType().toString().toLowerCase()+"/episode/" + episodeId;
    }
    
    @PostMapping("/content/{id}")
    public String redeemContentCode(@PathVariable("id") Long contentId,
                                    @RequestParam("code") String code,
                                    Principal principal,
                                    RedirectAttributes redirectAttributes) {

        if (principal == null) {
            redirectAttributes.addFlashAttribute("message", "로그인이 필요합니다.");
            redirectAttributes.addFlashAttribute("icon", "warning");
            return "redirect:/user/login";
        }
        Content content = contentService.getContent(contentId);
        
        if(content== null) {
        	redirectAttributes.addFlashAttribute("message", "해당하는 콘텐츠가 없습니다.");
            redirectAttributes.addFlashAttribute("icon", "warning");
            return "redirect:/";
        }

        Users user = userService.getUserByUsername(principal.getName());

        try {
            redeemCodeService.redeemForTarget(code, TargetType.CONTENT, contentId, user);
            redirectAttributes.addFlashAttribute("message", "리딤 코드가 적용되었습니다.");
            redirectAttributes.addFlashAttribute("icon", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            redirectAttributes.addFlashAttribute("icon", "error");
        }

        return "redirect:/content/"+ content.getType().toString().toLowerCase() + "/detail/" + contentId;
    }

    @PostMapping("/episode/{id}")
    public String redeemEpisodeCode(@PathVariable("id") Long episodeId,
                                    @RequestParam("code") String code,
                                    Principal principal,
                                    RedirectAttributes redirectAttributes) {

        if (principal == null) {
            redirectAttributes.addFlashAttribute("message", "로그인이 필요합니다.");
            redirectAttributes.addFlashAttribute("icon", "warning");
            return "redirect:/user/login";
        }
        
        Episode episode = contentService.getEpisodeById(episodeId);

        Users user = userService.getUserByUsername(principal.getName());

        try {
            redeemCodeService.redeemForTarget(code, TargetType.EPISODE, episodeId, user);
            redirectAttributes.addFlashAttribute("message", "리딤 코드가 적용되었습니다.");
            redirectAttributes.addFlashAttribute("icon", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            redirectAttributes.addFlashAttribute("icon", "error");
        }

        return "redirect:/content/"+episode.getSeries().getContent().getType().toString().toLowerCase()+"/episode/" + episodeId;
    }
}