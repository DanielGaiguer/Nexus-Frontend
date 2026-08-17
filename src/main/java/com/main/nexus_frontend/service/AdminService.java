package com.main.nexus_frontend.service;

import com.main.nexus_frontend.exception.NexusApiException;
import com.main.nexus_frontend.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class AdminService {

    private final RestClient restClient;

    public AdminService(@Value("${nexus.api.base-url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public AdminDashboardDTO getDashboard(String token) {
        return restClient.get()
                .uri("/admin/dashboard")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .body(AdminDashboardDTO.class);
    }

    public List<CompanyProfileDTO> getPendingCompanies(String token) {
        CompanyProfileDTO[] response = restClient.get()
                .uri("/admin/companies/pending")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .body(CompanyProfileDTO[].class);
        return response != null ? Arrays.asList(response) : Collections.emptyList();
    }

    public List<CompanyProfileDTO> getLatestCompanies(String token) {
        CompanyProfileDTO[] response = restClient.get()
                .uri("/admin/companies/latest")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .body(CompanyProfileDTO[].class);
        return response != null ? Arrays.asList(response) : Collections.emptyList();
    }

    public void approveCompany(String token, Long id) {
        restClient.post()
                .uri("/admin/companies/{id}/approve", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .toBodilessEntity();
    }

    public void rejectCompany(String token, Long id, String reason) {
        restClient.post()
                .uri("/admin/companies/{id}/reject", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(java.util.Map.of("reason", reason))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .toBodilessEntity();
    }

    public void closeProject(String token, Long id) {
        restClient.put()
                .uri("/admin/projects/{id}/close", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .toBodilessEntity();
    }

    public List<SkillDTO> getSkills(String token) {
        SkillDTO[] response = restClient.get()
                .uri("/admin/skills")
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

    public void createSkill(String token, String name, String category) {
        restClient.post()
                .uri("/admin/skills")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new SkillRequestDTO(name, category))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .toBodilessEntity();
    }

    public void deleteSkill(String token, Long id) {
        restClient.delete()
                .uri("/admin/skills/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .toBodilessEntity();
    }

    public List<UserSummaryDTO> getUsers(String token) {
        UserSummaryDTO[] response = restClient.get()
                .uri("/admin/users")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .body(UserSummaryDTO[].class);
        return response != null ? Arrays.asList(response) : Collections.emptyList();
    }

    public void toggleUser(String token, Long id) {
        restClient.post()
                .uri("/admin/users/{id}/toggle", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .toBodilessEntity();
    }

    public List<ProjectDTO> getAllProjects(String token) {
        ProjectDTO[] response = restClient.get()
                .uri("/admin/projects")
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

    public ProfessionalProfileDTO getProfessionalProfile(String token, Long professionalId) {
        return restClient.get()
                .uri("/admin/professionals/{id}/profile", professionalId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .body(ProfessionalProfileDTO.class);
    }

    public ProfessionalDashboardAnalyticsDTO getProfessionalAnalytics(String token, Long professionalId) {
        return restClient.get()
                .uri("/analytics/professional/{professionalId}/dashboard", professionalId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .body(ProfessionalDashboardAnalyticsDTO.class);
    }

    public byte[] exportProfessionalPdf(String token, Long professionalId) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/professional/profile/export")
                        .queryParam("professionalId", professionalId)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .body(byte[].class);
    }

    public List<MatchDTO> getProfessionalMatches(String token, Long professionalId) {
        MatchDTO[] response = restClient.get()
                .uri("/admin/professionals/{id}/matches", professionalId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .body(MatchDTO[].class);
        return response != null ? Arrays.asList(response) : Collections.emptyList();
    }

    public List<PreviousProjectDTO> getProfessionalProjects(String token, Long professionalId) {
        PreviousProjectDTO[] response = restClient.get()
                .uri("/admin/professionals/{id}/projects", professionalId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .body(PreviousProjectDTO[].class);
        return response != null ? Arrays.asList(response) : Collections.emptyList();
    }

    public ProfessionalDashboardDTO getProfessionalDashboard(String token, Long professionalId) {
        return restClient.get()
                .uri("/admin/professionals/{id}/dashboard", professionalId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .body(ProfessionalDashboardDTO.class);
    }

    public List<CompanyProfileDTO> getAllCompanies(String token) {
        CompanyProfileDTO[] response = restClient.get()
                .uri("/admin/companies")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .body(CompanyProfileDTO[].class);
        return response != null ? List.of(response) : List.of();
    }

    public CompanyProfileDTO getCompanyProfile(String token, Long companyId) {
        return restClient.get()
                .uri("/admin/companies/{id}/profile", companyId)
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

    public CompanyDashboardDTO getCompanyDashboard(String token, Long companyId) {
        return restClient.get()
                .uri("/admin/companies/{id}/dashboard", companyId)
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

    public List<ProjectDTO> getCompanyProjects(String token, Long companyId) {
        ProjectDTO[] response = restClient.get()
                .uri("/admin/companies/{id}/projects", companyId)
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

    public List<MatchDTO> getCompanyMatches(String token, Long companyId) {
        MatchDTO[] response = restClient.get()
                .uri("/admin/companies/{id}/matches", companyId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .body(MatchDTO[].class);
        return response != null ? Arrays.asList(response) : Collections.emptyList();
    }

    public CompanyDashboardAnalyticsDTO getCompanyAnalytics(String token, Long companyId) {
        return restClient.get()
                .uri("/analytics/company/{companyId}/dashboard", companyId)
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
}
