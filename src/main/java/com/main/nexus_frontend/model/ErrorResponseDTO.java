package com.main.nexus_frontend.model;

public class ErrorResponseDTO {
    private String message;
    private Integer status;

    public ErrorResponseDTO() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
