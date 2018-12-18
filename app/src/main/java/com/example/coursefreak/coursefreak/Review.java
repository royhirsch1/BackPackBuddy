package com.example.coursefreak.coursefreak;

import java.util.List;

public class Review {
    public String reviewID;
    public String reviewText;
    public Integer numHelped;
    public List<ReviewComment> reviewComments;

    public Review() {
        // Default constr for Firebase
    }

    public Review(String id, String text, Integer helped, List<ReviewComment> comments) {
        this.reviewID = id;
        this.reviewText = text;
        this.numHelped = helped;
        this.reviewComments = comments;
    }

    public String getReviewID() {
        return this.reviewID;
    }

    public String getReviewText() {
        return this.reviewText;
    }

    public Integer getNumHelped() {
        return this.numHelped;
    }

    public List<ReviewComment> getReviewComments() {
        return this.reviewComments;
    }
}
