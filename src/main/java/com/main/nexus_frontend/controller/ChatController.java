package com.main.nexus_frontend.controller;

import com.main.nexus_frontend.model.ChatSummaryDTO;
import com.main.nexus_frontend.service.ChatBffService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private ChatBffService chatBffService;

    @Value("${nexus.ws.base-url}")
    private String wsBaseUrl;

    @GetMapping
    public String listPage(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/auth/login";
        }
        model.addAttribute("activePage", "chat");
        return "chat/chat-list";
    }

    @GetMapping("/{matchId}")
    public String chatPage(@PathVariable Long matchId, HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return "redirect:/auth/login";
        }
        String matchesUrl = matchesUrlForRole((String) session.getAttribute("userRole"));

        ChatSummaryDTO summary;
        try {
            summary = chatBffService.getMatches(token).stream()
                    .filter(c -> matchId.equals(c.getMatchId()))
                    .findFirst()
                    .orElse(null);
        } catch (ResponseStatusException e) {
            return "redirect:" + matchesUrl;
        }

        // Não encontrado na lista de chats disponíveis: match inexistente
        // ou sem acesso — a listagem já filtra por status MATCHED.
        if (summary == null) {
            return "redirect:" + matchesUrl;
        }

        model.addAttribute("matchId", matchId);
        model.addAttribute("summary", summary);
        model.addAttribute("wsToken", token);
        model.addAttribute("wsBaseUrl", wsBaseUrl);
        model.addAttribute("matchesUrl", matchesUrl);
        model.addAttribute("activePage", "chat");
        return "chat/chat";
    }

    private String matchesUrlForRole(String role) {
        if ("COMPANY".equals(role)) return "/company/matches";
        if ("PROFESSIONAL".equals(role)) return "/pro/matches";
        return "/";
    }
}
