package com.main.nexus_frontend.model;

import java.util.List;

public class CompanyDashboardAnalyticsDTO {
    private MatchSummaryDTO matchSummary;
    private List<MonthlyMatchDTO> matchesPerMonth;
    private List<ScoreDistributionDTO> scoreDistribution;
    private List<ProjectAcceptanceRateDTO> acceptanceRatePerProject;
    private List<SkillDemandDTO> mostRequiredSkills;
    private ReputationSummaryDTO reputationSummary;
    private List<SoftSkillFeedbackDTO> softSkillFeedback;
    private List<ProjectStatusDistributionDTO> projectStatusDistribution;

    public CompanyDashboardAnalyticsDTO() {}

    public MatchSummaryDTO getMatchSummary() {
        return matchSummary;
    }

    public void setMatchSummary(MatchSummaryDTO matchSummary) {
        this.matchSummary = matchSummary;
    }

    public List<MonthlyMatchDTO> getMatchesPerMonth() {
        return matchesPerMonth;
    }

    public void setMatchesPerMonth(List<MonthlyMatchDTO> matchesPerMonth) {
        this.matchesPerMonth = matchesPerMonth;
    }

    public List<ScoreDistributionDTO> getScoreDistribution() {
        return scoreDistribution;
    }

    public void setScoreDistribution(List<ScoreDistributionDTO> scoreDistribution) {
        this.scoreDistribution = scoreDistribution;
    }

    public List<ProjectAcceptanceRateDTO> getAcceptanceRatePerProject() {
        return acceptanceRatePerProject;
    }

    public void setAcceptanceRatePerProject(List<ProjectAcceptanceRateDTO> acceptanceRatePerProject) {
        this.acceptanceRatePerProject = acceptanceRatePerProject;
    }

    public List<SkillDemandDTO> getMostRequiredSkills() {
        return mostRequiredSkills;
    }

    public void setMostRequiredSkills(List<SkillDemandDTO> mostRequiredSkills) {
        this.mostRequiredSkills = mostRequiredSkills;
    }

    public ReputationSummaryDTO getReputationSummary() {
        return reputationSummary;
    }

    public void setReputationSummary(ReputationSummaryDTO reputationSummary) {
        this.reputationSummary = reputationSummary;
    }

    public List<SoftSkillFeedbackDTO> getSoftSkillFeedback() {
        return softSkillFeedback;
    }

    public void setSoftSkillFeedback(List<SoftSkillFeedbackDTO> softSkillFeedback) {
        this.softSkillFeedback = softSkillFeedback;
    }

    public List<ProjectStatusDistributionDTO> getProjectStatusDistribution() {
        return projectStatusDistribution;
    }

    public void setProjectStatusDistribution(List<ProjectStatusDistributionDTO> projectStatusDistribution) {
        this.projectStatusDistribution = projectStatusDistribution;
    }

}
