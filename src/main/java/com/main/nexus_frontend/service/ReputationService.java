package com.main.nexus_frontend.service;

import com.main.nexus_frontend.model.ReputationDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ReputationService {

    private final RestClient restClient;

    public ReputationService(@Value("${nexus.api.base-url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public ReputationDTO getProfessional(String token, Long professionalId) {
        return restClient.get()
                .uri("/reputation/professional/{id}", professionalId)
                .headers(headers -> {
                    if (token != null) headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
                })
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Failed to load professional reputation");
                })
                .body(ReputationDTO.class);
    }

    public ReputationDTO getCompany(String token, Long companyId) {
        return restClient.get()
                .uri("/reputation/company/{id}", companyId)
                .headers(headers -> {
                    if (token != null) headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
                })
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Failed to load company reputation");
                })
                .body(ReputationDTO.class);
    }
}
