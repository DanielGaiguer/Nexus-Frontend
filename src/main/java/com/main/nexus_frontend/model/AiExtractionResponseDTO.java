package com.main.nexus_frontend.model;

import java.util.List;

public class AiExtractionResponseDTO {
    private AiOpportunityExtractionDTO suggestion;
    private List<String> lowConfidenceFields;

    public AiExtractionResponseDTO() {}

    public AiOpportunityExtractionDTO getSuggestion() { return suggestion; }
    public void setSuggestion(AiOpportunityExtractionDTO suggestion) { this.suggestion = suggestion; }

    public List<String> getLowConfidenceFields() { return lowConfidenceFields; }
    public void setLowConfidenceFields(List<String> lowConfidenceFields) { this.lowConfidenceFields = lowConfidenceFields; }
}
