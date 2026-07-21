package com.main.nexus_frontend.controller;

import com.main.nexus_frontend.model.*;
import com.main.nexus_frontend.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/company")
public class CompanyController {

    @Autowired
    private CompanyService companyService;
    @Autowired
    private ProjectService projectService;
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
    private ProfessionalService professionalService;
    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        String userName = (String) session.getAttribute("userName");
        try {
            CompanyDashboardDTO dashboard = companyService.getDashboard(token);
            model.addAttribute("companyName", userName);
            model.addAttribute("totalProjects", dashboard.getTotalProjects() != null ? dashboard.getTotalProjects() : 0);
            model.addAttribute("confirmedMatches", dashboard.getConfirmedMatches() != null ? dashboard.getConfirmedMatches() : 0);
            List<ProjectDTO> allProjects = projectService.getProjects(token);
            long openCount = allProjects.stream().filter(p -> "OPEN".equals(p.getStatus())).count();
            model.addAttribute("openProjects", (int) openCount);
            model.addAttribute("recentProjects", allProjects.stream().limit(8).collect(Collectors.toList()));
        } catch (Exception e) {
            model.addAttribute("companyName", userName);
            model.addAttribute("totalProjects", 0);
            model.addAttribute("confirmedMatches", 0);
            model.addAttribute("openProjects", 0);
            model.addAttribute("recentProjects", List.of());
        }
        model.addAttribute("activePage", "dashboard");
        return "company/company-dashboard";
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        try {
            CompanyProfileDTO profile = companyService.getProfile(token);
            model.addAttribute("profile", profile);
            CompanyDashboardDTO dashboard = companyService.getDashboard(token);
            model.addAttribute("totalProjects", dashboard.getTotalProjects() != null ? dashboard.getTotalProjects() : 0);
            model.addAttribute("confirmedMatches", dashboard.getConfirmedMatches() != null ? dashboard.getConfirmedMatches() : 0);
        } catch (Exception e) {
            model.addAttribute("profile", new CompanyProfileDTO());
            model.addAttribute("totalProjects", 0);
            model.addAttribute("confirmedMatches", 0);
        }
        model.addAttribute("activePage", "profile");
        return "company/company-profile";
    }

    @PostMapping("/profile")
    public String updateProfile(
            @RequestParam String companyName,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String cep,
            @RequestParam(required = false) String description,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        try {
            UpdateCompanyDTO dto = new UpdateCompanyDTO();
            dto.setCompanyName(companyName);
            dto.setPhone(phone);
            dto.setCep(cep);
            dto.setDescription(description);
            companyService.updateProfile(token, dto);
            redirectAttributes.addFlashAttribute("successMsg", "Perfil atualizado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Erro ao atualizar perfil: " + e.getMessage());
        }
        return "redirect:/company/profile";
    }

    @GetMapping("/projects")
    public String projects(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        List<ProjectDTO> projects = projectService.getProjects(token);
        model.addAttribute("projects", projects);
        model.addAttribute("activePage", "projects");
        return "company/company-projects";
    }

    @GetMapping("/projects/new")
    public String newProjectForm(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        List<SkillDTO> allSkills = adminService.getSkills(token);
        model.addAttribute("editMode", false);
        model.addAttribute("project", null);
        model.addAttribute("allSkills", allSkills);
        model.addAttribute("activePage", "projects");
        return "company/company-project-form";
    }

    @PostMapping("/projects/new")
    public String createProject(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam Double minimumBudget,
            @RequestParam Double maximumBudget,
            @RequestParam String deadline,
            @RequestParam String workMode,
            @RequestParam String type,
            @RequestParam(required = false) String experienceLevel,
            @RequestParam(required = false, defaultValue = "1") Integer maxPositions,
            @RequestParam(value = "skillIds", required = false) List<Long> skillIds,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        try {
            CreateProjectDTO dto = new CreateProjectDTO();
            dto.setTitle(title);
            dto.setDescription(description);
            dto.setMinimumBudget(minimumBudget);
            dto.setMaximumBudget(maximumBudget);
            dto.setDeadline(deadline);
            dto.setWorkMode(workMode);
            dto.setType(type);
            dto.setExperienceLevel(experienceLevel);
            dto.setMaxPositions(maxPositions);
            dto.setSkillIds(skillIds);
            projectService.createProject(token, dto);
            redirectAttributes.addFlashAttribute("successMsg", "Projeto criado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Erro ao criar projeto: " + e.getMessage());
        }
        return "redirect:/company/projects";
    }

    @GetMapping("/projects/{id}/edit")
    public String editProjectForm(
            @PathVariable Long id,
            HttpSession session,
            Model model) {
        String token = (String) session.getAttribute("token");
        ProjectDTO project = projectService.getProject(token, id);
        List<SkillDTO> allSkills = adminService.getSkills(token);
        model.addAttribute("editMode", true);
        model.addAttribute("project", project);
        model.addAttribute("allSkills", allSkills);
        model.addAttribute("activePage", "projects");
        return "company/company-project-form";
    }

    @PostMapping("/projects/{id}/edit")
    public String updateProject(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam Double minimumBudget,
            @RequestParam Double maximumBudget,
            @RequestParam String deadline,
            @RequestParam String workMode,
            @RequestParam String type,
            @RequestParam(required = false) String experienceLevel,
            @RequestParam(required = false, defaultValue = "1") Integer maxPositions,
            @RequestParam(value = "skillIds", required = false) List<Long> skillIds,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        try {
            CreateProjectDTO dto = new CreateProjectDTO();
            dto.setTitle(title);
            dto.setDescription(description);
            dto.setMinimumBudget(minimumBudget);
            dto.setMaximumBudget(maximumBudget);
            dto.setDeadline(deadline);
            dto.setWorkMode(workMode);
            dto.setType(type);
            dto.setExperienceLevel(experienceLevel);
            dto.setMaxPositions(maxPositions);
            dto.setSkillIds(skillIds);
            projectService.updateProject(token, id, dto);
            redirectAttributes.addFlashAttribute("successMsg", "Projeto atualizado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Erro ao atualizar projeto: " + e.getMessage());
        }
        return "redirect:/company/projects";
    }

    @PostMapping("/projects/{id}/close")
    public String closeProject(
            @PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        try {
            projectService.closeProject(token, id);
            redirectAttributes.addFlashAttribute("successMsg", "Projeto encerrado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Erro ao encerrar projeto: " + e.getMessage());
        }
        return "redirect:/company/projects";
    }

    @PostMapping("/projects/{id}/delete")
    public String deleteProject(
            @PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        try {
            projectService.deleteProject(token, id);
            redirectAttributes.addFlashAttribute("successMsg", "Projeto excluído com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Erro ao excluir projeto: " + e.getMessage());
        }
        return "redirect:/company/projects";
    }

    @GetMapping("/projects/{id}/ranking")
    public String ranking(
            @PathVariable Long id,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String uf,
            @RequestParam(required = false) String experienceLevel,
            @RequestParam(required = false) String available,
            @RequestParam(required = false) Double minSalary,
            @RequestParam(required = false) Double maxSalary,
            @RequestParam(required = false) String skill,
            HttpSession session,
            Model model) {
        String token = (String) session.getAttribute("token");
        ProjectDTO project = projectService.getProject(token, id);

        Map<String, String> filters = new java.util.LinkedHashMap<>();
        if (city != null && !city.isBlank()) filters.put("city", city);
        if (uf != null && !uf.isBlank()) filters.put("uf", uf);
        if (experienceLevel != null && !experienceLevel.isBlank()) filters.put("experienceLevel", experienceLevel);
        if (available != null && !available.isBlank()) filters.put("available", available);
        if (minSalary != null) filters.put("minSalary", String.valueOf(minSalary));
        if (maxSalary != null) filters.put("maxSalary", String.valueOf(maxSalary));
        if (skill != null && !skill.isBlank()) filters.put("skill", skill);

        List<MatchDTO> ranking = projectService.getRanking(token, id, filters);

        model.addAttribute("project", project);
        model.addAttribute("ranking", ranking);
        model.addAttribute("activePage", "projects");

        model.addAttribute("filterCity", city != null ? city : "");
        model.addAttribute("filterUf", uf != null ? uf : "");
        model.addAttribute("filterExperienceLevel", experienceLevel != null ? experienceLevel : "");
        model.addAttribute("filterAvailable", available != null ? available : "");
        model.addAttribute("filterMinSalary", minSalary != null ? minSalary : "");
        model.addAttribute("filterMaxSalary", maxSalary != null ? maxSalary : "");
        model.addAttribute("filterSkill", skill != null ? skill : "");

        return "company/company-ranking";
    }

    @PostMapping("/matches/{matchId}/interest")
    public String companyInterest(
            @PathVariable Long matchId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        try {
            matchService.companyInterest(token, matchId);
            redirectAttributes.addFlashAttribute("successMsg", "Interesse demonstrado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Erro ao demonstrar interesse: " + e.getMessage());
        }
        return "redirect:/company/projects";
    }

    @GetMapping("/matches")
    public String matches(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        List<MatchDTO> allMatches = matchService.getCompanyMatches(token);
        List<MatchDTO> receivedInterests = allMatches.stream()
                .filter(m -> "PROFESSIONAL_INTERESTED".equals(m.getStatus()))
                .collect(Collectors.toList());
        List<MatchDTO> confirmed = allMatches.stream()
                .filter(m -> "MATCHED".equals(m.getStatus()))
                .collect(Collectors.toList());
        model.addAttribute("receivedInterests", receivedInterests);
        model.addAttribute("confirmed", confirmed);
        model.addAttribute("activePage", "matches");
        return "company/company-matches";
    }

    @PostMapping("/matches/{matchId}/accept")
    public String acceptMatch(
            @PathVariable Long matchId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        try {
            matchService.companyAccept(token, matchId);
            redirectAttributes.addFlashAttribute("successMsg", "Interesse aceito! Match confirmado.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Erro ao aceitar: " + e.getMessage());
        }
        return "redirect:/company/matches";
    }

    @PostMapping("/matches/{matchId}/reject")
    public String rejectMatch(
            @PathVariable Long matchId,
            @RequestParam(value = "reasons", required = false) List<String> reasons,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        try {
            matchService.companyReject(token, matchId, reasons != null ? reasons : List.of());
            redirectAttributes.addFlashAttribute("successMsg", "Interesse recusado.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Erro ao recusar: " + e.getMessage());
        }
        return "redirect:/company/matches";
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
            dto.setAuthorType("COMPANY");
            dto.setPositiveReasons(positiveReasons != null ? positiveReasons : List.of());
            dto.setNegativeReasons(negativeReasons != null ? negativeReasons : List.of());
            reviewService.submit(token, matchId, dto);
            redirectAttributes.addFlashAttribute("successMsg", "Avaliação enviada com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Erro ao enviar avaliação: " + e.getMessage());
        }
        return "redirect:/company/matches";
    }

    @GetMapping("/analytics")
    public String analytics(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");

        try {
            CompanyDashboardAnalyticsDTO analytics = companyService.getAnalytics(token);
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

        model.addAttribute("activePage", "analytics");
        return "company/company-analytics";
    }

    @GetMapping("/reputation")
    public String reputation(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        try {
            CompanyProfileDTO profile = companyService.getProfile(token);
            ReputationDTO reputation = reputationService.getCompany(token, profile.getId());
            model.addAttribute("reputation", reputation);
        } catch (Exception e) {
            model.addAttribute("reputation", new ReputationDTO());
        }
        model.addAttribute("activePage", "profile");
        return "company/company-reputation";
    }

    @GetMapping("/map")
    public String map(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        try {
            CompanyProfileDTO profile = companyService.getProfile(token);
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
        return "company/company-map";
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

    @PostMapping("/profile/photo")
    @ResponseBody
    public ResponseEntity<String> uploadCompanyPhoto(
            @RequestParam("file") MultipartFile file,
            HttpSession session) {
        String token = (String) session.getAttribute("token");
        try {
            String url = companyService.uploadPhoto(token, file);
            session.setAttribute("profilePhotoUrl", url);
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao fazer upload: " + e.getMessage());
        }
    }

    @DeleteMapping("/profile/photo")
    @ResponseBody
    public ResponseEntity<String> removeCompanyPhoto(HttpSession session) {
        String token = (String) session.getAttribute("token");
        try {
            companyService.removePhoto(token);
            session.removeAttribute("profilePhotoUrl");
            return ResponseEntity.ok("Foto removida.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao remover: " + e.getMessage());
        }
    }
}
