package com.main.nexus_frontend.model;

import java.util.List;

public class ReviewRequestDTO {
    private Integer rating;
    private String comment;
    private String authorType;
    private List<String> positiveReasons;
    private List<String> negativeReasons;

    public ReviewRequestDTO() {}

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public String getAuthorType() { return authorType; }
    public void setAuthorType(String authorType) { this.authorType = authorType; }
    public List<String> getPositiveReasons() { return positiveReasons; }
    public void setPositiveReasons(List<String> positiveReasons) { this.positiveReasons = positiveReasons; }
    public List<String> getNegativeReasons() { return negativeReasons; }
    public void setNegativeReasons(List<String> negativeReasons) { this.negativeReasons = negativeReasons; }
}
