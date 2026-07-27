package com.main.nexus_frontend.model;

public class ProjectAcceptanceRateDTO {
    private Long projectId;
    private String projectTitle;
    private String opportunityType;
    private Integer totalMatches;
    private Integer confirmedMatches;
    private Integer rejectedMatches;
    private Double acceptanceRate;
    private Double averageScore;

    public ProjectAcceptanceRateDTO() {}

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public String getOpportunityType() {
        return opportunityType;
    }

    public void setOpportunityType(String opportunityType) {
        this.opportunityType = opportunityType;
    }

    public Integer getTotalMatches() {
        return totalMatches;
    }

    public void setTotalMatches(Integer totalMatches) {
        this.totalMatches = totalMatches;
    }

    public Integer getConfirmedMatches() {
        return confirmedMatches;
    }

    public void setConfirmedMatches(Integer confirmedMatches) {
        this.confirmedMatches = confirmedMatches;
    }

    public Integer getRejectedMatches() {
        return rejectedMatches;
    }

    public void setRejectedMatches(Integer rejectedMatches) {
        this.rejectedMatches = rejectedMatches;
    }

    public Double getAcceptanceRate() {
        return acceptanceRate;
    }

    public void setAcceptanceRate(Double acceptanceRate) {
        this.acceptanceRate = acceptanceRate;
    }

    public Double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(Double averageScore) {
        this.averageScore = averageScore;
    }

    // confirmados / (confirmados + rejeitados) — ignora matches pendentes
    public void recomputeAcceptanceRate() {
        int confirmed = confirmedMatches != null ? confirmedMatches : 0;
        int rejected = rejectedMatches != null ? rejectedMatches : 0;
        int decided = confirmed + rejected;
        this.acceptanceRate = decided > 0 ? confirmed * 100.0 / decided : 0.0;
    }
}