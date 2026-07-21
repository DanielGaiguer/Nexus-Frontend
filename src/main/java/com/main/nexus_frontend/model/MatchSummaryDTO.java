package com.main.nexus_frontend.model;

public class MatchSummaryDTO {
    private Integer totalMatches;
    private Integer confirmedMatches;
    private Integer pendingMatches;
    private Integer rejectedMatches;
    private Double overallAcceptanceRate;
    private Double averageMatchScore;

    public MatchSummaryDTO() {}

    public Integer getTotalMatches() { return totalMatches; }
    public void setTotalMatches(Integer totalMatches) { this.totalMatches = totalMatches; }
    public Integer getConfirmedMatches() { return confirmedMatches; }
    public void setConfirmedMatches(Integer confirmedMatches) { this.confirmedMatches = confirmedMatches; }
    public Integer getPendingMatches() { return pendingMatches; }
    public void setPendingMatches(Integer pendingMatches) { this.pendingMatches = pendingMatches; }
    public Integer getRejectedMatches() { return rejectedMatches; }
    public void setRejectedMatches(Integer rejectedMatches) { this.rejectedMatches = rejectedMatches; }
    public Double getOverallAcceptanceRate() { return overallAcceptanceRate; }
    public void setOverallAcceptanceRate(Double overallAcceptanceRate) { this.overallAcceptanceRate = overallAcceptanceRate; }
    public Double getAverageMatchScore() { return averageMatchScore; }
    public void setAverageMatchScore(Double averageMatchScore) { this.averageMatchScore = averageMatchScore; }
}
