package com.main.nexus_frontend.controller;

import com.main.nexus_frontend.exception.NexusApiException;
import com.main.nexus_frontend.model.AiExtractionRequestDTO;
import com.main.nexus_frontend.model.AiExtractionResponseDTO;
import com.main.nexus_frontend.service.ProjectAiExtractionBffService;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

// Endpoint JSON usado pela tela "company-project-form" para o autopreenchimento por IA —
// o texto colado nunca sai do navegador em outro lugar além desta chamada, e a resposta só
// pré-preenche inputs no client-side; a publicação continua sendo o POST /company/projects/new
// de sempre, feito manualmente pela empresa depois de revisar o formulário.
@Controller
@RequestMapping("/app-api/projects")
public class ProjectAiExtractionBffController {

    @Autowired
    private ProjectAiExtractionBffService projectAiExtractionBffService;

    @PostMapping("/ai-extract")
    @ResponseBody
    public ResponseEntity<?> extract(@RequestBody AiExtractionRequestDTO request, HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Sua sessão expirou. Faça login novamente."));
        }
        try {
            AiExtractionResponseDTO response = projectAiExtractionBffService.extract(token, request.getRawText());
            return ResponseEntity.ok(response);
        } catch (NexusApiException e) {
            return ResponseEntity.status(e.getHttpStatus()).body(Map.of("message", e.getMessage()));
        }
    }
}
