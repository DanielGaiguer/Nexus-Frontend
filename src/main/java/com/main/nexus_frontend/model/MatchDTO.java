package com.main.nexus_frontend.model;

import java.time.LocalDateTime;

public class MatchDTO {
    private Long id;
    private Double matchScore;
    private String companyStatus;
    private String professionalStatus;
    private String status;
    private LocalDateTime createdAt;
    private ProjectDTO project;
    private ProfessionalSimpleDTO professional;

    public MatchDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getMatchScore() { return matchScore; }
    public void setMatchScore(Double matchScore) { this.matchScore = matchScore; }

    public String getCompanyStatus() { return companyStatus; }
    public void setCompanyStatus(String companyStatus) { this.companyStatus = companyStatus; }

    public String getProfessionalStatus() { return professionalStatus; }
    public void setProfessionalStatus(String professionalStatus) { this.professionalStatus = professionalStatus; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public ProjectDTO getProject() { return project; }
    public void setProject(ProjectDTO project) { this.project = project; }

    public ProfessionalSimpleDTO getProfessional() { return professional; }
    public void setProfessional(ProfessionalSimpleDTO professional) { this.professional = professional; }

    public int getScoreAsInt() {
        return matchScore != null ? (int) Math.round(matchScore) : 0;
    }
}
