package com.example.backend.controllers;

import com.example.backend.dto.request.MessageRequest;
import com.example.backend.services.ConversationService;
import com.example.backend.utils.AuthenticationUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/api/v1/message")
@CrossOrigin
public class ConversationController {

    @Autowired
    private ConversationService conversationService;
    @Autowired
    private AuthenticationUtil authenticationUtil;

    @PostMapping("")
    public ResponseEntity sendMessages(HttpServletRequest request, @RequestBody @Valid MessageRequest messageRequest) throws ParseException {
        String isAuthenticated = authenticationUtil.authenticateUser(request);
        if(isAuthenticated != null) {
            return ResponseEntity.ok().body(conversationService.sendMessage(isAuthenticated, messageRequest));
        }
        return ResponseEntity.badRequest().body("You should login first");
    }

    @GetMapping("/{otherUserId}")
    public ResponseEntity getDetailConversation(HttpServletRequest request, @PathVariable String otherUserId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "0") int pageSize) throws ParseException {
        String isAuthenticated = authenticationUtil.authenticateUser(request);
        if(isAuthenticated != null) {
            return ResponseEntity.ok().body(conversationService.getMessages(isAuthenticated, otherUserId, page, pageSize));
        }
        return ResponseEntity.badRequest().body("You should login first");
    }

    @GetMapping("/conversations")
    public ResponseEntity getConversations(HttpServletRequest request, String queryName) throws ParseException {
        String isAuthenticated = authenticationUtil.authenticateUser(request);
        if (isAuthenticated != null) {
            return ResponseEntity.ok().body(conversationService.getConversation(isAuthenticated, queryName));
        }
        return ResponseEntity.badRequest().body("You should login first");
    }

    @DeleteMapping("/delete-message")
    public ResponseEntity deleteMessage(HttpServletRequest request, @RequestParam String messageId) throws ParseException {
        String isAuthenticated = authenticationUtil.authenticateUser(request);
        if (isAuthenticated != null) {
            conversationService.removeMessage(messageId, isAuthenticated);
            return ResponseEntity.ok().body("Message is deleted successfully");
        }
        return ResponseEntity.badRequest().body("You should login first");
    }

    @GetMapping("/get-message")
    public ResponseEntity getMessageById(HttpServletRequest request, @RequestParam String messageId) throws ParseException {
        String isAuthenticated = authenticationUtil.authenticateUser(request);
        if (isAuthenticated != null) {
            return ResponseEntity.ok().body(conversationService.getMessageById(messageId, isAuthenticated));
        }
        return ResponseEntity.badRequest().body("You should login first");
    }
}
