package com.main.nexus_frontend.controller;

import com.main.nexus_frontend.model.*;
import com.main.nexus_frontend.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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
            @RequestParam(value = "preferredTypes", required = false) List<String> preferredTypes,
            @RequestParam(required = false) String experienceLevel,
            @RequestParam(value = "preferredOpportunityTypes", required = false) List<String> preferredOpportunityTypes,
            @RequestParam(required = false) Double expectedSalaryCLT,
            @RequestParam(required = false) Double expectedSalaryPJ,
            @RequestParam(required = false) Double freelanceMinExpectation,
            @RequestParam(required = false) Double freelanceMaxExpectation,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        try {
            // available não faz parte deste form (tem endpoint próprio) — preserva o valor atual
            ProfessionalProfileDTO currentProfile = professionalService.getProfile(token);

            ProfessionalProfileDTO dto = new ProfessionalProfileDTO();
            dto.setName(name);
            dto.setPhone(phone);
            dto.setCep(cep);
            dto.setAvailable(currentProfile.getAvailable());
            dto.setPreferredTypes(preferredTypes);
            dto.setExperienceLevel(experienceLevel);
            dto.setPreferredOpportunityTypes(preferredOpportunityTypes);
            dto.setExpectedSalaryCLT(expectedSalaryCLT);
            dto.setExpectedSalaryPJ(expectedSalaryPJ);
            dto.setFreelanceMinExpectation(freelanceMinExpectation);
            dto.setFreelanceMaxExpectation(freelanceMaxExpectation);
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
            ProfessionalProfileDTO dto = new ProfessionalProfileDTO();
            dto.setName(currentProfile.getName());
            dto.setPhone(currentProfile.getPhone());
            dto.setCep(currentProfile.getCep());
            dto.setPreferredTypes(currentProfile.getPreferredTypes());
            dto.setExperienceLevel(currentProfile.getExperienceLevel());
            dto.setPreferredOpportunityTypes(currentProfile.getPreferredOpportunityTypes());
            dto.setExpectedSalaryCLT(currentProfile.getExpectedSalaryCLT());
            dto.setExpectedSalaryPJ(currentProfile.getExpectedSalaryPJ());
            dto.setFreelanceMinExpectation(currentProfile.getFreelanceMinExpectation());
            dto.setFreelanceMaxExpectation(currentProfile.getFreelanceMaxExpectation());
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

    @GetMapping("/matches/{id}/history")
    @ResponseBody
    public ResponseEntity<?> getMatchHistory(@PathVariable Long id, HttpSession session) {
        String token = (String) session.getAttribute("token");
        try {
            return ResponseEntity.ok(matchService.getHistory(token, id));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao carregar histórico: " + e.getMessage());
        }
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
    public String map(
            @RequestParam(required = false) String type,
            HttpSession session,
            Model model) {
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
            List<MapProfessionalDTO> professionals = mapService.getProfessionals(token, null, null, type);
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

    @PostMapping("/resume")
    public String uploadResume(
            @RequestParam("file") MultipartFile file,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        try {
            if (file.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMsg", "Selecione um arquivo PDF.");
                return "redirect:/pro/profile";
            }
            if (!"application/pdf".equals(file.getContentType())) {
                redirectAttributes.addFlashAttribute("errorMsg", "Somente arquivos PDF são aceitos.");
                return "redirect:/pro/profile";
            }
            professionalService.uploadResume(token, file);
            redirectAttributes.addFlashAttribute("successMsg", "Currículo enviado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Erro ao enviar currículo: " + e.getMessage());
        }
        return "redirect:/pro/profile";
    }

    @GetMapping("/{professionalId}/resume")
    public void downloadResume(
            @PathVariable Long professionalId,
            HttpSession session,
            HttpServletResponse response) {
        String token = (String) session.getAttribute("token");
        try {
            byte[] content = professionalService.downloadResume(token, professionalId);
            response.setContentType("application/pdf");
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                    "inline; filename=\"curriculo.pdf\"");
            response.getOutputStream().write(content);
            response.getOutputStream().flush();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @GetMapping("/stats")
    public String stats(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        ProfessionalStatsDTO stats = professionalService.getStats(token);
        model.addAttribute("stats", stats);
        model.addAttribute("activePage", "stats");
        return "pro/pro-stats";
    }

    @GetMapping("/profile/export")
    @ResponseBody
    public ResponseEntity<byte[]> exportPdf(
            @RequestParam(required = false) Long professionalId,
            HttpSession session) {
        String token = (String) session.getAttribute("token");
        byte[] pdf = professionalService.exportPdf(token, professionalId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"perfil-nexus.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @PostMapping("/profile/photo")
    @ResponseBody
    public ResponseEntity<String> uploadProPhoto(
            @RequestParam("file") MultipartFile file,
            HttpSession session) {
        String token = (String) session.getAttribute("token");
        try {
            String url = professionalService.uploadPhoto(token, file);
            session.setAttribute("profilePhotoUrl", url);
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao fazer upload: " + e.getMessage());
        }
    }

    @DeleteMapping("/profile/photo")
    @ResponseBody
    public ResponseEntity<String> removeProPhoto(HttpSession session) {
        String token = (String) session.getAttribute("token");
        try {
            professionalService.removePhoto(token);
            session.removeAttribute("profilePhotoUrl");
            return ResponseEntity.ok("Foto removida.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao remover: " + e.getMessage());
        }
    }
}
