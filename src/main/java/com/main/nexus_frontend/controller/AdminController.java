package com.main.nexus_frontend.controller;

import com.main.nexus_frontend.exception.NexusApiException;
import com.main.nexus_frontend.model.*;
import com.main.nexus_frontend.service.AdminService;
import com.main.nexus_frontend.service.MapService;
import com.main.nexus_frontend.service.ReputationService;
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
import java.util.Set;
import java.util.TreeSet;
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
            model.addAttribute("monthlyData", monthlyData);
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
            model.addAttribute("monthlyData", List.of());
        }
        model.addAttribute("activePage", "dashboard");
        return "admin/admin-dashboard";
    }

    @GetMapping("/approvals")
    public String approvals(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        try {
            List<CompanyProfileDTO> companies = adminService.getAllCompanies(token);
            long pendingCount = companies.stream()
                    .filter(c -> "PENDING".equals(c.getStatus()))
                    .count();
            long approvedCount = companies.stream()
                    .filter(c -> "APPROVED".equals(c.getStatus()))
                    .count();
            long rejectedCount = companies.stream()
                    .filter(c -> "REJECTED".equals(c.getStatus()))
                    .count();
            model.addAttribute("companies", companies);
            model.addAttribute("pendingCount", (int) pendingCount);
            model.addAttribute("approvedCount", (int) approvedCount);
            model.addAttribute("rejectedCount", (int) rejectedCount);
        } catch (Exception e) {
            model.addAttribute("companies", List.of());
            model.addAttribute("pendingCount", 0);
            model.addAttribute("approvedCount", 0);
            model.addAttribute("rejectedCount", 0);
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
        } catch (NexusApiException e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Erro ao aprovar empresa: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg",
                    "Não foi possível conectar ao servidor. Tente novamente em instantes.");
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
        } catch (NexusApiException e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Erro ao rejeitar empresa: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg",
                    "Não foi possível conectar ao servidor. Tente novamente em instantes.");
        }
        return "redirect:/admin/approvals";
    }

    private static final List<String> DEFAULT_SKILL_CATEGORIES = List.of(
            "Backend", "Frontend", "Mobile", "DevOps", "Database", "Data", "API");

    @GetMapping("/skills")
    public String skills(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        List<SkillDTO> skills = adminService.getSkills(token);
        Map<String, List<SkillDTO>> skillsByCategory = skills.stream()
                .collect(Collectors.groupingBy(SkillDTO::getCategory));

        Set<String> allCategories = new TreeSet<>(DEFAULT_SKILL_CATEGORIES);
        allCategories.addAll(skillsByCategory.keySet());

        model.addAttribute("skills", skills);
        model.addAttribute("skillsByCategory", skillsByCategory);
        model.addAttribute("allCategories", allCategories);
        model.addAttribute("activePage", "skills");
        return "admin/admin-skills";
    }

    @PostMapping("/skills")
    public String createSkill(
            @RequestParam String name,
            @RequestParam String category,
            @RequestParam(required = false) String newCategory,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        String finalCategory = (newCategory != null && !newCategory.isBlank()) ? newCategory.trim() : category;
        try {
            adminService.createSkill(token, name, finalCategory);
            redirectAttributes.addFlashAttribute("successMsg", "Skill criada com sucesso!");
        } catch (NexusApiException e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Erro ao criar skill: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg",
                    "Não foi possível conectar ao servidor. Tente novamente em instantes.");
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
        } catch (NexusApiException e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Erro ao remover skill: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg",
                    "Não foi possível conectar ao servidor. Tente novamente em instantes.");
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
        } catch (NexusApiException e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Erro ao alterar status: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg",
                    "Não foi possível conectar ao servidor. Tente novamente em instantes.");
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/companies")
    public String companiesDirectory(Model model) {
        model.addAttribute("activePage", "companies");
        return "admin/admin-companies";
    }

    @GetMapping("/professionals")
    public String professionalsDirectory(Model model) {
        model.addAttribute("activePage", "professionals");
        return "admin/admin-professionals";
    }

    @GetMapping("/map")
    public String map(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String uf,
            @RequestParam(required = false) String type,
            HttpSession session,
            Model model) {
        String token = (String) session.getAttribute("token");
        double centerLat = -23.5505;
        double centerLng = -46.6333;

        try {
            List<MapProfessionalDTO> pros = mapService.getProfessionals(token, city, uf, type);
            List<MapCompanyDTO> cos       = mapService.getCompanies(token, city, uf);
            List<MapOpportunityDTO> opps  = mapService.getOpportunities(token, city, uf, null);
            model.addAttribute("professionalsJson", pros);
            model.addAttribute("companiesJson",     cos);
            model.addAttribute("opportunitiesJson", opps);
            if (city != null && !city.isBlank()) {
                double[] center = MapService.computeCenter(pros, cos, opps, centerLat, centerLng);
                centerLat = center[0];
                centerLng = center[1];
            }
        } catch (Exception e) {
            model.addAttribute("professionalsJson", List.of());
            model.addAttribute("companiesJson", List.of());
            model.addAttribute("opportunitiesJson", List.of());
        }

        model.addAttribute("centerLat", centerLat);
        model.addAttribute("centerLng", centerLng);
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
        List<SkillDTO> allSkills = adminService.getSkills(token);

        long openCount   = projects.stream().filter(p -> "OPEN".equals(p.getStatus())).count();
        long closedCount = projects.stream()
                .filter(p -> "CLOSED".equals(p.getStatus()) || "CANCELLED".equals(p.getStatus())).count();

        model.addAttribute("projects",   projects);
        model.addAttribute("allSkills",  allSkills);
        model.addAttribute("openCount",  openCount);
        model.addAttribute("closedCount", closedCount);
        model.addAttribute("filterOpportunityType", opportunityType != null ? opportunityType : "");
        model.addAttribute("activePage", "projects");
        return "admin/admin-projects";
    }

    @PostMapping("/projects/{id}/close")
    public String closeProject(
            @PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        try {
            adminService.closeProject(token, id);
            redirectAttributes.addFlashAttribute("successMsg", "Oportunidade encerrada com sucesso!");
        } catch (NexusApiException e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Erro ao encerrar oportunidade: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg",
                    "Não foi possível conectar ao servidor. Tente novamente em instantes.");
        }
        return "redirect:/admin/projects";
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
        List<MatchDTO> rejected = matches.stream()
                .filter(m -> "REJECTED".equals(m.getStatus()))
                .collect(Collectors.toList());
        List<PreviousProjectDTO> projects = adminService.getProfessionalProjects(token, id);

        long totalProjects = projects.size();
        long confirmedMatches = confirmed.size();
        long rejectedMatches = rejected.size();
        long decidedMatches = confirmedMatches + rejectedMatches;
        double successRate = decidedMatches > 0 ? confirmedMatches * 100.0 / decidedMatches : 0;

        ReputationDTO reputation;
        try {
            reputation = reputationService.getProfessional(token, id);
        } catch (Exception e) {
            reputation = new ReputationDTO();
        }

        model.addAttribute("profile", profile);
        model.addAttribute("professionalId", id);
        model.addAttribute("dashboard", dashboard);
        model.addAttribute("matches", confirmed);
        model.addAttribute("invites", invites);
        model.addAttribute("previousProjects", projects);
        model.addAttribute("totalProjects", totalProjects);
        model.addAttribute("confirmedMatches", confirmedMatches);
        model.addAttribute("decidedMatches", decidedMatches);
        model.addAttribute("successRate", Math.round(successRate));
        model.addAttribute("reputation", reputation);
        model.addAttribute("activePage", "users");
        return "admin/admin-professional-profile";
    }

    @GetMapping("/professional/{id}/analytics")
    public String professionalAnalytics(@PathVariable Long id, HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        try {
            ProfessionalDashboardAnalyticsDTO analytics = adminService.getProfessionalAnalytics(token, id);
            if (analytics.getMatchSummary() != null) analytics.getMatchSummary().recomputeAcceptanceRate();
            if (analytics.getAcceptanceRatePerCompany() != null) {
                analytics.getAcceptanceRatePerCompany().forEach(CompanyAcceptanceRateDTO::recomputeAcceptanceRate);
            }
            model.addAttribute("analytics", analytics);

            model.addAttribute("monthlyMatches", analytics.getMatchesPerMonth());
            model.addAttribute("scoreDistribution", analytics.getScoreDistribution());
            model.addAttribute("companyRates", analytics.getAcceptanceRatePerCompany());
            model.addAttribute("skillDemand", analytics.getMostRequiredSkills());
        } catch (Exception e) {
            model.addAttribute("analytics", null);
            model.addAttribute("monthlyMatches", List.of());
            model.addAttribute("scoreDistribution", List.of());
            model.addAttribute("companyRates", List.of());
            model.addAttribute("skillDemand", List.of());
            model.addAttribute("errorMsg", "Não foi possível carregar os dados analíticos.");
        }
        model.addAttribute("professionalId", id);
        model.addAttribute("activePage", "users");
        return "pro/pro-analytics";
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
        List<MatchDTO> rejected = matches.stream()
                .filter(m -> "REJECTED".equals(m.getStatus()))
                .collect(Collectors.toList());
        List<MatchDTO> previousProjects = matches.stream()
                .filter(m -> "MATCHED".equals(m.getStatus()) && Boolean.FALSE.equals(m.getActive()))
                .collect(Collectors.toList());

        List<ProjectDTO> closedProjects = projects.stream()
                .filter(p -> "CLOSED".equals(p.getStatus()))
                .collect(Collectors.toList());

        long totalProjects = projects.size();
        long openProjects = projects.stream()
                .filter(p -> "OPEN".equals(p.getStatus()))
                .count();
        long confirmedMatches = confirmed.size();
        long rejectedMatches = rejected.size();
        long decidedMatches = confirmedMatches + rejectedMatches;
        double successRate = decidedMatches > 0 ? confirmedMatches * 100.0 / decidedMatches : 0;

        ReputationDTO reputation;
        try {
            reputation = reputationService.getCompany(token, id);
        } catch (Exception e) {
            reputation = new ReputationDTO();
        }

        model.addAttribute("profile", profile);
        model.addAttribute("companyId", id);
        model.addAttribute("dashboard", dashboard);
        model.addAttribute("projects", projects);
        model.addAttribute("closedProjects", closedProjects);
        model.addAttribute("confirmed", confirmed);
        model.addAttribute("pending", pending);
        model.addAttribute("previousProjects", previousProjects);
        model.addAttribute("totalProjects", totalProjects);
        model.addAttribute("openProjects", openProjects);
        model.addAttribute("confirmedMatches", confirmedMatches);
        model.addAttribute("decidedMatches", decidedMatches);
        model.addAttribute("successRate", Math.round(successRate));
        model.addAttribute("reputation", reputation);
        model.addAttribute("activePage", "users");
        return "admin/admin-company-profile";
    }

    @GetMapping("/company/{id}/analytics")
    public String companyAnalytics(@PathVariable Long id, HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        try {
            CompanyDashboardAnalyticsDTO analytics = adminService.getCompanyAnalytics(token, id);
            if (analytics.getMatchSummary() != null) analytics.getMatchSummary().recomputeAcceptanceRate();
            if (analytics.getAcceptanceRatePerProject() != null) {
                analytics.getAcceptanceRatePerProject().forEach(ProjectAcceptanceRateDTO::recomputeAcceptanceRate);
            }
            model.addAttribute("analytics", analytics);

            model.addAttribute("monthlyMatches", analytics.getMatchesPerMonth());
            model.addAttribute("scoreDistribution", analytics.getScoreDistribution());
            model.addAttribute("projectRates", analytics.getAcceptanceRatePerProject());
            model.addAttribute("skillDemand", analytics.getMostRequiredSkills());
        } catch (Exception e) {
            model.addAttribute("analytics", null);
            model.addAttribute("monthlyMatches", List.of());
            model.addAttribute("scoreDistribution", List.of());
            model.addAttribute("projectRates", List.of());
            model.addAttribute("skillDemand", List.of());
            model.addAttribute("errorMsg", "Não foi possível carregar os dados analíticos.");
        }
        model.addAttribute("companyId", id);
        model.addAttribute("activePage", "users");
        return "company/company-analytics";
    }
}
