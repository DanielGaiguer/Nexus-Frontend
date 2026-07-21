package com.main.nexus_frontend.model;

public class PublicProjectDTO {
    private String title;
    private String technologies;
    private Integer yearOfCompletion;

    public PublicProjectDTO() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getTechnologies() { return technologies; }
    public void setTechnologies(String technologies) { this.technologies = technologies; }

    public Integer getYearOfCompletion() { return yearOfCompletion; }
    public void setYearOfCompletion(Integer yearOfCompletion) { this.yearOfCompletion = yearOfCompletion; }
}
