package com.main.nexus_frontend.service;

import com.main.nexus_frontend.model.CreateProjectDTO;
import com.main.nexus_frontend.model.MatchDTO;
import com.main.nexus_frontend.model.ProjectDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class ProjectService {

    private final RestClient restClient;

    public ProjectService(@Value("${nexus.api.base-url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public List<ProjectDTO> getProjects(String token) {
        ProjectDTO[] response = restClient.get()
                .uri("/projects")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Failed to load projects");
                })
                .body(ProjectDTO[].class);
        return response != null ? Arrays.asList(response) : Collections.emptyList();
    }

    public ProjectDTO getProject(String token, Long id) {
        return restClient.get()
                .uri("/projects/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Failed to load project");
                })
                .body(ProjectDTO.class);
    }

    public ProjectDTO createProject(String token, CreateProjectDTO dto) {

        System.out.println("========== ENVIANDO PROJETO ==========");
        System.out.println("Title: " + dto.getTitle());
        System.out.println("Description: " + dto.getDescription());
        System.out.println("WorkMode: " + dto.getWorkMode());
        System.out.println("Type: " + dto.getType());
        System.out.println("ExperienceLevel: " + dto.getExperienceLevel());
        System.out.println("MinimumBudget: " + dto.getMinimumBudget());
        System.out.println("MaximumBudget: " + dto.getMaximumBudget());
        System.out.println("Deadline: " + dto.getDeadline());
        System.out.println("MaxPositions: " + dto.getMaxPositions());
        System.out.println("SkillIds: " + dto.getSkillIds());
        System.out.println("OpportunityType: " + dto.getOpportunityType());
        System.out.println("ContractType: " + dto.getContractType());
        System.out.println("Benefits: " + dto.getBenefits());
        System.out.println("StartDate: " + dto.getStartDate());
        System.out.println("WorkloadHoursPerWeek: " + dto.getWorkloadHoursPerWeek());
        System.out.println("MonthlySalaryMin: " + dto.getMonthlySalaryMin());
        System.out.println("MonthlySalaryMax: " + dto.getMonthlySalaryMax());
        System.out.println("======================================");

        return restClient.post()
                .uri("/projects")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(dto)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {

                    System.out.println("========== ERRO DA API ==========");
                    System.out.println("Status: " + res.getStatusCode());

                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Failed to create project"
                    );
                })
                .body(ProjectDTO.class);
    }

    public ProjectDTO updateProject(String token, Long id, CreateProjectDTO dto) {
        return restClient.put()
                .uri("/projects/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(dto)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Failed to update project");
                })
                .body(ProjectDTO.class);
    }

    public void closeProject(String token, Long id) {
        restClient.put()
                .uri("/projects/{id}/close", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Failed to close project");
                })
                .toBodilessEntity();
    }

    public void deleteProject(String token, Long id) {
        restClient.delete()
                .uri("/projects/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Failed to delete project");
                })
                .toBodilessEntity();
    }

    public List<MatchDTO> getRanking(String token, Long id) {
        return getRanking(token, id, null);
    }

    public List<MatchDTO> getRanking(String token, Long id, Map<String, String> filters) {
        var uriBuilder = new StringBuilder("/projects/{id}/ranking");
        StringBuilder query = new StringBuilder();
        if (filters != null) {
            for (var entry : filters.entrySet()) {
                if (entry.getValue() != null && !entry.getValue().isBlank()) {
                    query.append(query.isEmpty() ? "?" : "&");
                    query.append(entry.getKey()).append("=").append(entry.getValue());
                }
            }
        }
        uriBuilder.append(query);

        MatchDTO[] response = restClient.get()
                .uri(uriBuilder.toString(), id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Failed to load ranking");
                })
                .body(MatchDTO[].class);
        return response != null ? Arrays.asList(response) : Collections.emptyList();
    }
}
