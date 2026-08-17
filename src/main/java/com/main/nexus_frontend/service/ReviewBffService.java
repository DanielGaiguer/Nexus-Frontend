package com.main.nexus_frontend.service;

import com.main.nexus_frontend.model.ReviewDisplayDTO;
import com.main.nexus_frontend.model.ReviewPageDTO;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

// Exibição de avaliações (card de preview + página dedicada) — endpoints públicos do
// backend, sem necessidade de token. Nome "Bff" separado do ReviewService existente
// (que trata envio de avaliação e IDs já avaliados) pra manter a responsabilidade clara.
@Service
public class ReviewBffService {

    private final RestClient restClient;

    public ReviewBffService(@Value("${nexus.api.base-url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    // Contagem leve — usado pelo badge do item "Avaliações" da sidebar, que roda em toda
    // página de quem está logado, então não pode trazer a lista inteira de avaliações.
    public long getCountForProfessional(Long professionalId) {
        Long count = restClient.get()
                .uri("/reviews/professional/{id}/count", professionalId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(HttpStatusCode.valueOf(res.getStatusCode().value()));
                })
                .body(Long.class);
        return count != null ? count : 0;
    }

    public long getCountForCompany(Long companyId) {
        Long count = restClient.get()
                .uri("/reviews/company/{id}/count", companyId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(HttpStatusCode.valueOf(res.getStatusCode().value()));
                })
                .body(Long.class);
        return count != null ? count : 0;
    }

    public List<ReviewDisplayDTO> getTop3ForProfessional(Long professionalId) {
        return top3(restClient.get()
                .uri("/reviews/professional/{id}/top3", professionalId));
    }

    public List<ReviewDisplayDTO> getTop3ForCompany(Long companyId) {
        return top3(restClient.get()
                .uri("/reviews/company/{id}/top3", companyId));
    }

    private List<ReviewDisplayDTO> top3(RestClient.RequestHeadersSpec<?> request) {
        ReviewDisplayDTO[] response = request
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(HttpStatusCode.valueOf(res.getStatusCode().value()));
                })
                .body(ReviewDisplayDTO[].class);
        return response != null ? Arrays.asList(response) : Collections.emptyList();
    }

    public ReviewPageDTO getAllForProfessional(Long professionalId, Integer rating) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/reviews/professional/{id}/all")
                        .queryParamIfPresent("rating", java.util.Optional.ofNullable(rating))
                        .build(professionalId))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(HttpStatusCode.valueOf(res.getStatusCode().value()));
                })
                .body(ReviewPageDTO.class);
    }

    public ReviewPageDTO getAllForCompany(Long companyId, Integer rating) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/reviews/company/{id}/all")
                        .queryParamIfPresent("rating", java.util.Optional.ofNullable(rating))
                        .build(companyId))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(HttpStatusCode.valueOf(res.getStatusCode().value()));
                })
                .body(ReviewPageDTO.class);
    }
}
