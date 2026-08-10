package com.main.nexus_frontend.model;

public class PendingStatusCheckDTO {
    private Long matchId;
    private String professionalName;
    private String projectTitle;

    public PendingStatusCheckDTO() {}

    public Long getMatchId() { return matchId; }
    public void setMatchId(Long matchId) { this.matchId = matchId; }

    public String getProfessionalName() { return professionalName; }
    public void setProfessionalName(String professionalName) { this.professionalName = professionalName; }

    public String getProjectTitle() { return projectTitle; }
    public void setProjectTitle(String projectTitle) { this.projectTitle = projectTitle; }
}
