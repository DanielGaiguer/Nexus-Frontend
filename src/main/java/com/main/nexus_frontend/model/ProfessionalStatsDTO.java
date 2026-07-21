package com.main.nexus_frontend.model;

public class ProfessionalStatsDTO {
    private int totalMatchesReceived;
    private int totalAccepted;
    private int totalRejectedByMe;
    private double acceptanceRate;
    private double avgScore;
    private String rankingPosition;

    public ProfessionalStatsDTO() {}

    public int getTotalMatchesReceived() { return totalMatchesReceived; }
    public void setTotalMatchesReceived(int totalMatchesReceived) { this.totalMatchesReceived = totalMatchesReceived; }

    public int getTotalAccepted() { return totalAccepted; }
    public void setTotalAccepted(int totalAccepted) { this.totalAccepted = totalAccepted; }

    public int getTotalRejectedByMe() { return totalRejectedByMe; }
    public void setTotalRejectedByMe(int totalRejectedByMe) { this.totalRejectedByMe = totalRejectedByMe; }

    public double getAcceptanceRate() { return acceptanceRate; }
    public void setAcceptanceRate(double acceptanceRate) { this.acceptanceRate = acceptanceRate; }

    public double getAvgScore() { return avgScore; }
    public void setAvgScore(double avgScore) { this.avgScore = avgScore; }

    public String getRankingPosition() { return rankingPosition; }
    public void setRankingPosition(String rankingPosition) { this.rankingPosition = rankingPosition; }

    public int getAvgScoreAsInt() { return (int) Math.round(avgScore); }
}
