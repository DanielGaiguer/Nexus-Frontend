package com.main.nexus_frontend.controller;

import com.main.nexus_frontend.model.ChatSummaryDTO;
import com.main.nexus_frontend.model.MessageDTO;
import com.main.nexus_frontend.service.ChatBffService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

// Endpoints de dados (JSON) chamados pelo JS vanilla das telas de chat.
// O navegador nunca fala direto com o backend :8081 — passa sempre por aqui,
// que injeta o JWT da sessão no header Authorization.
@Controller
@RequestMapping("/app-api/chat")
public class ChatBffController {

    @Autowired
    private ChatBffService chatBffService;

    @Value("${nexus.ws.base-url}")
    private String wsBaseUrl;

    @GetMapping("/matches")
    @ResponseBody
    public ResponseEntity<List<ChatSummaryDTO>> getMatches(HttpSession session) {
        String jwt = (String) session.getAttribute("token");
        if (jwt == null) {
            return ResponseEntity.status(401).build();
        }
        try {
            return ResponseEntity.ok(chatBffService.getMatches(jwt));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }

    @GetMapping("/{matchId}/messages")
    @ResponseBody
    public ResponseEntity<List<MessageDTO>> getMessages(@PathVariable Long matchId, HttpSession session) {
        String jwt = (String) session.getAttribute("token");
        if (jwt == null) {
            return ResponseEntity.status(401).build();
        }
        try {
            return ResponseEntity.ok(chatBffService.getMessages(matchId, jwt));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }

    @GetMapping("/unread-total")
    @ResponseBody
    public ResponseEntity<Long> getUnreadTotal(HttpSession session) {
        String jwt = (String) session.getAttribute("token");
        if (jwt == null) {
            return ResponseEntity.status(401).build();
        }
        try {
            return ResponseEntity.ok(chatBffService.getUnreadTotal(jwt));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }

    // Credenciais pra abrir a conexão WebSocket a partir de qualquer página (não só
    // a tela de chat) — usado pelo conector global que atualiza o badge em tempo real.
    // É o mesmo token de sessão que a página de chat já embute direto no HTML hoje;
    // aqui só fica disponível pra JS de qualquer tela pedir sob demanda.
    @GetMapping("/ws-token")
    @ResponseBody
    public ResponseEntity<Map<String, String>> getWsToken(HttpSession session) {
        String jwt = (String) session.getAttribute("token");
        if (jwt == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(Map.of("token", jwt, "wsBaseUrl", wsBaseUrl));
    }
}
