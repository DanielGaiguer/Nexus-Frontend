package com.main.nexus_frontend.model;

import java.util.List;

public class CandidateComparisonItemDTO {
    private Long matchId;
    private Long professionalId;
    private String name;
    private String city;
    private String state;
    private String profilePhotoUrl;
    private String experienceLevel;
    private Double reputation;
    private Double confidenceScore;
    private Integer totalReviews;
    private Double minimumSalary;
    private Double maximumSalary;
    private List<String> skills;
    private List<String> matchingSkills;
    private List<String> missingSkills;
    private Integer previousProjectsCount;
    private Boolean available;
    private String matchStatus;
    private ScoreBreakdownDTO scoreBreakdown;

    public CandidateComparisonItemDTO() {}

    public Long getMatchId() { return matchId; }
    public void setMatchId(Long matchId) { this.matchId = matchId; }

    public Long getProfessionalId() { return professionalId; }
    public void setProfessionalId(Long professionalId) { this.professionalId = professionalId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getProfilePhotoUrl() { return profilePhotoUrl; }
    public void setProfilePhotoUrl(String profilePhotoUrl) { this.profilePhotoUrl = profilePhotoUrl; }

    public String getExperienceLevel() { return experienceLevel; }
    public void setExperienceLevel(String experienceLevel) { this.experienceLevel = experienceLevel; }

    public Double getReputation() { return reputation; }
    public void setReputation(Double reputation) { this.reputation = reputation; }

    public Double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(Double confidenceScore) { this.confidenceScore = confidenceScore; }

    public Integer getTotalReviews() { return totalReviews; }
    public void setTotalReviews(Integer totalReviews) { this.totalReviews = totalReviews; }

    public Double getMinimumSalary() { return minimumSalary; }
    public void setMinimumSalary(Double minimumSalary) { this.minimumSalary = minimumSalary; }

    public Double getMaximumSalary() { return maximumSalary; }
    public void setMaximumSalary(Double maximumSalary) { this.maximumSalary = maximumSalary; }

    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }

    public List<String> getMatchingSkills() { return matchingSkills; }
    public void setMatchingSkills(List<String> matchingSkills) { this.matchingSkills = matchingSkills; }

    public List<String> getMissingSkills() { return missingSkills; }
    public void setMissingSkills(List<String> missingSkills) { this.missingSkills = missingSkills; }

    public Integer getPreviousProjectsCount() { return previousProjectsCount; }
    public void setPreviousProjectsCount(Integer previousProjectsCount) { this.previousProjectsCount = previousProjectsCount; }

    public Boolean getAvailable() { return available; }
    public void setAvailable(Boolean available) { this.available = available; }

    public String getMatchStatus() { return matchStatus; }
    public void setMatchStatus(String matchStatus) { this.matchStatus = matchStatus; }

    public ScoreBreakdownDTO getScoreBreakdown() { return scoreBreakdown; }
    public void setScoreBreakdown(ScoreBreakdownDTO scoreBreakdown) { this.scoreBreakdown = scoreBreakdown; }
}
