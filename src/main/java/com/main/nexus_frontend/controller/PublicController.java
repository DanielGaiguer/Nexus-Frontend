package com.main.nexus_frontend.controller;

import com.main.nexus_frontend.model.CompanyDTO;
import com.main.nexus_frontend.model.ProjectDTO;
import com.main.nexus_frontend.model.PublicProfessionalDTO;
import com.main.nexus_frontend.service.PublicOpportunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/public")
public class PublicController {

    private final RestClient restClient;

    @Autowired
    private PublicOpportunityService publicOpportunityService;

    public PublicController(@Value("${nexus.api.base-url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    @GetMapping("/professional/{id}")
    public String professionalProfile(@PathVariable Long id, Model model) {
        PublicProfessionalDTO dto = restClient.get()
                .uri("/public/professional/{id}", id)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Professional not found");
                })
                .body(PublicProfessionalDTO.class);
        model.addAttribute("professional", dto);
        return "public/public-profile";
    }

    @GetMapping("/opportunity/{id}")
    public String opportunityDetail(@PathVariable Long id, Model model) {
        try {
            ProjectDTO project = publicOpportunityService.getOpportunity(id);
            CompanyDTO company = project.getCompany();

            model.addAttribute("project", project);
            model.addAttribute("company", company);

            return "public/opportunity-detail";

        } catch (Exception e) {
            return "redirect:/?error=Oportunidade+nao+encontrada";
        }
    }
}
