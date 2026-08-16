package com.main.nexus_frontend.model;

import java.util.List;

public class ProfessionalSimpleDTO {
    private Long id;
    private String name;
    private String phone;
    private Double reputation;
    private String profilePhotoUrl;
    private List<String> skills;

    public ProfessionalSimpleDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Double getReputation() { return reputation; }
    public void setReputation(Double reputation) { this.reputation = reputation; }

    public String getProfilePhotoUrl() { return profilePhotoUrl; }
    public void setProfilePhotoUrl(String profilePhotoUrl) { this.profilePhotoUrl = profilePhotoUrl; }

    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }
}
