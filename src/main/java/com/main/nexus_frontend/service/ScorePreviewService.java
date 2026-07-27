package com.main.nexus_frontend.service;

import com.main.nexus_frontend.model.ScorePreviewResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ScorePreviewService {

    private final RestClient restClient;

    public ScorePreviewService(@Value("${nexus.api.base-url}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    // Empresa consultando o score de um profissional em um dos seus projetos
    public ScorePreviewResponseDTO previewForCompany(String token, Long professionalId, Long projectId) {
        return restClient.get()
                .uri("/score/preview?professionalId={professionalId}&projectId={projectId}",
                        professionalId, projectId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Não foi possível calcular o score.");
                })
                .body(ScorePreviewResponseDTO.class);
    }

    // Profissional consultando seu próprio score em uma vaga/projeto
    public ScorePreviewResponseDTO previewForProfessional(String token, Long projectId) {
        return restClient.get()
                .uri("/score/preview/self?projectId={projectId}", projectId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Não foi possível calcular o score.");
                })
                .body(ScorePreviewResponseDTO.class);
    }
}
