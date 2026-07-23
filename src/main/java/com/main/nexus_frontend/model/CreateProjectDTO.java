package com.main.nexus_frontend.model;

import java.util.List;

public class CreateProjectDTO {
    private String title;
    private String description;
    private String workMode;
    private String type;
    private String experienceLevel;
    private Double minimumBudget;
    private Double maximumBudget;
    private String deadline;
    private Integer maxPositions;
    private List<Long> skillIds;
    private String opportunityType;
    private String contractType;
    private String benefits;
    private String startDate;
    private Integer workloadHoursPerWeek;
    private Double monthlySalaryMin;
    private Double monthlySalaryMax;

    public CreateProjectDTO() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getWorkMode() { return workMode; }
    public void setWorkMode(String workMode) { this.workMode = workMode; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getExperienceLevel() { return experienceLevel; }
    public void setExperienceLevel(String experienceLevel) { this.experienceLevel = experienceLevel; }
    public Double getMinimumBudget() { return minimumBudget; }
    public void setMinimumBudget(Double minimumBudget) { this.minimumBudget = minimumBudget; }
    public Double getMaximumBudget() { return maximumBudget; }
    public void setMaximumBudget(Double maximumBudget) { this.maximumBudget = maximumBudget; }
    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }
    public Integer getMaxPositions() { return maxPositions; }
    public void setMaxPositions(Integer maxPositions) { this.maxPositions = maxPositions; }
    public List<Long> getSkillIds() { return skillIds; }
    public void setSkillIds(List<Long> skillIds) { this.skillIds = skillIds; }

    public String getOpportunityType() { return opportunityType; }
    public void setOpportunityType(String opportunityType) { this.opportunityType = opportunityType; }

    public String getContractType() { return contractType; }
    public void setContractType(String contractType) { this.contractType = contractType; }

    public String getBenefits() { return benefits; }
    public void setBenefits(String benefits) { this.benefits = benefits; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public Integer getWorkloadHoursPerWeek() { return workloadHoursPerWeek; }
    public void setWorkloadHoursPerWeek(Integer workloadHoursPerWeek) { this.workloadHoursPerWeek = workloadHoursPerWeek; }

    public Double getMonthlySalaryMin() { return monthlySalaryMin; }
    public void setMonthlySalaryMin(Double monthlySalaryMin) { this.monthlySalaryMin = monthlySalaryMin; }

    public Double getMonthlySalaryMax() { return monthlySalaryMax; }
    public void setMonthlySalaryMax(Double monthlySalaryMax) { this.monthlySalaryMax = monthlySalaryMax; }
}
