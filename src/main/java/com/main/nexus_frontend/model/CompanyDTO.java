package com.main.nexus_frontend.model;

import java.util.List;

public class CompanyDTO {
    private Long id;
    private String companyName;
    private String taxId;
    private String phone;
    private String city;
    private String uf;
    private String description;
    private Double reputation;
    private String profilePhotoUrl;
    private ReputationDTO reputationDetails;
    private List<CompanyPreviousProjectDTO> previousProjects;

    public CompanyDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getTaxId() { return taxId; }
    public void setTaxId(String taxId) { this.taxId = taxId; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getUf() { return uf; }
    public void setUf(String uf) { this.uf = uf; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getReputation() { return reputation; }
    public void setReputation(Double reputation) { this.reputation = reputation; }

    public String getProfilePhotoUrl() { return profilePhotoUrl; }
    public void setProfilePhotoUrl(String profilePhotoUrl) { this.profilePhotoUrl = profilePhotoUrl; }

    public ReputationDTO getReputationDetails() { return reputationDetails; }
    public void setReputationDetails(ReputationDTO reputationDetails) { this.reputationDetails = reputationDetails; }

    public List<CompanyPreviousProjectDTO> getPreviousProjects() { return previousProjects; }
    public void setPreviousProjects(List<CompanyPreviousProjectDTO> previousProjects) { this.previousProjects = previousProjects; }
}
