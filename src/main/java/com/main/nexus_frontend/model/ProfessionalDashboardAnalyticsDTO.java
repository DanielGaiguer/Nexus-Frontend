package com.main.nexus_frontend.model;

import java.util.List;

public class ProfessionalDashboardAnalyticsDTO {
    private MatchSummaryDTO matchSummary;
    private List<MonthlyMatchDTO> matchesPerMonth;
    private List<ScoreDistributionDTO> scoreDistribution;
    private List<CompanyAcceptanceRateDTO> acceptanceRatePerCompany;
    private List<SkillDemandDTO> mostRequiredSkills;
    private ReputationSummaryDTO reputationSummary;
    private List<SkillGapDTO> skillGaps;
    private List<SoftSkillFeedbackDTO> softSkillFeedback;

    public ProfessionalDashboardAnalyticsDTO() {}

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

    public List<CompanyAcceptanceRateDTO> getAcceptanceRatePerCompany() {
        return acceptanceRatePerCompany;
    }

    public void setAcceptanceRatePerCompany(List<CompanyAcceptanceRateDTO> acceptanceRatePerCompany) {
        this.acceptanceRatePerCompany = acceptanceRatePerCompany;
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

    public List<SkillGapDTO> getSkillGaps() {
        return skillGaps;
    }

    public void setSkillGaps(List<SkillGapDTO> skillGaps) {
        this.skillGaps = skillGaps;
    }

    public List<SoftSkillFeedbackDTO> getSoftSkillFeedback() {
        return softSkillFeedback;
    }

    public void setSoftSkillFeedback(List<SoftSkillFeedbackDTO> softSkillFeedback) {
        this.softSkillFeedback = softSkillFeedback;
    }
}
