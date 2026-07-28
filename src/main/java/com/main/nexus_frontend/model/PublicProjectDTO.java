package com.main.nexus_frontend.model;

import java.util.List;

public class PublicProjectDTO {
    private String title;
    private List<String> technologies;
    private Integer yearOfCompletion;

    public PublicProjectDTO() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public List<String> getTechnologies() { return technologies; }
    public void setTechnologies(List<String> technologies) { this.technologies = technologies; }

    public Integer getYearOfCompletion() { return yearOfCompletion; }
    public void setYearOfCompletion(Integer yearOfCompletion) { this.yearOfCompletion = yearOfCompletion; }
}
