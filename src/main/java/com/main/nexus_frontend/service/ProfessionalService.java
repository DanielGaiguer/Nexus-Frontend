package com.main.nexus_frontend.service;

import com.main.nexus_frontend.exception.NexusApiException;
import com.main.nexus_frontend.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

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

    public ProfessionalProfileDTO getProfile(String token) {
        return restClient.get()
                .uri("/professional/profile")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .body(ProfessionalProfileDTO.class);
    }

    public ProfessionalStatsDTO getStats(String token) {
        return restClient.get()
                .uri("/professional/stats")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .body(ProfessionalStatsDTO.class);
    }

    public void updateProfile(String token, ProfessionalProfileDTO dto) {
        restClient.put()
                .uri("/professional/profile")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(dto)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .toBodilessEntity();
    }

    public void updateSkills(String token, List<Long> skillIds) {
        restClient.put()
                .uri("/professional/skills")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(skillIds)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .toBodilessEntity();
    }

    public List<MatchDTO> getMatches(String token) {
        MatchDTO[] response = restClient.get()
                .uri("/professional/matches")
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

    public List<MatchDTO> getPendingInvites(String token) {
        MatchDTO[] response = restClient.get()
                .uri("/professional/matches/invites")
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

    // Espelho de getPendingInvites: interesses que o próprio profissional enviou e que
    // ainda aguardam resposta da empresa.
    public List<MatchDTO> getSentInterests(String token) {
        MatchDTO[] response = restClient.get()
                .uri("/professional/matches/sent")
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

    // Matches confirmados que já encerraram (expiraram ou foram cancelados depois de
    // confirmados) — espelho de ProjectService.getPreviousProjects do lado da empresa.
    public List<MatchDTO> getPreviousMatches(String token) {
        MatchDTO[] response = restClient.get()
                .uri("/professional/matches/previous")
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

    public List<PreviousProjectDTO> getProjects(String token) {
        PreviousProjectDTO[] response = restClient.get()
                .uri("/professional/projects")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .body(PreviousProjectDTO[].class);
        return response != null ? Arrays.asList(response) : Collections.emptyList();
    }

    public void addProject(String token, PreviousProjectDTO dto) {
        restClient.post()
                .uri("/professional/projects")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(dto)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .toBodilessEntity();
    }

    public void updateProject(String token, Long projectId, PreviousProjectDTO dto) {
        restClient.put()
                .uri("/professional/projects/{projectId}", projectId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(dto)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .toBodilessEntity();
    }

    public void deleteProject(String token, Long projectId) {
        restClient.delete()
                .uri("/professional/projects/{projectId}", projectId)
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

    public List<ProfessionalCredentialDTO> getCredentials(String token) {
        ProfessionalCredentialDTO[] response = restClient.get()
                .uri("/professional/credentials")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .body(ProfessionalCredentialDTO[].class);
        return response != null ? Arrays.asList(response) : Collections.emptyList();
    }

    public void addCredential(String token, ProfessionalCredentialDTO dto) {
        restClient.post()
                .uri("/professional/credentials")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(dto)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .toBodilessEntity();
    }

    public void updateCredential(String token, Long credentialId, ProfessionalCredentialDTO dto) {
        restClient.put()
                .uri("/professional/credentials/{credentialId}", credentialId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(dto)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .toBodilessEntity();
    }

    public void deleteCredential(String token, Long credentialId) {
        restClient.delete()
                .uri("/professional/credentials/{credentialId}", credentialId)
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

    public List<MatchDTO> getOpportunities(String token) {
        MatchDTO[] response = restClient.get()
                .uri("/professional/opportunities")
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

    public void sendInterest(String token, Long projectId) {
        restClient.post()
                .uri("/professional/opportunities/{projectId}/interest", projectId)
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

    public List<SkillDTO> getAllSkills(String token) {
        SkillDTO[] response = restClient.get()
                .uri("/professional/skills")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .body(SkillDTO[].class);
        return response != null ? Arrays.asList(response) : Collections.emptyList();
    }

    public void uploadResume(String token, MultipartFile file) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        try {
            body.add("file", new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            });
        } catch (java.io.IOException e) {
            throw new NexusApiException("Não foi possível ler o arquivo enviado. Tente novamente.", 500);
        }
        restClient.post()
                .uri("/professional/resume")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .toBodilessEntity();
    }

    public byte[] downloadResume(String token, Long professionalId) {
        return restClient.get()
                .uri("/professional/{professionalId}/resume", professionalId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .body(byte[].class);
    }

    public String uploadPhoto(String token, MultipartFile file) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        try {
            body.add("file", new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            });
        } catch (java.io.IOException e) {
            throw new NexusApiException("Não foi possível ler o arquivo enviado. Tente novamente.", 500);
        }
        return restClient.post()
                .uri("/professional/profile/photo")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .body(String.class);
    }

    public void removePhoto(String token) {
        restClient.method(org.springframework.http.HttpMethod.DELETE)
                .uri("/professional/profile/photo")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .body(String.class);
    }

    public void unlinkGithub(String token) {
        restClient.method(org.springframework.http.HttpMethod.DELETE)
                .uri("/auth/github/unlink")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .body(String.class);
    }

    public ProfessionalDashboardAnalyticsDTO getAnalytics(String token) {
        return restClient.get()
                .uri("/analytics/professional/dashboard")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .body(ProfessionalDashboardAnalyticsDTO.class);
    }

    // Perfil público — usado quando uma empresa visualiza um profissional
    public PublicProfessionalDTO getPublicProfile(Long id) {
        return restClient.get()
                .uri("/public/professional/{id}", id)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .body(PublicProfessionalDTO.class);
    }

    // Contato liberado só depois de um match confirmado com a empresa logada
    public ContactInfoDTO getContact(String token, Long professionalId) {
        return restClient.get()
                .uri("/professional/{professionalId}/contact", professionalId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw NexusApiException.from(res);
                })
                .body(ContactInfoDTO.class);
    }

    public byte[] exportPdf(String token, Long professionalId) {
        String uri = "/professional/profile/export";
        if (professionalId != null) {
            uri += "?professionalId=" + professionalId;
        }
        return restClient.get()
                .uri(uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .body(byte[].class);
    }
}
