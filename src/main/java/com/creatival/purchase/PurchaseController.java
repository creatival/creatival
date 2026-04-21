package com.creatival.purchase;

import java.math.BigDecimal;
import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.creatival.content.Content;
import com.creatival.content.ContentService;
import com.creatival.content.Episode;
import com.creatival.user.UserService;
import com.creatival.user.Users;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PurchaseController {

    private final ContentService contentService;
    private final UserService userService;
    private final PurchaseService purchaseService;

    @PostMapping("/content/{id}/purchase")
    public String purchaseContent(@PathVariable("id") Long contentId,
                                  Principal principal,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {

        Content content = contentService.getContent(contentId);
        String type = content.getType().toString().toLowerCase();
        String redirectUrl = "/content/" + type + "/detail/" + contentId;

        if (principal == null) {
            redirectAttributes.addFlashAttribute("isLogMsg", true);
            return "redirect:" + redirectUrl;
        }

        Users user = userService.getUserByUsername(principal.getName());

        if (!content.isPaid()) {
            redirectAttributes.addFlashAttribute("message", "해당 콘텐츠는 무료입니다.");
            redirectAttributes.addFlashAttribute("icon", "warning");
            return "redirect:" + redirectUrl;
        }

        if (content.getUser() != null && content.getUser().getId().equals(user.getId())) {
            redirectAttributes.addFlashAttribute("message", "본인 콘텐츠는 바로 열람할 수 있습니다.");
            redirectAttributes.addFlashAttribute("icon", "info");
            return "redirect:" + redirectUrl;
        }

        if (purchaseService.hasAccess(user, content)) {
            redirectAttributes.addFlashAttribute("message", "이미 구매한 콘텐츠입니다.");
            redirectAttributes.addFlashAttribute("icon", "info");
            return "redirect:" + redirectUrl;
        }

        String paymentId = "purchase_" + System.currentTimeMillis();

        model.addAttribute("paymentId", paymentId);
        model.addAttribute("targetType", "CONTENT");
        model.addAttribute("targetId", contentId);
        model.addAttribute("amount", content.getPrice());

        return "purchase/payment_ready";
    }
    
    @PostMapping("/episode/{id}/purchase")
    public String purchaseEpisode(@PathVariable("id") Long id, Principal principal, Model model, RedirectAttributes redirectAttributes) {
    	Episode episode = contentService.getEpisodeById(id);
    	Content content = episode.getSeries().getContent();
   	 if (principal == null) {
            redirectAttributes.addFlashAttribute("isLogMsg", true);
            return "redirect:/content/"+content.getType().toString().toLowerCase()+"/episode/"+episode.getId();
        }

       Users user = userService.getUserByUsername(principal.getName());
       

       if (!episode.isPaid()) {
           redirectAttributes.addFlashAttribute("message", "해당 콘텐츠는 무료 콘텐츠입니다.");
           redirectAttributes.addFlashAttribute("icon", "warning");
           return "redirect:/content/"+content.getType().toString().toLowerCase()+"/episode/"+episode.getId();
       }

       BigDecimal price = episode.getPrice();

       String paymentId = "purchase_" + System.currentTimeMillis();

       model.addAttribute("paymentId", paymentId);
       model.addAttribute("targetType", "EPISODE");
       model.addAttribute("targetId", id);
       model.addAttribute("amount", price);

       return "purchase_payment_ready";
    }
    
    @GetMapping("/purchase/success")
    public String purchaseSuccess(@RequestParam("targetType") String targetType,
                                  @RequestParam("targetId") Long targetId,
                                  Model model) {

        String redirectUrl = "/";

        if ("CONTENT".equalsIgnoreCase(targetType)) {
            Content content = contentService.getContent(targetId);
            redirectUrl = "/content/" + content.getType().toString().toLowerCase() + "/detail/" + targetId;
        } else if ("EPISODE".equalsIgnoreCase(targetType)) {
            Episode episode = contentService.getEpisodeById(targetId);
            Content content = episode.getSeries().getContent();
            redirectUrl = "/content/" + content.getType().toString().toLowerCase() + "/episode/" + episode.getId();
        }

        model.addAttribute("redirectUrl", redirectUrl);
        return "purchase_success";
    }

    @GetMapping("/purchase/fail")
    public String purchaseFail() {
        return "purchase_fail";
    }
}