package com.main.nexus_frontend.service;

import com.main.nexus_frontend.exception.NexusApiException;
import com.main.nexus_frontend.model.AiExtractionRequestDTO;
import com.main.nexus_frontend.model.AiExtractionResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

// Repassa POST /api/projects/ai-extract do backend para o formulário de criação de
// oportunidade. Só produz uma sugestão pré-preenchida — não persiste nada, o backend nem
// toca em ProjectRepository ao atender essa chamada.
@Service
public class ProjectAiExtractionBffService {

    private final RestClient restClient;

    public ProjectAiExtractionBffService(@Value("${nexus.api.base-url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public AiExtractionResponseDTO extract(String jwt, String rawText) {
        return restClient.post()
                .uri("/projects/ai-extract")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                .body(new AiExtractionRequestDTO(rawText))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .body(AiExtractionResponseDTO.class);
    }
}
