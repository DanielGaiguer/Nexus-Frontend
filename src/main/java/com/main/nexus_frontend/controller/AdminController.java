package com.main.nexus_frontend.controller;

import com.main.nexus_frontend.model.*;
import com.main.nexus_frontend.service.AdminService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
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
}
