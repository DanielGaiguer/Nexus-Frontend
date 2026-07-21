package com.main.nexus_frontend.service;

import com.main.nexus_frontend.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class ProfessionalService {

    private final RestClient restClient;

    public ProfessionalService(@Value("${nexus.api.base-url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public ProfessionalProfileDTO getProfile(String token) {
        return restClient.get()
                .uri("/professional/profile")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Failed to load profile");
                })
                .body(ProfessionalProfileDTO.class);
    }

    public void updateProfile(String token, UpdateProfessionalDTO dto) {
        restClient.put()
                .uri("/professional/profile")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(dto)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Failed to update profile");
                })
                .toBodilessEntity();
    }

    public void updateSkills(String token, List<Long> skillIds) {
        restClient.put()
                .uri("/professional/skills")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(skillIds)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Failed to update skills");
                })
                .toBodilessEntity();
    }

    public List<MatchDTO> getMatches(String token) {
        MatchDTO[] response = restClient.get()
                .uri("/professional/matches")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Failed to load matches");
                })
                .body(MatchDTO[].class);
        return response != null ? Arrays.asList(response) : Collections.emptyList();
    }

    public List<MatchDTO> getPendingInvites(String token) {
        MatchDTO[] response = restClient.get()
                .uri("/professional/matches/invites")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Failed to load invites");
                })
                .body(MatchDTO[].class);
        return response != null ? Arrays.asList(response) : Collections.emptyList();
    }

    public List<PreviousProjectDTO> getProjects(String token) {
        PreviousProjectDTO[] response = restClient.get()
                .uri("/professional/projects")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Failed to load projects");
                })
                .body(PreviousProjectDTO[].class);
        return response != null ? Arrays.asList(response) : Collections.emptyList();
    }

    public void addProject(String token, PreviousProjectDTO dto) {
        restClient.post()
                .uri("/professional/projects")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(dto)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Failed to add project");
                })
                .toBodilessEntity();
    }

    public void deleteProject(String token, Long projectId) {
        restClient.delete()
                .uri("/professional/projects/{projectId}", projectId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Failed to delete project");
                })
                .toBodilessEntity();
    }

    public List<MatchDTO> getOpportunities(String token) {
        MatchDTO[] response = restClient.get()
                .uri("/professional/opportunities")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Failed to load opportunities");
                })
                .body(MatchDTO[].class);
        return response != null ? Arrays.asList(response) : Collections.emptyList();
    }

    public void sendInterest(String token, Long projectId) {
        restClient.post()
                .uri("/professional/opportunities/{projectId}/interest", projectId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Failed to send interest");
                })
                .toBodilessEntity();
    }

    public List<SkillDTO> getAllSkills(String token) {
        SkillDTO[] response = restClient.get()
                .uri("/professional/skills")
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

    public void uploadResume(String token, MultipartFile file) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        try {
            body.add("file", new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            });
        } catch (java.io.IOException e) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(500), "Failed to read file");
        }
        restClient.post()
                .uri("/professional/resume")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Failed to upload resume");
                })
                .toBodilessEntity();
    }

    public byte[] downloadResume(String token, Long professionalId) {
        return restClient.get()
                .uri("/professional/{professionalId}/resume", professionalId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Failed to download resume");
                })
                .body(byte[].class);
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
            throw new ResponseStatusException(HttpStatusCode.valueOf(500), "Failed to read file");
        }
        return restClient.post()
                .uri("/professional/profile/photo")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Failed to upload photo");
                })
                .body(String.class);
    }

    public void removePhoto(String token) {
        restClient.method(org.springframework.http.HttpMethod.DELETE)
                .uri("/professional/profile/photo")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .body(String.class);
    }

    public ProfessionalStatsDTO getStats(String token) {
        return restClient.get()
                .uri("/professional/stats")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Failed to load stats");
                })
                .body(ProfessionalStatsDTO.class);
    }
}
