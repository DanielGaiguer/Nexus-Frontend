package com.main.nexus_frontend.service;

import com.main.nexus_frontend.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

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
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Failed to load admin dashboard");
                })
                .body(AdminDashboardDTO.class);
    }

    public List<CompanyProfileDTO> getPendingCompanies(String token) {
        CompanyProfileDTO[] response = restClient.get()
                .uri("/admin/companies/pending")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Failed to load pending companies");
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
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Failed to approve company");
                })
                .toBodilessEntity();
    }

    public void rejectCompany(String token, Long id) {
        restClient.post()
                .uri("/admin/companies/{id}/reject", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Failed to reject company");
                })
                .toBodilessEntity();
    }

    public List<SkillDTO> getSkills(String token) {
        SkillDTO[] response = restClient.get()
                .uri("/admin/skills")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Failed to load skills");
                })
                .body(SkillDTO[].class);
        return response != null ? Arrays.asList(response) : Collections.emptyList();
    }

    public void createSkill(String token, String name, String category) {
        restClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/admin/skills")
                        .queryParam("name", name)
                        .queryParam("category", category)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Failed to create skill");
                })
                .toBodilessEntity();
    }

    public void deleteSkill(String token, Long id) {
        restClient.delete()
                .uri("/admin/skills/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Failed to delete skill");
                })
                .toBodilessEntity();
    }

    public List<UserSummaryDTO> getUsers(String token) {
        UserSummaryDTO[] response = restClient.get()
                .uri("/admin/users")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Failed to load users");
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
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Failed to toggle user");
                })
                .toBodilessEntity();
    }

    public List<ProjectDTO> getAllProjects(String token) {
        ProjectDTO[] response = restClient.get()
                .uri("/admin/projects")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Erro ao buscar projetos");
                })
                .body(ProjectDTO[].class);
        return response != null ? Arrays.asList(response) : Collections.emptyList();
    }
}
