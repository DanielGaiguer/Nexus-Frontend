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
import java.util.stream.Collectors;

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
        MapProfessionalDTO[] response = restClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/map/professionals");
                    if (city != null && !city.isBlank()) uriBuilder.queryParam("city", city);
                    if (uf != null && !uf.isBlank()) uriBuilder.queryParam("uf", uf);
                    if (opportunityType != null && !opportunityType.isBlank()) uriBuilder.queryParam("type", opportunityType);
                    return uriBuilder.build();
                })
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
        MapCompanyDTO[] response = restClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/map/companies");
                    if (city != null && !city.isBlank()) uriBuilder.queryParam("city", city);
                    if (uf != null && !uf.isBlank()) uriBuilder.queryParam("uf", uf);
                    return uriBuilder.build();
                })
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
        try {
            MapOpportunityDTO[] response = restClient.get()
                    .uri(uriBuilder -> {
                        uriBuilder.path("/map/opportunities");
                        if (city != null && !city.isBlank()) uriBuilder.queryParam("city", city);
                        if (uf != null && !uf.isBlank()) uriBuilder.queryParam("uf", uf);
                        if (type != null && !type.isBlank()) uriBuilder.queryParam("type", type);
                        return uriBuilder.build();
                    })
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .body(MapOpportunityDTO[].class);
            List<MapOpportunityDTO> list = response != null ? Arrays.asList(response) : Collections.emptyList();
            // A API não filtra oportunidades por cidade/UF — filtra aqui como reforço
            return list.stream()
                    .filter(o -> city == null || city.isBlank()
                            || (o.getCity() != null && o.getCity().equalsIgnoreCase(city.trim())))
                    .filter(o -> uf == null || uf.isBlank()
                            || (o.getUf() != null && o.getUf().equalsIgnoreCase(uf.trim())))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    // Centro do mapa: centróide dos resultados encontrados, ou o fallback se não houver nenhum
    public static double[] computeCenter(List<MapProfessionalDTO> pros, List<MapCompanyDTO> cos,
                                          List<MapOpportunityDTO> opps, double fallbackLat, double fallbackLng) {
        double sumLat = 0, sumLng = 0;
        int count = 0;
        for (MapProfessionalDTO p : pros) {
            if (p.getLatitude() != null && p.getLongitude() != null) {
                sumLat += p.getLatitude();
                sumLng += p.getLongitude();
                count++;
            }
        }
        for (MapCompanyDTO c : cos) {
            if (c.getLatitude() != null && c.getLongitude() != null) {
                sumLat += c.getLatitude();
                sumLng += c.getLongitude();
                count++;
            }
        }
        for (MapOpportunityDTO o : opps) {
            if (o.getLatitude() != null && o.getLongitude() != null) {
                sumLat += o.getLatitude();
                sumLng += o.getLongitude();
                count++;
            }
        }
        if (count == 0) return new double[]{fallbackLat, fallbackLng};
        return new double[]{sumLat / count, sumLng / count};
    }
}
