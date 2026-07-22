package com.main.nexus_frontend.service;

import com.main.nexus_frontend.model.CandidateComparisonRequestDTO;
import com.main.nexus_frontend.model.CandidateComparisonResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ComparisonService {

    private final RestClient restClient;

    public ComparisonService(@Value("${nexus.api.base-url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public CandidateComparisonResponseDTO compare(String token, Long projectId, List<Long> matchIds) {
        CandidateComparisonRequestDTO request = new CandidateComparisonRequestDTO(projectId, matchIds);

        return restClient.post()
                .uri("/comparison/candidates")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Erro ao buscar comparação de candidatos");
                })
                .body(CandidateComparisonResponseDTO.class);
    }
}
