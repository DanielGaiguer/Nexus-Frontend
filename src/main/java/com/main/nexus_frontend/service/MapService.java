package com.main.nexus_frontend.service;

import com.main.nexus_frontend.model.MapCompanyDTO;
import com.main.nexus_frontend.model.MapProfessionalDTO;
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
public class MapService {

    private final RestClient restClient;

    public MapService(@Value("${nexus.api.base-url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public List<MapProfessionalDTO> getProfessionals(String token) {
        MapProfessionalDTO[] response = restClient.get()
                .uri("/map/professionals")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Failed to load map professionals");
                })
                .body(MapProfessionalDTO[].class);
        return response != null ? Arrays.asList(response) : Collections.emptyList();
    }

    public List<MapCompanyDTO> getCompanies(String token) {
        MapCompanyDTO[] response = restClient.get()
                .uri("/map/companies")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Failed to load map companies");
                })
                .body(MapCompanyDTO[].class);
        return response != null ? Arrays.asList(response) : Collections.emptyList();
    }
}
