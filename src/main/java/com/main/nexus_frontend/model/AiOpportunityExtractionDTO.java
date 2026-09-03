package com.main.nexus_frontend.model;

import java.util.List;

// Espelha com.main.nexus.dto.AiOpportunityExtractionDTO do backend — enums e datas chegam
// como String, igual ao resto dos DTOs deste módulo (ex: CreateProjectDTO), já que quem
// interpreta esses valores é o formulário HTML, não código Java aqui.
public class AiOpportunityExtractionDTO {
    private String title;
    private String description;
    private String opportunityType;
    private String type;
    private String workMode;
    private String experienceLevel;
    private Integer maxPositions;

    private Double minimumBudget;
    private Double maximumBudget;
    private String deadline;

    private String contractType;
    private Double monthlySalaryMin;
    private Double monthlySalaryMax;
    private List<String> benefits;
    private String startDate;
    private Integer workloadHoursPerWeek;

    private String cep;

    private List<AiSkillSuggestionDTO> requiredSkills;
    private List<AiSkillSuggestionDTO> niceToHaveSkills;

    public AiOpportunityExtractionDTO() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getOpportunityType() { return opportunityType; }
    public void setOpportunityType(String opportunityType) { this.opportunityType = opportunityType; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getWorkMode() { return workMode; }
    public void setWorkMode(String workMode) { this.workMode = workMode; }

    public String getExperienceLevel() { return experienceLevel; }
    public void setExperienceLevel(String experienceLevel) { this.experienceLevel = experienceLevel; }

    public Integer getMaxPositions() { return maxPositions; }
    public void setMaxPositions(Integer maxPositions) { this.maxPositions = maxPositions; }

    public Double getMinimumBudget() { return minimumBudget; }
    public void setMinimumBudget(Double minimumBudget) { this.minimumBudget = minimumBudget; }

    public Double getMaximumBudget() { return maximumBudget; }
    public void setMaximumBudget(Double maximumBudget) { this.maximumBudget = maximumBudget; }

    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }

    public String getContractType() { return contractType; }
    public void setContractType(String contractType) { this.contractType = contractType; }

    public Double getMonthlySalaryMin() { return monthlySalaryMin; }
    public void setMonthlySalaryMin(Double monthlySalaryMin) { this.monthlySalaryMin = monthlySalaryMin; }

    public Double getMonthlySalaryMax() { return monthlySalaryMax; }
    public void setMonthlySalaryMax(Double monthlySalaryMax) { this.monthlySalaryMax = monthlySalaryMax; }

    public List<String> getBenefits() { return benefits; }
    public void setBenefits(List<String> benefits) { this.benefits = benefits; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public Integer getWorkloadHoursPerWeek() { return workloadHoursPerWeek; }
    public void setWorkloadHoursPerWeek(Integer workloadHoursPerWeek) { this.workloadHoursPerWeek = workloadHoursPerWeek; }

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }

    public List<AiSkillSuggestionDTO> getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(List<AiSkillSuggestionDTO> requiredSkills) { this.requiredSkills = requiredSkills; }

    public List<AiSkillSuggestionDTO> getNiceToHaveSkills() { return niceToHaveSkills; }
    public void setNiceToHaveSkills(List<AiSkillSuggestionDTO> niceToHaveSkills) { this.niceToHaveSkills = niceToHaveSkills; }
}
