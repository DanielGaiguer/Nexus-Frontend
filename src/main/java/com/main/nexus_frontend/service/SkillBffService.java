package com.main.nexus_frontend.service;

import com.main.nexus_frontend.model.SkillDTO;
import com.main.nexus_frontend.model.SuggestSkillRequestDTO;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

// Catálogo de skills, usado pelo componente reutilizável de seleção/sugestão
// (nexus-skill-select.js). Endpoints de leitura são públicos no backend; suggest exige
// um professional ou company autenticado.
@Service
public class SkillBffService {

    private final RestClient restClient;

    public SkillBffService(@Value("${nexus.api.base-url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public List<SkillDTO> getAllSkills(String jwt) {
        SkillDTO[] response = auth(restClient.get().uri("/skills"), jwt)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(HttpStatusCode.valueOf(res.getStatusCode().value()));
                })
                .body(SkillDTO[].class);
        return response != null ? Arrays.asList(response) : Collections.emptyList();
    }

    public List<String> getCategories(String jwt) {
        String[] response = auth(restClient.get().uri("/skills/categories"), jwt)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(HttpStatusCode.valueOf(res.getStatusCode().value()));
                })
                .body(String[].class);
        return response != null ? Arrays.asList(response) : Collections.emptyList();
    }

    // Repassa o status code do backend (200 já existia vs 201 criada) sem achatar
    // essa distinção — o componente de frontend usa isso pra decidir o texto do toast.
    public ResponseEntity<SkillDTO> suggestSkill(String name, String category, String jwt) {
        return restClient.post()
                .uri("/skills/suggest")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                .body(new SuggestSkillRequestDTO(name, category))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(HttpStatusCode.valueOf(res.getStatusCode().value()));
                })
                .toEntity(SkillDTO.class);
    }

    private RestClient.RequestHeadersSpec<?> auth(RestClient.RequestHeadersSpec<?> request, String jwt) {
        return jwt != null ? request.header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt) : request;
    }
}
