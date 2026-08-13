package com.main.nexus_frontend.controller;

import com.main.nexus_frontend.exception.NexusApiException;
import com.main.nexus_frontend.service.MatchService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/matches")
public class MatchStatusCheckController {

    @Autowired
    private MatchService matchService;

    @GetMapping("/{matchId}/status-check")
    public String statusCheckForm(@PathVariable Long matchId, Model model) {
        model.addAttribute("matchId", matchId);
        return "matches/status-check";
    }

    @PostMapping("/{matchId}/status-check")
    public String submitStatusCheck(
            @PathVariable Long matchId,
            @RequestParam String outcome,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        try {
            matchService.answerStatusCheck(token, matchId, outcome);
            redirectAttributes.addFlashAttribute("successMsg", "Obrigado pelo feedback!");
            return "redirect:/company/matches";
        } catch (NexusApiException e) {
            if (e.getHttpStatus() == 409) {
                redirectAttributes.addFlashAttribute("successMsg", "Você já respondeu esta pergunta.");
                return "redirect:/company/matches";
            }
            redirectAttributes.addFlashAttribute("errorMsg",
                    "Não foi possível registrar sua resposta. Tente novamente.");
            return "redirect:/company/matches";
        }
    }

    // Não existe hoje uma tela de detalhe de um match único — encaminha para a listagem da empresa.
    @GetMapping("/{matchId}")
    public String matchDetailRedirect(@PathVariable Long matchId) {
        return "redirect:/company/matches";
    }
}
