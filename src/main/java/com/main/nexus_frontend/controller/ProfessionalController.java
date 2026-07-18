package com.main.nexus_frontend.controller;

import com.main.nexus_frontend.model.MatchDTO;
import com.main.nexus_frontend.service.ProfessionalService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/pro")
public class ProfessionalController {

    @Autowired
    private ProfessionalService professionalService;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        String userName = (String) session.getAttribute("userName");

        List<MatchDTO> allMatches = professionalService.getMatches(token);
        List<MatchDTO> invites = professionalService.getPendingInvites(token);

        List<MatchDTO> confirmed = allMatches.stream()
                .filter(m -> "MATCHED".equals(m.getStatus()))
                .collect(Collectors.toList());

        double avgScore = allMatches.isEmpty() ? 0.0 :
                allMatches.stream()
                        .mapToDouble(m -> m.getMatchScore() != null ? m.getMatchScore() : 0.0)
                        .average()
                        .orElse(0.0);

        model.addAttribute("userName", userName);
        model.addAttribute("invites", invites);
        model.addAttribute("invitesCount", invites.size());
        model.addAttribute("confirmed", confirmed);
        model.addAttribute("confirmedCount", confirmed.size());
        model.addAttribute("avgScore", Math.round(avgScore));
        model.addAttribute("recentInvites", invites.stream().limit(5).collect(Collectors.toList()));
        model.addAttribute("recentConfirmed", confirmed.stream().limit(5).collect(Collectors.toList()));

        return "pro-dashboard";
    }
}
