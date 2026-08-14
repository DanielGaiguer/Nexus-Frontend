package com.main.nexus_frontend.controller;

import com.main.nexus_frontend.model.CompanyDTO;
import com.main.nexus_frontend.model.CompanyStatsDTO;
import com.main.nexus_frontend.model.MatchDTO;
import com.main.nexus_frontend.model.ProjectDTO;
import com.main.nexus_frontend.model.PublicProfessionalDTO;
import com.main.nexus_frontend.model.ReputationDTO;
import com.main.nexus_frontend.service.PublicOpportunityService;
import com.main.nexus_frontend.service.PublicService;
import com.main.nexus_frontend.service.ReputationService;
import com.main.nexus_frontend.service.ProjectService;
import com.main.nexus_frontend.service.ProfessionalService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/public")
public class PublicController {

    @Autowired
    private PublicOpportunityService publicOpportunityService;

    @Autowired
    private PublicService publicService;

    @Autowired
    private ReputationService reputationService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProfessionalService professionalService;

    // Perfil público do profissional
    @GetMapping("/professional/{id}")
    public String professionalProfile(@PathVariable Long id, HttpSession session, Model model) {
        try {
            PublicProfessionalDTO professional = publicService.getProfessional(id);

            model.addAttribute("professional", professional);
            model.addAttribute("previousProjects", professional.getPreviousProjects());

            // Reputação detalhada — já vem embutida no perfil público
            model.addAttribute("reputation", professional.getReputationDetails());

            // Se visualizado por uma empresa, carrega os projetos dela para o modal de match
            if ("COMPANY".equals(session.getAttribute("userRole"))) {
                String token = (String) session.getAttribute("token");
                try {
                    List<ProjectDTO> companyProjects = projectService.getProjects(token).stream()
                            .filter(p -> "OPEN".equals(p.getStatus()))
                            .collect(Collectors.toList());
                    model.addAttribute("companyProjects", companyProjects);
                } catch (Exception e) {
                    model.addAttribute("companyProjects", Collections.emptyList());
                }
            }

            return "public/public-profile";
        } catch (Exception e) {
            return "redirect:/?error=Profissional+nao+encontrado";
        }
    }

    // Perfil público da empresa
    @GetMapping("/company/{id}")
    public String companyProfile(@PathVariable Long id, HttpSession session, Model model) {
        try {
            String token = (String) session.getAttribute("token");
            CompanyDTO company = publicService.getCompany(id);
            List<ProjectDTO> openProjects = publicService.getCompanyOpenProjects(id, token);
            List<ProjectDTO> closedProjects = publicService.getCompanyClosedProjects(id, token);

            model.addAttribute("company", company);
            model.addAttribute("openProjects", openProjects);
            model.addAttribute("closedProjects", closedProjects);

            try {
                model.addAttribute("reputation", reputationService.getCompany(null, id));
            } catch (Exception e) {
                model.addAttribute("reputation", new ReputationDTO());
            }

            CompanyStatsDTO stats = new CompanyStatsDTO();
            stats.setTotalProjects(openProjects.size());
            model.addAttribute("stats", stats);

            return "public/public-company";
        } catch (Exception e) {
            return "redirect:/?error=Empresa+nao+encontrada";
        }
    }

    @GetMapping("/opportunity/{id}")
    public String opportunityDetail(@PathVariable Long id, HttpSession session, Model model) {
        try {
            String token = (String) session.getAttribute("token");
            ProjectDTO project = publicOpportunityService.getOpportunity(id, token);
            CompanyDTO company = project.getCompany();

            model.addAttribute("project", project);
            model.addAttribute("company", company);

            // Score de match só faz sentido pro profissional logado vendo a própria
            // compatibilidade com essa vaga — mesmo score exibido em /pro/opportunities.
            if ("PROFESSIONAL".equals(session.getAttribute("userRole"))) {
                try {
                    MatchDTO scoreMatch = professionalService.getOpportunities(token).stream()
                            .filter(o -> o.getProject() != null && id.equals(o.getProject().getId()))
                            .findFirst()
                            .orElse(null);
                    model.addAttribute("opportunityScore", scoreMatch);
                } catch (Exception e) {
                    // Sem score disponível — card fica oculto
                }
            }

            return "public/opportunity-detail";

        } catch (Exception e) {
            return "redirect:/?error=Oportunidade+nao+encontrada";
        }
    }
}
