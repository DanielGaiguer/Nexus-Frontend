package com.main.nexus_frontend.model;

public class AiSkillSuggestionDTO {
    private String extractedName;
    private Long matchedSkillId;
    private String matchedSkillName;
    private boolean foundInCatalog;

    public AiSkillSuggestionDTO() {}

    public String getExtractedName() { return extractedName; }
    public void setExtractedName(String extractedName) { this.extractedName = extractedName; }

    public Long getMatchedSkillId() { return matchedSkillId; }
    public void setMatchedSkillId(Long matchedSkillId) { this.matchedSkillId = matchedSkillId; }

    public String getMatchedSkillName() { return matchedSkillName; }
    public void setMatchedSkillName(String matchedSkillName) { this.matchedSkillName = matchedSkillName; }

    public boolean isFoundInCatalog() { return foundInCatalog; }
    public void setFoundInCatalog(boolean foundInCatalog) { this.foundInCatalog = foundInCatalog; }
}
