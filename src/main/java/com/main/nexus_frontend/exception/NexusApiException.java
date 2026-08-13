package com.main.nexus_frontend.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.springframework.http.client.ClientHttpResponse;

// Equivalente ao NexusAuthException, mas para o resto da aplicação (perfil, projetos,
// matches, skills, admin etc.) — o fluxo de auth já tinha essa camada de tradução
// (AuthService.mapLoginError/mapRegisterProfessionalError); o restante dos services
// simplesmente relançava a resposta de erro do backend, e ResponseStatusException.getMessage()
// inclui o código HTTP cru no meio da frase (ex: '404 NOT_FOUND "Failed to update profile"'),
// texto técnico em inglês que acabava indo direto pro flash message do usuário final.
//
// getMessage() aqui é sempre a tradução amigável por status HTTP — não é por palavra-chave
// como no fluxo de auth, já que são dezenas de ações diferentes nesses controllers e não
// compensa mapear mensagem a mensagem. Quando algum controller específico precisa decidir o
// fluxo com base no motivo exato que o backend mandou (ex: MatchReviewController distinguindo
// "ainda não fez o status check" de "nunca teve contato"), getRawReason() dá acesso a esse
// texto original — só não é isso que vai parar num flash message pro usuário.
public class NexusApiException extends RuntimeException {
    private final int httpStatus;
    private final String rawReason;

    public NexusApiException(String message, int httpStatus) {
        this(message, httpStatus, null);
    }

    public NexusApiException(String message, int httpStatus, String rawReason) {
        super(message);
        this.httpStatus = httpStatus;
        this.rawReason = rawReason;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public String getRawReason() {
        return rawReason;
    }

    public static NexusApiException from(ClientHttpResponse res) {
        int status = readStatus(res);
        String rawReason = readReason(res);
        return new NexusApiException(friendlyMessage(status), status, rawReason);
    }

    private static int readStatus(ClientHttpResponse res) {
        try {
            return res.getStatusCode().value();
        } catch (java.io.IOException e) {
            return 500;
        }
    }

    // Lê o campo "message" do corpo {"status":...,"message":"..."} que ApiExceptionHandler
    // sempre devolve — best effort, nunca propaga erro de parsing.
    private static String readReason(ClientHttpResponse res) {
        try {
            Map<?, ?> body = new ObjectMapper().readValue(res.getBody(), Map.class);
            Object message = body.get("message");
            return message != null ? message.toString() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private static String friendlyMessage(int status) {
        return switch (status) {
            case 400 -> "Dados inválidos. Verifique os campos e tente novamente.";
            case 401 -> "Sua sessão expirou. Faça login novamente.";
            case 403 -> "Você não tem permissão para realizar essa ação.";
            case 404 -> "Registro não encontrado.";
            case 409 -> "Essa ação não pode ser concluída no estado atual do registro.";
            case 422 -> "Não foi possível processar os dados enviados.";
            case 429 -> "Muitas tentativas em pouco tempo. Aguarde um instante e tente novamente.";
            default -> status >= 500
                    ? "O servidor encontrou um problema. Tente novamente em instantes."
                    : "Não foi possível concluir a ação. Tente novamente.";
        };
    }
}
