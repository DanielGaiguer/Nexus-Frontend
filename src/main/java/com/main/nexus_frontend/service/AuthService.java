package com.main.nexus_frontend.service;

import com.main.nexus_frontend.model.LoginRequestDTO;
import com.main.nexus_frontend.model.LoginResponseDTO;
import com.main.nexus_frontend.model.RegisterCompanyRequestDTO;
import com.main.nexus_frontend.model.RegisterProfessionalRequestDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final RestClient restClient;

    public AuthService(@Value("${nexus.api.base-url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public void registerProfessional(RegisterProfessionalRequestDTO request) {
        String response = restClient.post()
                .uri("/auth/register/professional")
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Registration failed");
                })
                .body(String.class);
    }

    public void registerCompany(RegisterCompanyRequestDTO request) {
        String response = restClient.post()
                .uri("/auth/register/company")
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Registration failed");
                })
                .body(String.class);
    }

    public LoginResponseDTO login(LoginRequestDTO request) {
        return restClient.post()
                .uri("/auth/login")
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new ResponseStatusException(
                            HttpStatusCode.valueOf(res.getStatusCode().value()),
                            "Invalid email or password.");
                })
                .body(LoginResponseDTO.class);
    }
}
