package com.main.nexus_frontend.service;

import com.main.nexus_frontend.exception.NexusAuthException;
import com.main.nexus_frontend.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class AuthService {

    private final RestClient restClient;

    public AuthService(@Value("${nexus.api.base-url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    // ── Registro de profissional ─────────────────────────────
    public void registerProfessional(RegisterProfessionalRequestDTO request) {
        restClient.post()
                .uri("/auth/register/professional")
                .body(request)
                .retrieve()
                // 502: falha ao validar CEP no provedor externo — precisa vir ANTES do is5xxServerError,
                // senão cai no genérico e perde a mensagem específica
                .onStatus(status -> status.value() == 502, (req, res) -> {
                    String body = readBody(res);
                    throw new NexusAuthException(mapRegisterProfessionalError(502, body), 502);
                })
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    int status = res.getStatusCode().value();
                    String body = readBody(res);
                    throw new NexusAuthException(
                        mapRegisterProfessionalError(status, body), status);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw new NexusAuthException(
                        "O servidor encontrou um problema. Tente novamente em instantes.", 500);
                })
                .body(String.class);
    }

    // ── Registro de empresa ──────────────────────────────────
    public void registerCompany(RegisterCompanyRequestDTO request) {
        restClient.post()
                .uri("/auth/register/company")
                .body(request)
                .retrieve()
                .onStatus(status -> status.value() == 502, (req, res) -> {
                    String body = readBody(res);
                    throw new NexusAuthException(mapRegisterCompanyError(502, body), 502);
                })
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    int status = res.getStatusCode().value();
                    String body = readBody(res);
                    throw new NexusAuthException(
                        mapRegisterCompanyError(status, body), status);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw new NexusAuthException(
                        "O servidor encontrou um problema. Tente novamente em instantes.", 500);
                })
                .body(String.class);
    }

    // ── Registro de empresa via LinkedIn (companyName ainda precisa ser
    //    informado manualmente — o LinkedIn não fornece razão social) ─────
    public void registerCompanyViaLinkedIn(RegisterCompanyLinkedInRequestDTO request) {
        restClient.post()
                .uri("/auth/register/company/linkedin")
                .body(request)
                .retrieve()
                .onStatus(status -> status.value() == 502, (req, res) -> {
                    String body = readBody(res);
                    throw new NexusAuthException(mapRegisterCompanyError(502, body), 502);
                })
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    int status = res.getStatusCode().value();
                    String body = readBody(res);
                    if (status == 401) {
                        throw new NexusAuthException(
                            "Sua conexão com o LinkedIn expirou. Conecte-se novamente.", 401);
                    }
                    throw new NexusAuthException(
                        mapRegisterCompanyError(status, body), status);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw new NexusAuthException(
                        "O servidor encontrou um problema. Tente novamente em instantes.", 500);
                })
                .body(String.class);
    }

    // ── Login ────────────────────────────────────────────────
    public LoginResponseDTO login(LoginRequestDTO request) {
        return restClient.post()
                .uri("/auth/login")
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    int status = res.getStatusCode().value();
                    String body = readBody(res);
                    throw new NexusAuthException(
                        mapLoginError(status, body), status);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw new NexusAuthException(
                        "O servidor encontrou um problema. Tente novamente em instantes.", 500);
                })
                .body(LoginResponseDTO.class);
    }

    // ── Mapeamento de erros de login ─────────────────────────
    // Separado por status primeiro: cada status do backend tem um conjunto
    // fechado de mensagens possíveis, então keywords só são checadas dentro
    // do bloco certo — evita que uma palavra apareça em dois contextos diferentes.
    private String mapLoginError(int status, String body) {
        String lower = body != null ? body.toLowerCase() : "";

        if (status == 400) {
            return "E-mail e senha são obrigatórios.";
        }

        if (status == 401) {
            return "E-mail ou senha incorretos. Verifique suas credenciais e tente novamente.";
        }

        if (status == 403) {
            if (lower.contains("pending")) {
                return "Sua empresa ainda está aguardando aprovação do administrador. "
                     + "Você receberá um e-mail quando o acesso for liberado.";
            }
            if (lower.contains("rejected")) {
                return "O cadastro da sua empresa foi rejeitado. "
                     + "Entre em contato com o suporte para mais informações.";
            }
            if (lower.contains("inactive")) {
                return "Esta conta está desativada. Entre em contato com o suporte.";
            }
            return "Acesso negado. Sua conta não tem permissão para entrar neste momento.";
        }

        if (status == 404) {
            return "Não encontramos uma conta com esse e-mail. Verifique o endereço ou crie uma conta.";
        }

        if (status == 429) {
            return "Muitas tentativas de login. Aguarde alguns minutos antes de tentar novamente.";
        }

        return "Não foi possível entrar. Verifique seus dados e tente novamente.";
    }

    // ── Mapeamento de erros de registro de profissional ──────
    private String mapRegisterProfessionalError(int status, String body) {
        String lower = body != null ? body.toLowerCase() : "";

        if (status == 400) {
            if (lower.contains("email")) {
                return "O e-mail é obrigatório.";
            }
            if (lower.contains("password") || lower.contains("senha")) {
                return "A senha é obrigatória.";
            }
            if (lower.contains("name")) {
                return "O nome é obrigatório.";
            }
            if (lower.contains("cep") || lower.contains("zip")) {
                return "CEP inválido. Use o formato 00000-000 ou 00000000.";
            }
            return "Dados inválidos. Verifique os campos e tente novamente.";
        }

        if (status == 409) {
            if (lower.contains("email")) {
                return "Este e-mail já está cadastrado. Tente fazer login ou use outro endereço.";
            }
            return "Já existe um cadastro com esses dados.";
        }

        if (status == 422) {
            return "CEP não encontrado. Verifique o CEP informado e tente novamente.";
        }

        if (status == 502) {
            return "Não foi possível validar o CEP no momento. Tente novamente em instantes.";
        }

        return "Não foi possível criar sua conta. Tente novamente.";
    }

    // ── Mapeamento de erros de registro de empresa ───────────
    private String mapRegisterCompanyError(int status, String body) {
        String lower = body != null ? body.toLowerCase() : "";

        if (status == 400) {
            if (lower.contains("email")) {
                return "O e-mail é obrigatório.";
            }
            if (lower.contains("password") || lower.contains("senha")) {
                return "A senha é obrigatória.";
            }
            if (lower.contains("company name")) {
                return "O nome da empresa é obrigatório.";
            }
            if (lower.contains("cnpj")) {
                return "CNPJ inválido. Verifique o número informado e tente novamente.";
            }
            if (lower.contains("cep") || lower.contains("zip")) {
                return "CEP inválido. Use o formato 00000-000 ou 00000000.";
            }
            return "Dados inválidos. Verifique os campos e tente novamente.";
        }

        if (status == 409) {
            if (lower.contains("cnpj")) {
                return "Este CNPJ já está cadastrado no sistema.";
            }
            if (lower.contains("email")) {
                return "Este e-mail já está cadastrado. Tente fazer login ou use outro endereço.";
            }
            return "Já existe um cadastro com esses dados.";
        }

        if (status == 422) {
            return "CEP não encontrado. Verifique o CEP informado e tente novamente.";
        }

        if (status == 502) {
            return "Não foi possível validar o CEP no momento. Tente novamente em instantes.";
        }

        return "Não foi possível enviar o cadastro da empresa. Tente novamente.";
    }

    // ── Utilitário: lê o corpo da resposta de erro ───────────
    private String readBody(org.springframework.http.client.ClientHttpResponse res) {
        try {
            return new String(res.getBody().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "";
        }
    }
}