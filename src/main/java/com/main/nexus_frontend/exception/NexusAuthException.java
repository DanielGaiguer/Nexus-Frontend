package com.main.nexus_frontend.exception;

// carrega uma mensagem já traduzida para português e amigável ao usuário final, junto com o status HTTP original do backend. 
// É o que permite ao AuthService traduzir os erros técnicos do backend em mensagens compreensíveis
public class NexusAuthException extends RuntimeException {
    private final int httpStatus;

    public NexusAuthException(String message, int httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}
