package com.main.nexus_frontend.service;

import com.main.nexus_frontend.model.CompanyDashboardAnalyticsDTO;
import com.main.nexus_frontend.model.CompanyDashboardDTO;
import com.main.nexus_frontend.model.CompanyProfileDTO;
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
import org.springframework.web.server.ResponseStatusException;

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
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Failed to load skills");
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
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Failed to load dashboard");
                })
                .body(CompanyDashboardDTO.class);
    }

    public CompanyDashboardAnalyticsDTO getAnalytics(String token) {
        return restClient.get()
                .uri("/company/analytics/dashboard")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Erro ao buscar analytics");
                })
                .body(CompanyDashboardAnalyticsDTO.class);
    }

    public CompanyProfileDTO getProfile(String token) {
        return restClient.get()
                .uri("/company/profile")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Failed to load company profile");
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
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Failed to update company profile");
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
            throw new ResponseStatusException(HttpStatusCode.valueOf(500), "Failed to read file");
        }
        return restClient.post()
                .uri("/company/profile/photo")
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
                .uri("/company/profile/photo")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .body(String.class);
    }
}
