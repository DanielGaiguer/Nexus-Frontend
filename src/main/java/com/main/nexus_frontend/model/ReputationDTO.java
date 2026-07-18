package com.main.nexus_frontend.model;

public class ReputationDTO {
    private Double overallScore;
    private Double confidencePercent;
    private Integer totalReviews;
    private Double technicalCompetence;
    private Double communication;
    private Double reliability;
    private Double punctuality;
    private Double professionalism;
    private Double satisfactionAverage;
    private Double recommendationRate;

    public ReputationDTO() {}

    public Double getOverallScore() { return overallScore; }
    public void setOverallScore(Double overallScore) { this.overallScore = overallScore; }
    public Double getConfidencePercent() { return confidencePercent; }
    public void setConfidencePercent(Double confidencePercent) { this.confidencePercent = confidencePercent; }
    public Integer getTotalReviews() { return totalReviews; }
    public void setTotalReviews(Integer totalReviews) { this.totalReviews = totalReviews; }
    public Double getTechnicalCompetence() { return technicalCompetence; }
    public void setTechnicalCompetence(Double technicalCompetence) { this.technicalCompetence = technicalCompetence; }
    public Double getCommunication() { return communication; }
    public void setCommunication(Double communication) { this.communication = communication; }
    public Double getReliability() { return reliability; }
    public void setReliability(Double reliability) { this.reliability = reliability; }
    public Double getPunctuality() { return punctuality; }
    public void setPunctuality(Double punctuality) { this.punctuality = punctuality; }
    public Double getProfessionalism() { return professionalism; }
    public void setProfessionalism(Double professionalism) { this.professionalism = professionalism; }
    public Double getSatisfactionAverage() { return satisfactionAverage; }
    public void setSatisfactionAverage(Double satisfactionAverage) { this.satisfactionAverage = satisfactionAverage; }
    public Double getRecommendationRate() { return recommendationRate; }
    public void setRecommendationRate(Double recommendationRate) { this.recommendationRate = recommendationRate; }
}
