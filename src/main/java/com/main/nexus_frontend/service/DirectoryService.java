package com.main.nexus_frontend.service;

import com.main.nexus_frontend.model.CompanyDirectoryPageDTO;
import com.main.nexus_frontend.model.ProfessionalDirectoryPageDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

@Service
public class DirectoryService {

    private final RestClient restClient;

    public DirectoryService(@Value("${nexus.api.base-url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public CompanyDirectoryPageDTO getCompanies(String search, int page, int size) {
        CompanyDirectoryPageDTO response = restClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/public/companies");
                    uriBuilder.queryParam("page", page);
                    uriBuilder.queryParam("size", size);
                    if (search != null && !search.isBlank()) uriBuilder.queryParam("search", search);
                    return uriBuilder.build();
                })
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Failed to load companies");
                })
                .body(CompanyDirectoryPageDTO.class);
        return response != null ? response : new CompanyDirectoryPageDTO();
    }

    public ProfessionalDirectoryPageDTO getProfessionals(String search, int page, int size) {
        ProfessionalDirectoryPageDTO response = restClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/public/professionals");
                    uriBuilder.queryParam("page", page);
                    uriBuilder.queryParam("size", size);
                    if (search != null && !search.isBlank()) uriBuilder.queryParam("search", search);
                    return uriBuilder.build();
                })
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Failed to load professionals");
                })
                .body(ProfessionalDirectoryPageDTO.class);
        return response != null ? response : new ProfessionalDirectoryPageDTO();
    }
}
