package com.main.nexus_frontend.service;

import com.main.nexus_frontend.exception.NexusApiException;
import com.main.nexus_frontend.model.CompanyDashboardAnalyticsDTO;
import com.main.nexus_frontend.model.CompanyDashboardDTO;
import com.main.nexus_frontend.model.CompanyDTO;
import com.main.nexus_frontend.model.CompanyProfileDTO;
import com.main.nexus_frontend.model.ContactInfoDTO;
import com.main.nexus_frontend.model.ProjectDTO;
import com.main.nexus_frontend.model.SkillDTO;
import com.main.nexus_frontend.model.UpdateCompanyDTO;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CompanyService {

    private final RestClient restClient;

    public CompanyService(@Value("${nexus.api.base-url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public List<SkillDTO> getSkills(String token) {
        SkillDTO[] response = restClient.get()
                .uri("/projects/skills")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .body(SkillDTO[].class);
        return response != null ? Arrays.asList(response) : Collections.emptyList();
    }

    public CompanyDashboardDTO getDashboard(String token) {
        return restClient.get()
                .uri("/company/dashboard")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .body(CompanyDashboardDTO.class);
    }

    public CompanyDashboardAnalyticsDTO getAnalytics(String token) {
        return restClient.get()
                .uri("/analytics/company/dashboard") 
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .body(CompanyDashboardAnalyticsDTO.class);
    }

    public CompanyProfileDTO getProfile(String token) {
        return restClient.get()
                .uri("/company/profile")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .body(CompanyProfileDTO.class);
    }

    public void updateProfile(String token, UpdateCompanyDTO dto) {
        restClient.put()
                .uri("/company/profile")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(dto)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .toBodilessEntity();
    }

    public String uploadPhoto(String token, MultipartFile file) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        try {
            body.add("file", new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            });
        } catch (java.io.IOException e) {
            throw new NexusApiException("Não foi possível ler o arquivo enviado. Tente novamente.", 500);
        }
        return restClient.post()
                .uri("/company/profile/photo")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .body(String.class);
    }

    // Perfil público — usado quando um profissional visualiza uma empresa
    public CompanyDTO getPublicProfile(Long id) {
        return restClient.get()
                .uri("/public/company/{id}", id)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .body(CompanyDTO.class);
    }

    // Histórico de oportunidades encerradas da empresa (vagas e projetos) — usado no
    // perfil visto por profissionais para mostrar o histórico completo da empresa
    public List<ProjectDTO> getCompanyClosedProjects(Long id) {
        try {
            ProjectDTO[] res = restClient.get()
                    .uri("/public/company/{id}/projects/closed", id)
                    .retrieve()
                    .body(ProjectDTO[].class);
            return res != null ? Arrays.asList(res) : Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    // Contato liberado só depois de um match confirmado com o profissional logado
    public ContactInfoDTO getContact(String token, Long companyId) {
        return restClient.get()
                .uri("/company/{companyId}/contact", companyId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .body(ContactInfoDTO.class);
    }

    // Vitrine com todas as oportunidades da plataforma, para a company navegar o mercado
    public List<ProjectDTO> getAllOpportunities(String token) {
        ProjectDTO[] response = restClient.get()
                .uri("/company/opportunities")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .body(ProjectDTO[].class);
        return response != null ? Arrays.asList(response) : Collections.emptyList();
    }

    public void removePhoto(String token) {
        restClient.method(org.springframework.http.HttpMethod.DELETE)
                .uri("/company/profile/photo")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .body(String.class);
    }
}
