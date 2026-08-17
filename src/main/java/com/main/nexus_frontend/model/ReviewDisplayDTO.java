package com.main.nexus_frontend.model;

import java.util.List;

public class ReviewDisplayDTO {
    private Long id;
    private int rating;
    private String comment;
    private List<String> positiveReasons;
    private List<String> negativeReasons;
    private String reviewerName;
    private String reviewerPhotoUrl;
    private String reviewerType;
    private String opportunityTitle;
    // String, não LocalDateTime: o serializer JS-inline do Thymeleaf (usado pra injetar
    // REVIEWS_DATA na página) usa um ObjectMapper próprio sem JavaTimeModule — LocalDateTime
    // quebra a serialização no meio do stream (mesmo bug já visto em MapOpportunityDTO).
    private String createdAt;

    public ReviewDisplayDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<String> getPositiveReasons() {
        return positiveReasons;
    }

    public void setPositiveReasons(List<String> positiveReasons) {
        this.positiveReasons = positiveReasons;
    }

    public List<String> getNegativeReasons() {
        return negativeReasons;
    }

    public void setNegativeReasons(List<String> negativeReasons) {
        this.negativeReasons = negativeReasons;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public String getReviewerPhotoUrl() {
        return reviewerPhotoUrl;
    }

    public void setReviewerPhotoUrl(String reviewerPhotoUrl) {
        this.reviewerPhotoUrl = reviewerPhotoUrl;
    }

    public String getReviewerType() {
        return reviewerType;
    }

    public void setReviewerType(String reviewerType) {
        this.reviewerType = reviewerType;
    }

    public String getOpportunityTitle() {
        return opportunityTitle;
    }

    public void setOpportunityTitle(String opportunityTitle) {
        this.opportunityTitle = opportunityTitle;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
