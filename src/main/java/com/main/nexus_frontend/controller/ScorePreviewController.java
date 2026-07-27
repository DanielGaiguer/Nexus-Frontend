package com.main.nexus_frontend.controller;

import com.main.nexus_frontend.model.ScorePreviewResponseDTO;
import com.main.nexus_frontend.service.ScorePreviewService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ScorePreviewController {

    @Autowired
    private ScorePreviewService scorePreviewService;

    // Empresa consultando o score de um profissional em um dos seus projetos
    // Chamado pelo modal de match em public/public-profile.html
    @PostMapping("/company/score-preview")
    public ResponseEntity<?> companyScorePreview(
            @RequestBody Map<String, Long> body,
            HttpSession session) {
        String token = (String) session.getAttribute("token");
        try {
            Long professionalId = body.get("professionalId");
            Long projectId = body.get("projectId");
            ScorePreviewResponseDTO result =
                    scorePreviewService.previewForCompany(token, professionalId, projectId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Não foi possível calcular o score.");
        }
    }

    // Profissional consultando seu próprio score em uma vaga/projeto
    // Chamado pelo modal de match em public/public-company.html
    @PostMapping("/pro/score-preview")
    public ResponseEntity<?> proScorePreview(
            @RequestBody Map<String, Long> body,
            HttpSession session) {
        String token = (String) session.getAttribute("token");
        try {
            Long projectId = body.get("projectId");
            ScorePreviewResponseDTO result =
                    scorePreviewService.previewForProfessional(token, projectId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Não foi possível calcular o score.");
        }
    }
}
