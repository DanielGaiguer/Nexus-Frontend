package com.main.nexus_frontend.controller;

import com.main.nexus_frontend.model.*;
import com.main.nexus_frontend.service.AdminService;
import com.main.nexus_frontend.service.MapService;
import com.main.nexus_frontend.service.ReputationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;
    @Autowired
    private MapService mapService;
    @Autowired
    private ReputationService reputationService;
    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        try {
            AdminDashboardDTO dash = adminService.getDashboard(token);
            if (dash == null) dash = new AdminDashboardDTO();
            model.addAttribute("dashboard", dash);

            List<CompanyProfileDTO> pendingCompaniesList = adminService.getPendingCompanies(token);
            model.addAttribute("pendingList", pendingCompaniesList.stream().limit(5).collect(Collectors.toList()));
            model.addAttribute("latestCompanies", pendingCompaniesList);

            Integer totalMatches = dash.getTotalMatches();
            List<MonthlyDataDTO> monthlyData = List.of(
                    new MonthlyDataDTO("Jan", 0),
                    new MonthlyDataDTO("Fev", 0),
                    new MonthlyDataDTO("Mar", 0),
                    new MonthlyDataDTO("Abr", 0),
                    new MonthlyDataDTO("Mai", 0),
                    new MonthlyDataDTO("Jun", totalMatches != null ? totalMatches : 0)
            );
            model.addAttribute("monthlyDataJson", objectMapper.writeValueAsString(monthlyData));
        } catch (Exception e) {
            AdminDashboardDTO empty = new AdminDashboardDTO();
            empty.setTotalUsers(0);
            empty.setTotalProfessionals(0);
            empty.setTotalCompanies(0);
            empty.setTotalProjects(0);
            empty.setTotalOpenProjects(0);
            empty.setTotalMatches(0);
            empty.setTotalConfirmedMatches(0);
            empty.setAverageMatchScore(0.0);
            empty.setPendingCompanies(0);
            model.addAttribute("dashboard", empty);
            model.addAttribute("pendingList", List.of());
            model.addAttribute("latestCompanies", List.of());
            model.addAttribute("monthlyDataJson", "[]");
        }
        model.addAttribute("activePage", "dashboard");
        return "admin/admin-dashboard";
    }

    @GetMapping("/approvals")
    public String approvals(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        try {
            List<CompanyProfileDTO> companies = adminService.getPendingCompanies(token);
            long pendingCount = companies.stream()
                    .filter(c -> "PENDING".equals(c.getStatus()))
                    .count();
            model.addAttribute("companies", companies);
            model.addAttribute("pendingCount", (int) pendingCount);
        } catch (Exception e) {
            model.addAttribute("companies", List.of());
            model.addAttribute("pendingCount", 0);
        }
        model.addAttribute("activePage", "approvals");
        return "admin/admin-approvals";
    }

    @PostMapping("/companies/{id}/approve")
    public String approveCompany(
            @PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        try {
            adminService.approveCompany(token, id);
            redirectAttributes.addFlashAttribute("successMsg", "Empresa aprovada com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Erro ao aprovar empresa: " + e.getMessage());
        }
        return "redirect:/admin/approvals";
    }

    @PostMapping("/companies/{id}/reject")
    public String rejectCompany(
            @PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        try {
            adminService.rejectCompany(token, id);
            redirectAttributes.addFlashAttribute("successMsg", "Empresa rejeitada.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Erro ao rejeitar empresa: " + e.getMessage());
        }
        return "redirect:/admin/approvals";
    }

    @GetMapping("/skills")
    public String skills(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        List<SkillDTO> skills = adminService.getSkills(token);
        Map<String, List<SkillDTO>> skillsByCategory = skills.stream()
                .collect(Collectors.groupingBy(SkillDTO::getCategory));
        model.addAttribute("skills", skills);
        model.addAttribute("skillsByCategory", skillsByCategory);
        model.addAttribute("activePage", "skills");
        return "admin/admin-skills";
    }

    @PostMapping("/skills")
    public String createSkill(
            @RequestParam String name,
            @RequestParam String category,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        try {
            adminService.createSkill(token, name, category);
            redirectAttributes.addFlashAttribute("successMsg", "Skill criada com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Erro ao criar skill: " + e.getMessage());
        }
        return "redirect:/admin/skills";
    }

    @PostMapping("/skills/{id}/delete")
    public String deleteSkill(
            @PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        try {
            adminService.deleteSkill(token, id);
            redirectAttributes.addFlashAttribute("successMsg", "Skill removida com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Erro ao remover skill: " + e.getMessage());
        }
        return "redirect:/admin/skills";
    }

    @GetMapping("/users")
    public String users(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        List<UserSummaryDTO> users = adminService.getUsers(token);
        model.addAttribute("users", users);
        model.addAttribute("activePage", "users");
        return "admin/admin-users";
    }

    @PostMapping("/users/{id}/toggle")
    public String toggleUser(
            @PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        try {
            adminService.toggleUser(token, id);
            redirectAttributes.addFlashAttribute("successMsg", "Status do usuário alterado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Erro ao alterar status: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/map")
    public String map(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String uf,
            @RequestParam(required = false) String type,
            HttpSession session,
            Model model) {
        String token = (String) session.getAttribute("token");

        List<MapProfessionalDTO> pros = mapService.getProfessionals(token, city, uf, type);
        List<MapCompanyDTO> cos       = mapService.getCompanies(token, city, uf);

        try {
            model.addAttribute("professionalsJson", objectMapper.writeValueAsString(pros));
            model.addAttribute("companiesJson",     objectMapper.writeValueAsString(cos));
        } catch (Exception e) {
            model.addAttribute("professionalsJson", "[]");
            model.addAttribute("companiesJson",     "[]");
        }

        model.addAttribute("centerLat", -23.3045);
        model.addAttribute("centerLng", -51.1696);
        model.addAttribute("cityFilter", city != null ? city : "");
        model.addAttribute("ufFilter",   uf   != null ? uf   : "");
        model.addAttribute("activePage", "map");
        return "admin/admin-map";
    }

    @GetMapping("/projects")
    public String allProjects(
            @RequestParam(required = false) String opportunityType,
            HttpSession session,
            Model model) {
        String token = (String) session.getAttribute("token");
        List<ProjectDTO> projects = adminService.getAllProjects(token);

        long openCount   = projects.stream().filter(p -> "OPEN".equals(p.getStatus())).count();
        long closedCount = projects.stream()
                .filter(p -> "CLOSED".equals(p.getStatus()) || "CANCELLED".equals(p.getStatus())).count();

        model.addAttribute("projects",   projects);
        model.addAttribute("openCount",  openCount);
        model.addAttribute("closedCount", closedCount);
        model.addAttribute("filterOpportunityType", opportunityType != null ? opportunityType : "");
        model.addAttribute("activePage", "projects");
        return "admin/admin-projects";
    }

    @GetMapping("/professional/{id}")
    public String viewProfessional(@PathVariable Long id, HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        ProfessionalProfileDTO profile = adminService.getProfessionalProfile(token, id);
        ProfessionalDashboardDTO dashboard = adminService.getProfessionalDashboard(token, id);
        List<MatchDTO> matches = adminService.getProfessionalMatches(token, id);
        List<MatchDTO> invites = matches.stream()
                .filter(m -> "COMPANY_INTERESTED".equals(m.getStatus()))
                .collect(Collectors.toList());
        List<MatchDTO> confirmed = matches.stream()
                .filter(m -> "MATCHED".equals(m.getStatus()))
                .collect(Collectors.toList());
        List<PreviousProjectDTO> projects = adminService.getProfessionalProjects(token, id);

        long totalProjects = projects.size();
        long confirmedMatches = confirmed.size();
        double successRate = totalProjects > 0 ? confirmedMatches * 100.0 / totalProjects : 0;

        model.addAttribute("profile", profile);
        model.addAttribute("professionalId", id);
        model.addAttribute("dashboard", dashboard);
        model.addAttribute("matches", confirmed);
        model.addAttribute("invites", invites);
        model.addAttribute("previousProjects", projects);
        model.addAttribute("totalProjects", totalProjects);
        model.addAttribute("confirmedMatches", confirmedMatches);
        model.addAttribute("successRate", Math.round(successRate));
        model.addAttribute("activePage", "users");
        return "admin/admin-professional-profile";
    }

    @GetMapping("/professional/{id}/stats")
    public String professionalStats(@PathVariable Long id, HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        ProfessionalStatsDTO stats = adminService.getProfessionalStats(token, id);
        model.addAttribute("stats", stats);
        model.addAttribute("professionalId", id);
        model.addAttribute("activePage", "users");
        return "pro/pro-stats";
    }

    @GetMapping("/professional/{id}/export")
    @ResponseBody
    public ResponseEntity<byte[]> exportProfessionalPdf(
            @PathVariable Long id, HttpSession session) {
        String token = (String) session.getAttribute("token");
        byte[] pdf = adminService.exportProfessionalPdf(token, id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"perfil-nexus.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/company/{id}")
    public String viewCompany(@PathVariable Long id, HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        CompanyProfileDTO profile = adminService.getCompanyProfile(token, id);
        CompanyDashboardDTO dashboard = adminService.getCompanyDashboard(token, id);
        List<ProjectDTO> projects = adminService.getCompanyProjects(token, id);
        List<MatchDTO> matches = adminService.getCompanyMatches(token, id);
        List<MatchDTO> confirmed = matches.stream()
                .filter(m -> "MATCHED".equals(m.getStatus()))
                .collect(Collectors.toList());
        List<MatchDTO> pending = matches.stream()
                .filter(m -> "COMPANY_INTERESTED".equals(m.getStatus()) || "PROFESSIONAL_INTERESTED".equals(m.getStatus()))
                .collect(Collectors.toList());

        long totalProjects = projects.size();
        long openProjects = projects.stream()
                .filter(p -> "OPEN".equals(p.getStatus()))
                .count();
        long confirmedMatches = confirmed.size();
        double successRate = totalProjects > 0 ? confirmedMatches * 100.0 / totalProjects : 0;

        model.addAttribute("profile", profile);
        model.addAttribute("companyId", id);
        model.addAttribute("dashboard", dashboard);
        model.addAttribute("projects", projects);
        model.addAttribute("confirmed", confirmed);
        model.addAttribute("pending", pending);
        model.addAttribute("totalProjects", totalProjects);
        model.addAttribute("openProjects", openProjects);
        model.addAttribute("confirmedMatches", confirmedMatches);
        model.addAttribute("successRate", Math.round(successRate));
        model.addAttribute("activePage", "users");
        return "admin/admin-company-profile";
    }

    @GetMapping("/company/{id}/reputation")
    public String viewCompanyReputation(@PathVariable Long id, HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        try {
            ReputationDTO reputation = reputationService.getCompany(token, id);
            model.addAttribute("reputation", reputation);
        } catch (Exception e) {
            model.addAttribute("reputation", new ReputationDTO());
        }
        model.addAttribute("companyId", id);
        model.addAttribute("activePage", "users");
        return "admin/admin-company-reputation";
    }

    @GetMapping("/company/{id}/analytics")
    public String companyAnalytics(@PathVariable Long id, HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        try {
            CompanyDashboardAnalyticsDTO analytics = adminService.getCompanyAnalytics(token, id);
            model.addAttribute("analytics", analytics);

            model.addAttribute("monthlyMatchesJson",
                    objectMapper.writeValueAsString(analytics.getMatchesPerMonth()));
            model.addAttribute("scoreDistributionJson",
                    objectMapper.writeValueAsString(analytics.getScoreDistribution()));
            model.addAttribute("projectRatesJson",
                    objectMapper.writeValueAsString(analytics.getAcceptanceRatePerProject()));
            model.addAttribute("skillDemandJson",
                    objectMapper.writeValueAsString(analytics.getMostRequiredSkills()));
        } catch (Exception e) {
            model.addAttribute("analytics", null);
            model.addAttribute("monthlyMatchesJson", "[]");
            model.addAttribute("scoreDistributionJson", "[]");
            model.addAttribute("projectRatesJson", "[]");
            model.addAttribute("skillDemandJson", "[]");
            model.addAttribute("errorMsg", "Não foi possível carregar os dados analíticos.");
        }
        model.addAttribute("companyId", id);
        model.addAttribute("activePage", "users");
        return "company/company-analytics";
    }

    @GetMapping("/company/{companyId}/project/{projectId}")
    public String viewCompanyProject(
            @PathVariable Long companyId,
            @PathVariable Long projectId,
            HttpSession session,
            Model model) {
        String token = (String) session.getAttribute("token");
        CompanyProfileDTO profile = adminService.getCompanyProfile(token, companyId);
        List<ProjectDTO> projects = adminService.getCompanyProjects(token, companyId);
        ProjectDTO project = projects.stream()
                .filter(p -> projectId.equals(p.getId()))
                .findFirst()
                .orElse(null);
        if (project == null) {
            return "redirect:/admin/company/" + companyId;
        }
        model.addAttribute("profile", profile);
        model.addAttribute("companyId", companyId);
        model.addAttribute("project", project);
        model.addAttribute("activePage", "users");
        return "admin/admin-company-project-detail";
    }
}
