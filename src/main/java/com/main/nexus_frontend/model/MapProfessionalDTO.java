package com.main.nexus_frontend.model;

import java.util.List;

public class MapProfessionalDTO {
    private Long id;
    private String name;
    private String city;
    private String uf;
    private String experienceLevel;
    private Double latitude;
    private Double longitude;
    private Double reputation;
    private Boolean available;
    private List<String> skills;

    public MapProfessionalDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getUf() { return uf; }
    public void setUf(String uf) { this.uf = uf; }
    public String getExperienceLevel() { return experienceLevel; }
    public void setExperienceLevel(String experienceLevel) { this.experienceLevel = experienceLevel; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public Double getReputation() { return reputation; }
    public void setReputation(Double reputation) { this.reputation = reputation; }
    public Boolean getAvailable() { return available; }
    public void setAvailable(Boolean available) { this.available = available; }
    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }
}
