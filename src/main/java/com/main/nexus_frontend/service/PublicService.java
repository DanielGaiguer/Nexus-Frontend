package com.main.nexus_frontend.service;

import com.main.nexus_frontend.model.CompanyDTO;
import com.main.nexus_frontend.model.ProjectDTO;
import com.main.nexus_frontend.model.PublicProfessionalDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class PublicService {

    private final RestClient restClient;

    public PublicService(@Value("${nexus.api.base-url}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    // Perfil público do profissional
    public PublicProfessionalDTO getProfessional(Long id) {
        return restClient.get()
                .uri("/public/professional/{id}", id)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Profissional não encontrado.");
                })
                .body(PublicProfessionalDTO.class);
    }

    // Perfil público da empresa
    public CompanyDTO getCompany(Long id) {
        return restClient.get()
                .uri("/public/company/{id}", id)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Empresa não encontrada.");
                })
                .body(CompanyDTO.class);
    }

    // Projetos abertos da empresa (rota pública)
    public List<ProjectDTO> getCompanyOpenProjects(Long companyId) {
        try {
            ProjectDTO[] res = restClient.get()
                    .uri("/public/company/{id}/projects", companyId)
                    .retrieve()
                    .body(ProjectDTO[].class);
            return res != null ? Arrays.asList(res) : Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    // Histórico de oportunidades encerradas da empresa (vagas e projetos), rota pública
    public List<ProjectDTO> getCompanyClosedProjects(Long companyId) {
        try {
            ProjectDTO[] res = restClient.get()
                    .uri("/public/company/{id}/projects/closed", companyId)
                    .retrieve()
                    .body(ProjectDTO[].class);
            return res != null ? Arrays.asList(res) : Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
