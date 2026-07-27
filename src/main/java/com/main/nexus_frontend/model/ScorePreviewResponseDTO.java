package com.main.nexus_frontend.model;

public class ScorePreviewResponseDTO {
    private Double finalScore;
    private String matchStatus;
    private Long matchId;
    private ScoreBreakdownDTO scoreBreakdown;

    public ScorePreviewResponseDTO() {}

    public Double getFinalScore() { return finalScore; }
    public void setFinalScore(Double finalScore) { this.finalScore = finalScore; }

    public String getMatchStatus() { return matchStatus; }
    public void setMatchStatus(String matchStatus) { this.matchStatus = matchStatus; }

    public Long getMatchId() { return matchId; }
    public void setMatchId(Long matchId) { this.matchId = matchId; }

    public ScoreBreakdownDTO getScoreBreakdown() { return scoreBreakdown; }
    public void setScoreBreakdown(ScoreBreakdownDTO scoreBreakdown) { this.scoreBreakdown = scoreBreakdown; }
}
