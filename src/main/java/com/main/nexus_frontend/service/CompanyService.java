package com.main.nexus_frontend.service;

import com.main.nexus_frontend.model.CompanyDashboardDTO;
import com.main.nexus_frontend.model.CompanyProfileDTO;
import com.main.nexus_frontend.model.UpdateCompanyDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CompanyService {

    private final RestClient restClient;

    public CompanyService(@Value("${nexus.api.base-url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
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
}
