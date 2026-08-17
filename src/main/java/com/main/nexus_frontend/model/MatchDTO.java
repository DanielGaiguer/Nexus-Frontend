package com.main.nexus_frontend.model;

import java.time.LocalDateTime;
import java.util.List;

public class MatchDTO {
    private Long id;
    private Double matchScore;
    private String companyStatus;
    private String professionalStatus;
    private String status;
    private LocalDateTime createdAt;
    private ProjectDTO project;
    private ProfessionalSimpleDTO professional;
    private ScoreBreakdownDTO scoreBreakdown;
    private Boolean active;
    private List<String> rejectionReasons;
    private String rejectionDescription;

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

    public ScoreBreakdownDTO getScoreBreakdown() { return scoreBreakdown; }
    public void setScoreBreakdown(ScoreBreakdownDTO scoreBreakdown) { this.scoreBreakdown = scoreBreakdown; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public List<String> getRejectionReasons() { return rejectionReasons; }
    public void setRejectionReasons(List<String> rejectionReasons) { this.rejectionReasons = rejectionReasons; }

    public String getRejectionDescription() { return rejectionDescription; }
    public void setRejectionDescription(String rejectionDescription) { this.rejectionDescription = rejectionDescription; }

    public int getScoreAsInt() {
        if (matchScore == null) return 0;
        return Math.max(0, Math.min(100, (int) Math.round(matchScore)));
    }

    // Dias restantes até completar 30 dias desde createdAt (pode ser negativo se já passou)
    public long getDaysRemaining() {
        if (createdAt == null) return 30;
        long daysElapsed = java.time.Duration.between(createdAt, java.time.LocalDateTime.now()).toDays();
        return 30 - daysElapsed;
    }
}
