package com.main.nexus_frontend.service;

import com.main.nexus_frontend.model.MapCompanyDTO;
import com.main.nexus_frontend.model.MapOpportunityDTO;
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
        return getProfessionals(token, null, null, null);
    }

    public List<MapProfessionalDTO> getProfessionals(String token, String city, String uf) {
        return getProfessionals(token, city, uf, null);
    }

    public List<MapProfessionalDTO> getProfessionals(String token, String city, String uf, String opportunityType) {
        var uri = new StringBuilder("/map/professionals");
        boolean hasParams = false;
        if (city != null && !city.isBlank())           { uri.append(hasParams ? "&" : "?").append("city=").append(city);           hasParams = true; }
        if (uf   != null && !uf.isBlank())             { uri.append(hasParams ? "&" : "?").append("uf=").append(uf);               hasParams = true; }
        if (opportunityType != null && !opportunityType.isBlank()) { uri.append(hasParams ? "&" : "?").append("type=").append(opportunityType); hasParams = true; }

        MapProfessionalDTO[] response = restClient.get()
                .uri(uri.toString())
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
        return getCompanies(token, null, null);
    }

    public List<MapCompanyDTO> getCompanies(String token, String city, String uf) {
        var uri = new StringBuilder("/map/companies");
        boolean hasParams = false;
        if (city != null && !city.isBlank()) { uri.append(hasParams ? "&" : "?").append("city=").append(city); hasParams = true; }
        if (uf   != null && !uf.isBlank())   { uri.append(hasParams ? "&" : "?").append("uf=").append(uf);     hasParams = true; }

        MapCompanyDTO[] response = restClient.get()
                .uri(uri.toString())
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

    public List<MapOpportunityDTO> getOpportunities(String token, String city, String uf, String type) {
        var uri = new StringBuilder("/map/opportunities");
        boolean hasParams = false;
        if (city != null && !city.isBlank()) { uri.append(hasParams ? "&" : "?").append("city=").append(city); hasParams = true; }
        if (uf   != null && !uf.isBlank())   { uri.append(hasParams ? "&" : "?").append("uf=").append(uf);     hasParams = true; }
        if (type != null && !type.isBlank()) { uri.append(hasParams ? "&" : "?").append("type=").append(type); hasParams = true; }

        try {
            MapOpportunityDTO[] response = restClient.get()
                    .uri(uri.toString())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .body(MapOpportunityDTO[].class);
            return response != null ? Arrays.asList(response) : Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
