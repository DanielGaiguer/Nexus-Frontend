package com.main.nexus_frontend.model;

public class AiExtractionRequestDTO {
    private String rawText;

    public AiExtractionRequestDTO() {}

    public AiExtractionRequestDTO(String rawText) {
        this.rawText = rawText;
    }

    public String getRawText() { return rawText; }
    public void setRawText(String rawText) { this.rawText = rawText; }
}
