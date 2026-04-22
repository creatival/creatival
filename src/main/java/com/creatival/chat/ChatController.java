package com.creatival.chat;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.creatival.chat.dto.RequestChatDTO;
import com.creatival.chat.dto.ResponseChatDTO;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/chat")
    public String chatPage() {
        return "chat/chatbot";
    }

    @PostMapping("/chat/ask")
    @ResponseBody
    public ResponseChatDTO ask(@RequestBody RequestChatDTO request, Authentication authentication) {
        return chatService.ask(request.getMessage(), authentication);
    }
}