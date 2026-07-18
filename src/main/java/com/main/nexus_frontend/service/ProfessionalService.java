package com.main.nexus_frontend.service;

import com.main.nexus_frontend.model.MatchDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class ProfessionalService {

    private final RestClient restClient;

    public ProfessionalService(@Value("${nexus.api.base-url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public List<MatchDTO> getMatches(String token) {
        MatchDTO[] response = restClient.get()
                .uri("/professional/matches")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Failed to load matches");   
                })
                .body(MatchDTO[].class);
        return response != null ? Arrays.asList(response) : Collections.emptyList();
    }

    public List<MatchDTO> getPendingInvites(String token) {
        MatchDTO[] response = restClient.get()
                .uri("/professional/matches/invites")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Failed to load invites");
                })
                .body(MatchDTO[].class);
        return response != null ? Arrays.asList(response) : Collections.emptyList();
    }
}
