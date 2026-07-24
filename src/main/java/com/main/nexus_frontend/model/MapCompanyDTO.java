package com.main.nexus_frontend.model;

public class MapCompanyDTO {
    private Long id;
    private String companyName;
    private String city;
    private String uf;
    private Double latitude;
    private Double longitude;
    private Double reputation;
    private Integer openProjects;

    public MapCompanyDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
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

    public Double getReputation() {
        return reputation;
    }

    public void setReputation(Double reputation) {
        this.reputation = reputation;
    }

    public Integer getOpenProjects() {
        return openProjects;
    }

    public void setOpenProjects(Integer openProjects) {
        this.openProjects = openProjects;
    }


    
}
