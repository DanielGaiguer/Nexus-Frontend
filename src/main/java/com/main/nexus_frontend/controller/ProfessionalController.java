package com.main.nexus_frontend.controller;

import com.main.nexus_frontend.model.*;
import com.main.nexus_frontend.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/pro")
public class ProfessionalController {

    @Autowired
    private ProfessionalService professionalService;
    @Autowired
    private MatchService matchService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private ReputationService reputationService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private MapService mapService;
    @Autowired
    private ObjectMapper objectMapper;

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
        model.addAttribute("activePage", "dashboard");

        return "pro/pro-dashboard";
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        ProfessionalProfileDTO profile = professionalService.getProfile(token);
        List<SkillDTO> allSkills = professionalService.getAllSkills(token);

        List<MatchDTO> allMatches = professionalService.getMatches(token);
        double avgScore = allMatches.isEmpty() ? 0.0 :
                allMatches.stream()
                        .mapToDouble(m -> m.getMatchScore() != null ? m.getMatchScore() : 0.0)
                        .average()
                        .orElse(0.0);

        model.addAttribute("profile", profile);
        model.addAttribute("allSkills", allSkills);
        model.addAttribute("avgScore", Math.round(avgScore));
        model.addAttribute("activePage", "profile");
        return "pro/pro-profile";
    }

    @PostMapping("/profile")
    public String updateProfile(
            @RequestParam String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String cep,
            @RequestParam(required = false) Double minimumSalary,
            @RequestParam(required = false) Double maximumSalary,
            @RequestParam(value = "preferredTypes", required = false) List<String> preferredTypes,
            @RequestParam(required = false) String experienceLevel,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        try {
            UpdateProfessionalDTO dto = new UpdateProfessionalDTO();
            dto.setName(name);
            dto.setPhone(phone);
            dto.setCep(cep);
            dto.setMinimumSalary(minimumSalary);
            dto.setMaximumSalary(maximumSalary);
            dto.setPreferredTypes(preferredTypes);
            dto.setExperienceLevel(experienceLevel);
            professionalService.updateProfile(token, dto);
            redirectAttributes.addFlashAttribute("successMsg", "Perfil atualizado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Erro ao atualizar perfil: " + e.getMessage());
        }
        return "redirect:/pro/profile";
    }

    @PostMapping("/profile/skills")
    public String updateSkills(
            @RequestParam(value = "skillIds", required = false) List<Long> skillIds,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        try {
            professionalService.updateSkills(token, skillIds != null ? skillIds : List.of());
            redirectAttributes.addFlashAttribute("successMsg", "Skills atualizadas com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Erro ao atualizar skills: " + e.getMessage());
        }
        return "redirect:/pro/profile";
    }

    @PostMapping("/profile/availability")
    public String updateAvailability(
            @RequestParam(value = "available", required = false) String available,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        try {
            ProfessionalProfileDTO currentProfile = professionalService.getProfile(token);
            UpdateProfessionalDTO dto = new UpdateProfessionalDTO();
            dto.setName(currentProfile.getName());
            dto.setPhone(currentProfile.getPhone());
            dto.setCep(currentProfile.getCep());
            dto.setMinimumSalary(currentProfile.getMinimumSalary());
            dto.setMaximumSalary(currentProfile.getMaximumSalary());
            dto.setPreferredTypes(currentProfile.getPreferredTypes());
            dto.setExperienceLevel(currentProfile.getExperienceLevel());
            dto.setAvailable("on".equals(available));
            professionalService.updateProfile(token, dto);
            redirectAttributes.addFlashAttribute("successMsg", "Disponibilidade atualizada!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Erro ao atualizar disponibilidade: " + e.getMessage());
        }
        return "redirect:/pro/profile";
    }

    @GetMapping("/portfolio")
    public String portfolio(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        List<PreviousProjectDTO> projects = professionalService.getProjects(token);
        model.addAttribute("projects", projects);
        model.addAttribute("activePage", "portfolio");
        return "pro/pro-portfolio";
    }

    @PostMapping("/portfolio")
    public String addProject(
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam String technologies,
            @RequestParam Integer yearOfCompletion,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        try {
            PreviousProjectDTO dto = new PreviousProjectDTO();
            dto.setTitle(title);
            dto.setDescription(description);
            dto.setTechnologies(technologies);
            dto.setYearOfCompletion(yearOfCompletion);
            professionalService.addProject(token, dto);
            redirectAttributes.addFlashAttribute("successMsg", "Projeto adicionado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Erro ao adicionar projeto: " + e.getMessage());
        }
        return "redirect:/pro/portfolio";
    }

    @PostMapping("/portfolio/{projectId}/delete")
    public String deleteProject(
            @PathVariable Long projectId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        try {
            professionalService.deleteProject(token, projectId);
            redirectAttributes.addFlashAttribute("successMsg", "Projeto removido com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Erro ao remover projeto: " + e.getMessage());
        }
        return "redirect:/pro/portfolio";
    }

    @GetMapping("/matches")
    public String matches(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        List<MatchDTO> allMatches = professionalService.getMatches(token);
        List<MatchDTO> invites = professionalService.getPendingInvites(token);
        List<MatchDTO> confirmed = allMatches.stream()
                .filter(m -> "MATCHED".equals(m.getStatus()))
                .collect(Collectors.toList());

        model.addAttribute("invites", invites);
        model.addAttribute("confirmed", confirmed);
        model.addAttribute("activePage", "matches");
        return "pro/pro-matches";
    }

    @PostMapping("/matches/{matchId}/accept")
    public String acceptMatch(
            @PathVariable Long matchId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        try {
            matchService.professionalAccept(token, matchId);
            redirectAttributes.addFlashAttribute("successMsg", "Convite aceito! Match confirmado.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Erro ao aceitar convite: " + e.getMessage());
        }
        return "redirect:/pro/matches";
    }

    @PostMapping("/matches/{matchId}/reject")
    public String rejectMatch(
            @PathVariable Long matchId,
            @RequestParam(value = "reasons", required = false) List<String> reasons,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        try {
            matchService.professionalReject(token, matchId, reasons != null ? reasons : List.of());
            redirectAttributes.addFlashAttribute("successMsg", "Convite recusado.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Erro ao recusar convite: " + e.getMessage());
        }
        return "redirect:/pro/matches";
    }

    @GetMapping("/matches/{matchId}/review")
    public String reviewForm(
            @PathVariable Long matchId,
            HttpSession session,
            Model model) {
        String token = (String) session.getAttribute("token");
        MatchDTO match = matchService.getMatch(token, matchId);
        model.addAttribute("match", match);
        model.addAttribute("activePage", "matches");
        return "shared/review-form";
    }

    @PostMapping("/matches/{matchId}/review")
    public String submitReview(
            @PathVariable Long matchId,
            @RequestParam Integer rating,
            @RequestParam(required = false) String comment,
            @RequestParam(value = "positiveReasons", required = false) List<String> positiveReasons,
            @RequestParam(value = "negativeReasons", required = false) List<String> negativeReasons,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        try {
            ReviewRequestDTO dto = new ReviewRequestDTO();
            dto.setRating(rating);
            dto.setComment(comment);
            dto.setAuthorType("PROFESSIONAL");
            dto.setPositiveReasons(positiveReasons != null ? positiveReasons : List.of());
            dto.setNegativeReasons(negativeReasons != null ? negativeReasons : List.of());
            reviewService.submit(token, matchId, dto);
            redirectAttributes.addFlashAttribute("successMsg", "Avaliação enviada com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Erro ao enviar avaliação: " + e.getMessage());
        }
        return "redirect:/pro/matches";
    }

    @GetMapping("/opportunities")
    public String opportunities(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        List<MatchDTO> opportunities = professionalService.getOpportunities(token);
        model.addAttribute("opportunities", opportunities);
        model.addAttribute("activePage", "opportunities");
        return "pro/pro-opportunities";
    }

    @PostMapping("/opportunities/{projectId}/interest")
    public String sendInterest(
            @PathVariable Long projectId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        try {
            professionalService.sendInterest(token, projectId);
            redirectAttributes.addFlashAttribute("successMsg", "Interesse enviado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Erro ao enviar interesse: " + e.getMessage());
        }
        return "redirect:/pro/opportunities";
    }

    @GetMapping("/reputation")
    public String reputation(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        Long userId = (Long) session.getAttribute("userId");
        try {
            ProfessionalProfileDTO profile = professionalService.getProfile(token);
            ReputationDTO reputation = reputationService.getProfessional(token, profile.getId());
            model.addAttribute("reputation", reputation);
        } catch (Exception e) {
            model.addAttribute("reputation", new ReputationDTO());
        }
        model.addAttribute("activePage", "profile");
        return "pro/pro-reputation";
    }

    @GetMapping("/map")
    public String map(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        try {
            ProfessionalProfileDTO profile = professionalService.getProfile(token);
            model.addAttribute("userLat", profile.getLatitude() != null ? profile.getLatitude() : -23.55);
            model.addAttribute("userLng", profile.getLongitude() != null ? profile.getLongitude() : -46.63);
        } catch (Exception e) {
            model.addAttribute("userLat", -23.55);
            model.addAttribute("userLng", -46.63);
        }
        try {
            List<MapProfessionalDTO> professionals = mapService.getProfessionals(token);
            List<MapCompanyDTO> companies = mapService.getCompanies(token);
            model.addAttribute("professionalsJson", objectMapper.writeValueAsString(professionals));
            model.addAttribute("companiesJson", objectMapper.writeValueAsString(companies));
        } catch (Exception e) {
            model.addAttribute("professionalsJson", "[]");
            model.addAttribute("companiesJson", "[]");
        }
        model.addAttribute("activePage", "map");
        return "pro/pro-map";
    }
}
