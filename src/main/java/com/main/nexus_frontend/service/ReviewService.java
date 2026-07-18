package com.main.nexus_frontend.service;

import com.main.nexus_frontend.model.ReviewRequestDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ReviewService {

    private final RestClient restClient;

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
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Failed to submit review");
                })
                .toBodilessEntity();
    }
}
