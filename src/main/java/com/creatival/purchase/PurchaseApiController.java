package com.creatival.purchase;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.creatival.content.Content;
import com.creatival.content.ContentService;
import com.creatival.content.Episode;
import com.creatival.purchase.dto.PurchaseCompleteRequestDTO;
import com.creatival.user.UserService;
import com.creatival.user.Users;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/purchase")
public class PurchaseApiController {

    private final PurchaseService purchaseService;
    private final ContentService contentService;
    private final UserService userService;

    @PostMapping("/complete")
    public ResponseEntity<?> completePurchase(@RequestBody PurchaseCompleteRequestDTO request,
                                              java.security.Principal principal) {

        if (principal == null) {
            return ResponseEntity.badRequest().body("로그인이 필요합니다.");
        }

        Users user = userService.getUserByUsername(principal.getName());

        if ("CONTENT".equalsIgnoreCase(request.getTargetType())) {
            Content content = contentService.getContent(request.getTargetId());
            purchaseService.createContentPurchase(user, content);
            return ResponseEntity.ok("OK");
        }

        if ("EPISODE".equalsIgnoreCase(request.getTargetType())) {
            Episode episode = contentService.getEpisodeById(request.getTargetId());
            purchaseService.createEpisodePurchase(user, episode);
            return ResponseEntity.ok("OK");
        }

        return ResponseEntity.badRequest().body("지원하지 않는 구매 타입입니다.");
    }
}