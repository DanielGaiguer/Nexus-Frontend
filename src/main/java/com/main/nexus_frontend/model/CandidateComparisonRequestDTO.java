package com.main.nexus_frontend.model;

import java.util.List;

public class CandidateComparisonRequestDTO {
    private Long projectId;
    private List<Long> matchIds;

    public CandidateComparisonRequestDTO() {}

    public CandidateComparisonRequestDTO(Long projectId, List<Long> matchIds) {
        this.projectId = projectId;
        this.matchIds = matchIds;
    }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public List<Long> getMatchIds() { return matchIds; }
    public void setMatchIds(List<Long> matchIds) { this.matchIds = matchIds; }
}
