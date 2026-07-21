package com.main.nexus_frontend.model;

public class ReputationSummaryDTO {
    private Double overallReputation;
    private Double confidenceScore;
    private Integer totalReviews;
    private Double satisfactionAverage;
    private Double recommendationRate;
    private Double communication;
    private Double reliability;
    private Double punctuality;
    private Double professionalism;

    public ReputationSummaryDTO() {}

    public Double getOverallReputation() { return overallReputation; }
    public void setOverallReputation(Double overallReputation) { this.overallReputation = overallReputation; }
    public Double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(Double confidenceScore) { this.confidenceScore = confidenceScore; }
    public Integer getTotalReviews() { return totalReviews; }
    public void setTotalReviews(Integer totalReviews) { this.totalReviews = totalReviews; }
    public Double getSatisfactionAverage() { return satisfactionAverage; }
    public void setSatisfactionAverage(Double satisfactionAverage) { this.satisfactionAverage = satisfactionAverage; }
    public Double getRecommendationRate() { return recommendationRate; }
    public void setRecommendationRate(Double recommendationRate) { this.recommendationRate = recommendationRate; }
    public Double getCommunication() { return communication; }
    public void setCommunication(Double communication) { this.communication = communication; }
    public Double getReliability() { return reliability; }
    public void setReliability(Double reliability) { this.reliability = reliability; }
    public Double getPunctuality() { return punctuality; }
    public void setPunctuality(Double punctuality) { this.punctuality = punctuality; }
    public Double getProfessionalism() { return professionalism; }
    public void setProfessionalism(Double professionalism) { this.professionalism = professionalism; }
}
