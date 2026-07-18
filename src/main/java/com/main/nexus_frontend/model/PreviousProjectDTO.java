package com.main.nexus_frontend.model;

public class PreviousProjectDTO {
    private Long id;
    private String title;
    private String description;
    private String technologies;
    private Integer yearOfCompletion;

    public PreviousProjectDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getTechnologies() { return technologies; }
    public void setTechnologies(String technologies) { this.technologies = technologies; }
    public Integer getYearOfCompletion() { return yearOfCompletion; }
    public void setYearOfCompletion(Integer yearOfCompletion) { this.yearOfCompletion = yearOfCompletion; }
}
