package com.main.nexus_frontend.service;

import com.main.nexus_frontend.model.ProjectDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
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

    // O token é opcional. Quando presente, o backend identifica quem está pedindo (dono,
    // outra empresa, profissional ou admin) e aplica as regras de visibilidade/salário;
    // uma oportunidade oculta para outras empresas retorna 404 mesmo por link direto.
    public ProjectDTO getOpportunity(Long id, String token) {
        return restClient.get()
                .uri("/public/opportunity/" + id)
                .headers(headers -> {
                    if (token != null && !token.isBlank()) {
                        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
                    }
                })
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Oportunidade não encontrada.");
                })
                .body(ProjectDTO.class);
    }
}
