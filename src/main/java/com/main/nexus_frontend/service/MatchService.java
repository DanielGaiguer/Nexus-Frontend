package com.main.nexus_frontend.service;

import com.main.nexus_frontend.model.MatchDTO;
import com.main.nexus_frontend.model.MatchHistoryDTO;
import com.main.nexus_frontend.model.ProjectDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MatchService {

    private final RestClient restClient;
    private final ProjectService projectService;

    public MatchService(@Value("${nexus.api.base-url}") String baseUrl, ProjectService projectService) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
        this.projectService = projectService;
    }

    public MatchDTO getMatch(String token, Long matchId) {
        return restClient.get()
                .uri("/matches/{matchId}", matchId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Failed to load match");
                })
                .body(MatchDTO.class);
    }

    // TODO: Ineficiente — deveria virar uma rota dedicada no backend (GET /api/company/matches)
    public List<MatchDTO> getCompanyMatches(String token) {
        List<MatchDTO> allMatches = new ArrayList<>();
        List<ProjectDTO> projects = projectService.getProjects(token);
        for (var project : projects) {
            try {
                List<MatchDTO> ranking = projectService.getRanking(token, project.getId());
                allMatches.addAll(ranking);
            } catch (ResponseStatusException e) {
                // skip projects that fail to load ranking
            }
        }
        return allMatches.stream()
                .filter(m -> "MATCHED".equals(m.getStatus())
                        || "PROFESSIONAL_INTERESTED".equals(m.getStatus())
                        || "COMPANY_INTERESTED".equals(m.getStatus()))
                .collect(Collectors.toList());
    }

    public void professionalAccept(String token, Long matchId) {
        restClient.post()
                .uri("/matches/{matchId}/professional-accept", matchId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Failed to accept match");
                })
                .toBodilessEntity();
    }

    public void professionalReject(String token, Long matchId, List<String> reasons) {
        restClient.post()
                .uri("/matches/{matchId}/professional-reject", matchId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(reasons)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Failed to reject match");
                })
                .toBodilessEntity();
    }

    public void companyInterest(String token, Long matchId) {
        restClient.post()
                .uri("/matches/{matchId}/company-interest", matchId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Failed to send interest");
                })
                .toBodilessEntity();
    }

    public void companyAccept(String token, Long matchId) {
        restClient.post()
                .uri("/matches/{matchId}/company-accept", matchId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Failed to accept match");
                })
                .toBodilessEntity();
    }

    public void companyReject(String token, Long matchId, List<String> reasons) {
        restClient.post()
                .uri("/matches/{matchId}/company-reject", matchId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(reasons)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Failed to reject match");
                })
                .toBodilessEntity();
    }

    public void companyCancel(String token, Long matchId) {
        restClient.post()
                .uri("/matches/{matchId}/company-cancel", matchId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Failed to cancel match");
                })
                .toBodilessEntity();
    }

    public List<MatchHistoryDTO> getHistory(String token, Long matchId) {
        MatchHistoryDTO[] response = restClient.get()
                .uri("/matches/{matchId}/history", matchId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Failed to load match history");
                })
                .body(MatchHistoryDTO[].class);
        return response != null ? Arrays.asList(response) : Collections.emptyList();
    }
}
