package com.main.nexus_frontend.model;

public class PendingReviewDTO {
    private Long matchId;
    private String otherPartyName;
    private String projectTitle;

    public PendingReviewDTO() {}

    public Long getMatchId() { return matchId; }
    public void setMatchId(Long matchId) { this.matchId = matchId; }

    public String getOtherPartyName() { return otherPartyName; }
    public void setOtherPartyName(String otherPartyName) { this.otherPartyName = otherPartyName; }

    public String getProjectTitle() { return projectTitle; }
    public void setProjectTitle(String projectTitle) { this.projectTitle = projectTitle; }
}
