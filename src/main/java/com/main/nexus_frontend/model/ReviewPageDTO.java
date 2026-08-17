package com.main.nexus_frontend.model;

import java.util.List;

public class ReviewPageDTO {
    private List<ReviewDisplayDTO> reviews;
    private long totalReviews;
    private double averageRating;

    public ReviewPageDTO() {}

    public List<ReviewDisplayDTO> getReviews() {
        return reviews;
    }

    public void setReviews(List<ReviewDisplayDTO> reviews) {
        this.reviews = reviews;
    }

    public long getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(long totalReviews) {
        this.totalReviews = totalReviews;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }
}
