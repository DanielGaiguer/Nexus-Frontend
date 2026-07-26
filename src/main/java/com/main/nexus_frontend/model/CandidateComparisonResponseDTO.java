package com.main.nexus_frontend.model;

import java.util.List;

public class CandidateComparisonResponseDTO {
    private Long projectId;
    private String projectTitle;
    private List<String> requiredSkills;
    private String workMode;
    private String experienceLevelRequired;
    private String opportunityType;
    private Double minimumBudget;
    private Double maximumBudget;
    private Double monthlySalaryMin;
    private Double monthlySalaryMax;
    private List<CandidateComparisonItemDTO> candidates;

    public CandidateComparisonResponseDTO() {}

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public String getProjectTitle() { return projectTitle; }
    public void setProjectTitle(String projectTitle) { this.projectTitle = projectTitle; }

    public List<String> getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(List<String> requiredSkills) { this.requiredSkills = requiredSkills; }

    public String getWorkMode() { return workMode; }
    public void setWorkMode(String workMode) { this.workMode = workMode; }

    public String getExperienceLevelRequired() { return experienceLevelRequired; }
    public void setExperienceLevelRequired(String experienceLevelRequired) { this.experienceLevelRequired = experienceLevelRequired; }

    public String getOpportunityType() { return opportunityType; }
    public void setOpportunityType(String opportunityType) { this.opportunityType = opportunityType; }

    public Double getMinimumBudget() { return minimumBudget; }
    public void setMinimumBudget(Double minimumBudget) { this.minimumBudget = minimumBudget; }

    public Double getMaximumBudget() { return maximumBudget; }
    public void setMaximumBudget(Double maximumBudget) { this.maximumBudget = maximumBudget; }

    public Double getMonthlySalaryMin() { return monthlySalaryMin; }
    public void setMonthlySalaryMin(Double monthlySalaryMin) { this.monthlySalaryMin = monthlySalaryMin; }

    public Double getMonthlySalaryMax() { return monthlySalaryMax; }
    public void setMonthlySalaryMax(Double monthlySalaryMax) { this.monthlySalaryMax = monthlySalaryMax; }

    public List<CandidateComparisonItemDTO> getCandidates() { return candidates; }
    public void setCandidates(List<CandidateComparisonItemDTO> candidates) { this.candidates = candidates; }
}
