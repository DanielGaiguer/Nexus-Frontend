package com.main.nexus_frontend.model;

public class ScoreBreakdownDTO {
    private Double skills;
    private Double budget;
    private Double history;
    private Double reputation;
    private Double availability;
    private Double distance;
    private Double experience;
    private Double reputationAdjustment;
    private Double finalScore;

    public ScoreBreakdownDTO() {}

    public Double getSkills() { return skills; }
    public void setSkills(Double skills) { this.skills = skills; }

    public Double getBudget() { return budget; }
    public void setBudget(Double budget) { this.budget = budget; }

    public Double getHistory() { return history; }
    public void setHistory(Double history) { this.history = history; }

    public Double getReputation() { return reputation; }
    public void setReputation(Double reputation) { this.reputation = reputation; }

    public Double getAvailability() { return availability; }
    public void setAvailability(Double availability) { this.availability = availability; }

    public Double getDistance() { return distance; }
    public void setDistance(Double distance) { this.distance = distance; }

    public Double getExperience() { return experience; }
    public void setExperience(Double experience) { this.experience = experience; }

    public Double getReputationAdjustment() { return reputationAdjustment; }
    public void setReputationAdjustment(Double reputationAdjustment) { this.reputationAdjustment = reputationAdjustment; }

    public Double getFinalScore() { return finalScore; }
    public void setFinalScore(Double finalScore) { this.finalScore = finalScore; }
}
