package com.main.nexus_frontend.service;

import com.main.nexus_frontend.exception.NexusApiException;
import com.main.nexus_frontend.model.MatchDTO;
import com.main.nexus_frontend.model.MatchHistoryDTO;
import com.main.nexus_frontend.model.PendingStatusCheckDTO;
import com.main.nexus_frontend.model.ProjectDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MatchService {

    private final RestClient restClient;
    private final ProjectService projectService;

    public MatchService(@Value("${nexus.api.base-url}") String baseUrl, ProjectService projectService) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
        this.projectService = projectService;
    }

    public MatchDTO getMatch(String token, Long matchId) {
        return restClient.get()
                .uri("/matches/{matchId}", matchId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .body(MatchDTO.class);
    }

    // Interesses que profissionais enviaram pras vagas da empresa, aguardando resposta.
    public List<MatchDTO> getReceivedInterests(String token) {
        MatchDTO[] response = restClient.get()
                .uri("/projects/matches/received")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .body(MatchDTO[].class);
        return response != null ? Arrays.asList(response) : Collections.emptyList();
    }

    // Convites que a empresa enviou (a partir do ranking), aguardando resposta do profissional.
    public List<MatchDTO> getSentInvites(String token) {
        MatchDTO[] response = restClient.get()
                .uri("/projects/matches/sent")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .body(MatchDTO[].class);
        return response != null ? Arrays.asList(response) : Collections.emptyList();
    }

    public List<MatchDTO> getConfirmedCompanyMatches(String token) {
        MatchDTO[] response = restClient.get()
                .uri("/projects/matches/confirmed")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .body(MatchDTO[].class);
        return response != null ? Arrays.asList(response) : Collections.emptyList();
    }

    public List<MatchDTO> getRejectedCompanyMatches(String token) {
        MatchDTO[] response = restClient.get()
                .uri("/projects/matches/rejected")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .body(MatchDTO[].class);
        return response != null ? Arrays.asList(response) : Collections.emptyList();
    }

    public List<MatchDTO> getPreviousProjects(String token) {
        MatchDTO[] response = restClient.get()
                .uri("/projects/previous")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .body(MatchDTO[].class);
        return response != null ? Arrays.asList(response) : Collections.emptyList();
    }

    public void professionalAccept(String token, Long matchId) {
        restClient.post()
                .uri("/matches/{matchId}/professional-accept", matchId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .toBodilessEntity();
    }

    public void professionalReject(String token, Long matchId, List<String> reasons) {
        restClient.post()
                .uri("/matches/{matchId}/professional-reject", matchId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(reasons)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .toBodilessEntity();
    }

    public void companyInterest(String token, Long matchId) {
        restClient.post()
                .uri("/matches/{matchId}/company-interest", matchId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .toBodilessEntity();
    }

    public void companyAccept(String token, Long matchId) {
        restClient.post()
                .uri("/matches/{matchId}/company-accept", matchId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .toBodilessEntity();
    }

    public void companyReject(String token, Long matchId, List<String> reasons) {
        restClient.post()
                .uri("/matches/{matchId}/company-reject", matchId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(reasons)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .toBodilessEntity();
    }

    public void companyCancel(String token, Long matchId) {
        restClient.post()
                .uri("/matches/{matchId}/company-cancel", matchId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .toBodilessEntity();
    }

    // Cancelamento genérico — usado pelo profissional (empresa continua usando companyCancel)
    public void cancelMatch(String token, Long matchId) {
        restClient.post()
                .uri("/matches/{matchId}/cancel", matchId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .toBodilessEntity();
    }

    // Retorna null se a empresa não tem nenhum status check pendente (404 do backend) ou
    // se a chamada falhar por qualquer motivo — é um recurso auxiliar do dashboard, não
    // pode derrubar a página inteira.
    public PendingStatusCheckDTO getPendingStatusCheck(String token) {
        try {
            return restClient.get()
                    .uri("/matches/status-check/pending")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                        throw new ResponseStatusException(HttpStatusCode.valueOf(res.getStatusCode().value()));
                    })
                    .body(PendingStatusCheckDTO.class);
        } catch (Exception e) {
            return null;
        }
    }

    public void answerStatusCheck(String token, Long matchId, String outcome) {
        restClient.post()
                .uri("/matches/{matchId}/status-check", matchId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(Map.of("outcome", outcome))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .toBodilessEntity();
    }

    public List<MatchHistoryDTO> getHistory(String token, Long matchId) {
        MatchHistoryDTO[] response = restClient.get()
                .uri("/matches/{matchId}/history", matchId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .body(MatchHistoryDTO[].class);
        return response != null ? Arrays.asList(response) : Collections.emptyList();
    }
}
