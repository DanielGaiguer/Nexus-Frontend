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
    private List<SkillDTO> requiredSkills;
    private Long companyId;
    private String companyName;
    private CompanyDTO company;
    private String opportunityType;
    private String contractType;
    private String benefits;
    private LocalDate startDate;
    private Integer workloadHoursPerWeek;
    private Double monthlySalaryMin;
    private Double monthlySalaryMax;
    private String cep;
    private String city;
    private String uf;
    private Double latitude;
    private Double longitude;

    public ProjectDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getMinimumBudget() {
        return minimumBudget;
    }

    public void setMinimumBudget(Double minimumBudget) {
        this.minimumBudget = minimumBudget;
    }

    public Double getMaximumBudget() {
        return maximumBudget;
    }

    public void setMaximumBudget(Double maximumBudget) {
        this.maximumBudget = maximumBudget;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public String getModality() {
        return modality;
    }

    public void setModality(String modality) {
        this.modality = modality;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getMaxPositions() {
        return maxPositions;
    }

    public void setMaxPositions(Integer maxPositions) {
        this.maxPositions = maxPositions;
    }

    public Integer getFilledPositions() {
        return filledPositions;
    }

    public void setFilledPositions(Integer filledPositions) {
        this.filledPositions = filledPositions;
    }

    public String getExperienceLevel() {
        return experienceLevel;
    }

    public void setExperienceLevel(String experienceLevel) {
        this.experienceLevel = experienceLevel;
    }

    public List<SkillDTO> getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(List<SkillDTO> requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public CompanyDTO getCompany() {
        return company;
    }

    public void setCompany(CompanyDTO company) {
        this.company = company;
    }

    public String getOpportunityType() {
        return opportunityType;
    }

    public void setOpportunityType(String opportunityType) {
        this.opportunityType = opportunityType;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public String getBenefits() {
        return benefits;
    }

    public void setBenefits(String benefits) {
        this.benefits = benefits;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public Integer getWorkloadHoursPerWeek() {
        return workloadHoursPerWeek;
    }

    public void setWorkloadHoursPerWeek(Integer workloadHoursPerWeek) {
        this.workloadHoursPerWeek = workloadHoursPerWeek;
    }

    public Double getMonthlySalaryMin() {
        return monthlySalaryMin;
    }

    public void setMonthlySalaryMin(Double monthlySalaryMin) {
        this.monthlySalaryMin = monthlySalaryMin;
    }

    public Double getMonthlySalaryMax() {
        return monthlySalaryMax;
    }

    public void setMonthlySalaryMax(Double monthlySalaryMax) {
        this.monthlySalaryMax = monthlySalaryMax;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
