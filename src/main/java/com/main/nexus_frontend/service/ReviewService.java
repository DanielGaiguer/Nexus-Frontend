package com.main.nexus_frontend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.main.nexus_frontend.model.PendingReviewDTO;
import com.main.nexus_frontend.model.ReviewRequestDTO;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ReviewService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ReviewService(@Value("${nexus.api.base-url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public void submit(String token, Long matchId, ReviewRequestDTO dto) {
        restClient.post()
                .uri("/reviews/{matchId}", matchId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(dto)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    String message = "Failed to submit review";
                    try {
                        Map<?, ?> body = objectMapper.readValue(res.getBody(), Map.class);
                        if (body.get("message") != null) {
                            message = body.get("message").toString();
                        }
                    } catch (Exception ignored) {
                        // corpo não veio no formato esperado — mantém a mensagem genérica
                    }
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()), message);
                })
                .toBodilessEntity();
    }

    // Retorna null se não tiver nenhum match expirado ainda sem avaliação, ou se a
    // chamada falhar por qualquer motivo — é um recurso auxiliar do dashboard, não
    // pode derrubar a página inteira.
    public PendingReviewDTO getPendingForProfessional(String token) {
        try {
            return restClient.get()
                    .uri("/reviews/pending/professional")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                        throw new ResponseStatusException(HttpStatusCode.valueOf(res.getStatusCode().value()));
                    })
                    .body(PendingReviewDTO.class);
        } catch (Exception e) {
            return null;
        }
    }

    public PendingReviewDTO getPendingForCompany(String token) {
        try {
            return restClient.get()
                    .uri("/reviews/pending/company")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                        throw new ResponseStatusException(HttpStatusCode.valueOf(res.getStatusCode().value()));
                    })
                    .body(PendingReviewDTO.class);
        } catch (Exception e) {
            return null;
        }
    }

    // IDs dos matches já avaliados — usado pra trocar "Avaliar" por "Avaliado" na listagem
    public Set<Long> getReviewedMatchIdsForProfessional(String token) {
        try {
            Long[] response = restClient.get()
                    .uri("/reviews/reviewed-match-ids/professional")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                        throw new ResponseStatusException(HttpStatusCode.valueOf(res.getStatusCode().value()));
                    })
                    .body(Long[].class);
            return response != null ? new HashSet<>(java.util.Arrays.asList(response)) : Set.of();
        } catch (Exception e) {
            return Set.of();
        }
    }

    public Set<Long> getReviewedMatchIdsForCompany(String token) {
        try {
            Long[] response = restClient.get()
                    .uri("/reviews/reviewed-match-ids/company")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                        throw new ResponseStatusException(HttpStatusCode.valueOf(res.getStatusCode().value()));
                    })
                    .body(Long[].class);
            return response != null ? new HashSet<>(java.util.Arrays.asList(response)) : Set.of();
        } catch (Exception e) {
            return Set.of();
        }
    }
}
