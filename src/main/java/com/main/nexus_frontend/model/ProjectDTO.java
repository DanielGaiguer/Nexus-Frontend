package com.main.nexus_frontend.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ProjectDTO {
    private Long id;
    private String title;
    private String description;
    private Double minimumBudget;
    private Double maximumBudget;
    private LocalDate deadline;
    private String modality;
    private String status;
    private String type;
    private LocalDateTime createdAt;
    private Integer maxPositions;
    private Integer filledPositions;
    private String experienceLevel;
    private List<String> requiredSkills;
    private Long companyId;
    private String companyName;
    private CompanyDTO company;

    public ProjectDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getMinimumBudget() { return minimumBudget; }
    public void setMinimumBudget(Double minimumBudget) { this.minimumBudget = minimumBudget; }

    public Double getMaximumBudget() { return maximumBudget; }
    public void setMaximumBudget(Double maximumBudget) { this.maximumBudget = maximumBudget; }

    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

    public String getModality() { return modality; }
    public void setModality(String modality) { this.modality = modality; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Integer getMaxPositions() { return maxPositions; }
    public void setMaxPositions(Integer maxPositions) { this.maxPositions = maxPositions; }

    public Integer getFilledPositions() { return filledPositions; }
    public void setFilledPositions(Integer filledPositions) { this.filledPositions = filledPositions; }

    public String getExperienceLevel() { return experienceLevel; }
    public void setExperienceLevel(String experienceLevel) { this.experienceLevel = experienceLevel; }

    public List<String> getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(List<String> requiredSkills) { this.requiredSkills = requiredSkills; }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public CompanyDTO getCompany() { return company; }
    public void setCompany(CompanyDTO company) { this.company = company; }
}
