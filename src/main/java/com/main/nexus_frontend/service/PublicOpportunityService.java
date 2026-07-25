package com.main.nexus_frontend.service;

import com.main.nexus_frontend.model.ProjectDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PublicOpportunityService {

    private final RestClient restClient;

    public PublicOpportunityService(@Value("${nexus.api.base-url}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    public ProjectDTO getOpportunity(Long id) {
        return restClient.get()
                .uri("/public/opportunity/" + id)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Oportunidade não encontrada.");
                })
                .body(ProjectDTO.class);
    }
}
